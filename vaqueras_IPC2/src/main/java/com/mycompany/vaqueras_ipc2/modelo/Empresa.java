/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

/**
 *
 * @author jeffm
 */
public class Empresa {
    private Integer idEmpresa;
    private String nombre;
    private String descripcion;
        
    public Empresa() {}
    
    public Empresa(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
      
    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
