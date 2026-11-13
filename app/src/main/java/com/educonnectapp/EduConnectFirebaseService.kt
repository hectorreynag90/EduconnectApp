package com.educonnectapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.educonnectapp.data.api.guardarFcmToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Datos que viajan de la notificación a MainActivity (los lee NotificacionAbierta)
const val EXTRA_TIPO_NOTIFICACION = "educonnect.tipo"
const val EXTRA_REFERENCIA_NOTIFICACION = "educonnect.referenciaId"

// Ícono y colores de cada tipo de aviso (prototipo aprobado el 13/11/2026)
// fondo: círculo grande de la derecha y acento de la notificación · icono: color del símbolo
private data class EstiloNotificacion(@DrawableRes val icono: Int, val fondo: Int, val simbolo: Int)

private const val GRANATE = 0xFF910428.toInt()
private const val AMARILLO = 0xFFFFC107.toInt()
private const val AZUL_OSCURO = 0xFF2C3E50.toInt()
private const val TURQUESA = 0xFF16A085.toInt()
private const val GRIS = 0xFF6B7A86.toInt()
private const val BLANCO = 0xFFFFFFFF.toInt()
private const val TEXTO_OSCURO = 0xFF1F2D3A.toInt()   // símbolo sobre amarillo

// Elige el estilo según el tipo y el título que envía el backend (NotificacionListeners)
// Solo se mira el inicio del título ("Nueva tarea", "Tarea anulada"...): el nombre de la tarea no influye
private fun estiloDe(tipo: String, titulo: String): EstiloNotificacion {
    val t = titulo.substringBefore(":").lowercase()
    return when (tipo) {
        "COMUNICADO" ->
            if (t.startsWith("recordatorio")) EstiloNotificacion(R.drawable.ic_notif_recordatorio, GRANATE, BLANCO)
            else EstiloNotificacion(R.drawable.ic_notif_comunicado, GRANATE, BLANCO)
        "PUBLICACION" -> when {
            "anulada" in t -> EstiloNotificacion(R.drawable.ic_notif_anulada, GRIS, BLANCO)
            "actualizada" in t -> EstiloNotificacion(R.drawable.ic_notif_actualizada, AMARILLO, TEXTO_OSCURO)
            "evaluación" in t || "evaluacion" in t -> EstiloNotificacion(R.drawable.ic_notif_evaluacion, AZUL_OSCURO, BLANCO)
            else -> EstiloNotificacion(R.drawable.ic_notif_tarea, AMARILLO, TEXTO_OSCURO)
        }
        "CALIFICACION" ->
            if ("no entregada" in t) EstiloNotificacion(R.drawable.ic_notif_no_entregada, GRANATE, BLANCO)
            else EstiloNotificacion(R.drawable.ic_notif_entregada, TURQUESA, BLANCO)
        "ASISTENCIA" -> when {
            // "Asistencia Actualizada Falta → Presente": corrección del docente
            "actualizada" in t -> EstiloNotificacion(R.drawable.ic_notif_asistencia_editada, AZUL_OSCURO, BLANCO)
            "falta" in t -> EstiloNotificacion(R.drawable.ic_notif_falta, GRANATE, BLANCO)
            "tardanza" in t -> EstiloNotificacion(R.drawable.ic_notif_tardanza, AMARILLO, TEXTO_OSCURO)
            else -> EstiloNotificacion(R.drawable.ic_notif_presente, TURQUESA, BLANCO)
        }
        else -> EstiloNotificacion(R.drawable.ic_notif_aviso, TURQUESA, BLANCO)   // AVISO
    }
}

class EduConnectFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                guardarFcmToken(token)
            } catch (e: Exception) { }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["title"] ?: "EduConnect"
        val body  = message.data["body"]  ?: ""
        // Enviados por el backend: tipo (ASISTENCIA, COMUNICADO, PUBLICACION, CALIFICACION, AVISO) y referenciaId
        val tipo = message.data["tipo"].orEmpty()
        val referenciaId = message.data["referenciaId"].orEmpty()

        // Un id distinto por notificación: cada una conserva sus propios datos al tocarla
        val idNotificacion = System.currentTimeMillis().toInt()

        // Al tocarla abre MainActivity con el tipo y el registro de origen.
        // SINGLE_TOP + CLEAR_TOP: si la app ya está abierta, la reutiliza (onNewIntent).
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TIPO_NOTIFICACION, tipo)
            putExtra(EXTRA_REFERENCIA_NOTIFICACION, referenciaId)
        }
        // Antes: requestCode 0 + FLAG_ONE_SHOT -> todas compartían un mismo PendingIntent que se
        // anulaba tras el primer uso, y al tocar las siguientes no pasaba nada.
        val pendingIntent = PendingIntent.getActivity(
            this, idNotificacion, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Ícono pequeño: siempre el de EduConnect (fijo, sin color por tipo).
        // El tipo de aviso se distingue solo por el círculo de color de la derecha.
        val estilo = estiloDe(tipo, title)
        val notification = NotificationCompat.Builder(this, "asistencia_channel")
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setLargeIcon(crearIconoTipo(estilo))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(idNotificacion, notification)
    }

    // Círculo del color del tipo con su símbolo al centro (ícono grande de la notificación)
    private fun crearIconoTipo(estilo: EstiloNotificacion): Bitmap {
        val tamano = 128
        val salida = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(salida)
        val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = estilo.fondo }
        canvas.drawCircle(tamano / 2f, tamano / 2f, tamano / 2f, pincel)

        val simbolo = ContextCompat.getDrawable(this, estilo.icono)?.mutate() ?: return salida
        simbolo.setTint(estilo.simbolo)
        val margen = (tamano * 0.22f).toInt()   // el símbolo ocupa el 56 % del círculo
        simbolo.setBounds(margen, margen, tamano - margen, tamano - margen)
        simbolo.draw(canvas)
        return salida
    }
}
