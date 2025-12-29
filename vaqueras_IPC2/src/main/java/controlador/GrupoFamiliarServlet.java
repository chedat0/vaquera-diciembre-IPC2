/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.GrupoFamiliarDAO;
import dtos.GrupoFamiliarDTO;
import dtos.MiembroDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.GrupoFamiliar;
import com.mycompany.vaqueras_ipc2.modelo.GrupoMiembro;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */
@WebServlet ("/grupos/*")
public class GrupoFamiliarServlet extends HttpServlet{
    private GrupoFamiliarDAO grupoDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        grupoDAO = new GrupoFamiliarDAO();                
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());            
        gson = new Gson();        
        System.out.println("GrupoFamiliarServlet inicializado");
    }
    
    //Obtener grupos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Grupos - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("usuario")) {
                // /grupos/usuario/{idUsuario}
                int idUsuario = Integer.parseInt(pathParts[2]);
                listarGruposPorUsuario(response, idUsuario);
                
            } else if (pathParts.length >= 3 && pathParts[2].equals("miembros")) {
                // /grupos/{idGrupo}/miembros
                int idGrupo = Integer.parseInt(pathParts[1]);
                listarMiembrosGrupo(response, idGrupo);
                
            } else if (pathParts.length >= 2) {
                // /grupos/{idGrupo}
                int idGrupo = Integer.parseInt(pathParts[1]);
                obtenerGrupoPorId(response, idGrupo);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear nuevo grupo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Crear Grupo");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            GrupoFamiliarDTO dto = gson.fromJson(json, GrupoFamiliarDTO.class);
            
            // Validaciones
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El nombre del grupo es obligatorio");
                return;
            }
            
            if (dto.getIdCreador() == null || dto.getIdCreador() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del creador es obligatorio");
                return;
            }
            
            // Crear grupo
            GrupoFamiliar grupo = new GrupoFamiliar();
            grupo.setNombre(dto.getNombre().trim());
            grupo.setIdCreador(dto.getIdCreador());
            
            boolean exito = grupoDAO.crearGrupo(grupo);
            
            if (exito) {
                // Obtener grupo completo
                GrupoFamiliar grupoCreado = grupoDAO.obtenerGrupoPorId(grupo.getIdGrupo());
                
                GrupoFamiliarDTO respuestaDTO = new GrupoFamiliarDTO();
                respuestaDTO.setIdGrupo(grupoCreado.getIdGrupo());
                respuestaDTO.setNombre(grupoCreado.getNombre());
                respuestaDTO.setIdCreador(grupoCreado.getIdCreador());
                respuestaDTO.setNombreCreador(grupoCreado.getNombreCreador());
                respuestaDTO.setCantidadMiembros(grupoCreado.getCantidadMiembros());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Grupo creado correctamente", respuestaDTO);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear el grupo");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Grupo: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Actualizar grupo
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Actualizar Grupo - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el ID del grupo");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idGrupo = Integer.parseInt(pathParts[1]);
            
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            GrupoFamiliarDTO dto = gson.fromJson(json, GrupoFamiliarDTO.class);
            
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El nombre es obligatorio");
                return;
            }
            
            boolean exito = grupoDAO.actualizarNombreGrupo(idGrupo, dto.getNombre().trim());
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Grupo actualizado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al actualizar el grupo");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Eliminar grupo y/o miembro de grupo
     @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("DELETE Grupo - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 4 && pathParts[2].equals("miembros")) {
                // /grupos/{idGrupo}/miembros/{idUsuario}
                int idGrupo = Integer.parseInt(pathParts[1]);
                int idUsuario = Integer.parseInt(pathParts[3]);
                
                boolean exito = grupoDAO.eliminarMiembro(idGrupo, idUsuario);
                
                if (exito) {
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Miembro eliminado del grupo", null);
                } else {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "No se pudo eliminar el miembro. Verifique que no sea el creador.");
                }
                
            } else if (pathParts.length >= 2) {
                // /grupos/{idGrupo}
                int idGrupo = Integer.parseInt(pathParts[1]);
                
                boolean exito = grupoDAO.eliminarGrupo(idGrupo);
                
                if (exito) {
                    enviarRespuesta(response, HttpServletResponse.SC_OK, 
                        true, "Grupo eliminado correctamente", null);
                } else {
                    enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error al eliminar el grupo");
                }
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Listar grupos por ususario
    private void listarGruposPorUsuario(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<GrupoFamiliar> grupos = grupoDAO.obtenerGruposPorUsuario(idUsuario);
        
        List<GrupoFamiliarDTO> gruposDTO = new ArrayList<>();
        
        for (GrupoFamiliar g : grupos) {
            GrupoFamiliarDTO dto = new GrupoFamiliarDTO();
            dto.setIdGrupo(g.getIdGrupo());
            dto.setNombre(g.getNombre());
            dto.setIdCreador(g.getIdCreador());
            dto.setNombreCreador(g.getNombreCreador());
            dto.setCantidadMiembros(g.getCantidadMiembros());
            
            gruposDTO.add(dto);
        }
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Grupos obtenidos correctamente", gruposDTO);
    }
    
    //obtener grupo por id
     private void obtenerGrupoPorId(HttpServletResponse response, int idGrupo) 
            throws IOException {
        
        GrupoFamiliar grupo = grupoDAO.obtenerGrupoPorId(idGrupo);
        
        if (grupo == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, "Grupo no encontrado");
            return;
        }
        
        GrupoFamiliarDTO dto = new GrupoFamiliarDTO();
        dto.setIdGrupo(grupo.getIdGrupo());
        dto.setNombre(grupo.getNombre());
        dto.setIdCreador(grupo.getIdCreador());
        dto.setNombreCreador(grupo.getNombreCreador());
        dto.setCantidadMiembros(grupo.getCantidadMiembros());
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Grupo obtenido correctamente", dto);
    }
     
     //listar a los miembros que perteneces a un grupo
     private void listarMiembrosGrupo(HttpServletResponse response, int idGrupo) 
            throws IOException {
        
        List<GrupoMiembro> miembros = grupoDAO.obtenerMiembrosGrupo(idGrupo);
        
        List<MiembroDTO> miembrosDTO = new ArrayList<>();
        
        for (GrupoMiembro m : miembros) {
            MiembroDTO dto = new MiembroDTO();
            dto.setIdUsuario(m.getIdUsuario());
            dto.setNickname(m.getNombreUsuario());
            dto.setRol(m.getRol());
            
            miembrosDTO.add(dto);
        }
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Miembros obtenidos correctamente", miembrosDTO);
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
