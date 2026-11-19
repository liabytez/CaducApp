package com.example.caducapp.modelo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase principal de la base de datos Room.
 * Implementa el patrón Singleton para tener una única instancia
 * de la base de datos en toda la aplicación.
 */
@Database(entities = {Alimento.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Método que Room implementará automáticamente para obtener el DAO
    public abstract AlimentoDAO alimentoDAO();

    // Instancia única de la base de datos
    // volatile permite que los distintos hilos vean su valor actualizado
    private static volatile AppDatabase INSTANCE;

    // Número de hilos disponibles para ejecutar operaciones en segundo plano
    private static final int NUMBER_OF_THREADS = 4;

    // Pool de hilos para realizar operaciones sin bloquear la interfaz
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Devuelve la instancia única de la base de datos.
     */
    public static AppDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "caducapp_database"
                            )
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
