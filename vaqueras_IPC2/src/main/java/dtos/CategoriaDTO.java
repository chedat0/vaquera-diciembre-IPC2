/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author jeffm
 */
public class CategoriaDTO {
    private Integer idCategoria;
    private String nombre;
    private Boolean activado;
    
    public CategoriaDTO() {}
    
    public CategoriaDTO(Integer idCategoria, String nombre, Boolean activado) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.activado = activado;
    }
    
    // Getters y Setters
    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Boolean getActivado() { return activado; }
    public void setActivado(Boolean activado) { this.activado = activado; }
}
