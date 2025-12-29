/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.GrupoFamiliarDAO;
import daos.InvitacionGrupoDAO;
import dtos.InvitacionDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.InvitacionGrupo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet ("/invitaciones/*")
public class InvitacionServlet extends HttpServlet{
    
    private InvitacionGrupoDAO invitacionDAO;
    private GrupoFamiliarDAO grupoDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        invitacionDAO = new InvitacionGrupoDAO();
        grupoDAO = new GrupoFamiliarDAO();                
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());              
        gson = new Gson();        
        System.out.println("InvitacionServlet inicializado");
    }
    
    //Obtener invitaciones
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Invitaciones - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("usuario")) {
                int idUsuario = Integer.parseInt(pathParts[2]);
                
                if (pathParts.length >= 4 && pathParts[3].equals("pendientes")) {
                    // /invitaciones/usuario/{idUsuario}/pendientes
                    listarInvitacionesPendientes(response, idUsuario);
                    
                } else if (pathParts.length >= 4 && pathParts[3].equals("enviadas")) {
                    // /invitaciones/usuario/{idUsuario}/enviadas
                    listarInvitacionesEnviadas(response, idUsuario);
                    
                } else {
                    // /invitaciones/usuario/{idUsuario}
                    listarTodasInvitaciones(response, idUsuario);
                }
                
            } else if (pathParts.length >= 2) {
                // /invitaciones/{idInvitacion}
                int idInvitacion = Integer.parseInt(pathParts[1]);
                obtenerInvitacionPorId(response, idInvitacion);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear invitacion 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Crear Invitación");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            InvitacionDTO dto = objectMapper.readValue(json, InvitacionDTO.class);
            
            // validaciones
            
            if (dto.getIdGrupo() == null || dto.getIdGrupo() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del grupo es obligatorio");
                return;
            }
            
            if (dto.getIdUsuarioInvitado() == null || dto.getIdUsuarioInvitado() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del usuario invitado es obligatorio");
                return;
            }
            
            if (dto.getIdUsuarioInvitador() == null || dto.getIdUsuarioInvitador() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del usuario invitador es obligatorio");
                return;
            }
            
            if (dto.getFechaInvitacion() == null || dto.getFechaInvitacion().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha de invitación es obligatoria");
                return;
            }
            
            // Validar que el grupo existe
            if (grupoDAO.obtenerGrupoPorId(dto.getIdGrupo()) == null) {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                    "El grupo no existe");
                return;
            }
            
            // Validar que el invitador es miembro del grupo
            if (!grupoDAO.esMiembro(dto.getIdGrupo(), dto.getIdUsuarioInvitador())) {
                enviarError(response, HttpServletResponse.SC_FORBIDDEN, 
                    "Solo los miembros del grupo pueden enviar invitaciones");
                return;
            }
            
            // Validar que el invitado NO es ya miembro
            if (grupoDAO.esMiembro(dto.getIdGrupo(), dto.getIdUsuarioInvitado())) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El usuario ya es miembro del grupo");
                return;
            }
            
            // Validar que NO existe invitación pendiente
            if (invitacionDAO.tieneInvitacionPendiente(dto.getIdGrupo(), dto.getIdUsuarioInvitado())) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Ya existe una invitación pendiente para este usuario");
                return;
            }
            
            // Validar que el grupo no tiene 6 miembros ya
            int cantidadMiembros = grupoDAO.contarMiembros(dto.getIdGrupo());
            if (cantidadMiembros >= 6) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El grupo ya tiene el máximo de 6 miembros");
                return;
            }
            
            // Crear invitación
            InvitacionGrupo invitacion = new InvitacionGrupo();
            invitacion.setIdGrupo(dto.getIdGrupo());
            invitacion.setIdUsuarioInvitado(dto.getIdUsuarioInvitado());
            invitacion.setIdUsuarioInvitador(dto.getIdUsuarioInvitador());
            invitacion.setFechaInvitacion(LocalDate.parse(dto.getFechaInvitacion()));
            invitacion.setEstado(InvitacionGrupo.EstadoInvitacion.PENDIENTE);
            
            boolean exito = invitacionDAO.crearInvitacion(invitacion);
            
            if (exito) {
                InvitacionDTO respuestaDTO = new InvitacionDTO();
                respuestaDTO.setIdInvitacion(invitacion.getIdInvitacion());
                respuestaDTO.setIdGrupo(invitacion.getIdGrupo());
                respuestaDTO.setIdUsuarioInvitado(invitacion.getIdUsuarioInvitado());
                respuestaDTO.setIdUsuarioInvitador(invitacion.getIdUsuarioInvitador());
                respuestaDTO.setEstado(invitacion.getEstado().name());
                respuestaDTO.setFechaInvitacion(invitacion.getFechaInvitacion().toString());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Invitación enviada correctamente", respuestaDTO);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear la invitación");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Invitación: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Aceptar o rechazar una invitacion 
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Invitación - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la invitación");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 3) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idInvitacion = Integer.parseInt(pathParts[1]);
            String accion = pathParts[2];
            
            if (accion.equals("aceptar")) {
                aceptarInvitacion(response, idInvitacion);
            } else if (accion.equals("rechazar")) {
                rechazarInvitacion(response, idInvitacion);
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Acción no válida. Use 'aceptar' o 'rechazar'");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }

    //Cancelar invitacion
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println(" DELETE Invitación - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la invitación");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idInvitacion = Integer.parseInt(pathParts[1]);
            
            boolean exito = invitacionDAO.eliminarInvitacion(idInvitacion);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Invitación cancelada", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al cancelar la invitación");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Lista de invitaciones pendientes
    private void listarInvitacionesPendientes(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<InvitacionGrupo> invitaciones = invitacionDAO.obtenerInvitacionesPendientes(idUsuario);
        
        List<InvitacionDTO> invitacionesDTO = convertirADTO(invitaciones);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Invitaciones pendientes obtenidas", invitacionesDTO);
    }
    
    //Lista completa de invitaciones
    private void listarTodasInvitaciones(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<InvitacionGrupo> invitaciones = invitacionDAO.obtenerTodasInvitaciones(idUsuario);
        
        List<InvitacionDTO> invitacionesDTO = convertirADTO(invitaciones);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Invitaciones obtenidas", invitacionesDTO);
    }
    
    //Lista de invitaciones enviadas
    private void listarInvitacionesEnviadas(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<InvitacionGrupo> invitaciones = invitacionDAO.obtenerInvitacionesEnviadas(idUsuario);
        
        List<InvitacionDTO> invitacionesDTO = convertirADTO(invitaciones);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Invitaciones enviadas obtenidas", invitacionesDTO);
    }
    
    
    //Obtener invitaciones por medio de id
    private void obtenerInvitacionPorId(HttpServletResponse response, int idInvitacion) 
            throws IOException {
        
        InvitacionGrupo invitacion = invitacionDAO.obtenerInvitacionPorId(idInvitacion);
        
        if (invitacion == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Invitación no encontrada");
            return;
        }
        
        InvitacionDTO dto = convertirADTO(invitacion);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Invitación obtenida", dto);
    }
    
    //Acepta una invitacion
    private void aceptarInvitacion(HttpServletResponse response, int idInvitacion) 
            throws IOException {
        
        // Obtener invitación
        InvitacionGrupo invitacion = invitacionDAO.obtenerInvitacionPorId(idInvitacion);
        
        if (invitacion == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Invitación no encontrada");
            return;
        }
        
        if (invitacion.getEstado() != InvitacionGrupo.EstadoInvitacion.PENDIENTE) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La invitación ya fue procesada");
            return;
        }
        
        // Validar que el grupo no está lleno
        int cantidadMiembros = grupoDAO.contarMiembros(invitacion.getIdGrupo());
        if (cantidadMiembros >= 6) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El grupo ya está lleno (máximo 6 miembros)");
            return;
        }
        
        //  Agregar usuario al grupo
        boolean agregado = grupoDAO.agregarMiembro(
            invitacion.getIdGrupo(), 
            invitacion.getIdUsuarioInvitado()
        );
        
        if (!agregado) {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al agregar el usuario al grupo");
            return;
        }
        
        // Actualizar estado de invitación
        boolean actualizado = invitacionDAO.actualizarEstadoInvitacion(
            idInvitacion, 
            InvitacionGrupo.EstadoInvitacion.ACEPTADA
        );
        
        if (actualizado) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Invitación aceptada. Ahora eres miembro del grupo", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar la invitación");
        }
    }
    
    //Rechazar una invitacion
    private void rechazarInvitacion(HttpServletResponse response, int idInvitacion) 
            throws IOException {
        
        // Obtener invitación
        InvitacionGrupo invitacion = invitacionDAO.obtenerInvitacionPorId(idInvitacion);
        
        if (invitacion == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Invitación no encontrada");
            return;
        }
        
        if (invitacion.getEstado() != InvitacionGrupo.EstadoInvitacion.PENDIENTE) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La invitación ya fue procesada");
            return;
        }
        
        boolean actualizado = invitacionDAO.actualizarEstadoInvitacion(
            idInvitacion, 
            InvitacionGrupo.EstadoInvitacion.RECHAZADA
        );
        
        if (actualizado) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Invitación rechazada", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al rechazar la invitación");
        }
    }
    
    private List<InvitacionDTO> convertirADTO(List<InvitacionGrupo> invitaciones) {
        List<InvitacionDTO> dtos = new ArrayList<>();
        
        for (InvitacionGrupo inv : invitaciones) {
            dtos.add(convertirADTO(inv));
        }
        
        return dtos;
    }
    
    private InvitacionDTO convertirADTO(InvitacionGrupo inv) {
        InvitacionDTO dto = new InvitacionDTO();
        dto.setIdInvitacion(inv.getIdInvitacion());
        dto.setIdGrupo(inv.getIdGrupo());
        dto.setIdUsuarioInvitado(inv.getIdUsuarioInvitado());
        dto.setIdUsuarioInvitador(inv.getIdUsuarioInvitador());
        dto.setEstado(inv.getEstado().name());
        dto.setFechaInvitacion(inv.getFechaInvitacion().toString());
        dto.setNombreGrupo(inv.getNombreGrupo());
        dto.setNombreInvitador(inv.getNombreInvitador());
        dto.setNombreInvitado(inv.getNombreInvitado());
        
        return dto;
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
