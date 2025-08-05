package com.mundowebsolutions.quejaexpress;

import com.google.firebase.Timestamp;

public class Queja {
    private String id;
    private String tipoQueja;
    private String descripcion;
    private String ruta;
    private String unidad;
    private String imagenUrl;
    private Timestamp fechaHora; // Aquí está el cambio
    private String userId;

    public Queja() {} // Constructor vacío requerido por Firestore

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipoQueja() { return tipoQueja; }
    public void setTipoQueja(String tipoQueja) { this.tipoQueja = tipoQueja; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
