/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author jeffm
 */
public class MiembroDTO {
    private Integer idUsuario;
    private String nickname;
    private String rol; // "CREADOR" o "MIEMBRO"

    public MiembroDTO() {
    }

    public MiembroDTO(Integer idUsuario, String nickname, String rol) {
        this.idUsuario = idUsuario;
        this.nickname = nickname;
        this.rol = rol;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
