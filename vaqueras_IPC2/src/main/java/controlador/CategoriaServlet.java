/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

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

@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {
    private CategoriaServicio categoriaServicio;
    private Gson gson;
    
    @Override
    public void init() {
        categoriaServicio = new CategoriaServicio();
        gson = new Gson();
        System.out.println("CategoriaServlet inicializado correctamente");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String activasParam = request.getParameter("activas");
            
            if (idParam != null && !idParam.trim().isEmpty()) {
                // Buscar por ID con validación
                try {
                    Integer id = Integer.parseInt(idParam);
                    
                    if (id <= 0) {
                        VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser mayor a 0", null);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write(gson.toJson(respuesta));
                        return;
                    }
                    
                    CategoriaDTO categoria = categoriaServicio.buscarPorId(id);
                    
                    if (categoria != null) {
                        VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría encontrada", categoria);
                        response.getWriter().write(gson.toJson(respuesta));
                    } else {
                        VerificadorDTO respuesta = new VerificadorDTO(false, "Categoría no encontrada", null);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write(gson.toJson(respuesta));
                    }
                    
                } catch (NumberFormatException e) {
                    System.err.println("Error: ID inválido - " + idParam);
                    VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser un número válido", null);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(respuesta));
                }
                
            } else if ("true".equals(activasParam)) {
                // Listar solo categorías activas
                List<CategoriaDTO> categorias = categoriaServicio.listarActivas();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categorías activas obtenidas", categorias);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                // Listar todas las categorías
                List<CategoriaDTO> categorias = categoriaServicio.listarTodas();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categorías obtenidas", categorias);
                response.getWriter().write(gson.toJson(respuesta));
            }
            
        } catch (Exception e) {
            System.err.println("Error en doGet: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
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
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            try {
                Integer id = Integer.parseInt(idParam);
                
                if (id <= 0) {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser mayor a 0", null);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(respuesta));
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
}
