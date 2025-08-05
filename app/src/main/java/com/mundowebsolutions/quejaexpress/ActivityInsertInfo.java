package com.mundowebsolutions.quejaexpress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActivityInsertInfo extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextName;
    private ImageView imageViewProfile;
    private Uri imageUri;

    // Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_info);

        // Inicializa Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Referencias UI
        editTextName = findViewById(R.id.editTextName);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        Button buttonSaveInfo = findViewById(R.id.buttonSaveInfo);

        // Seleccionar imagen
        buttonSelectImage.setOnClickListener(v -> openFileChooser());

        // Guardar información
        buttonSaveInfo.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            if (!name.isEmpty() && imageUri != null) {
                saveUserData(name);
            } else {
                Toast.makeText(this, "Por favor, ingresa tu nombre y selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para abrir el selector de imágenes
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Subir la foto y guardar los datos en Firestore
    private void saveUserData(String userName) {
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("user_photos/" + userId + ".jpg");

        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String photoUrl = uri.toString();
            saveToFirestore(userId, userName, photoUrl);
        })).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
        });
    }

    // Guardar nombre y URL de la foto en Firestore y redirigir al MainActivity
    private void saveToFirestore(String userId, String userName, String photoUrl) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("photoUrl", photoUrl);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Información guardada con éxito", Toast.LENGTH_SHORT).show();
                    // Redirigir a MainActivity después de guardar los datos
                    Intent intent = new Intent(ActivityInsertInfo.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Cierra esta actividad para que no vuelva al pulsar 'atrás'
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar la información", Toast.LENGTH_SHORT).show();
                });
    }
}