package com.example.caducapp.controlador;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.caducapp.modelo.Alimento;
import com.example.caducapp.modelo.AlimentoRepository;
import java.util.List;

public class AlimentoController {
    private final AlimentoRepository repository;

    // Constructor
    public AlimentoController(Application application) {
        repository = new AlimentoRepository(application);
    }

    // Obtener todos los alimentos
    public LiveData<List<Alimento>> getTodosLosAlimentos() {
        return repository.getTodosLosAlimentos();
    }

    // Insertar un alimento
    public void insertar(Alimento alimento) {
        repository.insertar(alimento);
    }

    // Actualizar un alimento
    public void actualizar(Alimento alimento) {
        repository.actualizar(alimento);
    }

    // Eliminar un alimento
    public void eliminar(Alimento alimento) {
        repository.eliminar(alimento);
    }

    // Marcar un alimento como consumido
    public void marcarComoConsumido(Alimento alimento) {
        repository.marcarComoConsumido(alimento);
    }

    // Registrar un alimento caducado que se ha eliminado
    public void registrarAlimentoCaducadoEliminado() {
        repository.registrarAlimentoCaducadoEliminado();
    }

    // Obtener el total de alimentos aprovechados
    public int getAlimentosAprovechados() {
        return repository.getAlimentosAprovechados();
    }

    // Obtener el histórico de alimentos caducados eliminados
    public int getAlimentosCaducadosHistorico() {
        return repository.getAlimentosCaducadosHistorico();
    }

    // Obtener los alimentos caducados que siguen en la nevera
    public LiveData<Integer> getCaducadosEnNevera() {
        return repository.getCaducadosEnNevera();
    }
}
