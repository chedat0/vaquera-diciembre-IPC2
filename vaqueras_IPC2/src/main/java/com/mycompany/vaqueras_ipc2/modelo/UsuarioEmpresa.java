/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

import java.time.LocalDate;

/**
 *
 * @author jeffm
 */
public class UsuarioEmpresa {
    private int idUsuario;
    private String correo;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String password;
    private int idEmpresa;
    private int idRol; 
    
    public UsuarioEmpresa() {
        this.idRol = 2; 
    }
    
    public UsuarioEmpresa(String correo, String nombre, LocalDate fechaNacimiento, 
                          String password, int idEmpresa) {
        this.correo = correo;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.password = password;
        this.idEmpresa = idEmpresa;
        this.idRol = 2;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    @Override
    public String toString() {
        return "UsuarioEmpresa{" +
                "idUsuario=" + idUsuario +
                ", correo='" + correo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", idEmpresa=" + idEmpresa +
                '}';
    }
}
