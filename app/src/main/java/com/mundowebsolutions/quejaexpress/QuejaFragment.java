package com.mundowebsolutions.quejaexpress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class QuejaFragment extends Fragment {
    private static final String ARG_QUEJA_ID = "quejaId";
    private FirebaseFirestore db;
    private TextView textUsuario, textTipo, textRuta, textUnidad, textDescripcion, textFechaHora;
    private ImageView imageUsuario, imvImagen;
    private String quejaId;

    public static QuejaFragment newInstance(String quejaId) {
        QuejaFragment fragment = new QuejaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUEJA_ID, quejaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queja, container, false);

        if (getArguments() != null) {
            quejaId = getArguments().getString(ARG_QUEJA_ID);
        }

        db = FirebaseFirestore.getInstance();

        textUsuario = view.findViewById(R.id.textUsuario);
        textTipo = view.findViewById(R.id.textTipo);
        textRuta = view.findViewById(R.id.textRuta);
        textUnidad = view.findViewById(R.id.textUnidad);
        textDescripcion = view.findViewById(R.id.textDescripcion);
        textFechaHora = view.findViewById(R.id.textFechaHora);
        imageUsuario = view.findViewById(R.id.imageUsuario);
        imvImagen = view.findViewById(R.id.imvImagen);

        if (quejaId != null) {
            loadQuejaData(quejaId);
        }
        return view;
    }

    private void loadQuejaData(String quejaId) {
        db.collection("queja").document(quejaId).get().addOnSuccessListener(quejaDoc -> {
            if (quejaDoc.exists()) {
                String descripcion = quejaDoc.getString("descripcion");
                String ruta = quejaDoc.getString("ruta");
                String tipoQueja = quejaDoc.getString("tipoQueja");
                String unidad = quejaDoc.getString("unidad");
                String userId = quejaDoc.getString("userId");
                String imagenUrl = quejaDoc.getString("imagenUrl");

                textDescripcion.setText(descripcion);
                textRuta.setText("Ruta: " + ruta);
                textTipo.setText("Tipo: " + tipoQueja);
                textUnidad.setText("Unidad: " + unidad);

                if (imagenUrl != null && !imagenUrl.isEmpty()) {
                    Picasso.get().load(imagenUrl).into(imvImagen);
                }

                loadUserData(userId);
            }
        }).addOnFailureListener(e -> {
        });
    }

    private void loadUserData(String userId) {
        db.collection("usuario").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String nombre = userDoc.getString("nombre");
                String apellido = userDoc.getString("apellido");
                String fotoPerfilUrl = userDoc.getString("fotoPerfilUrl");

                textUsuario.setText(nombre + " " + apellido);

                if (fotoPerfilUrl != null && !fotoPerfilUrl.isEmpty()) {
                    Picasso.get().load(fotoPerfilUrl).into(imageUsuario);
                }
            }
        }).addOnFailureListener(e -> {
        });
    }
}