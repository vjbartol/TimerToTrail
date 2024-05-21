package com.example.timetotrail.proyect.model;

import java.io.Serializable;

public class Ruta implements Serializable {
    private String nombre, poblacion, dificultad, urlMapa, distancia, desnivel, alturaMax, alturaMin, id;

    public Ruta(String nombre, String poblacion, String dificultad, String urlMapa, String distancia, String desnivel, String alturaMax, String alturaMin, String id) {
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.dificultad = dificultad;
        this.urlMapa = urlMapa;
        this.distancia = distancia;
        this.desnivel = desnivel;
        this.alturaMax = alturaMax;
        this.alturaMin = alturaMin;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getUrlMapa() {
        return urlMapa;
    }

    public void setUrlMapa(String urlMapa) {
        this.urlMapa = urlMapa;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getDesnivel() {
        return desnivel;
    }

    public void setDesnivel(String desnivel) {
        this.desnivel = desnivel;
    }

    public String getAlturaMax() {
        return alturaMax;
    }

    public void setAlturaMax(String alturaMax) {
        this.alturaMax = alturaMax;
    }

    public String getAlturaMin() {
        return alturaMin;
    }

    public void setAlturaMin(String alturaMin) {
        this.alturaMin = alturaMin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "nombre='" + nombre + '\'' +
                ", poblacion='" + poblacion + '\'' +
                ", dificultad='" + dificultad + '\'' +
                ", urlMapa='" + urlMapa + '\'' +
                ", distancia='" + distancia + '\'' +
                ", desnivel='" + desnivel + '\'' +
                ", alturaMax='" + alturaMax + '\'' +
                ", alturaMin='" + alturaMin + '\'' +
                '}';
    }
}
