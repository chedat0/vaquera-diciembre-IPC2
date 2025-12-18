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
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String activasParam = request.getParameter("activas");
            
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                CategoriaDTO categoria = categoriaServicio.buscarPorId(id);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría encontrada", categoria);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else if ("true".equals(activasParam)) {
                List<CategoriaDTO> categorias = categoriaServicio.listarActivas();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categorías activas", categorias);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                List<CategoriaDTO> categorias = categoriaServicio.listarTodas();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categorías obtenidas", categorias);
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
            Categoria categoria = gson.fromJson(reader, Categoria.class);
            
            boolean creada = categoriaServicio.crear(categoria);
            
            if (creada) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría creada", categoria);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "La categoría ya existe", null);
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
            Categoria categoria = gson.fromJson(reader, Categoria.class);
            
            boolean actualizada = categoriaServicio.actualizar(categoria);
            
            if (actualizada) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría actualizada", null);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al actualizar", null);
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
                boolean eliminada = categoriaServicio.eliminar(id);

                if (eliminada) {
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Categoría eliminada", null);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Error al eliminar", null);
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
