package com.unsaapp.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unsaapp.R
import com.squareup.picasso.Picasso
import com.unsaapp.modelosClases.ItemData

class AdaptadorColaboradores(private val itemList: List<ItemData>, private val context: Context) :
    RecyclerView.Adapter<AdaptadorColaboradores.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.colaboradores, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.textViewName.text = item.name

        // Cargar la imagen utilizando una biblioteca como Picasso o Glide
        // Picasso.get().load(item.imageUrl).into(holder.imageView)
        Picasso.get().load(item.imageUrl).into(holder.imageView)

        // O alternativamente, puedes usar una imagen de recurso local como marcador de posición
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.nombre)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
