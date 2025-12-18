/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import dtos.JuegoDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Juego;
import servicios.JuegoServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jeffm
 */

@WebServlet("/Juegos")
public class JuegoServlet extends HttpServlet {    
    private JuegoServicio juegoServicio;
    private Gson gson;
    
    @Override
    public void init() {
        juegoServicio = new JuegoServicio();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String empresaParam = request.getParameter("idEmpresa");
            String tituloParam = request.getParameter("titulo");
            
            if (idParam != null) {
                // Buscar por ID
                Integer id = Integer.parseInt(idParam);
                JuegoDTO juego = juegoServicio.buscarPorId(id);
                
                if (juego != null) {
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Juego encontrado", juego);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Juego no encontrado", null);
                    response.getWriter().write(gson.toJson(respuesta));
                }
                
            } else if (empresaParam != null) {
                // Listar por empresa
                Integer idEmpresa = Integer.parseInt(empresaParam);
                List<JuegoDTO> juegos = juegoServicio.listarPorEmpresa(idEmpresa);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos obtenidos", juegos);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else if (tituloParam != null) {
                // Buscar por título
                List<JuegoDTO> juegos = juegoServicio.buscarPorTitulo(tituloParam);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos encontrados", juegos);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                // Listar todos
                List<JuegoDTO> juegos = juegoServicio.listarTodos();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos obtenidos", juegos);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            BufferedReader reader = request.getReader();
            Juego juego = gson.fromJson(reader, Juego.class);
            
            boolean creado = juegoServicio.crear(juego);
            
            if (creado) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego creado exitosamente", juego);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al crear juego", null);
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            BufferedReader reader = request.getReader();
            Juego juego = gson.fromJson(reader, Juego.class);
            
            boolean actualizado = juegoServicio.actualizar(juego);
            
            if (actualizado) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego actualizado exitosamente", null);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al actualizar juego", null);
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                boolean desactivado = juegoServicio.desactivarVenta(id);
                
                if (desactivado) {
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Venta desactivada", null);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Error al desactivar", null);
                    response.getWriter().write(gson.toJson(respuesta));
                }
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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
