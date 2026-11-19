package com.example.caducapp.notifications;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.caducapp.R;
import com.example.caducapp.modelo.Alimento;
import com.example.caducapp.modelo.AlimentoDAO;
import com.example.caducapp.modelo.AppDatabase;
import com.example.caducapp.vista.AlimentoAdapter;

import java.util.Calendar;
import java.util.List;

/**
 * Worker de WorkManager que revisa la base de datos Room
 * en busca de alimentos que caducan en 2 días o menos.
 */
public class CaducidadWorker extends Worker {

    public CaducidadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {

        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();

        try {

            // 1. Calcular el límite: final del día de hoy + 2 días.
            Calendar limite = Calendar.getInstance();

            limite.add(Calendar.DAY_OF_YEAR, 2);

            limite.set(Calendar.HOUR_OF_DAY, 23);
            limite.set(Calendar.MINUTE, 59);
            limite.set(Calendar.SECOND, 59);
            limite.set(Calendar.MILLISECOND, 999);

            long fechaLimiteMillis = limite.getTimeInMillis();

            // 2. Consultar los alimentos próximos a caducar.
            AlimentoDAO dao =
                    AppDatabase.getDatabase(context).alimentoDAO();

            List<Alimento> urgentes =
                    dao.obtenerProximosACaducarSync(fechaLimiteMillis);

            // 3. Si hay alimentos urgentes, preparar la notificación.
            if (urgentes != null && !urgentes.isEmpty()) {

                StringBuilder detalle = new StringBuilder();

                int limiteMostrar = Math.min(urgentes.size(), 4);

                for (int i = 0; i < limiteMostrar; i++) {

                    Alimento item = urgentes.get(i);

                    long dias = AlimentoAdapter.calcularDiasRestantes(
                            item.getFechaCaducidad()
                    );

                    String estadoDia;

                    if (dias < 0) {
                        estadoDia = "caducado";
                    } else if (dias == 0) {
                        estadoDia = "caduca hoy";
                    } else if (dias == 1) {
                        estadoDia = "caduca mañana";
                    } else {
                        estadoDia = "caduca en " + dias + " días";
                    }

                    if (i > 0) {
                        detalle.append("\n");
                    }

                    detalle.append("• ")
                            .append(item.getNombre())
                            .append(" (")
                            .append(estadoDia)
                            .append(")");
                }

                // Indicar si hay más de cuatro alimentos.
                if (urgentes.size() > limiteMostrar) {

                    detalle.append("\n• y ")
                            .append(urgentes.size() - limiteMostrar)
                            .append(" más...");
                }

                // 4. Mostrar la notificación.
                NotificationHelper helper =
                        new NotificationHelper(context);

                helper.mostrarNotificacionCaducidad(
                        context.getString(R.string.notif_titulo_urgente),
                        detalle.toString()
                );
            }

            return Result.success();

        } catch (Exception e) {

            // Si ocurre un error, WorkManager podrá reintentar el trabajo.
            return Result.retry();
        }
    }
}
