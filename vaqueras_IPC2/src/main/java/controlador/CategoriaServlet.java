/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dtos.CategoriaDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Categoria;
import servicios.CategoriaServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.websocket.server.ServerEndpointConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jeffm
 */

@WebServlet("/categorias/*")
public class CategoriaServlet extends HttpServlet {
    private CategoriaServicio categoriaServicio;
    private ObjectMapper mapper;
    private Gson gson;
    
    @Override
    public void init() {
        categoriaServicio = new CategoriaServicio();
        gson = new Gson();
        mapper = new ObjectMapper();
        System.out.println("CategoriaServlet inicializado correctamente");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /categorias 
                listarTodas(response);
                return;
            }
            
            String[] parts = pathInfo.split("/");
            String segment1 = parts.length > 1 ? parts[1] : "";
            
            switch (segment1) {
                case "activas":
                    // GET /categorias/activas
                    listarActivas(response);
                    break;
                    
                default:
                    // GET /categorias/{id}
                    buscarPorId(segment1, response);
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("Error en doGet: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                       "Error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            BufferedReader reader = request.getReader();
            Categoria categoria = gson.fromJson(reader, Categoria.class);
            
            // Validaciones
            if (categoria == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de categoría inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (categoria.getNombre().length() > 100) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre no puede exceder 100 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Crear categoría
            boolean creada = categoriaServicio.crear(categoria);
            
            if (creada) {
                System.out.println("Categoría creada: " + categoria.getNombre());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría creada exitosamente", categoria);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La categoría ya existe", null);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));
            
        } catch (Exception e) {
            System.err.println("Error en doPost: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID es requerido en la URL");
                return;
            }
            
            String[] parts = pathInfo.split("/");
            String idParam = parts.length > 1 ? parts[1] : "";
            
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser un número válido");
                return;
            }
            BufferedReader reader = request.getReader();
            Categoria categoria = gson.fromJson(reader, Categoria.class);
            
            // Validaciones
            if (categoria == null || categoria.getIdCategoria() == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID de categoría es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (categoria.getNombre().length() > 100) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre no puede exceder 100 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Actualizar categoría
            boolean actualizada = categoriaServicio.actualizar(categoria);
            
            if (actualizada) {
                System.out.println("Categoría actualizada: " + categoria.getNombre());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría actualizada exitosamente", null);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al actualizar categoría", null);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));
            
        } catch (Exception e) {
            System.err.println("Error en doPut: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
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
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID es requerido en la URL");
                return;
            }
            
            String[] parts = pathInfo.split("/");
            String idParam = parts.length > 1 ? parts[1] : "";
            
            try {
                Integer id = Integer.parseInt(idParam);
                
                if (id <= 0) {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser mayor a 0");
                    return;
                }

                boolean eliminada = categoriaServicio.eliminar(id);

                if (eliminada) {
                    System.out.println("Categoría eliminada: ID " + id);
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría eliminada", null);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Error al eliminar categoría", null);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(gson.toJson(respuesta));
                }
                
            } catch (NumberFormatException e) {
                System.err.println("Error: ID inválido - " + idParam);
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser un número válido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
            }

        } catch (Exception e) {
            System.err.println("Error en doDelete: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }  
    
    private void listarTodas(HttpServletResponse response) throws IOException {
        List<CategoriaDTO> categorias = categoriaServicio.listarTodas();
        VerificadorDTO respuesta = new VerificadorDTO(true, "Categorías obtenidas", categorias);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
    
    private void listarActivas(HttpServletResponse response) throws IOException {
        List<CategoriaDTO> categorias = categoriaServicio.listarActivas();
        VerificadorDTO respuesta = new VerificadorDTO(true, "Categorías activas obtenidas", categorias);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
    
    private void buscarPorId(String idParam, HttpServletResponse response) throws IOException {
        try {
            Integer id = Integer.parseInt(idParam);
            
            if (id <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser mayor a 0");
                return;
            }
            
            CategoriaDTO categoria = categoriaServicio.buscarPorId(id);
            
            if (categoria != null) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría encontrada", categoria);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
            } else {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, "Categoría no encontrada");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser un número válido");
        }
    }
    
    private void enviarError(HttpServletResponse response, int status, String mensaje) 
            throws IOException {
        VerificadorDTO respuesta = new VerificadorDTO(false, mensaje, null);
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
}
