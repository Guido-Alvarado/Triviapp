package com.unsaapp

import com.unsaapp.utilidades.AdManager
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.provider.Settings.Secure
import android.text.TextUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.unsaapp.enlaces.UnitListManager.WEBSTD
import com.unsaapp.utilidades.Utilidades
import com.unsaapp.utilidades.Ventanas
import org.json.JSONException
import java.util.Calendar


class MainActivityInicio : AppCompatActivity() {
    private lateinit var imageUsuario: ImageView
    private lateinit var importante: ImageView
    private lateinit var intentos: ImageView
    private lateinit var carrerasImage: ImageView
    private lateinit var sobreapp: ImageView
    private lateinit var donacion: ImageView
    private lateinit var grupos: ImageView
    private lateinit var estudiante: TextView
    private lateinit var anio1: LinearLayout
    private lateinit var anio2: LinearLayout
    private lateinit var anio3: LinearLayout
    private lateinit var anio4: LinearLayout
    private lateinit var anio5: LinearLayout
    private lateinit var anio6: LinearLayout
    private lateinit var carreraTitulo: TextView
    private lateinit var num_ints: TextView
    private lateinit var saludo: TextView
    private lateinit var textoObjetivo: TextView
    private lateinit var btnCrearID: Button
    private lateinit var txtCrearID: TextView
    private lateinit var ProgressBarCrearID: ProgressBar

    private lateinit var txtApuntes: TextView
    private lateinit var imgApuntes: ImageView
    private lateinit var llApuntes: LinearLayout
    private lateinit var txtFotocopiadora: TextView
    private lateinit var imgFotocopiadora: ImageView
    private lateinit var llFotocopiadora: LinearLayout
    private lateinit var txtProfesor: TextView
    private lateinit var imgProfesor: ImageView
    private lateinit var llProfesor: LinearLayout
    private lateinit var botones: LinearLayout

    private lateinit var mAdView: AdView

    private lateinit var numint: SharedPreferences
    private lateinit var Almacen: SharedPreferences
    private lateinit var deposito: SharedPreferences
    private lateinit var modo: SharedPreferences
    private var valorActual: Int = 0
    private lateinit var androidId: String
    private var anio: Int = 0

    private var isExpanded = false

    private var isActivity1InForeground = false

    private var doubleBackToExitPressedOnce = false

    private var enProgreso = false

    @SuppressLint("MissingInflatedId", "ResourceType", "SetTextI18n", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_inicio)

        imgApuntes = findViewById(R.id.icApuntes)
        imgProfesor = findViewById(R.id.icProfesor)
        imgFotocopiadora = findViewById(R.id.icFotocopiadora)
        txtApuntes = findViewById(R.id.txtApuntes)
        txtProfesor = findViewById(R.id.txtProfesor)
        txtFotocopiadora = findViewById(R.id.txtFotocopiadora)
        llApuntes = findViewById(R.id.llapuntess)
        llProfesor = findViewById(R.id.llProfesor)
        llFotocopiadora = findViewById(R.id.llfotocopiadora)
        botones = findViewById(R.id.botones)

        estudiante = findViewById(R.id.estudiante1)
        carreraTitulo = findViewById(R.id.carrera)
        textoObjetivo = findViewById(R.id.textoObjetivo)
        textoObjetivo.text =
            getString(R.string.objetivo)
        androidId = Secure.getString(contentResolver, Secure.ANDROID_ID)
        num_ints = findViewById(R.id.num_ints)
        numint = getSharedPreferences("Puntajes", Context.MODE_PRIVATE)
        modo = getSharedPreferences("MODO", Context.MODE_PRIVATE)
        Almacen = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val IdCarrera = Almacen.getString("idCarrera", "")

        deposito = getSharedPreferences(IdCarrera, Context.MODE_PRIVATE)
        estudiante.text = deposito.getString("estu", "Estudiante")
        textoObjetivo.setOnClickListener {
            if (isExpanded) {
                // Si está expandido, contraer el texto
                textoObjetivo.maxLines = 1
                textoObjetivo.ellipsize = TextUtils.TruncateAt.END
                isExpanded = false
            } else {
                // Si está contraído, expandir el texto
                textoObjetivo.maxLines = Integer.MAX_VALUE
                textoObjetivo.ellipsize = null
                isExpanded = true
            }
        }

        mAdView =findViewById(R.id.adView) // Reemplaza "R.id.adView" con el ID de tu vista de anuncio de banner en el diseño XML
        cargarAnuncios()

        anio1 = findViewById(R.id.e1)
        anio2 = findViewById(R.id.e2)
        anio3 = findViewById(R.id.e3)
        anio4 = findViewById(R.id.e4)
        anio5 = findViewById(R.id.e5)
        anio6 = findViewById(R.id.e6)

        txtCrearID = findViewById(R.id.IDusuario)
        btnCrearID = findViewById(R.id.btnID)
        ProgressBarCrearID = findViewById(R.id.progressID)

        importante = findViewById(R.id.importante)
        intentos = findViewById(R.id.intentos)
        donacion = findViewById(R.id.donacion)
        carrerasImage = findViewById(R.id.carrerasImage)
        sobreapp = findViewById(R.id.sobreapp)
        grupos = findViewById(R.id.grupos)
        saludo = findViewById(R.id.saludo)
        imageUsuario = findViewById(R.id.usuario1)

        //saludo dependiendo de la hora del dia
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> saludo.text = "¡Buenos días!"
            in 12..19 -> saludo.text = "¡Buenas tardes!"
            in 20..24 -> saludo.text = "¡Buenas noches!"
            else -> saludo.text = "¡Hola!"
        }

        importante.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Ventanas.msjImportante(
                this,
                getString(R.string.utilizarApp),
                0, estudiante
            )
        }
        intentos.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Ventanas.ventanaMsj(
                this,
                getString(R.string.anuncios),
                0, num_ints
            )
        }
        donacion.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Ventanas.ventanaMsj(
                this,
                getString(R.string.donacion),
                1, num_ints
            )
        }
        carrerasImage.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Utilidades.mostrarMsjCorto(this, "Cargando")
            val intent = Intent(this, MainActivityLista::class.java)
            intent.putExtra("lista", "carreras")
            this.startActivity(intent)
        }
        //ca-app-pub-3940256099942544/5224354917 prueba
        AdManager.loadRewardedAd(this, "ca-app-pub-9869448126662919/8473560460", intentos, 1)
        AdManager.loadRewardedAd(this, "ca-app-pub-9869448126662919/4492037135", intentos, 2)

        sobreapp.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Ventanas.msjImportante(
                this, getString(R.string.presentacion), 0, estudiante
            )
        }

        anio1.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                if (existeValorAlmacen("idusuario", this)) {
                    Utilidades.mostrarMsjCorto(this, "Cargando")
                    val intent = Intent(this, MainActivityCatedras::class.java)
                    intent.putExtra("año", 1)
                    this.startActivity(intent)
                } else {
                    Utilidades.mostrarMsjCorto(this, "Debes Generar un ID de usuario")
                }
            }
        }
        anio2.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                if (existeValorAlmacen("idusuario", this)) {
                    Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivityCatedras::class.java)
                    intent.putExtra("año", 2)
                    this.startActivity(intent)
                } else {
                    Utilidades.mostrarMsjCorto(this, "Debes Generar un ID de usuario")
                }
            }


        }
        anio3.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                if (existeValorAlmacen("idusuario", this)) {
                    Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivityCatedras::class.java)
                    intent.putExtra("año", 3)
                    this.startActivity(intent)
                } else {
                    Utilidades.mostrarMsjCorto(this, "Debes Generar un ID de usuario")
                }
            }

        }
        anio4.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                if (existeValorAlmacen("idusuario", this)) {
                    Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivityCatedras::class.java)
                    intent.putExtra("año", 4)
                    this.startActivity(intent)
                } else {
                    Utilidades.mostrarMsjCorto(this, "Debes Generar un ID de usuario")
                }
            }

        }
        anio5.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                if (existeValorAlmacen("idusuario", this)) {
                    Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivityCatedras::class.java)
                    intent.putExtra("año", 5)
                    this.startActivity(intent)
                } else {
                    Utilidades.mostrarMsjCorto(this, "Debes Generar un ID de usuario")
                }
            }

        }
        anio6.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                if (existeValorAlmacen("idusuario", this)) {
                    Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivityCatedras::class.java)
                    intent.putExtra("año", 6)
                    this.startActivity(intent)
                } else {
                    Utilidades.mostrarMsjCorto(this, "Debes Generar un ID de usuario")
                }
            }

        }
        btnCrearID.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_200))

        btnCrearID.setOnClickListener {
            if (existeValorAlmacen("idCarrera", this)) {
                obtenerDatos()
            } else {
                Utilidades.mostrarMsjCorto(this, "Elegir Carrera")
            }
        }

        if (existeValorAlmacen("idCarrera", this)) {
            if (existeValorAlmacen("idusuario", this)) {
                txtCrearID.text = Almacen.getString("idusuario", "No hay")
                btnCrearID.visibility = View.GONE
                obtenerDatos()
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivityInicio,
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
    }


    //obtienen datos del administradores
    private fun obtenerDatos() {
        txtCrearID.visibility = View.GONE
        ProgressBarCrearID.visibility = View.VISIBLE
        val IdCarrera = Almacen.getString("idCarrera", "")
        val editA = Almacen.edit()
        val editoD = deposito.edit()

        val url = "$WEBSTD&action=get&sheet=$IdCarrera&pt=1"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray(IdCarrera.toString())
                    // Variable para verificar si se encontró el Android ID en algún elemento
                    var encontrado = false
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val id = obj.getString("id")

                        if (id == "Guido") {
                            val mensaje = obj.getString("mensajeAdmin")
                            val actualizacion = obj.getString("avisoAdmin")
                            val imagen = obj.getString("imagenAdmin")
                            val versionN = obj.getInt("version")
                            editoD.putString("actualizacion", actualizacion)
                            editoD.apply()
                            if (mensaje.isNotEmpty()) {
                                Ventanas.msjImportante(this, mensaje, 0, estudiante)
                            }
                            if (imagen.isNotEmpty()) {
                                Ventanas.ventanaImagen(this, imagen)
                            }
                            val versionCode = Utilidades.getVersionCode(this)
                            if (versionN != versionCode) {
                                Ventanas.msjImportante(this, "", 1, estudiante)
                            }
                        }
                        //mensaje Actualizar

                        if (id == androidId) {
                            val admin1 = obj.getInt("admin1")
                            val admin2 = obj.getInt("admin2")
                            val admin3 = obj.getInt("admin3")
                            val admin4 = obj.getInt("admin4")
                            val admin5 = obj.getInt("admin5")
                            val admin6 = obj.getInt("admin6")
                            val bloquear = obj.getInt("bloquear")
                            val mensajeP = obj.getString("mensajeP")

                            editA.putString("Creado", "")
                            editA.putString("idusuario", id)
                            editA.apply()

                            editoD.putInt("admin1", admin1)
                            editoD.putInt("admin2", admin2)
                            editoD.putInt("admin3", admin3)
                            editoD.putInt("admin4", admin4)
                            editoD.putInt("admin5", admin5)
                            editoD.putInt("admin6", admin6)
                            editoD.putInt("bloquear", bloquear)
                            editoD.apply()
                            if (mensajeP.isNotEmpty()) {
                                Ventanas.msjImportante(this, mensajeP, 1, estudiante)
                            }
                            encontrado = true
                            Utilidades.mostrarMsjCorto(this, "Usuario Obtenido")
                            break
                        }
                    }

                    // Realiza la acción si el Android ID no se encuentra en ningún elemento
                    if (!encontrado) {
                        Utilidades.mostrarMsjCorto(this, "Creando ID")
                        crearUsuario()
                    }

                    txtCrearID.text = androidId
                    btnCrearID.visibility = View.GONE
                    txtCrearID.visibility = View.VISIBLE
                    ProgressBarCrearID.visibility = View.GONE
                } catch (e: JSONException) {
                    Utilidades.mostrarMsjCorto(this, "No se pudo obtener usuario")
                }

            },
            { error ->
                Utilidades.mostrarMsjCorto(this, "No se pudo obtner usuario")
                txtCrearID.text = androidId
                ProgressBarCrearID.visibility = View.GONE
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    private fun crearUsuario() {
        val id = androidId
        val IdCarrera = Almacen.getString("idCarrera", "")
        val editA = Almacen.edit()
        val url = "$WEBSTD&action=cu&sheet=$IdCarrera&id=$id"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Utilidades.mostrarMsjCorto(this, "id creado")
                editA.putString("Creado", "")
                editA.putString("idusuario", id)
                editA.apply()
                enProgreso = false
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                enProgreso = false
            }
        )
        val queve = Volley.newRequestQueue(this)
        queve.add(stringRequest)
        enProgreso = true
    }

    private fun cargarAnuncios() {
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    //Funciones
    private fun existeValorAlmacen(key: String, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        return sharedPreferences.contains(key)
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

    override fun onResume() {
        super.onResume()
        isActivity1InForeground = true
        if (!existeValorAlmacen("IndInst", this)) {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
        }

        if (existeValorAlmacen("idusuario", this)) {
            botones.visibility = View.VISIBLE
        } else {
            botones.visibility = View.GONE
        }
        llApuntes.setOnClickListener {
            Utilidades.mostrarMsjCorto(this, "Cargando")
            val intent = Intent(this, MainActivityBD::class.java)
            intent.putExtra("unidad", "APUNTES")
            this.startActivity(intent)
        }
        llFotocopiadora.setOnClickListener {
            Utilidades.mostrarMsjCorto(this, "Cargando")
            val intent = Intent(this, MainActivityBD::class.java)
            intent.putExtra("unidad", "FOTOCOPIADORAS")
            this.startActivity(intent)
        }
        llProfesor.setOnClickListener {
            Utilidades.mostrarMsjCorto(this, "Cargando")
            val intent = Intent(this, MainActivityBD::class.java)
            intent.putExtra("unidad", "PROFESORES")
            this.startActivity(intent)
        }

        anio = Almacen.getInt("aniosC", 0)
        when (anio) {
            3 -> {
                anio1.visibility = View.VISIBLE
                anio2.visibility = View.VISIBLE
                anio3.visibility = View.VISIBLE
            }

            4 -> {
                anio1.visibility = View.VISIBLE
                anio2.visibility = View.VISIBLE
                anio3.visibility = View.VISIBLE
                anio4.visibility = View.VISIBLE
            }

            5 -> {
                anio1.visibility = View.VISIBLE
                anio2.visibility = View.VISIBLE
                anio3.visibility = View.VISIBLE
                anio4.visibility = View.VISIBLE
                anio5.visibility = View.VISIBLE
            }

            6 -> {
                anio1.visibility = View.VISIBLE
                anio2.visibility = View.VISIBLE
                anio3.visibility = View.VISIBLE
                anio4.visibility = View.VISIBLE
                anio5.visibility = View.VISIBLE
                anio6.visibility = View.VISIBLE
            }

        }
        grupos.setOnClickListener {
            if (!existeValorAlmacen("Carrera", this)) {
                Utilidades.mostrarMsjCorto(this, "Debes seleccionar una Carrera")
            } else {
                val group = Almacen.getString("grupoWh", "")

                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(group)
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Si WhatsApp no está instalado en el dispositivo, puedes manejar la excepción aquí
                    Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (existeValorAlmacen("Creado", this)) {
            btnCrearID.visibility = View.GONE
        } else {
            btnCrearID.visibility = View.VISIBLE
        }
        carreraTitulo.text = Almacen.getString("Carrera", "")
        if (carreraTitulo.text.isEmpty()) {
            carreraTitulo.visibility = View.GONE
        } else {
            carreraTitulo.visibility = View.VISIBLE
        }

        imageUsuario.setOnClickListener {
            Ventanas.msjImportante(this, "id: $androidId", 2, estudiante)
        }

        valorActual = numint.getInt("intentos", 5) // Valor inicial 5
        num_ints.text = valorActual.toString()
        val editor = numint.edit()
        editor.putInt("intentos", valorActual)
        editor.apply()
    }

    // esta funcion es para saber si se fue a otra activity y esta en pausa
    override fun onPause() {
        super.onPause()
        isActivity1InForeground = false
        //Utilidades.showToastShort(this,"Activity1 está en segundo plano")
    }

    // cuando se destruya la aplicacion
    override fun onDestroy() {
        clearCache()
        super.onDestroy()
    }

    private fun clearCache() {
        try {
            // Obtiene el contexto de la aplicación.
            val context = applicationContext

            // Llama al método para eliminar la caché de la aplicación.
            context.cacheDir.deleteOnExit()

            // También puedes eliminar la caché externa si la aplicación la utiliza.
            // context.getExternalCacheDir().deleteOnExit();
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}