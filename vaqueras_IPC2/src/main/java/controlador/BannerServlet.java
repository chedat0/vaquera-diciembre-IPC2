/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.BannerDAO;
import dtos.BannerDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Banner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet ("/banners/*")
public class BannerServlet extends HttpServlet{
    
    private BannerDAO bannerDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        bannerDAO = new BannerDAO();        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());        
        gson = new Gson();        
        System.out.println("BannerServlet inicializado");
    }
    
    //Obtener banners o balance
     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Banners - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todos los banners
                listarTodosBanners(response);
                
            } else {
                String[] pathParts = pathInfo.split("/");
                
                if (pathParts.length >= 2 && pathParts[1].equals("activos")) {
                    // /banners/activos
                    listarBannersActivos(response);
                    
                } else if (pathParts.length >= 3 && pathParts[1].equals("algoritmo") 
                           && pathParts[2].equals("mejor-balance")) {
                    // /banners/algoritmo/mejor-balance
                    String limiteParam = request.getParameter("limite");
                    int limite = limiteParam != null ? Integer.parseInt(limiteParam) : 10;
                    
                    obtenerMejorBalance(response, limite);
                    
                } else if (pathParts.length >= 2) {
                    // /banners/{idBanner}
                    int idBanner = Integer.parseInt(pathParts[1]);
                    obtenerBannerPorId(response, idBanner);
                    
                } else {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "Formato de URL no válido");
                }
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear nuevo banner
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println(" POST Crear Banner");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            BannerDTO dto = objectMapper.readValue(json, BannerDTO.class);
            
            // Validaciones
            if (dto.getIdJuego() == null || dto.getIdJuego() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID del juego es obligatorio");
                return;
            }
            
            if (dto.getPosicion() == null || dto.getPosicion() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La posición es obligatoria y debe ser mayor a 0");
                return;
            }
            
            // Crear banner
            Banner banner = new Banner();
            banner.setIdJuego(dto.getIdJuego());
            banner.setPosicion(dto.getPosicion());
            banner.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
            
            if (dto.getFechaInicio() != null && !dto.getFechaInicio().trim().isEmpty()) {
                banner.setFechaInicio(java.time.LocalDate.parse(dto.getFechaInicio()));
            }
            
            if (dto.getFechaFin() != null && !dto.getFechaFin().trim().isEmpty()) {
                banner.setFechaFin(java.time.LocalDate.parse(dto.getFechaFin()));
            }
            
            boolean exito = bannerDAO.crearBanner(banner);
            
            if (exito) {
                BannerDTO respuestaDTO = new BannerDTO();
                respuestaDTO.setIdBanner(banner.getIdBanner());
                respuestaDTO.setIdJuego(banner.getIdJuego());
                respuestaDTO.setPosicion(banner.getPosicion());
                respuestaDTO.setActivo(banner.isActivo());
                
                if (banner.getFechaInicio() != null) {
                    respuestaDTO.setFechaInicio(banner.getFechaInicio().toString());
                }
                if (banner.getFechaFin() != null) {
                    respuestaDTO.setFechaFin(banner.getFechaFin().toString());
                }
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Banner creado correctamente", respuestaDTO);
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Error al crear el banner. Verifique que la posición no esté ocupada.");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Banner: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Actualizar banner
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Banner - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el banner");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idBanner = Integer.parseInt(pathParts[1]);
            
            if (pathParts.length >= 3 && pathParts[2].equals("posicion")) {                
                actualizarPosicion(request, response, idBanner);
                
            } else if (pathParts.length >= 3 && pathParts[2].equals("estado")) {                
                actualizarEstado(request, response, idBanner);
                
            } else if (pathParts.length >= 3 && pathParts[2].equals("fechas")) {                
                actualizarFechas(request, response, idBanner);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Acción no válida. Use: /posicion, /estado o /fechas");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Eliminar banner
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("DELETE Banner - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el banner");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idBanner = Integer.parseInt(pathParts[1]);
            
            boolean exito = bannerDAO.eliminarBanner(idBanner);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Banner eliminado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al eliminar el banner");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Lista de banners
    private void listarTodosBanners(HttpServletResponse response) throws IOException {
        List<Banner> banners = bannerDAO.obtenerTodosBanners();
        List<BannerDTO> bannersDTO = convertirADTO(banners);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Banners obtenidos correctamente", bannersDTO);
    }
    
    //Lista de banners activos
    private void listarBannersActivos(HttpServletResponse response) throws IOException {
        List<Banner> banners = bannerDAO.obtenerBannersActivos();
        List<BannerDTO> bannersDTO = convertirADTO(banners);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Banners activos obtenidos", bannersDTO);
    }
    
    //Obtiene banner por medio de su id
    private void obtenerBannerPorId(HttpServletResponse response, int idBanner) 
            throws IOException {
        
        Banner banner = bannerDAO.obtenerBannerPorId(idBanner);
        
        if (banner == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Banner no encontrado");
            return;
        }
        
        BannerDTO dto = convertirADTO(banner);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Banner obtenido correctamente", dto);
    }
    
    //obtiene balance por medio del algoritmo usado
    private void obtenerMejorBalance(HttpServletResponse response, int limite) 
            throws IOException {
        
        List<Map<String, Object>> juegos = bannerDAO.obtenerJuegosMejorBalance(limite);
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Juegos con mejor balance obtenidos ", 
            juegos);
    }
    
    //Actualizar posicion del banner
    private void actualizarPosicion(HttpServletRequest request, HttpServletResponse response, 
                                   int idBanner) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        Map<String, Object> datos = gson.fromJson(json, Map.class);
        
        if (!datos.containsKey("posicion")) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El campo 'posicion' es obligatorio");
            return;
        }
        
        int nuevaPosicion = ((Number) datos.get("posicion")).intValue();
        
        boolean exito = bannerDAO.actualizarPosicion(idBanner, nuevaPosicion);
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Posición actualizada correctamente", null);
        } else {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Error: La posición ya está ocupada por otro banner");
        }
    }
    
    //Actualizar estado activo o inactivo del banner
    private void actualizarEstado(HttpServletRequest request, HttpServletResponse response, 
                                 int idBanner) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        Map<String, Object> datos = gson.fromJson(json, Map.class);
        
        if (!datos.containsKey("activo")) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El campo 'activo' es obligatorio");
            return;
        }
        
        boolean activo = (Boolean) datos.get("activo");
        
        boolean exito = bannerDAO.actualizarEstado(idBanner, activo);
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Estado actualizado a " + (activo ? "ACTIVO" : "INACTIVO"), null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar el estado");
        }
    }
    
    //Actualizar fechas de los banners
    private void actualizarFechas(HttpServletRequest request, HttpServletResponse response, 
                                 int idBanner) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        try {
            BannerDTO dto = objectMapper.readValue(json, BannerDTO.class);
            
            java.time.LocalDate fechaInicio = null;
            java.time.LocalDate fechaFin = null;
            
            if (dto.getFechaInicio() != null && !dto.getFechaInicio().trim().isEmpty()) {
                fechaInicio = java.time.LocalDate.parse(dto.getFechaInicio());
            }
            
            if (dto.getFechaFin() != null && !dto.getFechaFin().trim().isEmpty()) {
                fechaFin = java.time.LocalDate.parse(dto.getFechaFin());
            }
            
            boolean exito = bannerDAO.actualizarFechas(idBanner, fechaInicio, fechaFin);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Fechas actualizadas correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al actualizar las fechas");
            }
            
        } catch (Exception e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Error al procesar las fechas: " + e.getMessage());
        }
    }
    
    private List<BannerDTO> convertirADTO(List<Banner> banners) {
        List<BannerDTO> dtos = new ArrayList<>();
        
        for (Banner b : banners) {
            dtos.add(convertirADTO(b));
        }
        
        return dtos;
    }
    
    private BannerDTO convertirADTO(Banner b) {
        BannerDTO dto = new BannerDTO();
        dto.setIdBanner(b.getIdBanner());
        dto.setIdJuego(b.getIdJuego());
        dto.setPosicion(b.getPosicion());
        dto.setActivo(b.isActivo());
        
        if (b.getFechaInicio() != null) {
            dto.setFechaInicio(b.getFechaInicio().toString());
        }
        
        if (b.getFechaFin() != null) {
            dto.setFechaFin(b.getFechaFin().toString());
        }
        
        dto.setTituloJuego(b.getTituloJuego());
        dto.setDescripcionJuego(b.getDescripcionJuego());
        
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
