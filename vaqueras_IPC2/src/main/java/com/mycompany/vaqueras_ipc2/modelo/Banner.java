/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

import java.time.LocalDate;
/**
 *
 * @author jeffm
 */
public class Banner {
     private int idBanner;
    private int idJuego;
    private int posicion; 
    private boolean activo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin; 
    private String tituloJuego;
    private String descripcionJuego;

    public Banner() {
        this.activo = true;
    }

    public Banner(int idJuego, int posicion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idJuego = idJuego;
        this.posicion = posicion;
        this.activo = true;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    public int getIdBanner() {
        return idBanner;
    }

    public void setIdBanner(int idBanner) {
        this.idBanner = idBanner;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public String getDescripcionJuego() {
        return descripcionJuego;
    }

    public void setDescripcionJuego(String descripcionJuego) {
        this.descripcionJuego = descripcionJuego;
    }
}
