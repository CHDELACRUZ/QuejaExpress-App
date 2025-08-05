package com.mundowebsolutions.quejaexpress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.mundowebsolutions.quejaexpress.R;


public class PerfilFragment extends Fragment {

    private TextView textViewNombre;
    private TextView textViewCorreo;
    private ImageView imagenPerfil;
    private View layoutEditarPerfil;
    private View layoutSalir;  // Referencia para el layout de salida

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializa Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a los elementos de la interfaz
        textViewNombre = view.findViewById(R.id.txvNombre);
        textViewCorreo = view.findViewById(R.id.txvCorreo);
        imagenPerfil = view.findViewById(R.id.ImagenPerfil);
        layoutEditarPerfil = view.findViewById(R.id.layoutEditarPerfil);
        layoutSalir = view.findViewById(R.id.btnSalir);
        ConstraintLayout layoutSeguimiento = view.findViewById(R.id.layoutSeguimiento); // El layout clickeable

        // Configurar el clic para redirigir a SeguimientoActivity
        layoutSeguimiento.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeguimientoActivity.class);
            startActivity(intent);
        });

        // Cargar los datos de perfil del usuario
        cargarDatosPerfil();

        // Configura el listener para abrir el fragmento de edición
        layoutEditarPerfil.setOnClickListener(v -> abrirCerrarEditarPerfilFragment());

        // Configura el listener para salir de la aplicación
        layoutSalir.setOnClickListener(v -> salirDeLaApp());

        return view;
    }

    private void cargarDatosPerfil() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            DocumentReference docRef = db.collection("usuarios").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nombre = documentSnapshot.getString("nombre");
                    String apellidos = documentSnapshot.getString("apellidos");
                    String correo = documentSnapshot.getString("email");
                    String urlFoto = documentSnapshot.getString("fotoPerfilUrl");

                    textViewNombre.setText(nombre != null ? nombre + " " + apellidos : "Nombre no disponible");
                    textViewCorreo.setText(correo != null ? correo : "Correo no disponible");

                    if (urlFoto != null && !urlFoto.isEmpty()) {
                        // Verificar y obtener el URL de descarga desde Firebase Storage
                        FirebaseStorage.getInstance().getReferenceFromUrl(urlFoto).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    // Cargar la imagen en ImageView usando Picasso con el enlace de descarga
                                    Picasso.get()
                                            .load(uri.toString())
                                            .placeholder(R.drawable.userimage) // Imagen temporal mientras carga
                                            .error(R.drawable.userimage) // Imagen de error si falla
                                            .into(imagenPerfil);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase Storage", "Error al obtener URL de descarga", e);
                                    imagenPerfil.setImageResource(R.drawable.userimage);
                                });
                    } else {
                        imagenPerfil.setImageResource(R.drawable.userimage);
                    }
                } else {
                    textViewNombre.setText("No se encontraron datos");
                    textViewCorreo.setText("");
                }
            }).addOnFailureListener(e -> {
                textViewNombre.setText("Error al cargar nombre");
                textViewCorreo.setText("Error al cargar correo");
                Log.e("Firebase", "Error al obtener datos del perfil", e);
            });
        } else {
            textViewNombre.setText("Usuario no autenticado");
            textViewCorreo.setText("");
        }
    }

    private boolean isFragmentVisible = false; // Variable para controlar la visibilidad del fragmento

    private void abrirCerrarEditarPerfilFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();

        if (isFragmentVisible) {
            // Si el fragmento está visible, lo cerramos
            Fragment fragment = fragmentManager.findFragmentById(R.id.layoutEditarPerfil);
            if (fragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(fragment); // Quitamos el fragmento
                transaction.commit();
            }
            isFragmentVisible = false; // Cambiamos el estado
        } else {
            // Si el fragmento no está visible, lo abrimos
            EditarPerfilFragment editarPerfilFragment = new EditarPerfilFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutEditarPerfil, editarPerfilFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            isFragmentVisible = true; // Cambiamos el estado
        }
    }

    // Método para salir de la aplicación
    private void salirDeLaApp() {
        Toast.makeText(getContext(), "Saliendo de la aplicación...", Toast.LENGTH_SHORT).show();
        getActivity().finishAffinity();  // Cierra todas las actividades de la aplicación
    }
}