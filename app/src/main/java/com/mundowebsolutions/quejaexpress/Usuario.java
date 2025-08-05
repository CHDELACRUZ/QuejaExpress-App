package com.mundowebsolutions.quejaexpress;

public class Usuario {
    private String id;
    private String nombre;
    private String apellidos;
    private String fotoPerfilUrl;

    public Usuario() {} // Constructor vacío requerido por Firestore

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellido(String apellido) { this.apellidos = apellido; }

    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }
}