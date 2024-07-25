package com.unsaapp.modelosClases

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class ModeloUnidades(
    val Image: Int,
    val cantidadP: Int,
    val ultimoT: Int,
    val mejorT: Int,
    val IndiceUnidad: String,
    val enlace: String,
    val catedra: String,
    val name: String,
    val puntaje: String,
    val layout: Int,
    val progress: Int,
    val tema: String,
    val mostrarPorcentaje: Boolean,
    val mostrarImagen: Boolean,
    var expandable: Boolean = false
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readBoolean(),
        parcel.readBoolean(),


        ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<ModeloUnidades> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): ModeloUnidades {
            return ModeloUnidades(parcel)
        }

        override fun newArray(size: Int): Array<ModeloUnidades?> {
            return arrayOfNulls(size)
        }
    }
}
