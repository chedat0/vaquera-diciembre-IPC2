/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author jeffm
 */
public class InvitacionDTO {
    private Integer idInvitacion;
    private Integer idGrupo;
    private Integer idUsuarioInvitado;
    private Integer idUsuarioInvitador;
    private String estado; 
    private String fechaInvitacion;    
    private String nombreGrupo;
    private String nombreInvitador;
    private String nombreInvitado;

    public InvitacionDTO() {
    }

    public Integer getIdInvitacion() {
        return idInvitacion;
    }

    public void setIdInvitacion(Integer idInvitacion) {
        this.idInvitacion = idInvitacion;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Integer getIdUsuarioInvitado() {
        return idUsuarioInvitado;
    }

    public void setIdUsuarioInvitado(Integer idUsuarioInvitado) {
        this.idUsuarioInvitado = idUsuarioInvitado;
    }

    public Integer getIdUsuarioInvitador() {
        return idUsuarioInvitador;
    }

    public void setIdUsuarioInvitador(Integer idUsuarioInvitador) {
        this.idUsuarioInvitador = idUsuarioInvitador;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaInvitacion() {
        return fechaInvitacion;
    }

    public void setFechaInvitacion(String fechaInvitacion) {
        this.fechaInvitacion = fechaInvitacion;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNombreInvitador() {
        return nombreInvitador;
    }

    public void setNombreInvitador(String nombreInvitador) {
        this.nombreInvitador = nombreInvitador;
    }

    public String getNombreInvitado() {
        return nombreInvitado;
    }

    public void setNombreInvitado(String nombreInvitado) {
        this.nombreInvitado = nombreInvitado;
    }
}
