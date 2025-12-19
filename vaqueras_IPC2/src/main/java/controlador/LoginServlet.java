/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import dtos.LoginDTO;
import dtos.VerificadorDTO;
import dtos.UsuarioDTO;
import jakarta.servlet.http.HttpSession;
import servicios.UsuarioServicio;
import java.io.IOException;
import java.io.BufferedReader;

/**
 *
 * @author jeffm
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
      
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private UsuarioServicio usuarioServicio;
    private ObjectMapper objectMapper;
    private Gson gson;
    
    @Override
    public void init() {
        // CORRECCIÓN: Inicializar todos los servicios y utilidades
        usuarioServicio = new UsuarioServicio();
        objectMapper = new ObjectMapper();
        gson = new Gson();
        System.out.println("LoginServlet inicializado correctamente");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Leer el JSON del request
            LoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            
            // VALIDACIONES COMPLETAS
            if (loginDTO == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de login inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (loginDTO.getCorreo() == null || loginDTO.getCorreo().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El correo es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La contraseña es requerida", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Validación de formato de correo
            if (!loginDTO.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Formato de correo inválido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Autenticar
            UsuarioDTO usuario = usuarioServicio.autenticar(
                loginDTO.getCorreo(), 
                loginDTO.getPassword()
            );
            
            if (usuario != null) {
                // Login exitoso - crear sesión
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", usuario);
                session.setMaxInactiveInterval(3600); // 1 hora
                
                System.out.println("✓ Login exitoso: " + usuario.getCorreo());
                
                VerificadorDTO respuesta = new VerificadorDTO(true, "Login exitoso", usuario);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                // Credenciales inválidas
                System.out.println("✗ Login fallido: credenciales incorrectas para " + loginDTO.getCorreo());
                
                VerificadorDTO respuesta = new VerificadorDTO(false, "Correo o contraseña incorrectos", null);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));
            
        } catch (Exception e) {
            System.err.println("Error inesperado en login: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error interno del servidor", null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Verificar si hay sesión activa
            HttpSession session = request.getSession(false);
            
            if (session != null && session.getAttribute("usuario") != null) {
                UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
                VerificadorDTO respuesta = new VerificadorDTO(true, "Sesión activa", usuario);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "No hay sesión activa", null);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (Exception e) {
            System.err.println("Error en verificación de sesión: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error al verificar sesión", null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
               
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Logout - destruir sesión
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
                if (usuario != null) {
                    System.out.println("Logout: " + usuario.getCorreo());
                }
                session.invalidate();
            }
            
            VerificadorDTO respuesta = new VerificadorDTO(true, "Logout exitoso", null);
            response.getWriter().write(gson.toJson(respuesta));
            
        } catch (Exception e) {
            System.err.println("Error en logout: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error al cerrar sesión", null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }
            
}
