package com.unsaapp.modelosClases

import android.os.Parcel
import android.os.Parcelable

data class ModeloMaterias(
    val catedra: String?,
    val titulo: String?,
    val puntaje: String?,
    val layout: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(catedra)
        parcel.writeString(titulo)
        parcel.writeString(puntaje)
        parcel.writeInt(layout)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModeloMaterias> {
        override fun createFromParcel(parcel: Parcel): ModeloMaterias {
            return ModeloMaterias(parcel)
        }

        override fun newArray(size: Int): Array<ModeloMaterias?> {
            return arrayOfNulls(size)
        }
    }

}
