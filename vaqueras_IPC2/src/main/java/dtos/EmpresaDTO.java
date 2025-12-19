/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author jeffm
 */
public class EmpresaDTO {
    private Integer idEmpresa;
    private String nombre;
    private String descripcion;
    private Integer cantidadJuegos;
    
    public EmpresaDTO() {}
    
    public EmpresaDTO(Integer idEmpresa, String nombre, String descripcion) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Integer getCantidadJuegos() { return cantidadJuegos; }
    public void setCantidadJuegos(Integer cantidadJuegos) { 
        this.cantidadJuegos = cantidadJuegos; 
    }
}
