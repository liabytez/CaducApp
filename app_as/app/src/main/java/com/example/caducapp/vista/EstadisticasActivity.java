package com.example.caducapp.vista;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caducapp.R;
import com.example.caducapp.controlador.AlimentoController;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

/**
 * Pantalla de estadísticas de CaducApp.
 * Compara los alimentos aprovechados frente a los caducados.
 */
public class EstadisticasActivity extends AppCompatActivity {

    private TextView tvTotalAprovechados;
    private TextView tvTotalCaducados;
    private TextView tvTasaAprovechamiento;

    private int aprovechados;
    private int caducadosHistorico;

    private AlimentoController alimentoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // 1. Configurar la Toolbar con el botón de volver
        MaterialToolbar toolbar = findViewById(R.id.toolbarEstadisticas);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Inicializar las vistas
        tvTotalAprovechados = findViewById(R.id.tvTotalAprovechados);
        tvTotalCaducados = findViewById(R.id.tvTotalCaducados);
        tvTasaAprovechamiento = findViewById(R.id.tvTasaAprovechamiento);

        // 3. Obtener los datos desde el controlador
        alimentoController = new AlimentoController(getApplication());

        aprovechados = alimentoController.getAlimentosAprovechados();
        caducadosHistorico = alimentoController.getAlimentosCaducadosHistorico();

        tvTotalAprovechados.setText(String.valueOf(aprovechados));

        // 4. Observar los alimentos actualmente caducados
        alimentoController.getCaducadosEnNevera().observe(this, caducadosActuales -> {

            int actualesEnNevera = caducadosActuales != null
                    ? caducadosActuales : 0;

            int totalCaducados = caducadosHistorico + actualesEnNevera;

            tvTotalCaducados.setText(String.valueOf(totalCaducados));

            actualizarTasaAprovechamiento(aprovechados, totalCaducados);
        });
    }

    /**
     * Calcula el porcentaje de alimentos aprovechados
     * respecto al total gestionado (aprovechados + caducados).
     */
    private void actualizarTasaAprovechamiento(
            int totalAprovechados,
            int totalCaducados) {

        int total = totalAprovechados + totalCaducados;

        if (total <= 0) {
            tvTasaAprovechamiento.setText("--");
            return;
        }

        int porcentaje = Math.round(
                (totalAprovechados * 100f) / total
        );

        tvTasaAprovechamiento.setText(
                String.format(Locale.getDefault(), "%d%%", porcentaje)
        );
    }
}
