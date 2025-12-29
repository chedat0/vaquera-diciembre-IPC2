/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.GrupoFamiliarDAO;
import daos.InstalacionJuegoDAO;
import daos.LicenciaDAO;
import dtos.InstalacionDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.InstalacionJuego;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet("/instalaciones/*")
public class InstalacionServlet extends HttpServlet{
    
    private InstalacionJuegoDAO instalacionDAO;
    private LicenciaDAO licenciaDAO;
    private GrupoFamiliarDAO grupoDAO;
    private ObjectMapper objectMapper;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        instalacionDAO = new InstalacionJuegoDAO();
        licenciaDAO = new LicenciaDAO();
        grupoDAO = new GrupoFamiliarDAO();               
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());              
        gson = new Gson();        
        System.out.println("InstalacionServlet inicializado");
    }
    
    //Obtener instalaciones
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Instalaciones - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la ruta");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("usuario")) {
                int idUsuario = Integer.parseInt(pathParts[2]);
                
                if (pathParts.length >= 4 && pathParts[3].equals("instalados")) {
                    // /instalaciones/usuario/{idUsuario}/instalados
                    listarJuegosInstalados(response, idUsuario);
                    
                } else if (pathParts.length >= 4 && pathParts[3].equals("disponibles")) {
                    // /instalaciones/usuario/{idUsuario}/disponibles
                    listarJuegosDisponibles(response, idUsuario);
                    
                } else {
                    // /instalaciones/usuario/{idUsuario}
                    listarTodasInstalaciones(response, idUsuario);
                }
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Instalar un juego
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Instalar Juego");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            InstalacionDTO dto = objectMapper.readValue(json, InstalacionDTO.class);
            
            // validaciones
            
            if (dto.getIdUsuario() == null || dto.getIdUsuario() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del usuario es obligatorio");
                return;
            }
            
            if (dto.getIdJuego() == null || dto.getIdJuego() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del juego es obligatorio");
                return;
            }
            
            if (dto.getFechaEstado() == null || dto.getFechaEstado().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha es obligatoria");
                return;
            }
            
            LocalDate fecha = LocalDate.parse(dto.getFechaEstado());
            
            // Verificar si el usuario tiene el juego (propio o prestado)
            boolean tieneLicencia = licenciaDAO.tieneLicencia(dto.getIdUsuario(), dto.getIdJuego());
            boolean esPrestado = !tieneLicencia;
            
            if (!tieneLicencia) {
                // Verificar si tiene acceso a través del grupo
                List<InstalacionJuego> disponibles = instalacionDAO.obtenerJuegosDisponiblesGrupo(dto.getIdUsuario());
                
                boolean tieneAcceso = false;
                for (InstalacionJuego disp : disponibles) {
                    if (disp.getIdJuego() == dto.getIdJuego()) {
                        tieneAcceso = true;
                        break;
                    }
                }
                
                if (!tieneAcceso) {
                    enviarError(response, HttpServletResponse.SC_FORBIDDEN, 
                        "No tienes acceso a este juego. Debe ser tuyo o de tu grupo familiar");
                    return;
                }
            }
            
            // Si es prestado, verificar límite de 1 juego prestado instalado
            if (esPrestado) {
                int prestadosInstalados = instalacionDAO.contarJuegosPrestadosInstalados(dto.getIdUsuario());
                
                if (prestadosInstalados >= 1) {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "Solo puedes tener 1 juego prestado instalado a la vez. " +
                        "Desinstala el juego prestado actual antes de instalar otro");
                    return;
                }
            }
            
            // Crear instalación
            InstalacionJuego instalacion = new InstalacionJuego();
            instalacion.setIdUsuario(dto.getIdUsuario());
            instalacion.setIdJuego(dto.getIdJuego());
            instalacion.setEsPrestado(esPrestado);
            instalacion.setEstado(InstalacionJuego.EstadoInstalacion.INSTALADO);
            instalacion.setFechaEstado(fecha);
            
            boolean exito = instalacionDAO.registrarInstalacion(instalacion);
            
            if (exito) {
                Map<String, Object> datosRespuesta = new HashMap<>();
                datosRespuesta.put("idUsuario", dto.getIdUsuario());
                datosRespuesta.put("idJuego", dto.getIdJuego());
                datosRespuesta.put("esPrestado", esPrestado);
                datosRespuesta.put("estado", "INSTALADO");
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Juego instalado correctamente", datosRespuesta);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al instalar el juego");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Instalación: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Cambiar estado de instalacion
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Instalación - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar usuario y juego");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 5 || !pathParts[1].equals("usuario") || !pathParts[3].equals("juego")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato: /instalaciones/usuario/{idUsuario}/juego/{idJuego}");
                return;
            }
            
            int idUsuario = Integer.parseInt(pathParts[2]);
            int idJuego = Integer.parseInt(pathParts[4]);
            
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            InstalacionDTO dto = objectMapper.readValue(json, InstalacionDTO.class);
            
            if (dto.getEstado() == null || dto.getEstado().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El estado es obligatorio (INSTALADO o NO_INSTALADO)");
                return;
            }
            
            if (dto.getFechaEstado() == null || dto.getFechaEstado().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha es obligatoria");
                return;
            }
            
            InstalacionJuego.EstadoInstalacion nuevoEstado;
            try {
                nuevoEstado = InstalacionJuego.EstadoInstalacion.valueOf(dto.getEstado().toUpperCase());
            } catch (IllegalArgumentException e) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Estado inválido. Use INSTALADO o NO_INSTALADO");
                return;
            }
            
            // Si quiere INSTALAR un juego prestado, validar límite
            if (nuevoEstado == InstalacionJuego.EstadoInstalacion.INSTALADO) {
                InstalacionJuego instalacionActual = instalacionDAO.obtenerInstalacion(idUsuario, idJuego);
                
                if (instalacionActual != null && instalacionActual.isEsPrestado()) {
                    int prestadosInstalados = instalacionDAO.contarJuegosPrestadosInstalados(idUsuario);
                    
                    if (prestadosInstalados >= 1) {
                        enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "Solo puedes tener 1 juego prestado instalado a la vez");
                        return;
                    }
                }
            }
            
            LocalDate fecha = LocalDate.parse(dto.getFechaEstado());
            
            boolean exito = instalacionDAO.actualizarEstadoInstalacion(idUsuario, idJuego, nuevoEstado, fecha);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Estado actualizado a " + nuevoEstado, null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al actualizar el estado");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Desintalar un juego
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println(" DELETE Instalación - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar usuario y juego");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 5 || !pathParts[1].equals("usuario") || !pathParts[3].equals("juego")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato: /instalaciones/usuario/{idUsuario}/juego/{idJuego}");
                return;
            }
            
            int idUsuario = Integer.parseInt(pathParts[2]);
            int idJuego = Integer.parseInt(pathParts[4]);
            
            boolean exito = instalacionDAO.eliminarInstalacion(idUsuario, idJuego);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Instalación eliminada", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al eliminar la instalación");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Lista de las instalaciones
    private void listarTodasInstalaciones(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<InstalacionJuego> instalaciones = instalacionDAO.obtenerInstalacionesPorUsuario(idUsuario);
        
        List<InstalacionDTO> instalacionesDTO = convertirADTO(instalaciones);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Instalaciones obtenidas", instalacionesDTO);
    }
    
    //lista de juegos instalados actualmente
     private void listarJuegosInstalados(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<InstalacionJuego> instalaciones = instalacionDAO.obtenerJuegosInstalados(idUsuario);
        
        List<InstalacionDTO> instalacionesDTO = convertirADTO(instalaciones);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Juegos instalados obtenidos", instalacionesDTO);
    }
     
     //Lista de juegos disponibles
     private void listarJuegosDisponibles(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        List<InstalacionJuego> disponibles = instalacionDAO.obtenerJuegosDisponiblesGrupo(idUsuario);
        
        List<InstalacionDTO> disponiblesDTO = new ArrayList<>();
        
        for (InstalacionJuego disp : disponibles) {
            InstalacionDTO dto = new InstalacionDTO();
            dto.setIdUsuario(idUsuario);
            dto.setIdJuego(disp.getIdJuego());
            dto.setTituloJuego(disp.getTituloJuego());
            dto.setEsPrestado(disp.isEsPrestado());
            dto.setIdPropietario(disp.getIdPropietario());
            dto.setNombrePropietario(disp.getNombreUsuario());
            
            // Verificar si ya está instalado
            InstalacionJuego instalacionActual = instalacionDAO.obtenerInstalacion(idUsuario, disp.getIdJuego());
            if (instalacionActual != null) {
                dto.setEstado(instalacionActual.getEstado().name());
                dto.setFechaEstado(instalacionActual.getFechaEstado().toString());
            } else {
                dto.setEstado("NO_INSTALADO");
            }
            
            disponiblesDTO.add(dto);
        }
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Juegos disponibles obtenidos", disponiblesDTO);
    }
     
    private List<InstalacionDTO> convertirADTO(List<InstalacionJuego> instalaciones) {
        List<InstalacionDTO> dtos = new ArrayList<>();
        
        for (InstalacionJuego inst : instalaciones) {
            InstalacionDTO dto = new InstalacionDTO();
            dto.setIdUsuario(inst.getIdUsuario());
            dto.setIdJuego(inst.getIdJuego());
            dto.setEsPrestado(inst.isEsPrestado());
            dto.setEstado(inst.getEstado().name());
            dto.setFechaEstado(inst.getFechaEstado().toString());
            dto.setTituloJuego(inst.getTituloJuego());
            
            dtos.add(dto);
        }
        
        return dtos;
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
