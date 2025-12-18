/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.time.LocalDate;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class JuegoDTO {
    private Integer idJuego;
    private Integer idEmpresa;
    private String nombreEmpresa;
    private String titulo;
    private String descripcion;
    private String requisitosMinimos;
    private double precio;
    private String clasificacionPorEdad;
    private LocalDate fechaLanzamiento;
    private Boolean ventaActiva;
    private List<String> categorias;
    private String imagenPortada;
    
    // Constructores
    public JuegoDTO() {}
        
    public JuegoDTO(Integer idJuego, Integer idEmpresa, String nombreEmpresa, String titulo,
                    String descripcion, String requisitosMinimos, double precio,
                    String clasificacionPorEdad, LocalDate fechaLanzamiento, Boolean ventaActiva) {
        this.idJuego = idJuego;
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.requisitosMinimos = requisitosMinimos;
        this.precio = precio;
        this.clasificacionPorEdad = clasificacionPorEdad;
        this.fechaLanzamiento = fechaLanzamiento;
        this.ventaActiva = ventaActiva;
    }
    
    // Getters y Setters
    public Integer getIdJuego() { return idJuego; }
    public void setIdJuego(Integer idJuego) { this.idJuego = idJuego; }
    
    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }
    
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getRequisitosMinimos() { return requisitosMinimos; }
    public void setRequisitosMinimos(String requisitosMinimos) { 
        this.requisitosMinimos = requisitosMinimos; 
    }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public String getClasificacionPorEdad() { return clasificacionPorEdad; }
    public void setClasificacionPorEdad(String clasificacionPorEdad) { 
        this.clasificacionPorEdad = clasificacionPorEdad; 
    }
    
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { 
        this.fechaLanzamiento = fechaLanzamiento; 
    }
    
    public Boolean getVentaActiva() { return ventaActiva; }
    public void setVentaActiva(Boolean ventaActiva) { this.ventaActiva = ventaActiva; }
    
    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }
    
    public String getImagenPortada() { return imagenPortada; }
    public void setImagenPortada(String imagenPortada) { this.imagenPortada = imagenPortada; }

}
