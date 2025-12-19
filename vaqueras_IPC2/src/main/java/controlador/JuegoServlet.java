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

@WebServlet("/juegos")
public class JuegoServlet extends HttpServlet {    
    private JuegoServicio juegoServicio;
    private Gson gson;
    
    @Override
    public void init() {
        juegoServicio = new JuegoServicio();
        gson = new Gson();
        System.out.println("JuegoServlet inicializado correctamente");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
              
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String empresaParam = request.getParameter("idEmpresa");
            String tituloParam = request.getParameter("titulo");
            
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
                    
                    JuegoDTO juego = juegoServicio.buscarPorId(id);
                    
                    if (juego != null) {
                        VerificadorDTO respuesta = new VerificadorDTO(true, "Juego encontrado", juego);
                        response.getWriter().write(gson.toJson(respuesta));
                    } else {
                        VerificadorDTO respuesta = new VerificadorDTO(false, "Juego no encontrado", null);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write(gson.toJson(respuesta));
                    }
                    
                } catch (NumberFormatException e) {
                    System.err.println("Error: ID inválido - " + idParam);
                    VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser un número válido", null);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(respuesta));
                }
                
            } else if (empresaParam != null && !empresaParam.trim().isEmpty()) {
                // Listar por empresa con validación
                try {
                    Integer idEmpresa = Integer.parseInt(empresaParam);
                    
                    if (idEmpresa <= 0) {
                        VerificadorDTO respuesta = new VerificadorDTO(false, "ID de empresa debe ser mayor a 0", null);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write(gson.toJson(respuesta));
                        return;
                    }
                    
                    List<JuegoDTO> juegos = juegoServicio.listarPorEmpresa(idEmpresa);
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos obtenidos", juegos);
                    response.getWriter().write(gson.toJson(respuesta));
                    
                } catch (NumberFormatException e) {
                    System.err.println("Error: ID de empresa inválido - " + empresaParam);
                    VerificadorDTO respuesta = new VerificadorDTO(false, "ID de empresa debe ser un número válido", null);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(respuesta));
                }
                
            } else if (tituloParam != null && !tituloParam.trim().isEmpty()) {
                // Buscar por título
                List<JuegoDTO> juegos = juegoServicio.buscarPorTitulo(tituloParam);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Búsqueda completada", juegos);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                // Listar todos
                List<JuegoDTO> juegos = juegoServicio.listarTodos();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos obtenidos", juegos);
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
            Juego juego = gson.fromJson(reader, Juego.class);
            
            // Validaciones
            if (juego == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de juego inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (juego.getIdEmpresa() == null || juego.getIdEmpresa() <= 0) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID de empresa es requerido y debe ser válido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (juego.getTitulo() == null || juego.getTitulo().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El título es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (juego.getTitulo().length() > 200) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El título no puede exceder 200 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (juego.getPrecio() < 0) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El precio no puede ser negativo", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Crear juego
            boolean creado = juegoServicio.crear(juego);
            
            if (creado) {
                System.out.println("Juego creado: " + juego.getTitulo());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego creado exitosamente", juego);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al crear juego", null);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            Juego juego = gson.fromJson(reader, Juego.class);
            
            // Validaciones
            if (juego == null || juego.getIdJuego() == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID de juego es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (juego.getTitulo() == null || juego.getTitulo().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El título es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Actualizar juego
            boolean actualizado = juegoServicio.actualizar(juego);
            
            if (actualizado) {
                System.out.println("Juego actualizado: " + juego.getTitulo());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego actualizado exitosamente", null);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al actualizar juego", null);
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
                
                // Desactivar venta (soft delete)
                boolean desactivado = juegoServicio.desactivarVenta(id);
                
                if (desactivado) {
                    System.out.println("Venta desactivada para juego ID: " + id);
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Venta desactivada exitosamente", null);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Error al desactivar venta", null);
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
