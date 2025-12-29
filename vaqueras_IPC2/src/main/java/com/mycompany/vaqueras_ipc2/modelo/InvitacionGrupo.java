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
public class InvitacionGrupo {
    private int idInvitacion;
    private int idGrupo;
    private int idUsuarioInvitado;
    private int idUsuarioInvitador;
    private EstadoInvitacion estado;
    private LocalDate fechaInvitacion;      
    private String nombreGrupo;
    private String nombreInvitador;
    private String nombreInvitado;

    public enum EstadoInvitacion {
        PENDIENTE, ACEPTADA, RECHAZADA
    }

    public InvitacionGrupo() {
        this.estado = EstadoInvitacion.PENDIENTE;
    }

    public InvitacionGrupo(int idGrupo, int idUsuarioInvitado, int idUsuarioInvitador, 
                          LocalDate fechaInvitacion) {
        this.idGrupo = idGrupo;
        this.idUsuarioInvitado = idUsuarioInvitado;
        this.idUsuarioInvitador = idUsuarioInvitador;
        this.fechaInvitacion = fechaInvitacion;
        this.estado = EstadoInvitacion.PENDIENTE;
    }
    
    public int getIdInvitacion() {
        return idInvitacion;
    }

    public void setIdInvitacion(int idInvitacion) {
        this.idInvitacion = idInvitacion;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdUsuarioInvitado() {
        return idUsuarioInvitado;
    }

    public void setIdUsuarioInvitado(int idUsuarioInvitado) {
        this.idUsuarioInvitado = idUsuarioInvitado;
    }

    public int getIdUsuarioInvitador() {
        return idUsuarioInvitador;
    }

    public void setIdUsuarioInvitador(int idUsuarioInvitador) {
        this.idUsuarioInvitador = idUsuarioInvitador;
    }

    public EstadoInvitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoInvitacion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInvitacion() {
        return fechaInvitacion;
    }

    public void setFechaInvitacion(LocalDate fechaInvitacion) {
        this.fechaInvitacion = fechaInvitacion;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNombreInvitador() {
        return nombreInvitador;
    }

    public void setNombreInvitador(String nombreInvitador) {
        this.nombreInvitador = nombreInvitador;
    }

    public String getNombreInvitado() {
        return nombreInvitado;
    }

    public void setNombreInvitado(String nombreInvitado) {
        this.nombreInvitado = nombreInvitado;
    }
}
