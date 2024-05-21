package com.example.timetotrail.proyect.model;


import java.io.Serializable;

public class Quedada implements Serializable {
    private String descripcion, diaHora, lugar, nombreRuta, titulo, id, numUsuarios, email;

    public Quedada(String descripcion, String diaHora, String lugar, String nombreRuta, String titulo, String id, String numUsuarios, String email) {
        this.descripcion = descripcion;
        this.diaHora = diaHora;
        this.lugar = lugar;
        this.nombreRuta = nombreRuta;
        this.titulo = titulo;
        this.numUsuarios = numUsuarios;
        this.id = id;
        this.email = email;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDiaHora() {
        return diaHora;
    }

    public void setDiaHora(String diaHora) {
        this.diaHora = diaHora;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNumUsuarios(String numUsuarios) {this.numUsuarios = numUsuarios;}

    public String getNumUsuarios() {return numUsuarios;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "Quedada{" +
                "descripcion='" + descripcion + '\'' +
                ", diaHora='" + diaHora + '\'' +
                ", lugar='" + lugar + '\'' +
                ", nombreRuta='" + nombreRuta + '\'' +
                ", titulo='" + titulo + '\'' +
                ", id='" + id + '\'' +
                ", numUsuarios='" + numUsuarios + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
