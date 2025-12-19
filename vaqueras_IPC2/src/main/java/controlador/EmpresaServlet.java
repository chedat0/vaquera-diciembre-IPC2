/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import dtos.EmpresaDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Empresa;
import servicios.EmpresaServicio;

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
@WebServlet("/empresas")
public class EmpresaServlet extends HttpServlet{
    private EmpresaServicio empresaServicio;
    private Gson gson;
    
    @Override
    public void init() {
        empresaServicio = new EmpresaServicio();
        gson = new Gson();
        System.out.println("EmpresaServlet inicializado correctamente");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String nombreParam = request.getParameter("nombre");
            
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
                    
                    EmpresaDTO empresa = empresaServicio.buscarPorId(id);
                    
                    if (empresa != null) {
                        VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa encontrada", empresa);
                        response.getWriter().write(gson.toJson(respuesta));
                    } else {
                        VerificadorDTO respuesta = new VerificadorDTO(false, "Empresa no encontrada", null);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write(gson.toJson(respuesta));
                    }
                    
                } catch (NumberFormatException e) {
                    System.err.println("✗ Error: ID inválido - " + idParam);
                    VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser un número válido", null);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(respuesta));
                }
                
            } else if (nombreParam != null && !nombreParam.trim().isEmpty()) {
                // Buscar por nombre
                List<EmpresaDTO> empresas = empresaServicio.buscarPorNombre(nombreParam);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Búsqueda completada", empresas);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                // Listar todas
                List<EmpresaDTO> empresas = empresaServicio.listarTodas();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresas obtenidas", empresas);
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
            Empresa empresa = gson.fromJson(reader, Empresa.class);
            
            // Validaciones
            if (empresa == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de empresa inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (empresa.getNombre().length() > 100) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre no puede exceder 100 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Crear empresa
            boolean creada = empresaServicio.crear(empresa);
            
            if (creada) {
                System.out.println("Empresa creada: " + empresa.getNombre());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa creada exitosamente", empresa);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al crear empresa", null);
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
            Empresa empresa = gson.fromJson(reader, Empresa.class);
            
            // Validaciones
            if (empresa == null || empresa.getIdEmpresa() == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID de empresa requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }
            
            // Actualizar empresa
            boolean actualizada = empresaServicio.actualizar(empresa);
            
            if (actualizada) {
                System.out.println("Empresa actualizada: " + empresa.getNombre());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa actualizada exitosamente", null);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al actualizar empresa", null);
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
                
                boolean eliminada = empresaServicio.eliminar(id);
                
                if (eliminada) {
                    System.out.println("Empresa eliminada: ID " + id);
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa eliminada", null);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Error al eliminar empresa", null);
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
