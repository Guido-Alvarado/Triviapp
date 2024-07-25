package com.unsaapp.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.unsaapp.MainActivityCatedras
import com.unsaapp.modelosClases.ModeloMaterias
import com.unsaapp.R

class AdaptadorMaterias(
    private val ListaMaterias: ArrayList<ModeloMaterias>,
    private val horizontalRecyclerView: RecyclerView
) : RecyclerView.Adapter<AdaptadorMaterias.PelisViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, catedra: String)
    }

    private var selectedItem = RecyclerView.NO_POSITION

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }


    class PelisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // aca referenciamos los objetos creados del item model
        val titulo: TextView = itemView.findViewById(R.id.Titulo3)

        //val puntaje : TextView = itemView.findViewById(R.id.titulo4)
        val layout: LinearLayout = itemView.findViewById(R.id.fondo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PelisViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_materias, parent, false)
        return PelisViewHolder(view)
    }


    override fun getItemCount(): Int {
        return ListaMaterias.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: PelisViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val materia = ListaMaterias[position]

        // aca cambiamos lo que queramos del objeto referenciado para cada item
        holder.titulo.text = materia.titulo
        holder.layout.setBackgroundResource(if (position == selectedItem) R.drawable.fondo_materia_seleccionada else materia.layout)

        holder.itemView.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(it.context, R.anim.item_scale_down)
            it.startAnimation(animation)
            val contexto = holder.itemView.context
            val oldSelectedItem = selectedItem
            selectedItem = position
            notifyDataSetChanged()

            if (oldSelectedItem != RecyclerView.NO_POSITION) {
                horizontalRecyclerView.smoothScrollToPosition(position)
            }
            onItemClickListener?.onItemClick(position, materia.catedra.toString())
        }
    }
}