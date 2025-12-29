/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import daos.ComisionDAO;
import dtos.VerificadorDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet("/comisiones/*")
public class ComisionServlet extends HttpServlet{
    private ComisionDAO comisionDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        comisionDAO = new ComisionDAO();
        gson = new Gson();        
        System.out.println("ComisionServlet inicializado");
    }
    
    //Obtener comisiones
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Comisiones - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 2 && pathParts[1].equals("global")) {
                
                if (pathParts.length >= 3 && pathParts[2].equals("historial")) {                   
                    List<Map<String, Object>> historial = comisionDAO.obtenerHistorialComisionGlobal();
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Historial obtenido", historial);
                } else {                    
                    double comision = comisionDAO.obtenerComisionGlobal();
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("porcentaje", comision);
                    
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Comisión global obtenida", datos);
                }
                
            } else if (pathParts.length >= 2 && pathParts[1].equals("empresas")) {
                
                if (pathParts.length >= 3) {                    
                    int idEmpresa = Integer.parseInt(pathParts[2]);
                    double comision = comisionDAO.obtenerComisionAplicable(idEmpresa);
                    Double comisionEspecifica = comisionDAO.obtenerComisionEmpresa(idEmpresa);
                    
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("idEmpresa", idEmpresa);
                    datos.put("comisionAplicable", comision);
                    datos.put("tieneComisionEspecifica", comisionEspecifica != null);
                    if (comisionEspecifica != null) {
                        datos.put("comisionEspecifica", comisionEspecifica);
                    }
                    
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Comisión obtenida", datos);
                } else {                    
                    List<Map<String, Object>> empresas = comisionDAO.obtenerEmpresasConComisionEspecifica();
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Empresas con comisión específica obtenidas", empresas);
                }
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Ruta no válida");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear comisiones... no se usa ya que hay una por defecto
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        enviarError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
            "Use PUT para actualizar comisiones");
    }
    
    //Actualizar comisiones
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Comisiones - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            Map<String, Object> datos = gson.fromJson(json, Map.class);
            
            if (!datos.containsKey("porcentaje")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El campo 'porcentaje' es obligatorio");
                return;
            }
            
            double porcentaje = ((Number) datos.get("porcentaje")).doubleValue();
            
            if (porcentaje < 0 || porcentaje > 100) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El porcentaje debe estar entre 0 y 100");
                return;
            }
            
            if (pathParts.length >= 2 && pathParts[1].equals("global")) {                
                boolean exito = comisionDAO.actualizarComisionGlobalConAjuste(porcentaje);
                
                if (exito) {
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Comisión global actualizada correctamente. " +
                              "Las empresas con comisión superior fueron ajustadas automáticamente", 
                        null);
                } else {
                    enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error al actualizar la comisión global");
                }
                
            } else if (pathParts.length >= 3 && pathParts[1].equals("empresas")) {                
                int idEmpresa = Integer.parseInt(pathParts[2]);
                
                boolean exito = comisionDAO.establecerComisionEmpresa(idEmpresa, porcentaje);
                
                if (exito) {
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Comisión específica establecida para la empresa", null);
                } else {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "Error: La comisión no puede ser mayor a la comisión global");
                }
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Ruta no válida");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID o porcentaje inválido");
        }
    }
    
    //Eliminar comision especifica de alguna empresa
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println(" DELETE Comisión - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la empresa");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("empresas")) {
                int idEmpresa = Integer.parseInt(pathParts[2]);
                
                boolean exito = comisionDAO.eliminarComisionEmpresa(idEmpresa);
                
                if (exito) {
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Comisión específica eliminada. La empresa usará la comisión global", 
                        null);
                } else {
                    enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error al eliminar la comisión");
                }
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Ruta no válida");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }

    private void enviarRespuesta(HttpServletResponse response, int statusCode, 
                                boolean success, String mensaje, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        
        VerificadorDTO respuesta = new VerificadorDTO(success, mensaje, data);
        response.getWriter().write(gson.toJson(respuesta));
    }
    
    private void enviarError(HttpServletResponse response, int statusCode, 
                            String mensaje) throws IOException {
        enviarRespuesta(response, statusCode, false, mensaje, null);
    }
}
