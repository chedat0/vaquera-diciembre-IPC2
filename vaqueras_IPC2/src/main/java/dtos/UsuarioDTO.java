/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author jeffm
 */
public class UsuarioDTO {
    private Integer idUsuario;
    private String nickname;
    private String correo;
    private String pais;
    private Integer idRol;
    
    // Constructor, getters y setters
    public UsuarioDTO(Integer idUsuario, String nickname, String correo,String pais, Integer idRol) {
        this.idUsuario = idUsuario;
        this.nickname = nickname;
        this.correo = correo;
        this.pais = pais;
        this.idRol = idRol;
    }
    
    // Getters y setters...
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    
    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }
}
