package com.unsaapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unsaapp.adaptadores.AdaptadorBD
import com.unsaapp.adaptadores.AdaptadorBDItems
import com.unsaapp.modelosClases.listaItemsClass
import com.unsaapp.modelosClases.listaPreguntasClass
import com.unsaapp.utilidades.Utilidades
import com.unsaapp.utilidades.Ventanas
import org.json.JSONException

class MainActivityBD : AppCompatActivity() {

    private val lista = ArrayList<listaPreguntasClass>()
    private val listaItems = ArrayList<listaItemsClass>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var buscar: EditText
    private lateinit var info: ImageView
    private lateinit var ttt: TextView
    var adaptadorBD = AdaptadorBD(this, lista, this)
    var adaptadorBDItems = AdaptadorBDItems(this, listaItems, this)
    private lateinit var agregar: FloatingActionButton
    private lateinit var actualizar: FloatingActionButton
    private var isDialogOpen = false
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var Almacen: SharedPreferences
    private lateinit var LLcargando: LinearLayout

    private lateinit var mAdView: AdView
    private val listaIDP = mutableListOf<String>()

    private var enProgreso = false
    var indiceUnidad = ""
    var WEB_URL = ""
    var UNIDAD = ""
    var idUsuario = ""
    private var doubleBackToExitPressedOnce = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_bd)

        mAdView =
            findViewById(R.id.adView) as AdView // Reemplaza "R.id.adView" con el ID de tu vista de anuncio de banner en el diseño XML
        initLoadAds()
        ttt = findViewById(R.id.ttt)
        LLcargando = findViewById(R.id.cargando)
        swipe = findViewById(R.id.swipe)
        Almacen = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val IdCarrera = Almacen.getString("idCarrera", "")
        val deposito = getSharedPreferences(IdCarrera, Context.MODE_PRIVATE)
        idUsuario = Almacen.getString("idusuario", "").toString()
        val bloqueado = deposito.getInt("bloquear", 0)

        // para verificar si le aparece las opciones de administador
        recyclerView = findViewById(R.id.recyclerview4)
        buscar = findViewById(R.id.buscar)
        info = findViewById(R.id.info1)
        agregar = findViewById(R.id.agregar)
        actualizar = findViewById(R.id.actualizar)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar22)

        if (bloqueado == 2) {
            agregar.visibility = View.GONE
        }
        //variables creadas
        indiceUnidad = intent.getStringExtra("unidad").toString()

        UNIDAD = indiceUnidad
        if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
            val enla = Almacen.getString("enlaceItems", "")
            if (enla != null) {
                WEB_URL = enla
            }
            toolbar.title = indiceUnidad
            obtenerDatosItems()
        } else {
            toolbar.title = "Unidad $indiceUnidad"
            val enlace = intent.getStringExtra("enlace")
            WEB_URL = enlace.toString()
            obtenerDatos()
        }
        configswipe()

        //boton ATRAS
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@MainActivityBD,
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

        agregar.setOnClickListener {
            val contador = Almacen.getInt("post2", 3)
            if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
                ventanaAgregarItem(this, 0, "", "", 0, 0, "", "")
            } else {
                if (contador < 1) {
                    ventanaOpciones(this)
                } else {
                    Ventanas.dialogoConfirmar(this, 2, "", "")
                }
            }
        }
        actualizar.setOnClickListener {
            if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
                listaItems.clear()
                obtenerDatosItems()
            } else {
                lista.clear()
                obtenerDatos()
            }
            Toast.makeText(this, "Actualizando", Toast.LENGTH_SHORT).show()
            swipe.isRefreshing = false
        }
        info.setOnClickListener {
            if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
                Ventanas.ventanaMsj(this, "", 4, ttt)
            } else {
                Ventanas.ventanaMsj(this, "", 5, ttt)
            }
        }
        buscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Este método se llama para notificar que el texto está a punto de cambiar
                // No se usa en este ejemplo, pero puedes realizar acciones antes de que cambie el texto
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama para notificar que el texto ha cambiado, pero aún no se ha aplicado al EditText
                // Se utiliza para realizar acciones mientras el texto está cambiando

                // La línea importante aquí: llama al método filter del adaptador con la cadena actual en el EditText
                if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
                    adaptadorBDItems.filter.filter(s)
                } else {
                    adaptadorBD.filter.filter(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Este método se llama para notificar que el texto ha cambiado y ya se ha aplicado al EditText
                // No se usa en este ejemplo, pero puedes realizar acciones después de que el texto ha cambiado
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun obtenerDatos() {
        LLcargando.visibility = View.VISIBLE
        agregar.visibility = View.GONE
        swipe.visibility = View.GONE
        val almacen = applicationContext.getSharedPreferences("Almacen", Context.MODE_PRIVATE)

        val url = "$WEB_URL&action=get&sheet=$UNIDAD&pt=1"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray(UNIDAD)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val creador = obj.getString("id")
                        val pregunta = obj.getString("question")
                        val repI1 = obj.getString("answer1")
                        val respI2 = obj.getString("answer2")
                        val respI3 = obj.getString("answer3")
                        val respC = obj.getString("answer4")
                        val idp = obj.getString("idp")
                        val valorV = obj.getInt("verificado")
                        val valor = almacen.getInt(idp, 0)

                        val newVersion = listaPreguntasClass(
                            valorV,
                            valor,
                            creador,
                            pregunta,
                            repI1,
                            respI2,
                            respI3,
                            respC,
                            idp
                        )
                        lista.add(newVersion)
                    }
                    LLcargando.visibility = View.GONE
                    adaptadorBD.notifyDataSetChanged()
                    agregar.visibility = View.VISIBLE
                    swipe.visibility = View.VISIBLE
                    simulateSpacePress()
                    configurarRecycler()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Utilidades.mostrarMsjCorto(this, "Sin conexion")
                LLcargando.visibility = View.GONE

            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun obtenerDatosItems() {
        LLcargando.visibility = View.VISIBLE
        agregar.visibility = View.GONE
        swipe.visibility = View.GONE
        val almacen = applicationContext.getSharedPreferences("Almacen", Context.MODE_PRIVATE)

        val url = "$WEB_URL&action=getI&sheet=$UNIDAD&pt=1"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jsonArray = response.getJSONArray(UNIDAD)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val creador = obj.getString("A")
                        val titulo = obj.getString("B")
                        val descripcion = obj.getString("C")
                        val linkImagen1 = obj.getString("D")
                        val linkImagen2 = obj.getString("E")
                        val linkImagen3 = obj.getString("F")
                        val precio = obj.getInt("G")
                        val num = obj.getLong("H")
                        val idItems = obj.getString("I")
                        val valorVerificado = obj.getInt("J")
                        //val precioFormateado = formatNumberWithDot(precio)
                        val newVersion = listaItemsClass(
                            valorVerificado,
                            creador,
                            num,
                            titulo,
                            linkImagen1,
                            linkImagen2,
                            linkImagen3,
                            precio,
                            descripcion,
                            idItems
                        )
                        listaItems.add(newVersion)
                    }
                    LLcargando.visibility = View.GONE
                    adaptadorBDItems.notifyDataSetChanged()
                    agregar.visibility = View.VISIBLE
                    swipe.visibility = View.VISIBLE
                    simulateSpacePress()
                    configurarRecycler()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Utilidades.mostrarMsjCorto(this, "Sin conexion")
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    fun verificarPregunta(idPregunta: String) {
        val unidad = intent.getStringExtra("unidad")
        val enlace = intent.getStringExtra("enlace")
        val url = "$enlace&action=aumentar&id=$idPregunta&sheet=$unidad"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Utilidades.mostrarMsjCorto(this, "Verificado")
                listaIDP.add(idPregunta)
            },
            { error ->
            }
        )
        val queve = Volley.newRequestQueue(this)
        queve.add(stringRequest)
    }

    ////ITEMS//////
    fun ventanaAgregarItem(
        context: Context,
        opc: Int,
        pTitulo: String,
        pDescripcion: String,
        pPrecio: Int,
        pNumero: Long,
        idItem: String,
        idCreador: String
    ) {
        if (isDialogOpen) {
            return  // Si el diálogo ya está abierto, no hagas nada
        }
        isDialogOpen = true
        var alertDialog: AlertDialog? = null // Declarar la variable alertDialog

        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        //Con el parametro Opcion vemos de donde se abre el cuadro
        if (opc == 1) {
            builder.setTitle("Actualizar")
        } else {
            builder.setTitle("Agregar")
        }
        builder.setCancelable(false)
        val scrollView = ScrollView(context)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        scrollView.addView(layout)

        val inputTitle = EditText(context)
        inputTitle.hint = "Ingrese el título (máximo 100 caracteres)"
        inputTitle.filters = arrayOf(InputFilter.LengthFilter(100))
        inputTitle.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(inputTitle)

        val inputDescription = EditText(context)
        inputDescription.hint = "Ingrese la descripción"
        inputDescription.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        inputDescription.maxLines = Int.MAX_VALUE
        inputDescription.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layout.addView(inputDescription)

        val inputPrice = EditText(context)
        inputPrice.hint = "Precio (máximo 6 dígitos)"
        inputPrice.filters =
            arrayOf(InputFilter.LengthFilter(7)) // Ajustamos para considerar el punto
        inputPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputPrice)

        val inputNumero = EditText(context)
        inputNumero.hint = "Numero al que contactaran ej 3873646049"
        inputNumero.filters =
            arrayOf(InputFilter.LengthFilter(10)) // Ajustamos para considerar el punto
        inputNumero.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(inputNumero)

        builder.setView(scrollView)
        if (opc == 1) {
            inputTitle.setText(pTitulo)
            inputDescription.setText(pDescripcion)
            inputPrice.setText(pPrecio.toString())
            inputNumero.setText(pNumero.toString())
        }
        if (opc == 1) {
            builder.setPositiveButton("Actualizar") { dialog, _ ->
                val titulo = inputTitle.text.toString()
                val descripcion = inputDescription.text.toString()
                val precio = inputPrice.text.toString()
                val numero = inputNumero.text.toString()

                if (titulo.isNotBlank() && descripcion.isNotBlank() && precio.isNotBlank() && numero.isNotBlank()) {
                    // Llama a tu función pasando las variables como argumentos
                    actualizarItem(idCreador, titulo, descripcion, precio, numero, idItem)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        context,
                        "Por favor, complete todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            builder.setPositiveButton("Agregar") { dialog, _ ->
                val id = idUsuario
                val titulo = inputTitle.text.toString()
                val descripcion = inputDescription.text.toString()
                val precio = inputPrice.text.toString()
                val numero = inputNumero.text.toString()

                if (titulo.isNotBlank() && descripcion.isNotBlank() && precio.isNotBlank() && numero.isNotBlank()) {
                    // Llama a tu función pasando las variables como argumentos
                    crearItem(id, titulo, descripcion, precio, numero)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        context,
                        "Por favor, complete todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        alertDialog =
            builder.create() //almacena todo lo que se va a crear en una variable para poder modificarla
        alertDialog.setOnShowListener {

            val positiveButtonColor = ContextCompat.getColor(context, R.color.colorPositiveButton)
            val negativeButtonColor = ContextCompat.getColor(context, R.color.colorNegativeButton)

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(positiveButtonColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(negativeButtonColor)
            alertDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        alertDialog.setOnDismissListener {
            isDialogOpen = false  // El diálogo se cerró, actualiza el estado
        }
        alertDialog.show()
    }

    fun crearItem(
        idUsuario: String,
        titulo: String,
        descripcion: String,
        precio: String,
        numero: String
    ) {
        Utilidades.mostrarMsjCorto(this, "Agregando ")
        if (enProgreso) {
            // Evitar que la función se ejecute si ya hay una solicitud en progreso
            return
        }
        val url =
            "$WEB_URL&action=agregarItem&id=$idUsuario&sheet=$UNIDAD&titulo=$titulo&descripcion=$descripcion&precio=$precio&numero=$numero"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Item agregado exitosamente", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Actualizar para ver resultados (SWIPE) ", Toast.LENGTH_SHORT)
                    .show()
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

    fun eliminaritem(id: String, titulo: String, callback: (Boolean) -> Unit) {
        Utilidades.mostrarMsjCorto(this, "Eliminando...")
        val url = "$WEB_URL&action=deleteItem&id=$id&sheet=$UNIDAD"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Item $titulo eliminado", Toast.LENGTH_SHORT).show()
                callback(true)
            },
            { error ->
                Toast.makeText(this, "No se pudo eliminar $titulo, actualizar", Toast.LENGTH_SHORT)
                    .show()
                callback(false)
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    fun actualizarItem(
        idCreador: String,
        titulo: String,
        descripcion: String,
        precio: String,
        numero: String,
        idp: String
    ) {
        Utilidades.mostrarMsjCorto(this, "Actualizando")
        val url =
            "$WEB_URL&action=actualizarItem&id=$idCreador&sheet=$UNIDAD&titulo=$titulo&descripcion=$descripcion&precio=$precio&numero=$numero&idp=$idp"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Item editado exitosamente", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Actualizar para ver resultados (SWIPE) ", Toast.LENGTH_SHORT)
                    .show()

            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        )
        val queve = Volley.newRequestQueue(this)
        queve.add(stringRequest)

    }

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    //funciones
    private fun configswipe() {
        swipe.setOnRefreshListener {
            if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
                listaItems.clear()
                obtenerDatosItems()
            } else {
                lista.clear()
                obtenerDatos()
            }
            Toast.makeText(this, "Actualizando", Toast.LENGTH_SHORT).show()
            swipe.isRefreshing = false
        }
    }

    private fun configurarRecycler() {
        if (indiceUnidad in listOf("PROFESORES", "APUNTES", "FOTOCOPIADORAS")) {
            recyclerView.adapter = adaptadorBDItems
        } else {
            recyclerView.adapter = adaptadorBD
        }
        recyclerView.setHasFixedSize(true)
    }

    private fun simulateSpacePress() {
        buscar.setText(" ") // Simula la pulsación de la tecla de espacio
        buscar.setSelection(1) // Coloca el cursor al final del espacio
        buscar.postDelayed({
            buscar.setText("") // Borra el espacio después de un pequeño retraso
            buscar.setSelection(0) // Coloca el cursor al principio
        }, 100)
    }

    fun camposEstanCompletos(editTexts: List<EditText>): Boolean {
        for (i in 0 until 4) {
            val texto = editTexts[i].text.toString().trim()
            if (texto.isEmpty()) {
                return false
            }
        }
        return true
    }

    fun camposCompleto(editTexts: EditText): Boolean {
        val texto = editTexts.text.toString().trim()
        if (texto.isEmpty()) {
            return false
        }

        return true
    }

    fun ventanaOpciones(context: Context) {
        val options = arrayOf("Verdadero o falso", "Multiple Choice")
        val unitCodes = arrayOf(1, 2)

        val ventana = AlertDialog.Builder(context, R.style.CustomDialogStyle)

        ventana.setTitle("Selecciona una opcion")

        ventana.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Seleccionado "Verdadero o falso", muestra otro AlertDialog
                    ventanaVoF(context, "", "")
                }

                1 -> {
                    // Seleccionado "Multiple Choice", muestra otro AlertDialog
                    ventanaMultipleChoice(context)
                }
            }
            dialog.dismiss()
        }
        ventana.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog = ventana.create()
        alertDialog.show()
    }

    fun ventanaVoF(context: Context, afirmacion: String, idp: String) {
        if (isDialogOpen) {
            return  // Si el diálogo ya está abierto, no hagas nada
        }
        isDialogOpen = true
        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        builder.setTitle("Verdadero o Falso")
        builder.setCancelable(false)

        // Crear un contenedor LinearLayout vertical
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        // Añadir un EditText para la pregunta con margen inferior
        val editText = EditText(context)
        val editTextParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editTextParams.bottomMargin = 15  // Margen inferior de 10dp
        editText.layoutParams = editTextParams
        editText.hint = "Ingrese la afirmacion"
        layout.addView(editText)
        //comprobamos que no este vacio los parametros recibidos
        if (afirmacion.isNotEmpty()) {
            editText.setText(afirmacion)
        }

        // Añadir un TextView para el contador de caracteres
        val characterCountTextView = TextView(context)

        characterCountTextView.gravity = Gravity.END
        characterCountTextView.text = "250 caracteres restantes"
        layout.addView(characterCountTextView)

        //texto para verdaero falso
        val texto = TextView(context)
        texto.gravity = Gravity.START
        texto.text = "Falso"
        layout.addView(texto)
        // Añadir un Switch para verdadero/falso con margen superior
        val switch = Switch(context)
        val switchParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        switchParams.topMargin = 10  // Margen superior de 10dp
        switch.layoutParams = switchParams
        layout.addView(switch)

        // Configurar el TextWatcher para actualizar el contador de caracteres
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val remainingChars = 250 - s?.length!!
                characterCountTextView.text = "$remainingChars caracteres restantes"
            }
        })

        // Configurar el Switch y el texto asociado
        switch.setOnCheckedChangeListener { _, isChecked ->
            texto.visibility = View.GONE
            val switchText = if (isChecked) "Verdadero" else "Falso"
            switch.text = switchText
        }

        builder.setView(layout)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            // Obtener los valores al hacer clic en Aceptar
            val id = idUsuario
            val pregunta = editText.text.toString()
            var opcion1 = ""
            var opcion2 = ""
            if (switch.isChecked) {
                opcion1 = "Verdadero"
                opcion2 = "Falso"
            } else {
                opcion1 = "Falso"
                opcion2 = "Verdadaro"
            }

            if (idp.isNotEmpty()) {
                subirPreguntaEditada(id, pregunta, "", "", opcion2, opcion1, idp)
            }
            if (camposCompleto(editText)) {

                subirPreguntaBD(id, pregunta, "", "", opcion2, opcion1)
                dialog.dismiss()

            } else {
                Toast.makeText(context, "Por favor, ingrese la afirmacion.", Toast.LENGTH_SHORT)
                    .show()
            }
            // Aquí puedes manejar la lógica según los valores obtenidos
            // ...

        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {

            val positiveButtonColor = ContextCompat.getColor(context, R.color.colorPositiveButton)
            val negativeButtonColor = ContextCompat.getColor(context, R.color.colorNegativeButton)

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(positiveButtonColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(negativeButtonColor)
            alertDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        alertDialog.setOnDismissListener {
            isDialogOpen = false  // El diálogo se cerró, actualiza el estado
        }
        alertDialog.show()
    }

    fun ventanaMultipleChoice(context: Context) {
        if (isDialogOpen) {
            return  // Si el diálogo ya está abierto, no hagas nada
        }
        isDialogOpen = true
        var alertDialog: AlertDialog? = null // Declarar la variable alertDialog

        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        builder.setTitle("Agregar pregunta")
        builder.setCancelable(false)
        val scrollView = ScrollView(context)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        scrollView.addView(layout)

        val hintStrings = listOf(
            "Ingrese pregunta", "Respuesta correcta", "Respuesta incorrecta 1",
            "Respuesta incorrecta 2", "Respuesta incorrecta 3"
        )

        val editTexts = mutableListOf<EditText>()

        for (hintText in hintStrings) {
            val input = EditText(context)
            val maxLength = 250  // Max caracteres permitidos
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            input.maxLines = Int.MAX_VALUE
            input.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            input.hint = hintText

            val paddingDp = 10
            val paddingPx = (paddingDp * context.resources.displayMetrics.density).toInt()
            input.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            input.layoutParams = layoutParams

            layout.addView(input)

            val characterCountTextView = TextView(context)
            characterCountTextView.text = "$maxLength caracteres maximo  "
            characterCountTextView.setTextColor(Color.GRAY)

            val characterCountLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            characterCountLayoutParams.gravity = Gravity.END
            characterCountTextView.layoutParams = characterCountLayoutParams

            layout.addView(characterCountTextView)

            // Configura el TextWatcher para actualizar el contador
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val remainingChars = maxLength - s?.length!!
                    characterCountTextView.text = "$remainingChars  "
                }
            })

            editTexts.add(input)
        }

        builder.setView(scrollView)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            val id = idUsuario
            val pregunta = editTexts[0].text.toString()
            var respuestaCorrecta = editTexts[1].text.toString()
            if (respuestaCorrecta.endsWith(" ")) {
                respuestaCorrecta = respuestaCorrecta.replace(Regex("\\s+$"), "")
            }
            val respuesta2 = editTexts[2].text.toString()
            val respuesta3 = editTexts[3].text.toString()
            val respuesta1 = editTexts[4].text.toString()

            //para verificar que todas las casillas estan llenas
            if (camposEstanCompletos(editTexts)) {
                // Llama a tu función pasando las variables como argumentos
                subirPreguntaBD(id, pregunta, respuesta1, respuesta2, respuesta3, respuestaCorrecta)
                dialog.dismiss()
            } else {
                Toast.makeText(
                    context,
                    "Por favor, complete los campos obligatorios.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // Llama a tu función pasando las variables como argumentos
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        alertDialog =
            builder.create() //almacena todo lo que se va a crear en una variable para poder modificarla
        alertDialog.setOnShowListener {

            val positiveButtonColor = ContextCompat.getColor(context, R.color.colorPositiveButton)
            val negativeButtonColor = ContextCompat.getColor(context, R.color.colorNegativeButton)

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(positiveButtonColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(negativeButtonColor)
            alertDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        alertDialog.setOnDismissListener {
            isDialogOpen = false  // El diálogo se cerró, actualiza el estado
        }
        alertDialog.show()
    }

    fun subirPreguntaBD(
        idUsuario: String,
        pregunta: String,
        respuesta1: String,
        respuesta2: String,
        respuesta3: String,
        respuestaCorrecta: String
    ) {
        Utilidades.mostrarMsjCorto(this, "Agregando ")
        if (enProgreso) {
            // Evitar que la función se ejecute si ya hay una solicitud en progreso
            return
        }
        val url =
            "$WEB_URL&action=create&id=$idUsuario&sheet=$UNIDAD&question=$pregunta&answer1=$respuesta1&answer2=$respuesta2&answer3=$respuesta3&answer4=$respuestaCorrecta&correct=$respuestaCorrecta"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Pregunta agregada exitosamente", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Actualizar para ver resultados (SWIPE) ", Toast.LENGTH_SHORT)
                    .show()
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

    fun eliminarPregunta(id: String, pre: String, callback: (Boolean) -> Unit) {
        Utilidades.mostrarMsjCorto(this, "Eliminando...")
        val url = "$WEB_URL&action=delete&id=$id&sheet=$UNIDAD"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Pregunta $pre eliminado", Toast.LENGTH_SHORT).show()
                callback(true)
            },
            { error ->
                Toast.makeText(this, "No se pudo eliminar $pre, actualizar", Toast.LENGTH_SHORT)
                    .show()
                callback(false)
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    fun Dilog_editarpregunta(
        context: Context,
        id: String,
        edit0: String,
        edit1: String,
        edit2: String,
        edit3: String,
        edit4: String,
        idp: String
    ) {
        if (isDialogOpen) {
            return  // Si el diálogo ya está abierto, no hagas nada
        }
        isDialogOpen = true
        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        builder.setTitle("Editar pregunta")
        builder.setCancelable(false)
        val scrollView = ScrollView(context)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        scrollView.addView(layout)

        val hintStrings = listOf(
            "Ingrese pregunta", "Respuesta incorrecta 1",
            "Respuesta incorrecta 2", "Respuesta incorrecta 3", "Respuesta correcta"
        )

        val editTexts = mutableListOf<EditText>()

        for ((index, hintText) in hintStrings.withIndex()) {
            val input = EditText(context)
            val maxLength = 250  // Max caracteres permitidos
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            input.maxLines = Int.MAX_VALUE
            input.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            input.hint = hintText

            val paddingDp = 10
            val paddingPx = (paddingDp * context.resources.displayMetrics.density).toInt()
            input.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            input.layoutParams = layoutParams

            layout.addView(input)

            val characterCountTextView = TextView(context)
            characterCountTextView.text = "$maxLength caracteres maximo  "
            characterCountTextView.setTextColor(Color.GRAY)

            val characterCountLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            characterCountLayoutParams.gravity = Gravity.END
            characterCountTextView.layoutParams = characterCountLayoutParams

            layout.addView(characterCountTextView)

            // Configura el TextWatcher para actualizar el contador
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val remainingChars = maxLength - s?.length!!
                    characterCountTextView.text = "$remainingChars  "
                }
            })
            when (index) {
                0 -> input.setText(edit0)
                1 -> input.setText(edit1)
                2 -> input.setText(edit2)
                3 -> input.setText(edit3)
                4 -> input.setText(edit4)
            }
            editTexts.add(input)
        }

        builder.setView(scrollView)

        builder.setPositiveButton("Confirmar") { dialog, _ ->
            val id = id
            val pregunta = editTexts[0].text.toString()
            val respuesta1 = editTexts[1].text.toString()
            val respuesta2 = editTexts[2].text.toString()
            val respuesta3 = editTexts[3].text.toString()
            val respuestaCorrecta = editTexts[4].text.toString()
            val idp = idp

            subirPreguntaEditada(
                id,
                pregunta,
                respuesta1,
                respuesta2,
                respuesta3,
                respuestaCorrecta,
                idp
            )



            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }


        val alertDialog =
            builder.create() //almacena todo lo que se va a crear en una variable para poder modificarla

        alertDialog.setOnShowListener {

            val positiveButtonColor = ContextCompat.getColor(context, R.color.colorPositiveButton)
            val negativeButtonColor = ContextCompat.getColor(context, R.color.colorNegativeButton)

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(positiveButtonColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(negativeButtonColor)
            alertDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        alertDialog.setOnDismissListener {
            isDialogOpen = false  // El diálogo se cerró, actualiza el estado
        }
        alertDialog.show()
    }

    fun subirPreguntaEditada(
        id: String,
        pregunta: String,
        respuesta1: String,
        respuesta2: String,
        respuesta3: String,
        respuestaCorrecta: String,
        idp: String
    ) {
        Utilidades.mostrarMsjCorto(this, "Actualizando")
        val url =
            "$WEB_URL&action=update&id=$id&sheet=$UNIDAD&question=$pregunta&answer1=$respuesta1&answer2=$respuesta2&answer3=$respuesta3&answer4=$respuestaCorrecta&correct=$respuestaCorrecta&idp=$idp"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "Pregunta editada exitosamente", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Actualizar para ver resultados (SWIPE) ", Toast.LENGTH_SHORT)
                    .show()

            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        )
        val queve = Volley.newRequestQueue(this)
        queve.add(stringRequest)

    }

    @SuppressLint("MissingInflatedId")
    override fun onPause() {
        super.onPause()

        // Elimina los valores guardados en SharedPreferences
        val sharedPreferences = getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for (valueKey in listaIDP) {
            editor.remove(valueKey)
        }
        editor.apply()
    }


}