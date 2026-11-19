package com.example.caducapp.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.caducapp.R;
import com.example.caducapp.controlador.AlimentoController;
import com.example.caducapp.modelo.Alimento;
import com.example.caducapp.notifications.CaducidadWorker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AlimentoController alimentoController;
    private AlimentoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEstadoVacio;
    private TextInputEditText etBuscarNombre;
    private Spinner spinnerFiltroCategoria;

    // Lista completa recibida de la base de datos
    private List<Alimento> listaCompletaAlimentos = new ArrayList<>();

    // Valores actuales de los filtros
    private String textoBusquedaActual = "";
    private String categoriaSeleccionada = "";
    private String opcionTodasCategorias;

    // Nombre único del trabajo diario de notificaciones
    private static final String WORK_NAME_CADUCIDAD_DIARIA =
            "caducapp_notificacion_diaria_9am";

    // Lanzador para solicitar el permiso de notificaciones
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (!isGranted) {
                            Toast.makeText(
                                    this,
                                    getString(R.string.permiso_notif_rechazado),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        opcionTodasCategorias = getString(R.string.todas_categorias);
        categoriaSeleccionada = opcionTodasCategorias;

        // 1. Configurar la barra superior
        MaterialToolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        // 2. Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewAlimentos);
        tvEstadoVacio = findViewById(R.id.tvEstadoVacio);
        etBuscarNombre = findViewById(R.id.etBuscarNombre);
        spinnerFiltroCategoria = findViewById(R.id.spinnerFiltroCategoria);

        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);

        // 3. Configurar RecyclerView y adaptador
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new AlimentoAdapter();
        recyclerView.setAdapter(adapter);

        // 4. Configurar filtros
        configurarFiltroCategorias();
        configurarBuscadorNombre();

        // 5. Configurar deslizamiento para eliminar
        configurarSwipeParaEliminar();

        // 6. Conectar con el controlador y observar la base de datos
        alimentoController = new AlimentoController(getApplication());

        alimentoController.getTodosLosAlimentos().observe(this, alimentos -> {
            listaCompletaAlimentos = alimentos != null
                    ? alimentos
                    : new ArrayList<>();

            aplicarFiltros();
        });

        // 7. Abrir formulario para añadir alimentos
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditActivity.class
            );
            startActivity(intent);
        });

        // 8. Configurar eventos de las tarjetas
        adapter.setOnAlimentoListener(new AlimentoAdapter.OnAlimentoListener() {

            @Override
            public void onAlimentoClick(Alimento alimento) {
                // Abrir formulario para editar
                Intent intent = new Intent(
                        MainActivity.this,
                        AddEditActivity.class
                );

                intent.putExtra(AddEditActivity.EXTRA_ID, alimento.getId());
                intent.putExtra(AddEditActivity.EXTRA_NOMBRE, alimento.getNombre());
                intent.putExtra(AddEditActivity.EXTRA_CATEGORIA, alimento.getCategoria());
                intent.putExtra(AddEditActivity.EXTRA_CANTIDAD, alimento.getCantidad());
                intent.putExtra(AddEditActivity.EXTRA_FECHA_CADUCIDAD, alimento.getFechaCaducidad());

                startActivity(intent);
            }

            @Override
            public void onConsumidoClick(Alimento alimento) {
                alimentoController.marcarComoConsumido(alimento);

                Toast.makeText(
                        MainActivity.this,
                        alimento.getNombre() + ": "
                                + getString(R.string.alimento_consumido_msg),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // 9. Solicitar permiso y programar las notificaciones
        solicitarPermisoNotificacionesSiNecesario();
        programarNotificacionDiariaLas9();
    }

    /**
     * Configura el Spinner con todas las categorías.
     */
    private void configurarFiltroCategorias() {
        List<String> opciones = new ArrayList<>();

        // Primera opción: mostrar todas las categorías
        opciones.add(opcionTodasCategorias);

        // Añadir las categorías del string-array
        String[] categoriasArray = getResources().getStringArray(
                R.array.categorias_alimentos
        );

        for (String categoria : categoriasArray) {
            opciones.add(categoria);
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opciones
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFiltroCategoria.setAdapter(spinnerAdapter);

        spinnerFiltroCategoria.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        categoriaSeleccionada = opciones.get(position);
                        aplicarFiltros();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        categoriaSeleccionada = opcionTodasCategorias;
                        aplicarFiltros();
                    }
                }
        );
    }

    /**
     * Configura el buscador para filtrar por nombre mientras escribimos.
     */
    private void configurarBuscadorNombre() {
        etBuscarNombre.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
                // No necesitamos realizar ninguna acción.
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                textoBusquedaActual = s != null
                        ? s.toString().trim().toLowerCase(Locale.getDefault())
                        : "";

                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No necesitamos realizar ninguna acción.
            }
        });
    }

    /**
     * Combina el filtro de categoría y el buscador por nombre.
     */
    private void aplicarFiltros() {
        List<Alimento> filtrados = new ArrayList<>();

        for (Alimento alimento : listaCompletaAlimentos) {

            boolean coincideCategoria =
                    opcionTodasCategorias.equals(categoriaSeleccionada)
                            || (alimento.getCategoria() != null
                            && alimento.getCategoria().equalsIgnoreCase(categoriaSeleccionada));

            boolean coincideNombre =
                    textoBusquedaActual.isEmpty()
                            || (alimento.getNombre() != null
                            && alimento.getNombre().toLowerCase(Locale.getDefault())
                            .contains(textoBusquedaActual));

            if (coincideCategoria && coincideNombre) {
                filtrados.add(alimento);
            }
        }

        adapter.setAlimentos(filtrados);

        // Actualizar el mensaje de estado
        if (filtrados.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEstadoVacio.setVisibility(View.VISIBLE);

            if (listaCompletaAlimentos.isEmpty()) {
                tvEstadoVacio.setText(R.string.lista_vacia);
            } else {
                tvEstadoVacio.setText(R.string.lista_sin_resultados);
            }

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEstadoVacio.setVisibility(View.GONE);
        }
    }

    /**
     * Configura el deslizamiento de las tarjetas para eliminarlas.
     */
    private void configurarSwipeParaEliminar() {

        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                ) {

                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            @NonNull RecyclerView.ViewHolder target
                    ) {
                        // No permitimos reordenar las tarjetas.
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {
                        int posicion = viewHolder.getBindingAdapterPosition();

                        if (posicion == RecyclerView.NO_POSITION) {
                            return;
                        }

                        Alimento alimentoDeslizado =
                                adapter.getAlimentoAt(posicion);

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.titulo_confirmar_eliminar)
                                .setMessage(
                                        getString(R.string.mensaje_confirmar_eliminar)
                                                + "\n\n• "
                                                + alimentoDeslizado.getNombre()
                                )
                                .setCancelable(false)
                                .setPositiveButton(R.string.si, (dialog, which) -> {

                                    // Comprobar si el alimento estaba caducado
                                    if (AlimentoAdapter.calcularDiasRestantes(
                                            alimentoDeslizado.getFechaCaducidad()
                                    ) < 0) {
                                        alimentoController.registrarAlimentoCaducadoEliminado();
                                    }

                                    // Eliminar mediante el controlador
                                    alimentoController.eliminar(alimentoDeslizado);

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Alimento eliminado",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })
                                .setNegativeButton(R.string.cancelar, (dialog, which) -> {
                                    // Restaurar la tarjeta si cancelamos
                                    adapter.notifyItemChanged(posicion);
                                })
                                .show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Mostrar el menú superior.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Gestionar las opciones del menú.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_estadisticas) {
            Intent intent = new Intent(
                    MainActivity.this,
                    EstadisticasActivity.class
            );

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Solicita el permiso de notificaciones si es necesario.
     */
    private void solicitarPermisoNotificacionesSiNecesario() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestNotificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );
            }
        }
    }

    /**
     * Programa una tarea periódica para comprobar los alimentos
     * que están próximos a caducar.
     */
    private void programarNotificacionDiariaLas9() {

        Calendar ahora = Calendar.getInstance();
        Calendar proximaEjecucion = Calendar.getInstance();

        proximaEjecucion.set(Calendar.HOUR_OF_DAY, 9);
        proximaEjecucion.set(Calendar.MINUTE, 0);
        proximaEjecucion.set(Calendar.SECOND, 0);
        proximaEjecucion.set(Calendar.MILLISECOND, 0);

        // Si las 9:00 ya han pasado, programar para mañana
        if (!proximaEjecucion.after(ahora)) {
            proximaEjecucion.add(Calendar.DAY_OF_YEAR, 1);
        }

        long initialDelayMillis =
                proximaEjecucion.getTimeInMillis() - ahora.getTimeInMillis();

        PeriodicWorkRequest trabajoDiario =
                new PeriodicWorkRequest.Builder(
                        CaducidadWorker.class,
                        24,
                        TimeUnit.HOURS
                )
                        .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(
                        WORK_NAME_CADUCIDAD_DIARIA,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        trabajoDiario
                );
    }
}
