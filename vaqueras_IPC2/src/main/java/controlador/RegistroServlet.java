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
    public void init() {
        usuarioServicio = new UsuarioServicio();
        gson = new Gson();
        System.out.println("RegistroServlet inicializado correctamente");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Respuesta informativa
        VerificadorDTO respuesta = new VerificadorDTO(
            false, 
            "Endpoint de registro. Usa POST con: nickname, correo, password, fechaNacimiento, pais", 
            null
        );
        
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(respuesta));
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
               
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    
        try {
            // Leer el body del request
            BufferedReader reader = request.getReader();
            Usuario usuario = gson.fromJson(reader, Usuario.class);
            
            // Usuario no null
            if (usuario == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de usuario inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Nickname
            if (usuario.getNickname() == null || usuario.getNickname().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nickname es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (usuario.getNickname().length() < 3) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nickname debe tener al menos 3 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (usuario.getNickname().length() > 50) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nickname no puede exceder 50 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            //  Correo
            if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El correo es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Validar formato de correo
            if (!usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El formato del correo es inválido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (usuario.getCorreo().length() > 100) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El correo no puede exceder 100 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Password
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La contraseña es requerida", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (usuario.getPassword().length() < 4) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La contraseña debe tener al menos 4 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Fecha de nacimiento
            if (usuario.getFechaNacimiento() == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La fecha de nacimiento es requerida", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Validar que la fecha no sea futura
            if (usuario.getFechaNacimiento().isAfter(LocalDate.now())) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La fecha de nacimiento no puede ser futura", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Validar edad mínima 
            LocalDate hace13Anios = LocalDate.now().minusYears(12);
            if (usuario.getFechaNacimiento().isAfter(hace13Anios)) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Debes tener al menos 12 años para registrarte", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Validar edad máxima 
            LocalDate hace120Anios = LocalDate.now().minusYears(120);
            if (usuario.getFechaNacimiento().isBefore(hace120Anios)) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La fecha de nacimiento no es válida", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // País
            if (usuario.getPais() == null || usuario.getPais().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El país es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (usuario.getPais().length() > 50) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El país no puede exceder 50 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Teléfono 
            if (usuario.getTelefono() != null && !usuario.getTelefono().trim().isEmpty()) {
                if (usuario.getTelefono().length() < 8 || usuario.getTelefono().length() > 15) {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "El teléfono debe tener entre 8 y 15 dígitos", null);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(respuesta));
                    return;
                }
            }
            
            // Verificar si el correo ya existe
            if (usuarioServicio.existeCorreo(usuario.getCorreo())) {
                System.out.println("Intento de registro con correo existente: " + usuario.getCorreo());
                VerificadorDTO respuesta = new VerificadorDTO(false, "El correo ya está registrado", null);
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Asignar rol por defecto (GAMER = 3)
            if (usuario.getIdRol() == null) {
                usuario.setIdRol(3);
            }
            
            // REGISTRAR 
            boolean registrado = usuarioServicio.registrar(usuario);
            
            if (registrado) {
                System.out.println("Usuario registrado exitosamente: " + usuario.getCorreo());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Usuario registrado exitosamente", null);
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                System.err.println("Error al registrar usuario: " + usuario.getCorreo());
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al registrar usuario", null);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));
            
        } catch (java.time.DateTimeException e) {
            System.err.println("Error: Fecha inválida - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "El formato de la fecha es inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));
            
        } catch (Exception e) {
            System.err.println("Error inesperado en registro: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error interno del servidor", null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }
        
}
