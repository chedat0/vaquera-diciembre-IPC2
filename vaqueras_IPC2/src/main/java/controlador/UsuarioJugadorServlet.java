/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.UsuarioDAO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */

@WebServlet("/jugadores/*")
public class UsuarioJugadorServlet extends HttpServlet{
    private UsuarioDAO usuarioDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        usuarioDAO = new UsuarioDAO();                
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());                
        gson = new Gson();        
        
        System.out.println(" UsuarioJugadorServlet inicializado");        
    }
    
    //Lista de jugadores
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        String buscar = request.getParameter("buscar");
        
        System.out.println("GET Jugadores - Path: " + pathInfo + " | Buscar: " + buscar);
        
        try {
            if (buscar != null && !buscar.trim().isEmpty()) {
                // Buscar por nickname
                List<Usuario> jugadores = usuarioDAO.buscarJugadoresPorNickname(buscar);
                List<Map<String, Object>> jugadoresDTO = convertirAMapas(jugadores);
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Jugadores encontrados", jugadoresDTO);
                
            } else if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todos
                List<Usuario> jugadores = usuarioDAO.obtenerTodosJugadores();
                List<Map<String, Object>> jugadoresDTO = convertirAMapas(jugadores);
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Jugadores obtenidos", jugadoresDTO);
                
            } else {
                // Obtener uno específico
                String[] pathParts = pathInfo.split("/");
                int idUsuario = Integer.parseInt(pathParts[1]);
                
                Usuario jugador = usuarioDAO.obtenerUsuarioPorId(idUsuario);
                
                if (jugador == null || jugador.getIdRol() != 3) {
                    enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                        "Jugador no encontrado");
                    return;
                }
                
                Map<String, Object> jugadorDTO = convertirAMapa(jugador);
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Jugador obtenido", jugadorDTO);
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Actualizar jugador
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Jugador - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el ID del jugador");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            int idUsuario = Integer.parseInt(pathParts[1]);
            
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            Usuario usuario = objectMapper.readValue(json, Usuario.class);
            usuario.setIdUsuario(idUsuario);
            
            boolean exito = usuarioDAO.actualizarJugador(usuario);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Jugador actualizado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al actualizar el jugador");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Error al procesar datos: " + e.getMessage());
        }
    }
    
    //Eliminar jugador
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println(" DELETE Jugador - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el ID del jugador");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            int idUsuario = Integer.parseInt(pathParts[1]);
            
            boolean exito = usuarioDAO.eliminarJugador(idUsuario);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Jugador eliminado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al eliminar el jugador");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    private Map<String, Object> convertirAMapa(Usuario u) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("idUsuario", u.getIdUsuario());
        mapa.put("nickname", u.getNickname());
        mapa.put("correo", u.getCorreo());
        mapa.put("fechaNacimiento", u.getFechaNacimiento() != null
                ? u.getFechaNacimiento().toString() : null);
        mapa.put("telefono", u.getTelefono());
        mapa.put("pais", u.getPais());
        mapa.put("bibliotecaPublica", u.getBibliotecaPublica());
        mapa.put("idRol", u.getIdRol());

        return mapa;
    }

    //Método auxiliar para convertir lista de Usuarios
    private List<Map<String, Object>> convertirAMapas(List<Usuario> usuarios) {
        List<Map<String, Object>> mapas = new ArrayList<>();

        for (Usuario u : usuarios) {
            mapas.add(convertirAMapa(u));
        }

        return mapas;
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
