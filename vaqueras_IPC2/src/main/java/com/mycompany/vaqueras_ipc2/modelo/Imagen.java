/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

import java.sql.Blob;
import java.util.Date;

/**
 *
 * @author jeffm
 */
public class Imagen {
    private int idImagen;
    private int idEntidad; // idJuego, idUsuario, idBanner
    private byte[] datos;
    private String nombreArchivo;
    private String tipoMime;
    private int tamanoBytes;
    private boolean esPortada; // Solo para juegos
    private Date fechaSubida;
    private String tipoEntidad;
  
    public Imagen() {
    }
    
    public Imagen(int idEntidad, byte[] datos, String nombreArchivo, 
                  String tipoMime, String tipoEntidad) {
        this.idEntidad = idEntidad;
        this.datos = datos;
        this.nombreArchivo = nombreArchivo;
        this.tipoMime = tipoMime;
        this.tamanoBytes = datos != null ? datos.length : 0;
        this.tipoEntidad = tipoEntidad;
        this.fechaSubida = new Date();
    }

    public int getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(int idImagen) {
        this.idImagen = idImagen;
    }

    public int getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(int idEntidad) {
        this.idEntidad = idEntidad;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
        this.tamanoBytes = datos != null ? datos.length : 0;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public int getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(int tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public boolean isEsPortada() {
        return esPortada;
    }

    public void setEsPortada(boolean esPortada) {
        this.esPortada = esPortada;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }
}
