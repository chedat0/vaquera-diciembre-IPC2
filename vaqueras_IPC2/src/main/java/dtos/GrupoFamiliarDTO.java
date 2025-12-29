/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.util.List;
/**
 *
 * @author jeffm
 */
public class GrupoFamiliarDTO {
    private Integer idGrupo;
    private String nombre;
    private Integer idCreador;
    private String nombreCreador;
    private Integer cantidadMiembros;
    private List<MiembroDTO> miembros;

    public GrupoFamiliarDTO() {
    }
    
    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(Integer idCreador) {
        this.idCreador = idCreador;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }

    public Integer getCantidadMiembros() {
        return cantidadMiembros;
    }

    public void setCantidadMiembros(Integer cantidadMiembros) {
        this.cantidadMiembros = cantidadMiembros;
    }

    public List<MiembroDTO> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<MiembroDTO> miembros) {
        this.miembros = miembros;
    }
}
