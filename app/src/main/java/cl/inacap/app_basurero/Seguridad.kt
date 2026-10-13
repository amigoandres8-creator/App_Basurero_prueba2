package cl.inacap.app_basurero

import java.security.MessageDigest

object Seguridad {

    fun cifrar(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun limpiarRut(rut: String): String {
        return rut.trim().replace(".", "").uppercase()
    }

    fun rutValido(rut: String): Boolean {
        return Regex("^[0-9]{7,8}-[0-9K]$").matches(rut)
    }

}
