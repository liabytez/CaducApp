package com.example.caducapp.modelo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Interfaz DAO (Data Access Object).
 * Define las operaciones para interactuar con la tabla de alimentos.
 */
@Dao
public interface AlimentoDAO {

    // Inserta un nuevo alimento en la base de datos
    @Insert
    void insertar(Alimento alimento);

    // Actualiza un alimento existente
    @Update
    void actualizar(Alimento alimento);

    // Elimina un alimento concreto
    @Delete
    void eliminar(Alimento alimento);

    // Obtiene todos los alimentos ordenados por fecha de caducidad
    // LiveData permite observar los cambios automáticamente
    @Query("SELECT * FROM tabla_alimentos ORDER BY fechaCaducidad ASC")
    LiveData<List<Alimento>> obtenerTodosOrdenados();

    // Consulta síncrona para el Worker de notificaciones
    // Busca alimentos que caducan antes o en la fecha límite indicada
    @Query("SELECT * FROM tabla_alimentos WHERE fechaCaducidad <= :fechaLimite ORDER BY fechaCaducidad ASC")
    List<Alimento> obtenerProximosACaducarSync(long fechaLimite);

    // Cuenta los alimentos que ya han caducado
    @Query("SELECT COUNT(*) FROM tabla_alimentos WHERE fechaCaducidad < :fechaActual")
    LiveData<Integer> contarCaducadosActuales(long fechaActual);
}
