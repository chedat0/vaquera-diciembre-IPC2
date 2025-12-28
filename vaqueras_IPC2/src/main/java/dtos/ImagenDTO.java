/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;


/**
 *
 * @author jeffm
 */
public class ImagenDTO {
    private int idImagen;
    private String nombreArchivo;
    private String tipoMime;
    private int tamanoBytes;
    private boolean esPortada;
    private String urlDescarga; // URL para descargar la imagen
    
    public ImagenDTO() {
    }

    public ImagenDTO(int idImagen, String nombreArchivo, String tipoMime, 
                         int tamanoBytes, boolean esPortada, String urlDescarga) {
        this.idImagen = idImagen;
        this.nombreArchivo = nombreArchivo;
        this.tipoMime = tipoMime;
        this.tamanoBytes = tamanoBytes;
        this.esPortada = esPortada;
        this.urlDescarga = urlDescarga;
    }
    
    public int getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(int idImagen) {
        this.idImagen = idImagen;
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

    public String getUrlDescarga() {
        return urlDescarga;
    }

    public void setUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
    }
}
