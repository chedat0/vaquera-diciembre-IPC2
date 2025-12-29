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
public class Comentario {
    private int idComentario;
    private int idJuego;
    private int idUsuario;
    private String contenido;
    private int calificacion;
    private LocalDate fecha;
    private boolean visible;
    private String tituloJuego;
    private String nombreUsuario;
    private int cantidadRespuestas;

    public Comentario() {
        this.visible = true;
    }
    
    public Comentario(int idJuego, int idUsuario, String contenido, 
                     int calificacion, LocalDate fecha) {
        this.idJuego = idJuego;
        this.idUsuario = idUsuario;
        this.contenido = contenido;
        this.calificacion = calificacion;
        this.fecha = fecha;
        this.visible = true;
    }
    
    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getCantidadRespuestas() {
        return cantidadRespuestas;
    }

    public void setCantidadRespuestas(int cantidadRespuestas) {
        this.cantidadRespuestas = cantidadRespuestas;
    }
}
