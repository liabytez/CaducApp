package com.example.caducapp.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.caducapp.R;
import com.example.caducapp.vista.MainActivity;

/**
 * Clase auxiliar para crear el canal de notificaciones y mostrar alertas
 * de alimentos próximos a caducar.
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "caducapp_canal_caducidad";
    public static final int NOTIFICATION_ID = 1001;

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        crearCanalNotificacion();
    }

    /**
     * Crea el canal de notificaciones en Android 8.0 (API 26) o superior.
     */
    private void crearCanalNotificacion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence nombre = context.getString(
                    R.string.canal_nombre_caducidad
            );

            String descripcion = context.getString(
                    R.string.canal_desc_caducidad
            );

            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    nombre,
                    importancia
            );

            channel.setDescription(descripcion);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Muestra una notificación si se dispone del permiso necesario.
     * Al pulsarla, abre MainActivity.
     */
    public void mostrarNotificacionCaducidad(String titulo, String mensaje) {

        // Comprobar el permiso en Android 13 o superior.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        // Preparar la pantalla que se abrirá al pulsar la notificación.
        Intent intent = new Intent(context, MainActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        int flagsPendingIntent = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flagsPendingIntent |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                flagsPendingIntent
        );

        // Construir la notificación.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(mensaje)
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        // Mostrar la notificación.
        NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID,
                builder.build()
        );
    }
}
