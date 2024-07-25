package com.unsaapp.utilidades

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.unsaapp.MainActivityBD
import com.unsaapp.R
import com.unsaapp.modelosClases.ItemData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLEncoder

class Utilidades {
    //El uso de companion object en Kotlin te permite definir métodos y propiedades que se pueden acceder directamente desde el nombre de la clase
    companion object {
        fun compartirApp(context: Context){
            val packageName = context.packageName
            val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
            val message = "Descarga mi app desde la Play Store: $playStoreUrl"

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT,message)
            shareIntent.type = "text/plain"
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(shareIntent)
            }
        }
        fun mostrarMsjCorto(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun showToastLong(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun enviarMensajeWhatsApp(
            context: Context,
            index: Int,
            numeroTel: Long,
            titulo: String,
            idItem:String
        ) {
            val Almacen = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
            val idUsuario = Almacen.getString("idusuario", "").toString()
            val sharedPreferences = context.getSharedPreferences("MODO", Context.MODE_PRIVATE)
            val id = sharedPreferences.getString("idnombre", "")

            var numeroTelefono = "5493875033993"
            var mensaje = ""

            when(index){
                1->{
                    mensaje="Solo quería dejarles saber lo increíble que encuentro su aplicación educativa. ¡Es realmente genial! Quiero agradecerles por el arduo trabajo que han puesto en esto. La verdad es que hace una gran diferencia.\n" + "Así que aquí va mi pequeña contribución. ¡Espero que ayude a mantener el buen trabajo!\n" + "MERCADO PAGO :\n" + " alias: Guido.Alvarado.mp\n" + "CVU: 0000003100025604484546"
                }
                2->{
                    mensaje="Hola, ¿cómo estás? quisiera colaborar mi id es $id "
                }
                3->{
                    mensaje="Hola, ¿cómo estás? Soy profesor/a y quisiera ser administrador para poder ayudar a los estudiantes verificando las preguntas, mi id es: $idUsuario"
                }
                4->{
                    mensaje =
                        "Buenas ¿Como estas ? Quisiera verificar esto:$titulo ,el id del Item es:$idItem"
                }
                5->{
                    numeroTelefono = "549$numeroTel"
                    mensaje = "Buenas ¿Como estas ? Me interesa esto:$titulo"
                }
            }

            try {
                // Crea la URI de WhatsApp con el número de teléfono y el mensaje
                val uri = Uri.parse(
                    "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${
                        URLEncoder.encode(
                            mensaje,
                            "UTF-8"
                        )
                    }"
                )
                // Abre WhatsApp con la URI
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cargarListaDesdeJSON(
            context: Context,
            jsonFileName: String,
            string: String
        ): List<ItemData> {
            val items = ArrayList<ItemData>()

            try {
                val inputStream = context.openFileInput(jsonFileName)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

                val jsonString = bufferedReader.use { it.readText() }
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray(string)

                for (i in 0 until jsonArray.length()) {
                    val itemObject = jsonArray.getJSONObject(i)
                    val nombre = itemObject.getString("nombre")
                    val imageUrl = itemObject.getString("imagen")
                    val itemData = ItemData(nombre, imageUrl)
                    items.add(itemData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            items.shuffle()
            return items
        }

        private fun jsonFileExists(filename: String, context: Context): Boolean {
            val file = context.getFileStreamPath(filename)
            return file.exists()
        }
        fun getVersionCode(context: Context): Int {
            return try {
                val packageInfo: PackageInfo =
                    context.packageManager.getPackageInfo(context.packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                -1
            }
        }

        fun existeValorAlmacen(key: String, context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
            return sharedPreferences.contains(key)
        }

        fun agregarPreguntas(context: Context,indiceUnidad: String, enlace: String) {

            val intent = Intent(context, MainActivityBD::class.java)

            intent.putExtra("unidad", indiceUnidad)
            intent.putExtra("enlace", enlace)
            context.startActivity(intent)
        }
        fun numeroToImagen(imageN: Int): Int {
            return when (imageN) {
                1 -> R.drawable.una
                2 -> R.drawable.dos
                3 -> R.drawable.tres
                4 -> R.drawable.cuatro
                5 -> R.drawable.cinco
                6 -> R.drawable.seis
                7 -> R.drawable.siete
                8 -> R.drawable.ocho
                9 -> R.drawable.nueve
                10 -> R.drawable.diez
                11 -> R.drawable.once
                12 -> R.drawable.doce
                13 -> R.drawable.vv13
                14 -> R.drawable.vv14
                15 -> R.drawable.vv15
                16 -> R.drawable.vv16
                17 -> R.drawable.vv17
                18 -> R.drawable.vv18
                19 -> R.drawable.vv19
                20 -> R.drawable.vv20
                21 -> R.drawable.vv21
                22 -> R.drawable.vv22
                23 -> R.drawable.vv23
                24 -> R.drawable.vv24
                25 -> R.drawable.vv25
                // Agrega más casos según sea necesario
                else -> R.drawable.una
                // Puedes establecer un valor predeterminado si el valor de imageN no coincide con ninguno de los casos
            }
        }
        fun aplicarEfectoBote(view: View) {
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f)
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f)
            scaleX.duration = 300
            scaleY.duration = 300

            val bounceAnimatorSet = AnimatorSet()
            bounceAnimatorSet.playTogether(scaleX, scaleY)
            bounceAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
            bounceAnimatorSet.start()
        }

    }
}