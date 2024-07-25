package com.unsaapp.utilidades

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.widget.CheckBox
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.unsaapp.MainActivityBD
import com.unsaapp.R

class Ventanas {
    companion object {
        @SuppressLint("SetTextI18n")
        fun ventanaMsj(
            context: Context,
            msj: String,
            index: Int,
            numerito: TextView
        ) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.ventana1, null)
            builder.setView(dialogView)

            val text1 = dialogView.findViewById<TextView>(R.id.text1)
            val btnAds1 = dialogView.findViewById<Button>(R.id.btn1)
            val btnAds2 = dialogView.findViewById<Button>(R.id.btn2)
            val btnobtener = dialogView.findViewById<Button>(R.id.btnobtener)
            val compartir = dialogView.findViewById<Button>(R.id.compartir)
            val compartir1 = dialogView.findViewById<Button>(R.id.compartir1)

            val lista = dialogView.findViewById<TextView>(R.id.listo)
            val pendiente = dialogView.findViewById<TextView>(R.id.pendiente)
            val enProceso = dialogView.findViewById<TextView>(R.id.proceso)

            //BD
            val verificado = dialogView.findViewById<TextView>(R.id.veri)
            val txt_profe = dialogView.findViewById<TextView>(R.id.txt_profe)
            val btn_profe = dialogView.findViewById<Button>(R.id.btn_profe)

            val bd = dialogView.findViewById<LinearLayout>(R.id.capaBD)
            val materias = dialogView.findViewById<LinearLayout>(R.id.capaMate)
            val anuncios = dialogView.findViewById<LinearLayout>(R.id.anuncio)

            bd.visibility = View.GONE
            materias.visibility = View.GONE
            anuncios.visibility = View.VISIBLE

            val alertDialog = builder.create()
            text1.text = msj
            btnAds1.setOnClickListener {
                AdManager.mostrarAnuncioRecompensado(context, numerito, 1)
            }
            btnAds2.setOnClickListener {
                AdManager.mostrarAnuncioRecompensado(context, numerito, 2)
            }
            compartir1.setOnClickListener {
                Utilidades.mostrarMsjCorto(context, "Cargando")
                Utilidades.compartirApp(context)
            }
            when (index) {
                0 -> {
                    btnobtener.visibility = View.GONE
                    compartir.visibility = View.GONE
                }

                1 -> {
                    btnAds1.visibility = View.GONE
                    btnAds2.visibility = View.GONE
                    btnobtener.visibility = View.VISIBLE
                    btnobtener.text = "Quiero donar 😁"
                    btnobtener.setOnClickListener {
                        Utilidades.enviarMensajeWhatsApp(context,  1,0,"","")
                        alertDialog.dismiss()
                    }
                    compartir.setOnClickListener {
                        Utilidades.mostrarMsjCorto(context, "Cargando")
                        Utilidades.compartirApp(context)
                    }
                }

                2 -> {
                    btnAds1.visibility = View.GONE
                    btnAds2.visibility = View.GONE
                    btnobtener.visibility = View.VISIBLE
                    btnobtener.text = "Quiero ser administrador"
                    btnobtener.setOnClickListener {
                        if (Utilidades.existeValorAlmacen("idusuario", context)) {
                            Utilidades.enviarMensajeWhatsApp(context, 2,0,"","")
                        } else {
                            Utilidades.mostrarMsjCorto(
                                context,
                                "Necesita internet o volver a ingresar a la aplicacion"
                            )
                        }
                        alertDialog.dismiss()
                    }

                }

                3 -> {
                    lista.text =
                        "Los estudiantes de la carrera terminaron de agregar todas las materias de todos sus respectivos años"
                    pendiente.text = "Los estudiantes de la carrera no agregaron ninguna materia "
                    enProceso.text =
                        "Los estudiantes de la carrera agregaron almenos 1 materia de la carrera, y ya pueden utilizar las materias agregadas"
                    bd.visibility = View.GONE
                    materias.visibility = View.VISIBLE
                    anuncios.visibility = View.GONE
                }

                4 -> {
                    bd.visibility = View.VISIBLE
                    materias.visibility = View.GONE
                    anuncios.visibility = View.GONE
                    verificado.text = "Items verificados"
                    txt_profe.text =
                        "Gracias a los item verificados podemos evitarnos estafas no deseadas"
                    btn_profe.visibility = View.GONE
                }

                5 -> {
                    bd.visibility = View.VISIBLE
                    materias.visibility = View.GONE
                    anuncios.visibility = View.GONE
                    verificado.text = "Preguntas verificadas por profesores"
                    txt_profe.text =
                        "Si eres profesor y quieres ayudar a los estudiantes, puedes hacerlo verificando las preguntas, para que sepan los estudiantes que estan bien formuladas\n" + "\n" + "NOTA: Las Preguntas verificadas ya no podran ser editadas por sus creadores"
                }
            }
            btn_profe.setOnClickListener {
                Utilidades.mostrarMsjCorto(context,"Cargando")
                Utilidades.enviarMensajeWhatsApp(context,3,0,"","")
            }
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Fondo transparente
            alertDialog.window?.attributes?.windowAnimations =
                R.style.DialogAnimation // Animación personalizada
            alertDialog.show()
        }
        fun msjImportante(context: Context, mensaje: String, index: Int,estudiante: TextView) {
            val Almacen=context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
            val IdCarrera = Almacen.getString("idCarrera", "")
            val deposito = context.getSharedPreferences(IdCarrera, Context.MODE_PRIVATE)

            val edit = deposito.edit()
            val builder = androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialogStyle)
            if (index == 1) {
                builder.setMessage("Existe una nueva version\n" + "NOTA: puedes omitir hasta 5 veces antes de actualizar")
            } else {
                builder.setMessage(mensaje)
            }

            val editText = EditText(context)
            val nombre = deposito.getString("estu", "Estudiante")
            var contador = deposito.getInt("postergar", 5)
            editText.hint = nombre

            // Establecer el inputType y agregar un filtro para limitar a una sola línea y 15 caracteres
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            editText.filters = arrayOf(InputFilter.LengthFilter(15))
            if (index == 2) {
                builder.setView(editText)
            }

            val positiveButtonTitle = if (index == 1) "Actualizar" else "Aceptar"
            builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                // Resto de tu código...
                when (index) {
                    1 -> {
                        Utilidades.mostrarMsjCorto(context, "Cargando")
                        val packageName =
                            "com.unsaapp"  // Reemplaza con el paquete de tu aplicación en la Play Store
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$packageName")
                                )
                            )
                        } catch (e: ActivityNotFoundException) {
                            // Si la aplicación de Play Store no está instalada, abrir en el navegador web
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                )
                            )
                        }
                    }

                    2 -> {
                        estudiante.text = editText.text.toString()
                        edit.putString("estu", estudiante.text.toString())
                        edit.apply()
                        dialog.dismiss() // Cierra el diálogo
                    }
                }
            }
            if (index == 1) {
                builder.setNeutralButton("Más tarde($contador)") { dialog, _ ->
                    contador -= 1
                    edit.putInt("postergar", contador)
                    edit.apply()
                    dialog.dismiss() // Cierra el diálogo
                    // Lógica para el botón "Más tarde"
                    // Puedes agregar aquí lo que necesites hacer cuando se presiona "Más tarde"
                }
            }
            val alertDialog = builder.create()
            alertDialog.setCancelable(index != 1) // Permitir o no cerrar tocando fuera del diálogo según el índice
            alertDialog.setOnShowListener {
                val neutralButton = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL)
                if (contador == 0) {
                    neutralButton?.isEnabled = false
                    neutralButton?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.darker_gray
                        )
                    )
                } else {
                    neutralButton.setTextColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.holo_green_dark
                        )
                    )
                }
                val positiveButton = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.holo_red_dark
                    )
                )
            }
            alertDialog.show()
        }

        @SuppressLint("MissingInflatedId")
        fun ventanaImagen(context: Context, url: String) {
            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.ventana_imagen, null)
            builder.setView(dialogView)

            val alertDialog = builder.create()
            val imagen = dialogView.findViewById<ImageView>(R.id.imagenAnuncio)
            val pb = dialogView.findViewById<ProgressBar>(R.id.pbImg)

            val requestOptions = RequestOptions()
                .override(1200, 1200)

            Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        pb.visibility = View.GONE // Ocultar el ProgressBar si la carga falla
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        pb.visibility = View.GONE // Ocultar el ProgressBar cuando la imagen está lista
                        return false
                    }
                })
                .into(imagen)

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Fondo transparente
            alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Animación personalizada

            alertDialog.show()
        }
        fun dialogoConfirmar(context: Context, index: Int, string1: String, string2: String) {
            val Almacen = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
            val carrera = Almacen.getString("Carrera", "")
            val edit = Almacen.edit()
            var contador1 = Almacen.getInt("post1", 3)
            var contador2 = Almacen.getInt("post2", 3)

            val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)

            // Configurar el mensaje y el texto de la CheckBox según el índice
            val mensaje = if (index == 1) "¿Eres estudiante de la carrera de $carrera ?\n(Se te preguntara hasta 3 veces)\nMarca la casilla." else "Si agregas preguntas fuera de lugar/innecesarias o solo para molestar, tu id de usuario sera bloqueado y ya no podras ingresar mas preguntas en NINGUNA CARRERA \n(Se te preguntara hasta 3 veces)\n¿Estás de acuerdo?\n Marca la casilla."
            val textoCheckBox = if (index == 1) "Si, soy estudiante" else "Estoy de acuerdo"

            // Crear el TextView para el mensaje
            val mensajeTextView = TextView(context).apply {
                text = mensaje
                setPadding(32, 32, 32, 32)
            }

            // Crear la CheckBox
            val checkBox = CheckBox(context).apply {
                text = textoCheckBox
            }

            // Crear un LinearLayout para contener el mensaje y la CheckBox
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                addView(mensajeTextView)
                addView(checkBox)
            }

            // Configurar el AlertDialog
            builder.setView(layout)
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val positiveButtonTitle = if (index == 1) "Continuar ($contador1)" else "Continuar ($contador2)"
            builder.setPositiveButton(positiveButtonTitle, null)

            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.setOnShowListener {
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                positiveButton?.isEnabled = false // Desactivar el botón Aceptar inicialmente

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    positiveButton?.isEnabled = isChecked
                    positiveButton?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            if (isChecked) android.R.color.holo_red_dark else android.R.color.darker_gray
                        )
                    )
                }
                negativeButton.setTextColor(ContextCompat.getColor(context, R.color.green))
                positiveButton?.setOnClickListener {
                    when (index) {
                        1 -> {
                            contador1 -= 1
                            edit.putInt("post1", contador1)
                            edit.apply()
                            Utilidades.agregarPreguntas(context, string1, string2)
                        }
                        2 -> {
                            contador2 -= 1
                            edit.putInt("post2", contador2)
                            edit.apply()
                            if (context is MainActivityBD){
                                context.ventanaOpciones(context)
                            }
                        }
                    }
                    alertDialog.dismiss() // Cierra el diálogo
                }
            }

            alertDialog.show()
        }
    }

}