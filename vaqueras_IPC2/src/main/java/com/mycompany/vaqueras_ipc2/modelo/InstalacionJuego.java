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
public class InstalacionJuego {
    private int idUsuario;
    private int idJuego;
    private boolean esPrestado;
    private EstadoInstalacion estado;
    private LocalDate fechaEstado;     
    private String tituloJuego;
    private String nombreUsuario;
    private int idPropietario; // si el juego es prestado 

    public enum EstadoInstalacion {
        INSTALADO, NO_INSTALADO
    }

    public InstalacionJuego() {
    }

    public InstalacionJuego(int idUsuario, int idJuego, boolean esPrestado, 
                           EstadoInstalacion estado, LocalDate fechaEstado) {
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.esPrestado = esPrestado;
        this.estado = estado;
        this.fechaEstado = fechaEstado;
    }
   
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public boolean isEsPrestado() {
        return esPrestado;
    }

    public void setEsPrestado(boolean esPrestado) {
        this.esPrestado = esPrestado;
    }

    public EstadoInstalacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoInstalacion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaEstado() {
        return fechaEstado;
    }

    public void setFechaEstado(LocalDate fechaEstado) {
        this.fechaEstado = fechaEstado;
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

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }
}
