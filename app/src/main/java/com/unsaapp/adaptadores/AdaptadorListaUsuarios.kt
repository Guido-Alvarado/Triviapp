package com.unsaapp.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.unsaapp.MainActivityLista
import com.unsaapp.R
import com.unsaapp.modelosClases.ListaUsuarios
import java.util.Locale

class AdaptadorListaUsuarios(private val context: Context, val versionlist: ArrayList<ListaUsuarios>, private val mainActivityLista: MainActivityLista) :
    RecyclerView.Adapter<AdaptadorListaUsuarios.VersionVH>(),Filterable {

    private var filteredList: MutableList<ListaUsuarios> = versionlist.toMutableList()
    private var isInitialLoad = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionVH {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent,false)
        return VersionVH(view)
    }
    override fun getItemCount(): Int {
        return filteredList.size   //demuestra el tamaño de la lista que va a mostrar

    }

    class VersionVH (itemView: View):  RecyclerView.ViewHolder(itemView) {

        var boton: Button=itemView.findViewById(R.id.btn1)

        var idUsuario : TextView = itemView.findViewById(R.id.usuario11)
        var admin1 : CheckBox=itemView.findViewById(R.id.checkBox1)
        var admin2 : CheckBox=itemView.findViewById(R.id.checkBox2)
        var admin3 : CheckBox=itemView.findViewById(R.id.checkBox3)

        var linearLayout : LinearLayout = itemView.findViewById(R.id.linearlayout)
        var expandable : LinearLayout= itemView.findViewById(R.id.expadible2)
    }

    override fun onBindViewHolder(holder: VersionVH, position: Int) {
        val elementos: ListaUsuarios = filteredList[position]

        holder.idUsuario.text = elementos.idUsuari

        holder.admin1.isChecked= elementos.admin1
        holder.admin2.isChecked= elementos.admin2
        holder.admin3.isChecked= elementos.admin3

        holder.admin1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                mainActivityLista.designarAdmin(elementos.idUsuari,1,1)
            } else {
                mainActivityLista.designarAdmin(elementos.idUsuari,1,0)
            }
        }
        holder.admin2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mainActivityLista.designarAdmin(elementos.idUsuari,2,1)
            } else {
                mainActivityLista.designarAdmin(elementos.idUsuari,2,0)
            }
        }
        holder.admin3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mainActivityLista.designarAdmin(elementos.idUsuari,3,1)
            } else {
                mainActivityLista.designarAdmin(elementos.idUsuari,3,0)
            }
        }

        when (elementos.boton) {
            0 -> {holder.boton.text ="ADVERTIR"
                holder.boton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
            }
            1 -> {holder.boton.text="BLOQUEAR"
                holder.boton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            }
            2 -> {holder.boton.text="DESBLOQUEAR"
                holder.boton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.naranja))
            }
        }

        holder.boton.setOnClickListener {
            when (elementos.boton) {
                0 -> {holder.boton.text ="BLOQUEAR"
                    holder.boton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
                }
                1 -> {holder.boton.text="DESBLOQUEAR"
                    holder.boton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.naranja))
                }
                2 -> {holder.boton.text="ADVERTIR"
                    holder.boton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
                }
            }
            mainActivityLista.bloquear(elementos.idUsuari,elementos.boton)
        }

        val Isexpandable : Boolean = versionlist[position].expandable

        holder.expandable.visibility= if (Isexpandable) View.VISIBLE else View.GONE

        holder.linearLayout.setOnClickListener{
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
                    val tempList = mutableListOf<ListaUsuarios>()
                    for (item in versionlist) {
                        if (item.idUsuari.toLowerCase(Locale.getDefault()).contains(query)) {
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