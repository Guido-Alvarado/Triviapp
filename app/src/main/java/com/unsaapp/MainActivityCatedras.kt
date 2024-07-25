package com.unsaapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.unsaapp.adaptadores.AdaptadorMaterias
import com.unsaapp.adaptadores.AdaptadorUnidades
import com.unsaapp.modelosClases.ModeloMaterias
import com.unsaapp.modelosClases.ModeloUnidades
import com.unsaapp.preguntas.unidad6
import com.unsaapp.utilidades.AdManager
import com.unsaapp.utilidades.Utilidades
import com.unsaapp.utilidades.Ventanas
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar


class MainActivityCatedras : AppCompatActivity() {
    private lateinit var listaUnidadesR: RecyclerView
    private lateinit var listaMateriasR: RecyclerView
    private lateinit var unidadesAdapter: AdaptadorUnidades
    private lateinit var materiasAdapter: AdaptadorMaterias
    private lateinit var administradores: ImageView
    private lateinit var intentos: ImageView
    private lateinit var usuarios: ImageView
    private lateinit var num_ints: TextView
    private lateinit var saludo: TextView
    private lateinit var anio: TextView
    private lateinit var estudiante: TextView

    private lateinit var modo: SharedPreferences
    private lateinit var Materia: String
    private lateinit var Unidad: String

    private lateinit var listMaterL: ArrayList<ModeloMaterias>
    private lateinit var listUnidL: ArrayList<ModeloUnidades>

    private lateinit var mAdView: AdView

    private lateinit var almacen: SharedPreferences
    private lateinit var almacen2: SharedPreferences

    private var valorActual: Int = 0

    private var isActivity1InForeground = false

    private var doubleBackToExitPressedOnce = false

    @SuppressLint("MissingInflatedId", "ResourceType", "SetTextI18n", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_catedras)

        listaUnidadesR = findViewById(R.id.recyclerview3)
        listaMateriasR = findViewById(R.id.recyclerview2)

        estudiante = findViewById(R.id.estudiante)
        administradores = findViewById(R.id.administradores)
        intentos = findViewById(R.id.intentos)
        usuarios = findViewById(R.id.usuarios)
        saludo = findViewById(R.id.saludo)
        anio = findViewById(R.id.anio)
        num_ints = findViewById(R.id.num_ints)
        almacen = getSharedPreferences("Puntajes", Context.MODE_PRIVATE)
        almacen2 = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        modo = getSharedPreferences("MODO", Context.MODE_PRIVATE)

        estudiante.text = "Estudiante"
        configurarRecycler()

        AdManager.loadRewardedAd(this, "ca-app-pub-9869448126662919/8473560460", intentos, 1)
        AdManager.loadRewardedAd(this, "ca-app-pub-9869448126662919/4492037135", intentos, 2)

        mAdView = findViewById(R.id.adView)
        initLoadAds()
        val numero = intent.getIntExtra("año", 0)

        val editor = almacen2.edit()
        editor.putInt("anoAdmin", numero)
        editor.apply()
        anio.text = "Asignaturas de $numero año"

        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        when (hora) {
            in 5..11 -> saludo.text = "¡Buenos días!"
            in 12..19 -> saludo.text = "¡Buenas tardes!"
            in 20..24 -> saludo.text = "¡Buenas noches!"
            else -> saludo.text = "¡Hola!"
        }//saludo dependiendo de la hora del dia

        administradores.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Ventanas.ventanaMsj(
                this, getString(R.string.txtAdmin),
                2, num_ints
            )

        }
        intentos.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            Ventanas.ventanaMsj(
                this, getString(R.string.anuncios),
                0, num_ints
            )
        }
        usuarios.setOnClickListener {
            Utilidades.aplicarEfectoBote(it)
            val Almacen = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
            val IdCarrera = Almacen.getString("idCarrera", "")
            val deposito = getSharedPreferences(IdCarrera, Context.MODE_PRIVATE)
            val mensaje = deposito.getString("actualizacion", "")

            Ventanas.msjImportante(this, mensaje.toString(), 0, anio)
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivityCatedras,
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

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    fun cargarUnidadesDesdeJSON(
        context: Context,
        nombre: String,
        catedraF: String
    ): ArrayList<ModeloUnidades> {
        val items = ArrayList<ModeloUnidades>()
        val sharedPref = applicationContext.getSharedPreferences("Puntajes", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        try {
            val inputStream = context.openFileInput("$nombre.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

            val jsonString = bufferedReader.use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(nombre)

            for (i in 0 until jsonArray.length()) {
                val itemObject = jsonArray.getJSONObject(i)
                val imageN = itemObject.getInt("A")
                val titulo = itemObject.getString("B")
                val subtitulo = itemObject.getString("F")
                val nombrePuntaje = itemObject.getString("C")
                val catedra = itemObject.getString("D")
                val enlace = itemObject.getString("E")
                val porcentaje = itemObject.getString("G")
                val imagen = itemObject.getString("H")

                if (catedra == catedraF) {
                    val imageResource = Utilidades.numeroToImagen(imageN)
                    val mostrarP = letraToBoolean(porcentaje)
                    val mostrarI = letraToBoolean(imagen)
                    //guardar puntaje
                    val valorPorcentajeGuardado = sharedPref.getInt(nombrePuntaje, 0)

                    //creamos nombres para los tiempos
                    val nombreMejorT = nombrePuntaje + "ttt"
                    val nombreUltimoT = nombrePuntaje + "uuu"
                    val nombreCantidadP = nombrePuntaje + "ccc"

                    val tiempoG = sharedPref.getInt(nombreMejorT, 0)
                    val ultimoT = sharedPref.getInt(nombreUltimoT, 0)
                    val cantidadP = sharedPref.getInt(nombreCantidadP, 0)

                    val keys = listOf(nombrePuntaje, nombreMejorT, nombreUltimoT, nombreCantidadP)

                    keys.forEach { key ->
                        if (!sharedPref.contains(key)) {
                            editor.putInt(key, 0)
                            editor.apply()
                        }
                    }

                    val itemData = ModeloUnidades(
                        imageResource,
                        cantidadP,
                        ultimoT,
                        tiempoG,
                        nombrePuntaje,
                        enlace,
                        catedra,
                        titulo,
                        "$valorPorcentajeGuardado %",
                        R.drawable.fondo_unidades,
                        valorPorcentajeGuardado,
                        subtitulo,
                        mostrarP,
                        mostrarI
                    )
                    items.add(itemData)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }

    fun cargarMateriasDesdeJSON(
        context: Context,
        nombreMate: String,
        filtrarAño: Int
    ): ArrayList<ModeloMaterias> {
        val items = ArrayList<ModeloMaterias>()
        try {
            val inputStream = context.openFileInput("$nombreMate.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

            val jsonString = bufferedReader.use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray(nombreMate)

            for (i in 0 until jsonArray.length()) {
                val itemObject = jsonArray.getJSONObject(i)
                val materias = itemObject.getString("B")
                val catedraF = itemObject.getString("C")
                val numero = itemObject.getInt("A")

                if (numero == filtrarAño) {
                    val item2 =
                        ModeloMaterias(catedraF, materias, "", R.drawable.background_materias)
                    items.add(item2)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utilidades.mostrarMsjCorto(this, "error al cargar")
        }
        return items
    }

    //FUnciones
    fun configurarRecycler() {
        listaUnidadesR.setHasFixedSize(true) // para establecer que los elementos no cambiaran de tamaño, de dimesion
        listaMateriasR.setHasFixedSize(true)
        Materia = almacen2.getString("Materias", "").toString()
        Unidad = almacen2.getString("Unidades", "").toString()

        listaUnidadesR.layoutManager = LinearLayoutManager(this) //establecer el layoutManager
        listaMateriasR.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val numero = intent.getIntExtra("año", 0)

        listMaterL = cargarMateriasDesdeJSON(this, Materia, numero)
        listUnidL = cargarUnidadesDesdeJSON(this, Unidad, "")

        materiasAdapter = AdaptadorMaterias(listMaterL, listaMateriasR)
        listaMateriasR.adapter = materiasAdapter

        listaMateriasR.findViewHolderForAdapterPosition(0)?.itemView?.performClick()

        unidadesAdapter = AdaptadorUnidades(listUnidL)
        listaUnidadesR.adapter = unidadesAdapter
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

    fun letraToBoolean(input: String): Boolean {
        return input.equals("v", ignoreCase = true)
    }

    fun existeJson(filename: String): Boolean {
        val file = this.getFileStreamPath(filename)
        return file.exists()
    }

    fun intentarUnidad(unidad: String) {

        if (valorActual < 1) {
            Utilidades.mostrarMsjCorto(this, "No tienes Intentos")
        } else {

            val jsonFileName = "$unidad.json"
            if (existeJson(jsonFileName)) {
                // Ejecutar la acción si el archivo JSON existe
                val intent = Intent(this, unidad6::class.java)
                intent.putExtra("nombre", unidad)
                this.startActivity(intent)

                val nuevovalor = valorActual - 1
                val editor = almacen.edit()
                editor.putInt("intentos", nuevovalor)
                editor.apply()
            } else {
                // Mostrar mensaje si el archivo JSON no existe
                Toast.makeText(this, "Actualizar Preguntas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun actualizarPreguntas(unidad: String, enlace: String) {

        val queue = Volley.newRequestQueue(this)
        //eliminar puntaje actual
        val ultimoP = unidad + "ttt"
        val mejorT = unidad + "uuu"
        val cantidadP = unidad + "ccc"
        val sharedPref = applicationContext.getSharedPreferences("Puntajes", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.remove(unidad)
        editor.remove(ultimoP)
        editor.remove(mejorT)
        editor.apply()

        val url = "$enlace&action=get&sheet=$unidad"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Guardar el JSON completo en el almacenamiento interno
                val success = saveJsonToFile("$unidad.json", response.toString())
                if (success) {
                    val unidadArray = response.getJSONArray(unidad)
                    val longitudUnidad = unidadArray.length()
                    editor.putInt(cantidadP, longitudUnidad)
                    editor.apply()


                    Toast.makeText(this, "Preguntas actualizadas correctamente", Toast.LENGTH_LONG)
                        .show()
                    Toast.makeText(
                        this,
                        "La cantidad de preguntas es: $longitudUnidad",
                        Toast.LENGTH_LONG
                    ).show()

                }
            },
            { error ->
                Utilidades.mostrarMsjCorto(this, "No hay preguntas en la unidad ")
            })
        queue.add(jsonObjectRequest)

    }

    override fun onResume() {
        super.onResume()
        isActivity1InForeground = true
        Utilidades.mostrarMsjCorto(this, "Seleccionar Materia 😀")

        Unidad = almacen2.getString("Unidades", "").toString()

        valorActual = almacen.getInt("intentos", 5) // Valor inicial 5
        num_ints.text = valorActual.toString()
        materiasAdapter.setOnItemClickListener(object : AdaptadorMaterias.OnItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(position: Int, catedra: String) {
                unidadesAdapter = AdaptadorUnidades(
                    cargarUnidadesDesdeJSON(
                        this@MainActivityCatedras,
                        Unidad,
                        catedra
                    )
                )
                listaUnidadesR.adapter = unidadesAdapter
                unidadesAdapter.notifyDataSetChanged()
            }
        })
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