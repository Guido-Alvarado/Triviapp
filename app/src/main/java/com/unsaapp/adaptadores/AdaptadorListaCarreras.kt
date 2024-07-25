package com.unsaapp.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.RecyclerView
import com.unsaapp.MainActivityLista
import com.unsaapp.R
import com.unsaapp.modelosClases.ListaCarreras
import com.unsaapp.utilidades.Utilidades
import java.util.Locale

class AdaptadorListaCarreras(
    private val context: Context,
    val listaAdaptador: List<ListaCarreras>,
    private val mainActivityLista: MainActivityLista
) :
    RecyclerView.Adapter<AdaptadorListaCarreras.VersionVH>(), Filterable {

    private var listaFiltrable: MutableList<ListaCarreras> = listaAdaptador.toMutableList()
    private var isInitialLoad = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionVH {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_carreras, parent, false)
        return VersionVH(view)
    }

    override fun getItemCount(): Int {
        return listaFiltrable.size   //demuestra el tamaño de la lista que va a mostrar
    }

    class VersionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnElegirC: Button = itemView.findViewById(R.id.prepararC)
        var btnMaterias: Button = itemView.findViewById(R.id.btnMaterias)
        var btnAñadirP: Button = itemView.findViewById(R.id.añadirP)
        var pbarC: ProgressBar = itemView.findViewById(R.id.pbC)

        var carrera: TextView = itemView.findViewById(R.id.carrera1)
        var anosC: TextView = itemView.findViewById(R.id.añosC)
        var imagenListo: ImageView = itemView.findViewById(R.id.imagenListo)

        var linearLayout: LinearLayout = itemView.findViewById(R.id.linearlayout)
        var expandable: RelativeLayout = itemView.findViewById(R.id.expadible)
    }

    override fun onBindViewHolder(holder: VersionVH, position: Int) {
        val version: ListaCarreras = listaFiltrable[position]

        //OBTENEMOS DATOS QUE GUARDAMOS EN MAINACTIVITY3,(DIALOGBCE1()) USUARIO()
        val almacen = context.getSharedPreferences("Almacen", Context.MODE_PRIVATE)
        val editorA = almacen.edit()

        holder.imagenListo.setImageResource(version.estadoI)

        if (version.contador == 1) {
            holder.btnElegirC.visibility = View.VISIBLE

        } else {
            holder.btnElegirC.visibility = View.GONE
        }
        holder.carrera.text = version.carreras
        holder.anosC.text = version.aniosC.toString()

        val Isexpandable: Boolean = listaAdaptador[position].expandable

        holder.expandable.visibility = if (Isexpandable) View.VISIBLE else View.GONE
        holder.btnMaterias.setOnClickListener {
            mainActivityLista.msjImportante(context, version.materiasA)
        }
        holder.btnAñadirP.setOnClickListener {
            Utilidades.mostrarMsjCorto(context,"Cargando")
            mainActivityLista.agregarM()
        }

        holder.btnElegirC.setOnClickListener {
            val materia = version.idCarrera + "mate"
            editorA.putInt("aniosC", version.aniosC)
            editorA.putString("idCarrera", version.idCarrera)
            editorA.putString("Carrera", version.carreras)
            editorA.putString("Materias", materia)
            editorA.putString("Unidades", version.idCarrera)
            editorA.putString("grupoWh", version.grupoWha)
            editorA.putString("enlaceItems", version.enlaceItems)
            editorA.apply()
            holder.btnElegirC.visibility = View.GONE
            holder.pbarC.visibility = View.VISIBLE

            Utilidades.mostrarMsjCorto(context,"Cargando Materias de la carrera de ${version.carreras}")
            mainActivityLista.guardarMaterias(version.idCarrera, version.facu, version.carreras)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                // Después de 5 segundos, invierte la visibilidad
                holder.btnElegirC.visibility = View.VISIBLE
                holder.pbarC.visibility = View.GONE
            }, 5000) // 5000 milisegundos = 5 segundos
        }
        holder.linearLayout.setOnClickListener {
            val versions = listaAdaptador[position]
            versions.expandable = !versions.expandable

            notifyItemChanged(position)
            holder.carrera.isSelected = false
            holder.carrera.isSelected = true

        }
    }



    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().toLowerCase(Locale.getDefault()).trim()
                if (query.isEmpty()) {
                    listaFiltrable.clear()
                    listaFiltrable.addAll(listaAdaptador)
                } else {
                    val tempList = mutableListOf<ListaCarreras>()
                    for (item in listaAdaptador) {
                        if (item.carreras.toLowerCase(Locale.getDefault()).contains(query)) {
                            tempList.add(item)
                        }
                    }
                    listaFiltrable = tempList
                }
                val results = FilterResults()
                results.values = listaFiltrable
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (isInitialLoad) {
                    listaFiltrable.clear()
                    listaFiltrable.addAll(listaAdaptador)
                    isInitialLoad = false
                }
                notifyDataSetChanged()
            }
        }
    }


}