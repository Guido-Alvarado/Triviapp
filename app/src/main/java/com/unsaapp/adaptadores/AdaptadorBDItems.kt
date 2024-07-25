package com.unsaapp.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.unsaapp.MainActivityBD
import com.unsaapp.R
import com.unsaapp.modelosClases.listaItemsClass
import com.unsaapp.utilidades.Utilidades
import com.unsaapp.utilidades.Ventanas
import java.util.Locale

class AdaptadorBDItems(
    private val context: Context,
    val versionlist: List<listaItemsClass>,
    private val mainActivityBD: MainActivityBD
) :
    RecyclerView.Adapter<AdaptadorBDItems.VersionVH>(), Filterable {

    private var filteredList: MutableList<listaItemsClass> = versionlist.toMutableList()
    private var isInitialLoad = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionVH {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_preguntas, parent, false)
        return VersionVH(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size   //demuestra el tamaño de la lista que va a mostrar

    }

    class VersionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // ITEM PREGUNTAS
        var creador: TextView = itemView.findViewById(R.id.creador)

        var linearLayoutItems: LinearLayout = itemView.findViewById(R.id.llApuntes)
        var llLayout: LinearLayout = itemView.findViewById(R.id.llFondo)
        var llimgs: LinearLayout = itemView.findViewById(R.id.llimgs)
        var OpcionesAdmin: LinearLayout = itemView.findViewById(R.id.llAdminApuntes)

        //ITEM APUNTES
        var tituloA: TextView = itemView.findViewById(R.id.tituloApuntes)
        var descripA: TextView = itemView.findViewById(R.id.infApuntes)
        var imagen: ImageView = itemView.findViewById(R.id.imgApuntes)
        var imagen1: ImageView = itemView.findViewById(R.id.imgApuntes1)
        var imagen2: ImageView = itemView.findViewById(R.id.imgApuntes2)
        var imagen3: ImageView = itemView.findViewById(R.id.imgApuntes3)
        var imgVerif: ImageView = itemView.findViewById(R.id.imgVerifApuntes)
        var precioA: TextView = itemView.findViewById(R.id.precioApuntes)
        var pbEliminar: ProgressBar = itemView.findViewById(R.id.pbelimItem)

        var btnObtenerItem: Button = itemView.findViewById(R.id.btncomprar)
        var btnEliminarA: Button = itemView.findViewById(R.id.btneliminarA)
        var btnVerificar: Button = itemView.findViewById(R.id.btnverificarItem)
        var btnEditarA: Button = itemView.findViewById(R.id.btnEditarI)

        var layoutExpandibleApuntes: RelativeLayout =
            itemView.findViewById(R.id.layoutApuntesExpandible)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VersionVH, position: Int) {
        val listaItems: listaItemsClass = filteredList[position]
        val almacen = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val idUsuario = almacen.getString("idusuario", "")

        // verificamos si queremos que se muestre las opciones de administrador
        if (idUsuario == listaItems.idCreador) {
            holder.linearLayoutItems.setBackgroundColor(Color.parseColor("#FFDDE1E0"))
            holder.OpcionesAdmin.visibility = View.VISIBLE
        } else {
            holder.linearLayoutItems.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            holder.OpcionesAdmin.visibility = View.GONE
        }

        if (listaItems.valorVerificado == 1) {
            Picasso.get().load(listaItems.imagenLink1).into(holder.imagen)
            Picasso.get().load(listaItems.imagenLink1).into(holder.imagen1)
            Picasso.get().load(listaItems.imagenLink2).into(holder.imagen2)
            Picasso.get().load(listaItems.imagenLink3).into(holder.imagen3)
            holder.imgVerif.visibility = View.VISIBLE
        } else {
            holder.imagen.visibility = View.GONE
            holder.llimgs.visibility = View.GONE
            holder.imgVerif.visibility = View.GONE
        }
        holder.llLayout.visibility = View.GONE
        holder.tituloA.text = listaItems.titulo
        holder.descripA.text = listaItems.descripcion
        holder.precioA.text = "Precio " + numeroConPunto(listaItems.precio) + " $"

        //Mostrar logo de verificado
        if (listaItems.valorVerificado == 1) {
            holder.imgVerif.visibility = View.VISIBLE
            holder.btnVerificar.visibility = View.GONE
        } else {
            holder.imgVerif.visibility = View.GONE
            holder.btnVerificar.visibility = View.VISIBLE
        }
        //mostrar imagenes
        holder.imagen1.setOnClickListener {
            Ventanas.ventanaImagen(context, listaItems.imagenLink1)
        }
        holder.imagen2.setOnClickListener {
            Ventanas.ventanaImagen(context, listaItems.imagenLink2)
        }
        holder.imagen3.setOnClickListener {
            Ventanas.ventanaImagen(context, listaItems.imagenLink3)
        }
        val Isexpandable: Boolean = versionlist[position].expandable

        holder.layoutExpandibleApuntes.visibility = if (Isexpandable) {
            View.VISIBLE
        } else View.GONE

        if (holder.layoutExpandibleApuntes.visibility == View.VISIBLE) {
            holder.precioA.visibility = View.GONE
            holder.btnObtenerItem.text = "Obtener " + listaItems.precio + " $"
            holder.imagen.visibility = View.GONE
            if (listaItems.valorVerificado == 1) {
                holder.imagen.visibility = View.GONE
                holder.llimgs.visibility = View.VISIBLE
            }
        } else {
            if (listaItems.valorVerificado == 1) {
                holder.imagen.visibility = View.VISIBLE
            }
            holder.precioA.visibility = View.VISIBLE
        }
        holder.btnEliminarA.setOnClickListener {
            val position = holder.adapterPosition // Obtiene la posición del elemento a eliminar
            if (position != RecyclerView.NO_POSITION) { // Verifica que la posición sea válida
                // Llama a eliminaritem con un callback
                holder.pbEliminar.visibility = View.VISIBLE
                holder.btnEliminarA.visibility = View.GONE
                mainActivityBD.eliminaritem(listaItems.idItem, listaItems.titulo) { eliminado ->
                    if (eliminado) {
                        holder.btnEliminarA.visibility = View.VISIBLE
                        holder.pbEliminar.visibility = View.GONE
                        // Elimina el elemento de la lista solo si se eliminó correctamente
                        filteredList.removeAt(position)
                        // Notifica al adaptador que se ha eliminado un elemento en esa posición
                        notifyItemRemoved(position)
                        notifyDataSetChanged()
                    } else {
                        holder.btnEliminarA.visibility = View.VISIBLE
                        holder.pbEliminar.visibility = View.GONE
                    }
                }
            }
        }
        holder.btnEditarA.setOnClickListener {
            mainActivityBD.ventanaAgregarItem(
                context,
                1,
                listaItems.titulo,
                listaItems.descripcion,
                listaItems.precio,
                listaItems.numeroTel,
                listaItems.idItem,
                listaItems.idCreador
            )
        }
        holder.btnVerificar.setOnClickListener {
            Utilidades.enviarMensajeWhatsApp(
                context,
                4,
                listaItems.numeroTel,
                listaItems.titulo,
                listaItems.idItem
            )
        }
        holder.btnObtenerItem.setOnClickListener {
            Utilidades.enviarMensajeWhatsApp(
                context,
                5,
                listaItems.numeroTel,
                listaItems.titulo,
                listaItems.idItem
            )
        }

        holder.linearLayoutItems.setOnClickListener {
            val versions = versionlist[position]
            versions.expandable = !versions.expandable
            notifyItemChanged(position)
        }

    }

    fun numeroConPunto(number: Int): String {
        val numberString = number.toString()
        return if (numberString.length > 3) {
            val sb = StringBuilder(numberString)
            val dotPosition = numberString.length - 3
            sb.insert(dotPosition, '.')
            sb.toString()
        } else {
            numberString
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().toLowerCase(Locale.getDefault()).trim()
                if (query.isEmpty()) {
                    filteredList.clear()
                    filteredList.addAll(versionlist)
                } else {
                    val tempList = mutableListOf<listaItemsClass>()
                    for (item in versionlist) {
                        if (item.titulo.toLowerCase(Locale.getDefault()).contains(query)) {
                            tempList.add(item)
                        }
                    }
                    filteredList = tempList
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (isInitialLoad) {
                    filteredList.clear()
                    filteredList.addAll(versionlist)
                    isInitialLoad = false
                }
                notifyDataSetChanged()
            }
        }
    }
}
