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
    private ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson;
        
    @Override
    public void init() {
        // Inicializar en el método init() que el contenedor llama automáticamente
        objectMapper = new ObjectMapper();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
                      
        try {
            LoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            
            // Validaciones
            if (loginDTO.getCorreo() == null || loginDTO.getCorreo().trim().isEmpty() ||
                loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
                
                VerificadorDTO respuesta = new VerificadorDTO(false, "Correo y contraseña requeridos", null);
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
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                
                VerificadorDTO respuesta = new VerificadorDTO(true, "Login exitoso", usuario);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Credenciales incorrectas", null);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
                
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        configurarCORS(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
    
}
