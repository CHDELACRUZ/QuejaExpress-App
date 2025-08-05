package com.mundowebsolutions.quejaexpress;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioFragment extends Fragment {
    private RecyclerView recyclerView;
    private QuejaAdapter adapter;
    private List<Queja> quejaList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        quejaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Cargar datos
        cargarQuejas();

        return view;
    }

    private void cargarQuejas() {
        Log.d("InicioFragment", "Iniciando carga de quejas...");
        db.collection("Quejas")
                .orderBy("fechaHora", Query.Direction.DESCENDING) // Ordenar por fecha y hora, descendente
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        quejaList.clear(); // Limpiar la lista para evitar duplicados
                        Log.d("InicioFragment", "Datos obtenidos correctamente.");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Queja queja = document.toObject(Queja.class);
                            queja.setId(document.getId()); // Asignar el ID del documento
                            quejaList.add(queja);
                            Log.d("InicioFragment", "Queja cargada: " + queja.getDescripcion());
                        }

                        if (quejaList.isEmpty()) {
                            Log.d("InicioFragment", "No hay datos para mostrar.");
                        }

                        // Configurar el adaptador
                        if (isAdded()) {
                            adapter = new QuejaAdapter(requireContext(), quejaList);
                            recyclerView.setAdapter(adapter);
                            Log.d("InicioFragment", "Adaptador configurado.");
                        } else {
                            Log.w("InicioFragment", "El fragmento ya no está adjunto.");
                        }
                    } else {
                        Log.e("InicioFragment", "Error al cargar quejas", task.getException());
                    }
                });
    }
}