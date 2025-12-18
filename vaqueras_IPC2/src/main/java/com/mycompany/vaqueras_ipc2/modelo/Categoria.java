/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

/**
 *
 * @author jeffm
 */
public class Categoria {
    private Integer idCategoria;
    private String nombre;
    private Boolean activado;
    
    public Categoria() {}
    
    public Categoria(String nombre) {
        this.nombre = nombre;
        this.activado = true;
    }
        
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Boolean getActivado() { return activado; }
    public void setActivado(Boolean activado) { this.activado = activado; }
}
