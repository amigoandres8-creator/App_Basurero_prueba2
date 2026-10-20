package cl.inacap.app_basurero

data class Accion(
    var fecha: String = "",
    var hora: String = "",
    var usuario: String = "",
    var accion: String = "",
    var distancia: Int = 0
)
