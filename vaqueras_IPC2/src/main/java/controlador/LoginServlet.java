/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

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
@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
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
    private Gson gson;
    
    @Override
    public void init() {
        usuarioServicio = new UsuarioServicio();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        // Configurar CORS
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Leer el body del request
            BufferedReader reader = request.getReader();
            LoginDTO loginDTO = gson.fromJson(reader, LoginDTO.class);
            
            // Validar datos
            if (loginDTO.getCorreo() == null || loginDTO.getPassword() == null) {
                VerificadorDTO respuesta = new VerificadorDTO(
                    false, 
                    "Correo y contraseña son requeridos", 
                    null
                );
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
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                
                VerificadorDTO respuesta = new VerificadorDTO(
                    true, 
                    "Login exitoso", 
                    usuario
                );
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                // Login fallido
                VerificadorDTO respuesta = new VerificadorDTO(
                    false, 
                    "Correo o contraseña incorrectos", 
                    null
                );
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

    
}
