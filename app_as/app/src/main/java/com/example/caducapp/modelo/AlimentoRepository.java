package com.example.caducapp.modelo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

/**
 * Repositorio de datos.
 * Actúa como intermediario entre el controlador y las fuentes de datos
 * (Room y SharedPreferences).
 */
public class AlimentoRepository {

    private final AlimentoDAO alimentoDAO;
    private final LiveData<List<Alimento>> todosLosAlimentos;
    private final SharedPreferences sharedPreferences;

    // Constantes para guardar estadísticas en SharedPreferences
    private static final String PREFS_NAME = "caducapp_prefs";
    private static final String KEY_APROVECHADOS = "contador_aprovechados";
    private static final String KEY_CADUCADOS_HISTORICO = "contador_caducados_historico";

    // Constructor: obtiene el DAO y las preferencias de la aplicación
    public AlimentoRepository(Application application) {

        AppDatabase db = AppDatabase.getDatabase(application);

        alimentoDAO = db.alimentoDAO();

        todosLosAlimentos = alimentoDAO.obtenerTodosOrdenados();

        sharedPreferences = application.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    // Devuelve la lista observable de alimentos ordenados por caducidad
    public LiveData<List<Alimento>> getTodosLosAlimentos() {
        return todosLosAlimentos;
    }

    // Inserta un alimento en segundo plano
    public void insertar(Alimento alimento) {
        AppDatabase.databaseWriteExecutor.execute(
                () -> alimentoDAO.insertar(alimento)
        );
    }

    // Actualiza un alimento en segundo plano
    public void actualizar(Alimento alimento) {
        AppDatabase.databaseWriteExecutor.execute(
                () -> alimentoDAO.actualizar(alimento)
        );
    }

    // Elimina un alimento en segundo plano
    public void eliminar(Alimento alimento) {
        AppDatabase.databaseWriteExecutor.execute(
                () -> alimentoDAO.eliminar(alimento)
        );
    }

    // Marca un alimento como consumido y aumenta el contador de aprovechados
    public void marcarComoConsumido(Alimento alimento) {
        eliminar(alimento);

        int actuales = getAlimentosAprovechados();

        sharedPreferences.edit()
                .putInt(KEY_APROVECHADOS, actuales + 1)
                .apply();
    }

    // Registra un alimento caducado que el usuario ha eliminado
    public void registrarAlimentoCaducadoEliminado() {
        int actuales = getAlimentosCaducadosHistorico();

        sharedPreferences.edit()
                .putInt(KEY_CADUCADOS_HISTORICO, actuales + 1)
                .apply();
    }

    // Obtiene el total de alimentos aprovechados
    public int getAlimentosAprovechados() {
        return sharedPreferences.getInt(KEY_APROVECHADOS, 0);
    }

    // Obtiene el total histórico de alimentos caducados eliminados
    public int getAlimentosCaducadosHistorico() {
        return sharedPreferences.getInt(KEY_CADUCADOS_HISTORICO, 0);
    }

    // Obtiene los alimentos de la nevera que ya han caducado
    public LiveData<Integer> getCaducadosEnNevera() {

        long inicioDeHoy = obtenerInicioDelDiaActual();

        return alimentoDAO.contarCaducadosActuales(inicioDeHoy);
    }

    // Calcula los milisegundos correspondientes a las 00:00 de hoy
    private long obtenerInicioDelDiaActual() {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
}
