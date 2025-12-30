/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.UsuarioAdminDAO;
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
@WebServlet ("/usuarios-admin/*")
public class UsuardioAdminServlet extends HttpServlet{
    private UsuarioAdminDAO adminDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        adminDAO = new UsuarioAdminDAO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());        
        gson = new Gson();
        
        System.out.println("UsuarioAdminServlet inicializado");
    }
    
    //Obtener lista de administradores
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Usuarios Admin - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todos
                List<Usuario> admins = adminDAO.obtenerTodosAdmins();
                List<Map<String, Object>> adminsDTO = convertirAMapas(admins);
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Administradores obtenidos", adminsDTO);
                
            } else {
                // Obtener uno específico
                String[] pathParts = pathInfo.split("/");
                int idUsuario = Integer.parseInt(pathParts[1]);
                
                Usuario admin = adminDAO.obtenerAdminPorId(idUsuario);
                Map<String, Object> adminDTO = convertirAMapa(admin); 
                if (admin == null) {
                    enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                        "Usuario administrador no encontrado");
                    return;
                }
                                
                
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Administrador obtenido", adminDTO);
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Crear nuevo administrador
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println(" POST Crear Administrador");
        
        try {
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
                        
            Usuario usuario = objectMapper.readValue(json, Usuario.class);
            
            // Validaciones
            if (usuario.getNickname() == null || usuario.getNickname().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El nickname es obligatorio");
                return;
            }
            
            if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El correo es obligatorio");
                return;
            }
            
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La contraseña es obligatoria");
                return;
            }
            
            if (usuario.getFechaNacimiento() == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha de nacimiento es obligatoria");
                return;
            }
            
            // Crear administrador 
            boolean exito = adminDAO.crearUsuarioAdmin(usuario);
            
            if (exito) {
                Map<String, Object> respuestaData = new HashMap<>();
                respuestaData.put("idUsuario", usuario.getIdUsuario());
                respuestaData.put("nickname", usuario.getNickname());
                respuestaData.put("correo", usuario.getCorreo());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Administrador creado correctamente", respuestaData);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear el administrador. Verifique que el correo no esté duplicado.");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Admin: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Actualizar administrador
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Admin - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el ID del usuario");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 2) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
                return;
            }
            
            int idUsuario = Integer.parseInt(pathParts[1]);
            
            if (pathParts.length >= 3 && pathParts[2].equals("password")) {
                // Cambiar contraseña
                cambiarPassword(request, response, idUsuario);
            } else {
                // Actualizar datos
                actualizarDatos(request, response, idUsuario);
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Eliminar administrador
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println(" DELETE Admin - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el ID del usuario");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            int idUsuario = Integer.parseInt(pathParts[1]);
            
            boolean exito = adminDAO.eliminarAdmin(idUsuario);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Administrador eliminado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "No se puede eliminar (podría ser el último administrador)");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Actualizar datos
    private void actualizarDatos(HttpServletRequest request, HttpServletResponse response, 
                                 int idUsuario) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        try {
            Usuario usuario = objectMapper.readValue(json, Usuario.class);
            usuario.setIdUsuario(idUsuario);
            
            boolean exito = adminDAO.actualizarAdmin(usuario);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Administrador actualizado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al actualizar el administrador");
            }
            
        } catch (Exception e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Error al procesar datos: " + e.getMessage());
        }
    }
    
    //Cambiar o actualizar contraseña
     private void cambiarPassword(HttpServletRequest request, HttpServletResponse response, 
                                int idUsuario) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        Map<String, String> datos = gson.fromJson(json, Map.class);
        String password = datos.get("password");
        
        if (password == null || password.trim().isEmpty()) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La contraseña es obligatoria");
            return;
        }
        
        boolean exito = adminDAO.actualizarPasswordAdmin(idUsuario, password);
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Contraseña actualizada correctamente", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar la contraseña");
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
