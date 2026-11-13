package com.educonnectapp.data.api

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// =====================================================================
// SESIÓN GUARDADA EN EL CELULAR (paso 30)
// Si Android cierra la app en segundo plano (pasa mucho en Xiaomi), al volver
// —por ejemplo, tocando una notificación— la sesión se recupera sin pedir login.
// La sesión vence tras MINUTOS_INACTIVIDAD minutos sin usar la app.
// - El token y los datos del usuario se guardan CIFRADOS con una clave del
//   Android Keystore (AES-GCM): ninguna otra app ni una copia de seguridad puede leerlos.
// - "Inactividad" = tiempo desde que la app pasó a segundo plano (o se bloqueó el celular).
// =====================================================================

const val MINUTOS_INACTIVIDAD = 10

// Datos mínimos para volver a armar la sesión
@Serializable
data class UsuarioGuardado(
    val id: Long,
    val nombreCompleto: String,
    val email: String,
    val rol: String,
    val dni: String = "",
    val telefono: String = "",
    val fotoId: Long? = null
)

@Serializable
private data class SesionCifrable(val token: String, val usuario: UsuarioGuardado)

object SesionGuardada {
    private const val PREFERENCIAS = "educonnect_sesion"
    private const val CLAVE_DATOS = "datos"
    private const val CLAVE_ULTIMA_ACTIVIDAD = "ultima_actividad"
    private const val ALIAS_KEYSTORE = "educonnect_sesion"
    private const val LIMITE_MS = MINUTOS_INACTIVIDAD * 60_000L

    private val json = Json { ignoreUnknownKeys = true }

    // MainActivity la pone en true al volver después de 10 min; EduConnectApp cierra la sesión
    val expirada = mutableStateOf(false)

    private fun preferencias(context: Context) =
        context.applicationContext.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)

    // Guarda (o actualiza) la sesión actual: token en memoria + usuario
    fun guardar(context: Context, usuario: UsuarioGuardado) {
        val token = SesionApi.token ?: return
        try {
            val datos = cifrar(json.encodeToString(SesionCifrable.serializer(), SesionCifrable(token, usuario)))
            preferencias(context).edit()
                .putString(CLAVE_DATOS, datos)
                .putLong(CLAVE_ULTIMA_ACTIVIDAD, System.currentTimeMillis())
                .apply()
        } catch (e: Exception) {
            // Si el Keystore falla, la app sigue funcionando: solo no se recordará la sesión
        }
    }

    // Se llama al pasar a segundo plano o bloquear el celular
    fun marcarActividad(context: Context) {
        if (SesionApi.token == null) return
        preferencias(context).edit().putLong(CLAVE_ULTIMA_ACTIVIDAD, System.currentTimeMillis()).apply()
    }

    // ¿Pasaron más de 10 min desde la última actividad guardada?
    fun vencida(context: Context): Boolean {
        val ultima = preferencias(context).getLong(CLAVE_ULTIMA_ACTIVIDAD, 0L)
        return ultima == 0L || System.currentTimeMillis() - ultima > LIMITE_MS
    }

    // Al abrir la app: recupera la sesión si no venció (y deja el token listo para las peticiones)
    fun restaurar(context: Context): UsuarioGuardado? {
        val datos = preferencias(context).getString(CLAVE_DATOS, null) ?: return null
        if (vencida(context)) {
            borrar(context)
            return null
        }
        return try {
            val sesion = json.decodeFromString(SesionCifrable.serializer(), descifrar(datos))
            SesionApi.token = sesion.token
            sesion.usuario
        } catch (e: Exception) {
            borrar(context)   // datos dañados o clave nueva: se pide login
            null
        }
    }

    // Cerrar sesión o sesión vencida
    fun borrar(context: Context) {
        preferencias(context).edit().remove(CLAVE_DATOS).remove(CLAVE_ULTIMA_ACTIVIDAD).apply()
    }

    // ---------- Cifrado AES-GCM con clave del Android Keystore ----------

    private fun clave(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(ALIAS_KEYSTORE, null) as? SecretKey)?.let { return it }
        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generador.init(
            KeyGenParameterSpec.Builder(
                ALIAS_KEYSTORE, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generador.generateKey()
    }

    // Resultado: "iv:cifrado" en Base64
    private fun cifrar(texto: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, clave())
        val cifrado = cipher.doFinal(texto.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv, Base64.NO_WRAP) + ":" + Base64.encodeToString(cifrado, Base64.NO_WRAP)
    }

    private fun descifrar(datos: String): String {
        val (iv, cifrado) = datos.split(":", limit = 2).map { Base64.decode(it, Base64.NO_WRAP) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, clave(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(cifrado), Charsets.UTF_8)
    }
}
