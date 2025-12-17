/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import daos.UsuarioDAO;
import dtos.UsuarioDTO;
import com.mycompany.vaqueras_ipc2.modelo.Usuario;
import com.mycompany.vaqueras_ipc2.modelo.Encriptar; 
/**
 *
 * @author jeffm
 */
public class UsuarioServicio {
     private UsuarioDAO usuarioDAO;
    
    public UsuarioServicio() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public boolean registrar(Usuario usuario) {
        
        try {
            //Permite encriptar la contraseña antes de guardarla
            String passwordEncriptada = Encriptar.hashPassword(usuario.getPassword());
            usuario.setPassword(passwordEncriptada);
            
            //aqui la guarda y envia a la base de datos
            return usuarioDAO.crearUsuario(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Autenticar usuario
    public UsuarioDTO autenticar(String correo, String password) {
        
        try {
            Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

            if (usuario == null) {
                return null; // Usuario no encontrado
            }     
            
            // Verificar contraseña
            if (Encriptar.checkPassword(password, usuario.getPassword())) {
                // Contraseña correcta - crear DTO sin password
                return new UsuarioDTO(
                        usuario.getIdUsuario(),
                        usuario.getNickname(),
                        usuario.getCorreo(),
                        usuario.getPais(),
                        usuario.getIdRol()
                );
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }                                    
    }
    
    public boolean existeCorreo(String correo) {
        return usuarioDAO.existeCorreo(correo);
    }
}
