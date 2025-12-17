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
public class Usuario {
    private int idUsuario;
    private String nickname;
    private String correo;
    private String password;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String pais;
    private Integer idRol;
    
    public Usuario(){}
    
    public Usuario(String nickname, String correo, String password, LocalDate fechaNacimiento, String telefono, String pais, Integer idRol){
        this.nickname = nickname;
        this.correo = correo;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.pais = pais;
        this.idRol = idRol;
    }
    
    // Getters y Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { 
        this.fechaNacimiento = fechaNacimiento; 
    }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    
    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

}
