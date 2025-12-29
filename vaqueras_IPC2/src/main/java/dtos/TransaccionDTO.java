/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author jeffm
 */
public class TransaccionDTO {
    private int idTransaccion;
    private int idUsuario;
    private double monto;
    private String tipo; // "RECARGA" o "COMPRA"
    private String fecha;
    private double comisionAplicada;
    private double gananciaEmpresa;
    private double gananciaPlataforma;        
    private String tituloJuego;
    
    public TransaccionDTO(){        
    }
    
    public int getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Integer idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getComisionAplicada() {
        return comisionAplicada;
    }

    public void setComisionAplicada(double comisionAplicada) {
        this.comisionAplicada = comisionAplicada;
    }

    public double getGananciaEmpresa() {
        return gananciaEmpresa;
    }

    public void setGananciaEmpresa(double gananciaEmpresa) {
        this.gananciaEmpresa = gananciaEmpresa;
    }

    public double getGananciaPlataforma() {
        return gananciaPlataforma;
    }

    public void setGananciaPlataforma(double gananciaPlataforma) {
        this.gananciaPlataforma = gananciaPlataforma;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }
}
