package com.example.caducapp.vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caducapp.R;
import com.example.caducapp.modelo.Alimento;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Adaptador para conectar la lista de objetos Alimento con el RecyclerView.
 * También calcula los días restantes y aplica el semáforo de colores.
 */
public class AlimentoAdapter extends RecyclerView.Adapter<AlimentoAdapter.AlimentoViewHolder> {

    private List<Alimento> listaAlimentos = new ArrayList<>();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private OnAlimentoListener listener;

    // Interfaz para comunicar los clics de cada tarjeta hacia MainActivity
    public interface OnAlimentoListener {
        void onAlimentoClick(Alimento alimento);
        void onConsumidoClick(Alimento alimento);
    }

    public void setOnAlimentoListener(OnAlimentoListener listener) {
        this.listener = listener;
    }

    // Actualiza los datos mostrados en pantalla
    public void setAlimentos(List<Alimento> alimentos) {
        this.listaAlimentos = alimentos;
        notifyDataSetChanged();
    }

    // Devuelve el alimento de una posición concreta
    public Alimento getAlimentoAt(int position) {
        return listaAlimentos.get(position);
    }

    @NonNull
    @Override
    public AlimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alimento, parent, false);

        return new AlimentoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlimentoViewHolder holder, int position) {

        Alimento actual = listaAlimentos.get(position);
        Context context = holder.itemView.getContext();

        // 1. Asignar los textos básicos
        holder.tvNombre.setText(actual.getNombre());

        holder.tvCategoriaCantidad.setText(
                actual.getCategoria() + " • " + actual.getCantidad()
        );

        holder.tvFechaCaducidad.setText(
                "Caduca: " + dateFormat.format(new Date(actual.getFechaCaducidad()))
        );

        // 2. Calcular los días restantes
        long diasRestantes = calcularDiasRestantes(actual.getFechaCaducidad());

        // 3. Configurar el texto y los colores según la urgencia
        int colorPrincipal;
        int colorFondo;
        String textoDias;

        if (diasRestantes < 0) {

            long diasPasados = Math.abs(diasRestantes);

            textoDias = diasPasados == 1
                    ? "Caducó ayer"
                    : "Caducado hace " + diasPasados + "d";

            colorPrincipal = ContextCompat.getColor(context, R.color.urgencia_roja);
            colorFondo = ContextCompat.getColor(context, R.color.fondo_rojo_suave);

        } else if (diasRestantes == 0) {

            textoDias = "¡Caduca HOY!";

            colorPrincipal = ContextCompat.getColor(context, R.color.urgencia_roja);
            colorFondo = ContextCompat.getColor(context, R.color.fondo_rojo_suave);

        } else if (diasRestantes <= 2) {

            textoDias = diasRestantes == 1
                    ? "En 1 día"
                    : "En " + diasRestantes + " días";

            colorPrincipal = ContextCompat.getColor(context, R.color.urgencia_roja);
            colorFondo = ContextCompat.getColor(context, R.color.fondo_rojo_suave);

        } else if (diasRestantes <= 5) {

            textoDias = "En " + diasRestantes + " días";

            colorPrincipal = ContextCompat.getColor(context, R.color.urgencia_naranja);
            colorFondo = ContextCompat.getColor(context, R.color.fondo_naranja_suave);

        } else {

            textoDias = "En " + diasRestantes + " días";

            colorPrincipal = ContextCompat.getColor(context, R.color.urgencia_verde);
            colorFondo = ContextCompat.getColor(context, R.color.fondo_verde_suave);
        }

        holder.viewIndicadorColor.setBackgroundColor(colorPrincipal);
        holder.tvDiasRestantes.setText(textoDias);
        holder.tvDiasRestantes.setTextColor(colorPrincipal);
        holder.tvDiasRestantes.setBackgroundColor(colorFondo);

        // 4. Configurar los eventos de clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlimentoClick(actual);
            }
        });

        holder.btnConsumido.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConsumidoClick(actual);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAlimentos.size();
    }

    /**
     * Calcula la diferencia en días naturales entre hoy y la fecha de caducidad.
     * Normaliza ambas fechas a medianoche para evitar errores por las horas.
     */
    public static long calcularDiasRestantes(long fechaCaducidadMillis) {

        Calendar hoy = Calendar.getInstance();

        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MILLISECOND, 0);

        Calendar caducidad = Calendar.getInstance();

        caducidad.setTimeInMillis(fechaCaducidadMillis);
        caducidad.set(Calendar.HOUR_OF_DAY, 0);
        caducidad.set(Calendar.MINUTE, 0);
        caducidad.set(Calendar.SECOND, 0);
        caducidad.set(Calendar.MILLISECOND, 0);

        long diferenciaMillis =
                caducidad.getTimeInMillis() - hoy.getTimeInMillis();

        return TimeUnit.MILLISECONDS.toDays(diferenciaMillis);
    }

    // ViewHolder que mantiene las referencias a las vistas de cada tarjeta
    static class AlimentoViewHolder extends RecyclerView.ViewHolder {

        private final View viewIndicadorColor;
        private final TextView tvNombre;
        private final TextView tvCategoriaCantidad;
        private final TextView tvFechaCaducidad;
        private final TextView tvDiasRestantes;
        private final MaterialButton btnConsumido;

        public AlimentoViewHolder(@NonNull View itemView) {

            super(itemView);

            viewIndicadorColor = itemView.findViewById(R.id.viewIndicadorColor);
            tvNombre = itemView.findViewById(R.id.tvNombreAlimento);
            tvCategoriaCantidad = itemView.findViewById(R.id.tvCategoriaCantidad);
            tvFechaCaducidad = itemView.findViewById(R.id.tvFechaCaducidad);
            tvDiasRestantes = itemView.findViewById(R.id.tvDiasRestantes);
            btnConsumido = itemView.findViewById(R.id.btnConsumido);
        }
    }
}
