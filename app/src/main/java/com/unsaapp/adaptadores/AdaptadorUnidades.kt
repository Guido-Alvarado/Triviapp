package com.unsaapp.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unsaapp.MainActivityCatedras
import com.unsaapp.modelosClases.ModeloUnidades
import com.unsaapp.R
import com.unsaapp.utilidades.Utilidades
import com.unsaapp.utilidades.Ventanas
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdaptadorUnidades(val ListaUnidades: ArrayList<ModeloUnidades>) :
    RecyclerView.Adapter<AdaptadorUnidades.UnidadesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnidadesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_unidades, parent, false)
        return UnidadesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ListaUnidades.size
    }

    class UnidadesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // aca referenciamos los objetos creados del item model
        val Imagen: ImageView = itemView.findViewById(R.id.Portada3)
        val titulo: TextView = itemView.findViewById(R.id.Titulo3)
        val puntaje: TextView = itemView.findViewById(R.id.titulo4)
        val layoutUnidades: LinearLayout = itemView.findViewById(R.id.fondo)
        val progress: ProgressBar = itemView.findViewById(R.id.progressBar)
        val pbUnid: ProgressBar = itemView.findViewById(R.id.pbUnid)
        val tema: TextView = itemView.findViewById(R.id.tema)

        val mejorT: TextView = itemView.findViewById(R.id.mt)
        val ultimoT: TextView = itemView.findViewById(R.id.ut)
        val cantidadP: TextView = itemView.findViewById(R.id.cp)

        var agregarP: Button = itemView.findViewById(R.id.agregarP)
        var actualizarP: Button = itemView.findViewById(R.id.actualizarP)
        var intentar: Button = itemView.findViewById(R.id.intentar)


        var expandable: RelativeLayout = itemView.findViewById(R.id.expadable_layout)

    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: UnidadesViewHolder, position: Int) {
        val unidad = ListaUnidades[position]
        val context = holder.itemView.context

// aca cambiamos lo que queramos del objeto referenciado para cada item
        holder.Imagen.setImageResource(unidad.Image)  // cambiamos la imagen
        holder.titulo.text = unidad.name
        // holder.titulo.textSize = pelis.sizes.toFloat() con esto cambiarias el tamaño de la letra
        holder.puntaje.text = unidad.puntaje
        holder.layoutUnidades.setBackgroundResource(unidad.layout)
        holder.progress.progress = unidad.progress
        holder.tema.text = unidad.tema
        //puntaje
        val segundos = unidad.ultimoT
        val horas = segundos / 3600
        val minutos = (segundos % 3600) / 60
        val segundosRestantes = segundos % 60

        val segundo2 = unidad.mejorT
        val horas2 = segundo2 / 3600
        val minutos2 = (segundo2 % 3600) / 60
        val segundosRestantes2 = segundo2 % 60

        // Mostrar tiempo en formato HH:MM:SS
        holder.ultimoT.text = String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes)
        holder.mejorT.text = String.format("%02d:%02d:%02d", horas2, minutos2, segundosRestantes2)
        holder.cantidadP.text = unidad.cantidadP.toString()

        val Isexpandable: Boolean = ListaUnidades[position].expandable
        holder.expandable.visibility = if (Isexpandable) View.VISIBLE else View.GONE

        if (!unidad.mostrarPorcentaje) {
            holder.expandable.visibility = View.GONE
        }

        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)
        val imagen = holder.itemView.findViewById<ImageView>(R.id.Portada3)
        if (unidad.mostrarPorcentaje) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
        if (unidad.mostrarImagen) {
            imagen.visibility = View.VISIBLE
        } else {
            imagen.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(it.context, R.anim.item_scale_down)
            if (context is MainActivityCatedras) {
                Utilidades.agregarPreguntas(context, unidad.IndiceUnidad, unidad.enlace)
            }
            it.startAnimation(animation)
        }
        holder.layoutUnidades.setOnClickListener {
            val versions = ListaUnidades[position]
            versions.expandable = !versions.expandable
            notifyItemChanged(position)
        }
        holder.actualizarP.setOnClickListener {
            holder.pbUnid.visibility = View.VISIBLE
            holder.actualizarP.visibility = View.GONE
            if (context is MainActivityCatedras) {
                Utilidades.mostrarMsjCorto(context, "Actualizando")
                context.actualizarPreguntas(unidad.IndiceUnidad, unidad.enlace)
            }
            GlobalScope.launch(Dispatchers.Main) {
                delay(5000) // 2000 milisegundos = 2 segundos
                holder.pbUnid.visibility = View.GONE
                holder.actualizarP.visibility = View.VISIBLE
                notifyItemChanged(position)
            }
        }
        holder.agregarP.setOnClickListener {

            if (context is MainActivityCatedras) {

                val Almacen = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
                val contador = Almacen.getInt("post1", 3)
                if (contador > 0) {
                    Ventanas.dialogoConfirmar(context, 1, unidad.IndiceUnidad, unidad.enlace)
                } else {
                    Utilidades.agregarPreguntas(context, unidad.IndiceUnidad, unidad.enlace)
                }
            }
        }
        holder.intentar.setOnClickListener {
            if (context is MainActivityCatedras) {
                context.intentarUnidad(unidad.IndiceUnidad)
            }
        }
    }


}