package com.mundowebsolutions.quejaexpress;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrearFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private StorageReference storageRef;

    private Spinner spnTipoQueja, spnRuta;
    private EditText edtUnidad, edmDescripcion;
    private ImageView imvImagen;
    private Button btnEnviar;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        spnTipoQueja = view.findViewById(R.id.spnTipoQueja);
        spnRuta = view.findViewById(R.id.spnRuta);
        edtUnidad = view.findViewById(R.id.edtUnidad);
        edmDescripcion = view.findViewById(R.id.edmDescripcion);
        imvImagen = view.findViewById(R.id.imvImagen);
        btnEnviar = view.findViewById(R.id.btnEnviar);

        imvImagen.setOnClickListener(v -> openFileChooser());

        btnEnviar.setOnClickListener(v -> {
            String tipoQueja = spnTipoQueja.getSelectedItem().toString();
            String ruta = spnRuta.getSelectedItem().toString();
            String unidad = edtUnidad.getText().toString();
            String descripcion = edmDescripcion.getText().toString();

            if (tipoQueja.isEmpty() || ruta.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                uploadImage(tipoQueja, ruta, unidad, descripcion);
            } else {
                crearQueja(tipoQueja, ruta, unidad, descripcion, null);
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imvImagen.setImageURI(imageUri);
        }
    }

    private void uploadImage(String tipoQueja, String ruta, String unidad, String descripcion) {
        String imageId = UUID.randomUUID().toString();
        StorageReference fileReference = storageRef.child("quejas/" + imageId + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            crearQueja(tipoQueja, ruta, unidad, descripcion, imageUrl);
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    Log.e("UploadImage", "Error al subir la imagen", e);
                });
    }

    private void crearQueja(String tipoQueja, String ruta, String unidad, String descripcion, String imagenUrl) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> quejaData = new HashMap<>();
        quejaData.put("tipoQueja", tipoQueja);
        quejaData.put("ruta", ruta);
        quejaData.put("unidad", unidad);
        quejaData.put("descripcion", descripcion);
        quejaData.put("imagenUrl", imagenUrl);
        quejaData.put("userId", userId);
        quejaData.put("fechaHora", FieldValue.serverTimestamp()); // Agrega la marca de tiempo

        db.collection("Quejas").add(quejaData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Queja creada exitosamente", Toast.LENGTH_SHORT).show();
                    Log.d("CrearQueja", "Documento de queja creado con ID: " + documentReference.getId());

                    // Limpiar los campos
                    limpiarCampos();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al crear la queja", Toast.LENGTH_SHORT).show();
                    Log.w("CrearQueja", "Error al crear el documento de queja", e);
                });
    }

    private void limpiarCampos() {
        spnTipoQueja.setSelection(0);
        spnRuta.setSelection(0);
        edtUnidad.setText("");
        edmDescripcion.setText("");
        imvImagen.setImageResource(R.drawable.imagelogo);
        imageUri = null;
    }

}
