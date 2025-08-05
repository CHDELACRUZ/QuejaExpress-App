package com.mundowebsolutions.quejaexpress;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class EditarPerfilFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 2;
    private String userId; // ID dinámico del usuario autenticado
    private Uri imagenSeleccionada;
    private String fotoPerfilUrl;
    private ImageView imagenPerfil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        // Obtener el ID del usuario autenticado
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid(); // Obtener el UID del usuario
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return view; // Salir del fragmento si no hay usuario autenticado
        }

        // Inicializar elementos de la vista
        imagenPerfil = view.findViewById(R.id.imagenPerfil);
        Button botonSeleccionarImagen = view.findViewById(R.id.botonSeleccionarImagen);
        Button botonGuardar = view.findViewById(R.id.botonGuardar);

        // Listener para seleccionar imagen
        botonSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagenDeGaleria();
            }
        });

        // Listener para guardar los cambios
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });

        // Cargar imagen de perfil actual desde Firestore si existe
        cargarImagenPerfil();

        return view;
    }

    // Método para seleccionar una imagen de la galería
    public void seleccionarImagenDeGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void guardarCambios() {
        // Sube la imagen a Firebase Storage
        if (imagenSeleccionada != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("perfil/" + userId + "/fotoPerfil.jpg");

            UploadTask uploadTask = storageRef.putFile(imagenSeleccionada);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                fotoPerfilUrl = uri.toString(); // Obtén el URL de la imagen subida

                // Guarda el URL en Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> usuarioData = new HashMap<>();
                usuarioData.put("fotoPerfilUrl", fotoPerfilUrl);

                db.collection("usuarios").document(userId)
                        .update(usuarioData)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
            })).addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarImagenPerfil() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String url = documentSnapshot.getString("fotoPerfilUrl");
                        if (url != null && !url.isEmpty()) {
                            Picasso.get().load(url).into(imagenPerfil); // Cargar imagen existente en el ImageView
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar imagen de perfil", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            imagenSeleccionada = data.getData();
            if (imagenSeleccionada != null) {
                Picasso.get().load(imagenSeleccionada).into(imagenPerfil); // Cargar imagen seleccionada en el ImageView
            }
        }
    }
}