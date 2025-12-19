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
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String nombreParam = request.getParameter("nombre");
            
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                EmpresaDTO empresa = empresaServicio.buscarPorId(id);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa encontrada", empresa);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else if (nombreParam != null) {
                List<EmpresaDTO> empresas = empresaServicio.buscarPorNombre(nombreParam);
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresas encontradas", empresas);
                response.getWriter().write(gson.toJson(respuesta));
                
            } else {
                List<EmpresaDTO> empresas = empresaServicio.listarTodas();
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresas obtenidas", empresas);
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
            Empresa empresa = gson.fromJson(reader, Empresa.class);
            
            boolean creada = empresaServicio.crear(empresa);
            
            if (creada) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa creada", empresa);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al crear empresa", null);
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
            Empresa empresa = gson.fromJson(reader, Empresa.class);
            
            boolean actualizada = empresaServicio.actualizar(empresa);
            
            if (actualizada) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa actualizada", null);
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
                boolean eliminada = empresaServicio.eliminar(id);
                
                if (eliminada) {
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa eliminada", null);
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
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
