/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

/**
 *
 * @author jeffm
 */
public class GrupoMiembro {
    private int idGrupo;
    private int idUsuario;
    private String rol; // creador o miembro       
    private String nombreUsuario;
    private String nombreGrupo;

    public GrupoMiembro() {
    }

    public GrupoMiembro(int idGrupo, int idUsuario, String rol) {
        this.idGrupo = idGrupo;
        this.idUsuario = idUsuario;
        this.rol = rol;
    }
    
    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }
}
