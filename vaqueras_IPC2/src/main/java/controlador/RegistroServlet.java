/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Usuario;
import servicios.UsuarioServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.BufferedReader;
import java.time.LocalDate;

/**
 *
 * @author jeffm
 */

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
    private UsuarioServicio usuarioServicio;
    private Gson gson;
    
    @Override
    public void init(){
        usuarioServicio = new UsuarioServicio();
        gson = new Gson();
    }
    
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        
        //Configuracion CORS
        response.setHeader("Access-Control-Allow-Origin", "https://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    
    try {
            // Leer el body del request
            BufferedReader reader = request.getReader();
            Usuario usuario = gson.fromJson(reader, Usuario.class);
            
            // Validaciones básicas
            if (usuario.getCorreo() == null || usuario.getPassword() == null) {
                VerificadorDTO respuesta = new VerificadorDTO(
                    false, 
                    "Correo y contraseña son requeridos", 
                    null
                );
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Verificar si el correo ya existe
            if (usuarioServicio.existeCorreo(usuario.getCorreo())) {
                VerificadorDTO respuesta = new VerificadorDTO(
                    false, 
                    "El correo ya está registrado", 
                    null
                );
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Por defecto, nuevo usuario es GAMER (id_rol = 3)
            if (usuario.getIdRol() == null) {
                usuario.setIdRol(3);
            }
            
            // REGISTRAR (aquí se encripta automáticamente en el service)
            boolean registrado = usuarioServicio.registrar(usuario);
            
            if (registrado) {
                VerificadorDTO respuesta = new VerificadorDTO(
                    true, 
                    "Usuario registrado exitosamente", 
                    null
                );
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(
                    false, 
                    "Error al registrar usuario", 
                    null
                );
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(
                false, 
                "Error en el servidor: " + e.getMessage(), 
                null
            );
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "https://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
