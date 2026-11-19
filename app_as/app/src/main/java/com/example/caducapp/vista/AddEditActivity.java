package com.example.caducapp.vista;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caducapp.R;
import com.example.caducapp.modelo.Alimento;
import com.example.caducapp.controlador.AlimentoController;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Pantalla para añadir un alimento nuevo o editar uno existente.
 * Incluye validación, selector de categoría, selector de fecha
 * y eliminación con confirmación.
 */
public class AddEditActivity extends AppCompatActivity {

    // Claves para recibir los datos mediante Intent
    public static final String EXTRA_ID =
            "com.example.caducapp.vista.EXTRA_ID";

    public static final String EXTRA_NOMBRE =
            "com.example.caducapp.vista.EXTRA_NOMBRE";

    public static final String EXTRA_CATEGORIA =
            "com.example.caducapp.vista.EXTRA_CATEGORIA";

    public static final String EXTRA_CANTIDAD =
            "com.example.caducapp.vista.EXTRA_CANTIDAD";

    public static final String EXTRA_FECHA_CADUCIDAD =
            "com.example.caducapp.vista.EXTRA_FECHA_CADUCIDAD";

    private TextInputLayout tilNombre;
    private TextInputLayout tilCantidad;
    private TextInputLayout tilFechaCaducidad;

    private TextInputEditText etNombre;
    private TextInputEditText etCantidad;
    private TextInputEditText etFechaCaducidad;

    private Spinner spinnerCategoria;

    private MaterialButton btnGuardar;
    private MaterialButton btnEliminar;

    private AlimentoController alimentoController;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // -1 indica que todavía no se ha seleccionado una fecha
    private long fechaSeleccionadaMillis = -1L;

    // -1 indica que estamos creando un alimento nuevo
    private int alimentoIdEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // 1. Configurar la barra superior
        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Inicializar las vistas
        tilNombre = findViewById(R.id.tilNombre);
        tilCantidad = findViewById(R.id.tilCantidad);
        tilFechaCaducidad = findViewById(R.id.tilFechaCaducidad);

        etNombre = findViewById(R.id.etNombre);
        etCantidad = findViewById(R.id.etCantidad);
        etFechaCaducidad = findViewById(R.id.etFechaCaducidad);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);

        // 3. Inicializar el controlador
        alimentoController = new AlimentoController(getApplication());

        // 4. Configurar el Spinner de categorías
        ArrayAdapter<CharSequence> categoriaAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.categorias_alimentos,
                        android.R.layout.simple_spinner_item
                );

        categoriaAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategoria.setAdapter(categoriaAdapter);

        // 5. Comprobar si estamos editando un alimento
        if (getIntent().hasExtra(EXTRA_ID)) {

            alimentoIdEditar = getIntent().getIntExtra(EXTRA_ID, -1);

            toolbar.setTitle(R.string.editar_alimento);
            btnEliminar.setVisibility(View.VISIBLE);

            String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
            String categoria = getIntent().getStringExtra(EXTRA_CATEGORIA);
            String cantidad = getIntent().getStringExtra(EXTRA_CANTIDAD);

            fechaSeleccionadaMillis = getIntent().getLongExtra(
                    EXTRA_FECHA_CADUCIDAD,
                    -1L
            );

            etNombre.setText(nombre);
            etCantidad.setText(cantidad);

            // Seleccionar la categoría correspondiente
            if (categoria != null) {

                int posicion = categoriaAdapter.getPosition(categoria);

                if (posicion >= 0) {
                    spinnerCategoria.setSelection(posicion);
                }
            }

            // Mostrar la fecha guardada
            if (fechaSeleccionadaMillis > 0) {

                etFechaCaducidad.setText(
                        dateFormat.format(new Date(fechaSeleccionadaMillis))
                );
            }

        } else {

            // Modo creación
            toolbar.setTitle(R.string.anadir_alimento);
            btnEliminar.setVisibility(View.GONE);
        }

        // 6. Configurar el selector de fecha
        etFechaCaducidad.setOnClickListener(
                v -> mostrarDatePicker()
        );

        tilFechaCaducidad.setEndIconOnClickListener(
                v -> mostrarDatePicker()
        );

        // 7. Configurar los botones
        btnGuardar.setOnClickListener(
                v -> guardarAlimento()
        );

        btnEliminar.setOnClickListener(
                v -> mostrarDialogoConfirmarEliminar()
        );
    }

    /**
     * Abre un DatePickerDialog inicializado con la fecha actual
     * o con la fecha que ya se había seleccionado.
     */
    private void mostrarDatePicker() {

        Calendar calendario = Calendar.getInstance();

        if (fechaSeleccionadaMillis > 0) {
            calendario.setTimeInMillis(fechaSeleccionadaMillis);
        }

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    Calendar fechaElegida = Calendar.getInstance();

                    fechaElegida.set(Calendar.YEAR, year);
                    fechaElegida.set(Calendar.MONTH, monthOfYear);
                    fechaElegida.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Utilizamos el mediodía para evitar desfases horarios
                    fechaElegida.set(Calendar.HOUR_OF_DAY, 12);
                    fechaElegida.set(Calendar.MINUTE, 0);
                    fechaElegida.set(Calendar.SECOND, 0);
                    fechaElegida.set(Calendar.MILLISECOND, 0);

                    fechaSeleccionadaMillis =
                            fechaElegida.getTimeInMillis();

                    etFechaCaducidad.setText(
                            dateFormat.format(new Date(fechaSeleccionadaMillis))
                    );

                    tilFechaCaducidad.setError(null);
                },
                anio,
                mes,
                dia
        );

        datePickerDialog.show();
    }

    /**
     * Valida los datos y guarda o actualiza el alimento
     * a través del controlador.
     */
    private void guardarAlimento() {

        String nombre = etNombre.getText() != null
                ? etNombre.getText().toString().trim()
                : "";

        String cantidad = etCantidad.getText() != null
                ? etCantidad.getText().toString().trim()
                : "";

        String categoria = spinnerCategoria.getSelectedItem() != null
                ? spinnerCategoria.getSelectedItem().toString()
                : "Otros";

        boolean valido = true;

        // Validar nombre
        if (TextUtils.isEmpty(nombre)) {

            tilNombre.setError(getString(R.string.error_nombre_vacio));
            valido = false;

        } else {

            tilNombre.setError(null);
        }

        // Validar cantidad
        if (TextUtils.isEmpty(cantidad)) {

            tilCantidad.setError(getString(R.string.error_cantidad_vacio));
            valido = false;

        } else {

            tilCantidad.setError(null);
        }

        // Validar fecha
        if (fechaSeleccionadaMillis <= 0) {

            tilFechaCaducidad.setError(
                    getString(R.string.error_fecha_vacio)
            );

            valido = false;

        } else {

            tilFechaCaducidad.setError(null);
        }

        // Si hay algún campo incorrecto, no continuamos
        if (!valido) {
            return;
        }

        if (alimentoIdEditar != -1) {

            // Modo edición
            Alimento alimentoActualizado = new Alimento(
                    nombre,
                    categoria,
                    cantidad,
                    fechaSeleccionadaMillis
            );

            alimentoActualizado.setId(alimentoIdEditar);

            alimentoController.actualizar(alimentoActualizado);

            Toast.makeText(
                    this,
                    "Alimento actualizado",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // Modo creación
            Alimento nuevoAlimento = new Alimento(
                    nombre,
                    categoria,
                    cantidad,
                    fechaSeleccionadaMillis
            );

            alimentoController.insertar(nuevoAlimento);

            Toast.makeText(
                    this,
                    "Alimento añadido",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar
     * el alimento que estamos editando.
     */
    private void mostrarDialogoConfirmarEliminar() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_confirmar_eliminar)
                .setMessage(R.string.mensaje_confirmar_eliminar)

                .setPositiveButton(R.string.si, (dialog, which) -> {

                    Alimento alimentoEliminar = new Alimento();
                    alimentoEliminar.setId(alimentoIdEditar);

                    // Comprobar si el alimento ya estaba caducado
                    if (AlimentoAdapter.calcularDiasRestantes(
                            fechaSeleccionadaMillis
                    ) < 0) {

                        alimentoController.registrarAlimentoCaducadoEliminado();
                    }

                    alimentoController.eliminar(alimentoEliminar);

                    Toast.makeText(
                            this,
                            "Alimento eliminado",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })

                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}
