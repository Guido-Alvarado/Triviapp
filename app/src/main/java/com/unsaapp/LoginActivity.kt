package com.unsaapp


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.unsaapp.utilidades.Utilidades
import org.json.JSONException

class LoginActivity : AppCompatActivity() {

    private lateinit var imagen: ImageView
    private lateinit var txtlogin: TextView
    private lateinit var boton: Button
    private lateinit var spinner: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var cargando: LinearLayout
    private lateinit var Almacen: SharedPreferences
    val textoCompleto = "▼ Primero debe seleccionar una Institucion ▼"
    val delayMillis = 100 // Cambia este valor para ajustar la velocidad de la animación

    var index = 0
    val handler = Handler()
    private val enlaceInstitutos =
        "https://script.google.com/macros/s/AKfycbx4o56Ltx52khq_NJ3q8vJqPQh0V40j5JC_izDlxIEkLE-uf4Tg9WJE65NLt50_B7Qj/exec?"

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtlogin = findViewById(R.id.txtlogin)
        imagen = findViewById(R.id.imglogin)
        boton = findViewById(R.id.btnLogin)
        spinner = findViewById(R.id.spnLogin)
        cargando = findViewById(R.id.cargandoSpiner)
        progressBar = findViewById(R.id.pgbar)
        Almacen = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val edit = Almacen.edit()
        boton.setOnClickListener {
            Utilidades.mostrarMsjCorto(this,spinner.selectedItemPosition.toString())
            progressBar.visibility = View.VISIBLE
            boton.visibility = View.GONE
            edit.putInt("IndInst", spinner.selectedItemPosition)
            edit.apply()
            finish()
        }

        obtenerIntitutos()
    }

    private fun obtenerIntitutos() {
        cargando.visibility = View.VISIBLE
        spinner.visibility = View.GONE
        txtlogin.visibility = View.GONE
        boton.visibility = View.GONE
        val runnable = object : Runnable {
            override fun run() {
                if (index < textoCompleto.length) {
                    val letra = textoCompleto[index]
                    txtlogin.append(letra.toString())
                    index++
                    handler.postDelayed(this, delayMillis.toLong())
                }
            }
        }
        val listaSpinnerCarreras =
            mutableListOf<String>()// variable que contendra todas las carreras

        val url = "$enlaceInstitutos&action=obtener&sheet=INST&pt=1"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray("INST")
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val instutos = obj.getString("B")

                        // agreando a la lista para el spinner
                        listaSpinnerCarreras.add(
                            CarreraFacultad(instutos, "").toString()
                        )
                        //listaSpinnerCarreras.add(carreras)
                    }
                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaSpinnerCarreras
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    //SpinnerCarreras.adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1,listaSpinnerCarreras)
                    spinner.visibility = View.VISIBLE
                    txtlogin.visibility = View.VISIBLE
                    cargando.visibility = View.GONE
                    boton.visibility = View.VISIBLE
                    Utilidades.mostrarMsjCorto(this,"Seleccionar Institucion 😀")
                    handler.post(runnable)

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

    data class CarreraFacultad(val carrera: String, val carre: String) {
        override fun toString(): String {
            // Esta función determina cómo se mostrará cada objeto en el Spinner
            return "$carrera "
        }
    }

}