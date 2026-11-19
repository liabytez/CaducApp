package com.example.caducapp.modelo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entidad que representa un alimento en la base de datos Room.
 * Cada objeto equivale a una fila de la tabla tabla_alimentos.
 */
@Entity(tableName = "tabla_alimentos")
public class Alimento {

    // Clave primaria autogenerada por SQLite
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Datos principales del alimento
    private String nombre;
    private String categoria;
    private String cantidad;
    private long fechaCaducidad;

    // Constructor vacío utilizado por Room
    public Alimento() {
    }

    // Constructor para crear alimentos desde la aplicación
    @Ignore
    public Alimento(String nombre, String categoria, String cantidad, long fechaCaducidad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.fechaCaducidad = fechaCaducidad;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public long getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(long fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
}
