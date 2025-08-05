package com.mundowebsolutions.quejaexpress;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.bumptech.glide.Glide;

public class QuejaAdapter extends RecyclerView.Adapter<QuejaAdapter.ViewHolder> {
    private Context context;
    private List<Queja> quejaList;
    private OnQuejaClickListener onQuejaClickListener;

    public QuejaAdapter(Context context, List<Queja> quejaList) {
        this.context = context;
        this.quejaList = quejaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_queja, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Queja queja = quejaList.get(position);
        Log.d("QuejaAdapter", "Mostrando queja: " + queja.getDescripcion());

        // Asignar datos de la queja
        holder.textTipo.setText("Tipo: " + queja.getTipoQueja());
        holder.textRuta.setText("Ruta: " + queja.getRuta());
        holder.textUnidad.setText("Unidad: " + queja.getUnidad());
        holder.textDescripcion.setText(queja.getDescripcion());

        if (queja.getFechaHora() != null) {
            Date fechaHoraDate = queja.getFechaHora().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy h:mm a", new Locale("es", "MX"));
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));
            String fechaHoraString = sdf.format(fechaHoraDate);
            holder.textFechaHora.setText(fechaHoraString);
        } else {
            holder.textFechaHora.setText("Fecha: N/A");
        }

        if (queja.getImagenUrl() != null && !queja.getImagenUrl().isEmpty()) {
            holder.imvImagen.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(queja.getImagenUrl())
                    .into(holder.imvImagen);
        } else {
            holder.imvImagen.setVisibility(View.GONE);
        }

        String userId = queja.getUserId();
        FirebaseFirestore.getInstance().collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        holder.textUsuario.setText(usuario.getNombre() + " " + usuario.getApellidos());

                        if (usuario.getFotoPerfilUrl() != null && !usuario.getFotoPerfilUrl().isEmpty()) {
                            holder.imageUsuario.setVisibility(View.VISIBLE);
                            Picasso.get().load(usuario.getFotoPerfilUrl()).into(holder.imageUsuario);
                        } else {
                            holder.imageUsuario.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("QuejaAdapter", "Error al cargar datos del usuario", e));
    }

    @Override
    public int getItemCount() {
        return quejaList.size();
    }

    public void setOnQuejaClickListener(OnQuejaClickListener listener) {
        this.onQuejaClickListener = listener;
    }

    public interface OnQuejaClickListener {
        void onQuejaClick(Queja queja);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUsuario, imvImagen;
        TextView textUsuario, textTipo, textRuta, textUnidad, textDescripcion, textFechaHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUsuario = itemView.findViewById(R.id.imageUsuario);
            imvImagen = itemView.findViewById(R.id.imvImagen);
            textUsuario = itemView.findViewById(R.id.textUsuario);
            textTipo = itemView.findViewById(R.id.textTipo);
            textRuta = itemView.findViewById(R.id.textRuta);
            textUnidad = itemView.findViewById(R.id.textUnidad);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            textFechaHora = itemView.findViewById(R.id.textFechaHora);
        }
    }
}