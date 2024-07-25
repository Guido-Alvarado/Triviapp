package com.unsaapp.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.unsaapp.MainActivityBD
import com.unsaapp.R
import com.unsaapp.modelosClases.listaPreguntasClass
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.Locale

class AdaptadorBD(
    private val context: Context,
    val versionlist: List<listaPreguntasClass>,
    private val mainActivityBD: MainActivityBD
) :
    RecyclerView.Adapter<AdaptadorBD.VersionVH>(), Filterable {

    private var filteredList: MutableList<listaPreguntasClass> = versionlist.toMutableList()
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
        var eliminar: Button = itemView.findViewById(R.id.bteliminar)
        var editar: Button = itemView.findViewById(R.id.bteditar)
        var verificar: Button = itemView.findViewById(R.id.verificar)
        var verificado: Button = itemView.findViewById(R.id.Pverificado)
        var creador: TextView = itemView.findViewById(R.id.creador)
        var pregunta: TextView = itemView.findViewById(R.id.pregunta)
        var respI1: TextView = itemView.findViewById(R.id.ri1)
        var respI2: TextView = itemView.findViewById(R.id.ri2)
        var respI3: TextView = itemView.findViewById(R.id.ri3)
        var respC: TextView = itemView.findViewById(R.id.rc)
        var idp: TextView = itemView.findViewById(R.id.idp)

        var imagenV: ImageView = itemView.findViewById(R.id.imagenV)

        var linearLayout: LinearLayout = itemView.findViewById(R.id.llFondo)
        var OpcionesAdmin: LinearLayout = itemView.findViewById(R.id.adminis)
        var btnVerificar: LinearLayout = itemView.findViewById(R.id.btnVerificar)
        var expandable: RelativeLayout = itemView.findViewById(R.id.expadable_layout)

        var layoutApuntes: LinearLayout = itemView.findViewById(R.id.llApuntes)
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onBindViewHolder(holder: VersionVH, position: Int) {
        val version: listaPreguntasClass = filteredList[position]
        val almacen = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val idCarrera = almacen.getString("idCarrera", "")
        val deposito = context.getSharedPreferences(idCarrera, Context.MODE_PRIVATE)

        val editor = almacen.edit()
        holder.layoutApuntes.visibility = View.GONE
        //obtenemos valores guardado con anterioridad para verificar si aparecen las opciones de administrador
        val anio = almacen.getInt("anoAdmin", 0)
        val admin1 = deposito.getInt("admin1", 0)
        val admin2 = deposito.getInt("admin2", 0)
        val admin3 = deposito.getInt("admin3", 0)
        val admin4 = deposito.getInt("admin4", 0)
        val admin5 = deposito.getInt("admin5", 0)
        val admin6 = deposito.getInt("admin6", 0)
        val idUsuario = almacen.getString("idusuario", "")

        // verificamos si queremos que se muestre las opciones de administrador
        if (idUsuario == version.id) {
            holder.linearLayout.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FF37CB95"))
            if (version.valorV == 2) {
                holder.OpcionesAdmin.visibility = View.VISIBLE
            }
            if (anio == 1 && admin1 == 1 || anio == 2 && admin2 == 1 || anio == 3 && admin3 == 1) {
                holder.btnVerificar.visibility = View.VISIBLE
            }
        } else if (anio == 1 && admin1 == 1 || anio == 2 && admin2 == 1 || anio == 3 && admin3 == 1 || anio == 4 && admin4 == 1 || anio == 5 && admin5 == 1 || anio == 6 && admin6 == 1) {
            holder.OpcionesAdmin.visibility = View.VISIBLE
            holder.btnVerificar.visibility = View.VISIBLE
        } else {
            holder.linearLayout.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            holder.OpcionesAdmin.visibility = View.GONE
            holder.btnVerificar.visibility = View.GONE
        }

        val valor = almacen.getInt(version.idp, 0)

        if (version.valorV == 1 || valor == 1) {
            holder.imagenV.visibility = View.VISIBLE
            holder.verificar.visibility = View.GONE
            holder.verificado.visibility = View.VISIBLE
        } else {
            holder.imagenV.visibility = View.GONE
            holder.verificar.visibility = View.VISIBLE
            holder.verificado.visibility = View.GONE
        }

        //verificamos si los textos tienen informacion, y si no la tienen lo escondemos
        if (version.respI1.isNotEmpty()) {
            holder.respI1.visibility = View.VISIBLE
            holder.respI1.text = version.respI1
        } else {
            holder.respI1.visibility = View.GONE
        }
        if (version.respI2.isNotEmpty()) {
            holder.respI2.visibility = View.VISIBLE
            holder.respI2.text = version.respI2
        } else {
            holder.respI2.visibility = View.GONE
        }
        if (version.respI3.isNotEmpty()) {
            holder.respI3.visibility = View.VISIBLE
            holder.respI3.text = version.respI3
        } else {
            holder.respI3.visibility = View.GONE
        }
        if (version.respC.isNotEmpty()) {
            holder.respC.visibility = View.VISIBLE
            holder.respC.text = version.respC
        } else {
            holder.respC.visibility = View.GONE
        }
        if (version.id.isNotEmpty()) {
            holder.creador.visibility = View.VISIBLE
            holder.creador.text = version.id
        } else {
            holder.creador.visibility = View.GONE
        }

        holder.pregunta.text = version.pregunta
        holder.idp.text = version.idp

        val Isexpandable: Boolean = versionlist[position].expandable

        holder.expandable.visibility = if (Isexpandable) View.VISIBLE else View.GONE
        holder.verificar.setOnClickListener {
            editor.putInt(version.idp, 1)
            editor.apply()
            holder.imagenV.visibility = View.VISIBLE
            holder.verificado.visibility = View.VISIBLE
            holder.verificar.visibility = View.GONE
            notifyItemChanged(position)
            mainActivityBD.verificarPregunta(version.idp)
        }
        holder.eliminar.setOnClickListener {
            //Toast.makeText(context, version.id, Toast.LENGTH_SHORT).show()
            val position = holder.adapterPosition // Obtiene la posición del elemento a eliminar
            if (position != RecyclerView.NO_POSITION) { // Verifica que la posición sea válida
                mainActivityBD.eliminarPregunta(version.idp, version.pregunta) { eliminado ->
                    if (eliminado) {
                        // Elimina el elemento de la lista solo si se eliminó correctamente
                        filteredList.removeAt(position)
                        // Notifica al adaptador que se ha eliminado un elemento en esa posición
                        notifyItemRemoved(position)
                        notifyDataSetChanged()
                    }
                }
            }
        }
        holder.editar.setOnClickListener {
            if (version.respI2.isNotEmpty()) {
                mainActivityBD.Dilog_editarpregunta(
                    mainActivityBD,
                    version.id,
                    version.pregunta,
                    version.respI1,
                    version.respI2,
                    version.respI3,
                    version.respC,
                    version.idp
                )
            } else {
                mainActivityBD.ventanaVoF(mainActivityBD, version.pregunta, version.idp)
            }

        }
        holder.linearLayout.setOnClickListener {

            val versions = versionlist[position]
            versions.expandable = !versions.expandable
            notifyItemChanged(position)
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
                    val tempList = mutableListOf<listaPreguntasClass>()
                    for (item in versionlist) {
                        if (item.pregunta.toLowerCase(Locale.getDefault()).contains(query)) {
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
