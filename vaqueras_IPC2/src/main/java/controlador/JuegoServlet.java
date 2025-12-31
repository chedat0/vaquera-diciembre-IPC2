/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import otros.JacksonConfig;
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

@WebServlet("/juegos/*")
public class JuegoServlet extends HttpServlet {    
    private JuegoServicio juegoServicio;
    private ObjectMapper mapper;
    
    @Override
    public void init() {
        juegoServicio = new JuegoServicio();
        mapper = JacksonConfig.createMapper();
        System.out.println("JuegoServlet inicializado correctamente");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
              
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Obtener la ruta después de /juegos
            String pathInfo = request.getPathInfo();
          
            if (pathInfo == null || pathInfo.equals("/")) {
                // Verificar si hay parámetros de búsqueda
                String tituloParam = request.getParameter("titulo");

                if (tituloParam != null && !tituloParam.trim().isEmpty()) {
                    // GET /juegos?titulo
                    buscarPorTitulo(tituloParam, response);
                } else {                    
                    listarTodos(response);
                }
                return;
            }

            // Parsear la ruta
            String[] parts = pathInfo.split("/");
            
            String s1 = parts.length > 1 ? parts[1] : "";
            String s2 = parts.length > 2 ? parts[2] : "";

            // Manejar rutas específicas
            switch (s1) {
                case "activos":
                    // GET /juegos/activos → Listar solo activos
                    listarActivos(response);
                    break;

                case "empresa":
                    // GET /juegos/empresa/{id} → Listar por empresa
                    if (s2.isEmpty()) {
                        enviarError(response, HttpServletResponse.SC_BAD_REQUEST,
                                "ID de empresa es requerido");
                    } else {
                        listarPorEmpresa(s2, response);
                    }
                    break;

                default:
                    // GET /juegos/{id} → Buscar por ID
                    buscarPorId(s1, response);
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
            Juego juego = mapper.readValue(request.getReader(), Juego.class);
            
            // Validaciones
            if (juego == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de juego inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
                return;
            }
            
            if (juego.getIdEmpresa() == null || juego.getIdEmpresa() <= 0) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID de empresa es requerido y debe ser válido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
                return;
            }
            
            if (juego.getTitulo() == null || juego.getTitulo().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El título es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
                return;
            }
            
            if (juego.getTitulo().length() > 200) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El título no puede exceder 200 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
                return;
            }
            
            if (juego.getPrecio() < 0) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El precio no puede ser negativo", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
                return;
            }
            
            // Crear juego
            boolean creado = juegoServicio.crear(juego);
            
            if (creado) {
                System.out.println("Juego creado: " + juego.getTitulo());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego creado exitosamente", juego);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al crear juego", null);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
            }
            
        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(mapper.writeValueAsString(respuesta));
            
        } catch (Exception e) {
            System.err.println("Error en doPost: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(mapper.writeValueAsString(respuesta));
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
              
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Obtener ID de la ruta: PUT /juegos/{id}
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID de juego es requerido en la URL");
                return;
            }
            
            String[] parts = pathInfo.split("/");
            String idParam = parts.length > 1 ? parts[1] : "";
            
            if (idParam.isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID de juego es requerido");
                return;
            }
            
            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID debe ser un número válido");
                return;
            }
            
            BufferedReader reader = request.getReader();
            Juego juego = mapper.readValue(reader, Juego.class);
            
            // Asegurar que el ID del cuerpo coincida con el de la URL
            juego.setIdJuego(id);
            
            if (juego.getTitulo() == null || juego.getTitulo().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "El título es requerido");
                return;
            }
            
            // Actualizar juego
            boolean actualizado = juegoServicio.actualizar(juego);
            
            if (actualizado) {
                System.out.println("Juego actualizado: " + juego.getTitulo());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego actualizado exitosamente", null);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                           "Error al actualizar juego");
            }
            
        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "Formato JSON inválido");
            
        } catch (Exception e) {
            System.err.println("Error en doPut: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                       "Error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
               
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // /juegos/{id}
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID de juego es requerido en la URL");
                return;
            }
            
            String[] parts = pathInfo.split("/");
            String idParam = parts.length > 1 ? parts[1] : "";
            
            if (idParam.isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID de juego es requerido");
                return;
            }
            
            try {
                Integer id = Integer.parseInt(idParam);
                
                if (id <= 0) {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                               "ID debe ser mayor a 0");
                    return;
                }
                
                // Desactivar venta
                boolean desactivado = juegoServicio.desactivarVenta(id);
                
                if (desactivado) {
                    System.out.println("Venta desactivada para juego ID: " + id);
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Venta desactivada exitosamente", null);
                    response.getWriter().write(mapper.writeValueAsString(respuesta));
                } else {
                    enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                               "Error al desactivar venta");
                }
                
            } catch (NumberFormatException e) {
                System.err.println("Error: ID inválido - " + idParam);
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID debe ser un número válido");
            }
            
        } catch (Exception e) {
            System.err.println("Error en doDelete: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                       "Error: " + e.getMessage());
        }
    }        
    
    private void listarTodos(HttpServletResponse response) throws IOException {
        List<JuegoDTO> juegos = juegoServicio.listarTodos();
        VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos obtenidos", juegos);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
    
    /**
     * GET /juegos/activos → Listar solo juegos activos
     */
    private void listarActivos(HttpServletResponse response) throws IOException {
        List<JuegoDTO> juegos = juegoServicio.listarActivos();
        VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos activos obtenidos", juegos);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
       
    private void buscarPorId(String idParam, HttpServletResponse response) throws IOException {
        try {
            Integer id = Integer.parseInt(idParam);
            
            if (id <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID debe ser mayor a 0");
                return;
            }
            
            JuegoDTO juego = juegoServicio.buscarPorId(id);
            
            if (juego != null) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Juego encontrado", juego);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
            } else {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, "Juego no encontrado");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Error: ID inválido - " + idParam);
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                       "ID debe ser un número válido");
        }
    }
      
    private void listarPorEmpresa(String idParam, HttpServletResponse response) throws IOException {
        try {
            Integer idEmpresa = Integer.parseInt(idParam);
            
            if (idEmpresa <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                           "ID de empresa debe ser mayor a 0");
                return;
            }
            
            List<JuegoDTO> juegos = juegoServicio.listarPorEmpresa(idEmpresa);
            VerificadorDTO respuesta = new VerificadorDTO(true, "Juegos obtenidos", juegos);
            response.getWriter().write(mapper.writeValueAsString(respuesta));
            
        } catch (NumberFormatException e) {
            System.err.println("Error: ID de empresa inválido - " + idParam);
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                       "ID de empresa debe ser un número válido");
        }
    }
    
    private void buscarPorTitulo(String titulo, HttpServletResponse response) throws IOException {
        List<JuegoDTO> juegos = juegoServicio.buscarPorTitulo(titulo);
        VerificadorDTO respuesta = new VerificadorDTO(true, "Búsqueda completada", juegos);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
    
    private void enviarError(HttpServletResponse response, int status, String mensaje) 
            throws IOException {
        VerificadorDTO respuesta = new VerificadorDTO(false, mensaje, null);
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
}
