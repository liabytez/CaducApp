
package com.example.caducapp.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caducapp.R;
import com.example.caducapp.modelo.Alimento;
import com.example.caducapp.controlador.AlimentoController;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Actividad principal de CaducApp.
 * Muestra la lista de alimentos ordenados por urgencia de caducidad.
 */
public class MainActivity extends AppCompatActivity {

    private AlimentoController alimentoController;
    private AlimentoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEstadoVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Configurar la barra superior
        MaterialToolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        // 2. Inicializar las vistas
        recyclerView = findViewById(R.id.recyclerViewAlimentos);
        tvEstadoVacio = findViewById(R.id.tvEstadoVacio);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);

        // 3. Configurar RecyclerView y su adaptador
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new AlimentoAdapter();
        recyclerView.setAdapter(adapter);

        // 4. Inicializar el controlador
        alimentoController = new AlimentoController(getApplication());

        // 5. Obtener los alimentos y observar los cambios
        alimentoController.getTodosLosAlimentos().observe(this, alimentos -> {

            adapter.setAlimentos(alimentos);

            // Mostrar u ocultar el mensaje de lista vacía
            if (alimentos == null || alimentos.isEmpty()) {
                tvEstadoVacio.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEstadoVacio.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // 6. Configurar el botón para añadir alimentos
        fabAgregar.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Formulario en Fase 3",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // 7. Configurar los clics de las tarjetas
        adapter.setOnAlimentoListener(new AlimentoAdapter.OnAlimentoListener() {

            @Override
            public void onAlimentoClick(Alimento alimento) {
                Toast.makeText(
                        MainActivity.this,
                        "Editar: " + alimento.getNombre(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onConsumidoClick(Alimento alimento) {
                Toast.makeText(
                        MainActivity.this,
                        "Consumido: " + alimento.getNombre(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
