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
public class Transaccion {
    private int idTransaccion;
    private int idUsuario;
    private double monto;
    private TipoTransaccion tipo; // RECARGA o COMPRA
    private LocalDate fecha;          
    private Double comisionAplicada;
    private Double gananciaEmpresa;
    private Double gananciaPlataforma;

    public enum TipoTransaccion {
        RECARGA, COMPRA
    }

    public Transaccion() {
    }

    public Transaccion(int idUsuario, double monto, TipoTransaccion tipo, LocalDate fecha) {
        this.idUsuario = idUsuario;
        this.monto = monto;
        this.tipo = tipo;
        this.fecha = fecha;
    }
   
    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getComisionAplicada() {
        return comisionAplicada;
    }

    public void setComisionAplicada(Double comisionAplicada) {
        this.comisionAplicada = comisionAplicada;
    }

    public Double getGananciaEmpresa() {
        return gananciaEmpresa;
    }

    public void setGananciaEmpresa(Double gananciaEmpresa) {
        this.gananciaEmpresa = gananciaEmpresa;
    }

    public Double getGananciaPlataforma() {
        return gananciaPlataforma;
    }

    public void setGananciaPlataforma(Double gananciaPlataforma) {
        this.gananciaPlataforma = gananciaPlataforma;
    }
   
}
