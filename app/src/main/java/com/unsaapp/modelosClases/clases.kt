package com.unsaapp.modelosClases

class listaPreguntasClass(
    val valorV: Int,
    val valor: Int,
    val id: String,
    val pregunta: String,
    val respI1: String,
    val respI2: String,
    val respI3: String,
    val respC: String,
    var idp: String,
    var expandable: Boolean = false
) {
}

class listaItemsClass(
    val valorVerificado: Int,
    val idCreador: String,
    val numeroTel: Long,
    val titulo: String,
    val imagenLink1: String,
    val imagenLink2: String,
    val imagenLink3: String,
    val precio: Int,
    val descripcion: String,
    var idItem: String,
    var expandable: Boolean = false
) {
}

class ListaCarreras(
    val numero: Int,
    val estadoI: Int,
    val idCarrera: String,
    val carreras: String,
    val contador: Int,
    val facu: String,
    val grupoWha: String,
    var idp: String,
    val aniosC: Int,
    val materiasA: String,
    val enlaceItems: String,
    var expandable: Boolean = false
) {
}

class ListaUsuarios(
    val idUsuari: String,
    val admin1: Boolean,
    val admin2: Boolean,
    val admin3: Boolean,
    val boton: Int,
    var expandable: Boolean = false
) {
}

class ItemData(val name: String, val imageUrl: String)
