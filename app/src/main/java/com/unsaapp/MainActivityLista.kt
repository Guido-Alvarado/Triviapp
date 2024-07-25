package com.unsaapp

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.unsaapp.adaptadores.AdaptadorListaCarreras
import com.unsaapp.modelosClases.ListaCarreras
import com.unsaapp.modelosClases.ListaUsuarios
import com.unsaapp.adaptadores.AdaptadorListaUsuarios
import com.unsaapp.utilidades.Utilidades
import com.unsaapp.utilidades.Ventanas
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivityLista : AppCompatActivity() {

    //variables
    val versionList = ArrayList<ListaCarreras>()
    val versionListUsuarios = ArrayList<ListaUsuarios>()
    lateinit var recyclerView: RecyclerView
    lateinit var buscarCarrera: EditText
    private lateinit var LLcargando: LinearLayout
    lateinit var buscarUsuario: EditText
    private lateinit var tete: TextView
    lateinit var info: ImageView

    var adaptadorBD = AdaptadorListaCarreras(this, versionList, this)
    var adaptadorUsuarios = AdaptadorListaUsuarios(this, versionListUsuarios, this)
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var tolbar: androidx.appcompat.widget.Toolbar
    private lateinit var almacen: SharedPreferences

    private lateinit var mAdView: AdView
    private var enProgreso = false
    private var ejecutar = ""

    private var doubleBackToExitPressedOnce = false
    var enlace1 =
        "https://script.google.com/macros/s/AKfycbzyf3u8_svB0f0QPYMC9i9n6Rmta-qcFwzeu3ubDYQMmQpQNWMZ-ECFuPMpMX_NqOJBSA/exec?"
    var enlace2 =
        "https://script.google.com/macros/s/AKfycbwDNFumGWvlrx6Med0rNzsmgTB7q-pZfbEVBkRJeOclbff1Qdo6tjLZOs5ryjby9fQ_6A/exec?"
    var enlaceCarreras = ""
    var enlaceUsuarios =
        "https://script.google.com/macros/s/AKfycbwDNFumGWvlrx6Med0rNzsmgTB7q-pZfbEVBkRJeOclbff1Qdo6tjLZOs5ryjby9fQ_6A/exec?"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_lista)

        //initLoadAds()
        almacen = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        mAdView =
            findViewById(R.id.adView) as AdView // Reemplaza "R.id.adView" con el ID de tu vista de anuncio de banner en el diseño XML
        cargarAnuncios()
        LLcargando = findViewById(R.id.cargando)
        swipe = findViewById(R.id.swipe)
        info = findViewById(R.id.info)
        tolbar = findViewById(R.id.toolbar2)
        recyclerView = findViewById(R.id.RCcarreras)
        buscarCarrera = findViewById(R.id.editBuscarCarrera)
        buscarUsuario = findViewById(R.id.editBuscarUsuario)
        tete=findViewById(R.id.tete)

        ejecutar = intent.getStringExtra("lista").toString()

        val numero = almacen.getInt("IndInst", 3)
        if (numero == 0) {
            tolbar.title = "Carreras UNSa"
            enlaceCarreras = enlace1
        } else {
            tolbar.title = "Carreras ISdM"
            enlaceCarreras = enlace2
        }

        /*if (ejecutar=="usuarios"){
            info.visibility=View.GONE
            tolbar.title="Usuarios UNSa y Extensiones"
            buscarUsuario.visibility=View.VISIBLE
            buscarCarrera.visibility=View.GONE
            obtenerUsuarios()
        }else {
            tolbar.title="Carreras UNSa y Extensiones"
            buscarCarrera.visibility=View.VISIBLE
            buscarUsuario.visibility=View.GONE
            obtenerCarreras()
        }*/
        obtenerCarreras()
        configswipe()

        //boton ATRAS
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivityLista,
                        "Presione nuevamente para salir",
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        info.setOnClickListener {
            Ventanas.ventanaMsj(this,"",3,tete)
        }
        buscarCarrera.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Este método se llama para notificar que el texto está a punto de cambiar
                // No se usa en este ejemplo, pero puedes realizar acciones antes de que cambie el texto
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama para notificar que el texto ha cambiado, pero aún no se ha aplicado al EditText
                // Se utiliza para realizar acciones mientras el texto está cambiando

                // La línea importante aquí: llama al método filter del adaptador con la cadena actual en el EditText
                adaptadorBD.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // Este método se llama para notificar que el texto ha cambiado y ya se ha aplicado al EditText
                // No se usa en este ejemplo, pero puedes realizar acciones después de que el texto ha cambiado
            }
        })

        /*buscarUsuario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Este método se llama para notificar que el texto está a punto de cambiar
                // No se usa en este ejemplo, pero puedes realizar acciones antes de que cambie el texto
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama para notificar que el texto ha cambiado, pero aún no se ha aplicado al EditText
                // Se utiliza para realizar acciones mientras el texto está cambiando

                // La línea importante aquí: llama al método filter del adaptador con la cadena actual en el EditText
                adaptadorUsuarios.filter.filter(s)


            }

            override fun afterTextChanged(s: Editable?) {
                // Este método se llama para notificar que el texto ha cambiado y ya se ha aplicado al EditText
                // No se usa en este ejemplo, pero puedes realizar acciones después de que el texto ha cambiado
            }
        })*/

    }

    private fun cargarAnuncios() {
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    //llamadas HTTP GET
    @SuppressLint("NotifyDataSetChanged")
    private fun obtenerCarreras() {
        LLcargando.visibility = View.VISIBLE
        swipe.visibility = View.GONE

        val url = "$enlaceCarreras&action=obtener&sheet=CARRERAS&pt=1"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray("CARRERAS")
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val carreras = obj.getString("B")
                        val numeroI = obj.getInt("A")
                        val indiceCarre = obj.getString("D")
                        val contadorVotos = obj.getInt("C")
                        val facu = obj.getString("E")
                        val grupoWha = obj.getString("F")
                        val anosC = obj.getInt("G")
                        val materiasA = obj.getString("H")
                        val enlaceItems = obj.getString("I")

                        val imagen = transformarImagen(numeroI)
                        //mandamos la info de cada item
                        val newVersion = ListaCarreras(
                            numeroI,
                            imagen,
                            indiceCarre,
                            carreras,
                            contadorVotos,
                            facu,
                            grupoWha,
                            "PUNTOS",
                            anosC,
                            materiasA,
                            enlaceItems
                        )
                        versionList.add(newVersion)
                    }
                    LLcargando.visibility = View.GONE
                    adaptadorBD.notifyDataSetChanged()
                    swipe.visibility = View.VISIBLE
                    simulateSpacePress()
                    configurarRecycler()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    Utilidades.mostrarMsjCorto(this,"Hubo un error")
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                Utilidades.mostrarMsjCorto(this,"No se pudo")
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    fun msjImportante(context: Context, mensaje: String) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        builder.setMessage(mensaje)

        val positiveButtonTitle = "Aceptar"
        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            dialog.dismiss() // Cierra el diálogo
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun obtenerUsuarios() {
        val idCarrera = almacen.getString("idCarrera", "")
        swipe.visibility = View.GONE

        val url = "$enlaceUsuarios&action=get&sheet=$idCarrera"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray(idCarrera.toString())
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val idUsuario = obj.getString("id")
                        val admin1 = obj.getInt("admin1")
                        val admin2 = obj.getInt("admin2")
                        val admin3 = obj.getInt("admin3")
                        val boton = obj.getInt("bloquear")
                        //creamos un nombre para guardarlo y saber si ya voto

                        val admin1B = convertirNumeroABoolean(admin1)
                        val admin2B = convertirNumeroABoolean(admin2)
                        val admin3B = convertirNumeroABoolean(admin3)

                        val newVersion = ListaUsuarios(idUsuario, admin1B, admin2B, admin3B, boton)
                        versionListUsuarios.add(newVersion)
                    }

                    adaptadorUsuarios.notifyDataSetChanged()
                    swipe.visibility = View.VISIBLE
                    simulateSpacePress1()
                    configurarRecycler2()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    Utilidades.mostrarMsjCorto(this,"Hubo un error")


                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                Utilidades.mostrarMsjCorto(this,"No se pudo")
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    fun designarAdmin(idUsuario: String, ano: Int, veri: Int) {
        if (enProgreso) {
            enProgreso = false
            Utilidades.mostrarMsjCorto(this,"Espera un momento")
            return
        }
        val url = "$enlaceUsuarios&action=admin&id=$idUsuario&sheet=ENF&ano=$ano&si=$veri"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Utilidades.mostrarMsjCorto(this,"Podes actualizar para ver")
                enProgreso = false
            },
            { error ->
                enProgreso = false
                Utilidades.mostrarMsjCorto(this,"Volver a intentar")
            }
        )
        val queve = Volley.newRequestQueue(this)
        queve.add(stringRequest)
        enProgreso = true
    }

    fun bloquear(idUsuario: String, num: Int) {
        if (enProgreso) {
            enProgreso = false
            Utilidades.mostrarMsjCorto(this,"Espera un momento")
            return
        }
        val url = "$enlaceUsuarios&action=bloq&id=$idUsuario&sheet=ENF&num=$num"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Utilidades.mostrarMsjCorto(this,"Podes actualizar para ver")
                enProgreso = false
            },
            { error ->
                enProgreso = false
                Utilidades.mostrarMsjCorto(this,"Volver a intentar")
            }
        )
        val queve = Volley.newRequestQueue(this)
        queve.add(stringRequest)
        enProgreso = true
    }

    // al seleccionar una carrera se cargan los datos de la carrera
    internal fun guardarMaterias(IDcarrera: String, facu: String, carreraa: String) {
        val mate = IDcarrera + "mate"
        val queue = Volley.newRequestQueue(this)
        val url = "$enlaceCarreras&action=obtener&sheet=$facu&pt=1"
        val jsonArrayFiltered = JSONArray()
        //aca obtenemos toda la lista de las materias de todas la carreras de la facu
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray(facu)
                    for (i in 0 until jsonArray.length()) {
                        val itemObject = jsonArray.getJSONObject(i)
                        val idCarrera = itemObject.getString("D")
                        // aca vamos a filtrar las materias de la carrera que seleccionamos , ej ENF
                        if (idCarrera == IDcarrera) {
                            jsonArrayFiltered.put(itemObject)
                        }
                    }
                    val jsonObjectFiltered = JSONObject()
                    jsonObjectFiltered.put(mate, jsonArrayFiltered)

                    // Convertir el JSONObject a una cadena JSON
                    val jsonString = jsonObjectFiltered.toString()
                    // Guardar la cadena JSON en un archivo llamado el nombre de la indice de la carrera+ mate eje ENFmate.json
                    val fileName = "$mate.json"
                    this.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                        it.write(jsonString.toByteArray())
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    Utilidades.mostrarMsjCorto(this,"No hay materias en la carrera")


                }
                Utilidades.mostrarMsjCorto(this,"Materias cargadas correctamente")
                Utilidades.mostrarMsjCorto(this,"Cargando Unidades")
                guardarUnidades(IDcarrera, carreraa)

            },
            { error ->
                Utilidades.mostrarMsjCorto(this,"Volver a Intentar")
            })
        queue.add(jsonObjectRequest)
    }

    private fun guardarUnidades(hoja: String, Carrera: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "$enlaceCarreras&action=obtener&sheet=$hoja&pt=1"
        val jsonArrayFiltered = JSONArray()
        val jsonObjectRequest = JsonObjectRequest(

            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray(hoja)
                    for (i in 0 until jsonArray.length()) {
                        val itemObject = jsonArray.getJSONObject(i)
                        val idCarrera = itemObject.getString("D")
                        jsonArrayFiltered.put(itemObject)
                    }
                    val jsonObjectFiltered = JSONObject()
                    jsonObjectFiltered.put(hoja, jsonArrayFiltered)

                    Utilidades.mostrarMsjCorto(this,"Unidades cargadas correctamente")

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    Utilidades.mostrarMsjCorto(this,"Hubo un error")


                }
                // Guardar el JSON completo en el almacenamiento interno
                val success = saveJsonToFile("$hoja.json", response.toString())

                finish()
            },
            { error ->
                Utilidades.mostrarMsjCorto(this,"Error al cargar, volver a cargar ")
            })
        queue.add(jsonObjectRequest)
    }

    //funcion especificas

    private fun simulateSpacePress() {
        buscarCarrera.setText(" ") // Simula la pulsación de la tecla de espacio
        buscarCarrera.setSelection(1) // Coloca el cursor al final del espacio
        buscarCarrera.postDelayed({
            buscarCarrera.setText("") // Borra el espacio después de un pequeño retraso
            buscarCarrera.setSelection(0) // Coloca el cursor al principio
        }, 100)
    }

    private fun simulateSpacePress1() {
        buscarUsuario.setText(" ") // Simula la pulsación de la tecla de espacio
        buscarUsuario.setSelection(1) // Coloca el cursor al final del espacio
        buscarUsuario.postDelayed({
            buscarUsuario.setText("") // Borra el espacio después de un pequeño retraso
            buscarUsuario.setSelection(0) // Coloca el cursor al principio
        }, 100)
    }

    private fun configswipe() {
        if (ejecutar == "usuarios") {
            swipe.setOnRefreshListener {
                versionListUsuarios.clear() //agregar condicion
                obtenerUsuarios()
                swipe.isRefreshing = false

            }
        } else {
            swipe.setOnRefreshListener {
                versionList.clear() //agregar condicion
                obtenerCarreras()
                swipe.isRefreshing = false

            }
        }
    }

    private fun saveJsonToFile(filename: String, json: String): Boolean {
        return try {
            openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun transformarImagen(imageN: Int): Int {
        return when {
            imageN == 3 -> R.drawable.listo
            imageN == 2 -> R.drawable.enproceso
            imageN == 1 -> R.drawable.pendiente
            else -> R.drawable.listo
        }
    }

    private fun configurarRecycler2() {
        recyclerView.adapter = adaptadorUsuarios
        recyclerView.setHasFixedSize(true)
    }

    private fun configurarRecycler() {
        recyclerView.adapter = adaptadorBD
        recyclerView.setHasFixedSize(true)
    }

    fun convertirNumeroABoolean(numero: Int): Boolean {
        return numero == 1
    }

    fun agregarM() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://t.me/+ToVONBdbXJxkNDZh")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Si WhatsApp no está instalado en el dispositivo, puedes manejar la excepción aquí
            Toast.makeText(this, "Telegram no está instalado", Toast.LENGTH_SHORT).show()
        }

    }


}