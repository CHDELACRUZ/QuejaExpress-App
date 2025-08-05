package com.mundowebsolutions.quejaexpress;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SeguimientoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuejaAdapter adapter;
    private List<Queja> quejaList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento);

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSeguimiento);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        quejaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Cargar datos
        cargarQuejas();
    }

    private void cargarQuejas() {
        Log.d("SeguimientoActivity", "Iniciando carga de quejas...");
        db.collection("Quejas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SeguimientoActivity", "Datos obtenidos correctamente.");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Queja queja = document.toObject(Queja.class);
                    quejaList.add(queja);
                    Log.d("SeguimientoActivity", "Queja cargada: " + queja.getDescripcion());
                }

                if (quejaList.isEmpty()) {
                    Log.d("SeguimientoActivity", "No hay datos para mostrar.");
                }

                // Configurar el adaptador
                adapter = new QuejaAdapter(this, quejaList);
                recyclerView.setAdapter(adapter);
                Log.d("SeguimientoActivity", "Adaptador configurado.");
            } else {
                Log.e("SeguimientoActivity", "Error al cargar quejas", task.getException());
            }
        });
    }
}