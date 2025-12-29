/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.UsuarioEmpresaDAO;
import dtos.UsuarioEmpresaDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.UsuarioEmpresa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jeffm
 */
@WebServlet("/usuarios-empresa/*")
public class UsuarioEmpresaServlet extends HttpServlet {
    
    private UsuarioEmpresaDAO usuarioEmpresaDAO;
    private ObjectMapper objectMapper;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        usuarioEmpresaDAO = new UsuarioEmpresaDAO();                
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());              
        gson = new Gson();        
        System.out.println("UsuarioEmpresaServlet inicializado");
    }
    
    //Obtener usuarios de empresa
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Usuarios Empresa - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar la empresa o usuario");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length >= 3 && pathParts[1].equals("empresa")) {
                // /usuarios-empresa/empresa/{idEmpresa}
                int idEmpresa = Integer.parseInt(pathParts[2]);
                listarUsuariosPorEmpresa(response, idEmpresa);
                
            } else if (pathParts.length >= 2) {
                // /usuarios-empresa/{idUsuario}
                int idUsuario = Integer.parseInt(pathParts[1]);
                obtenerUsuarioPorId(response, idUsuario);
                
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de URL no válido");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "ID debe ser un número válido");
        }
    }
    
    //Crear usuario de empresa
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Crear Usuario Empresa");
        
        try {
            // Leer JSON del request
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
            
            // Parsear con Jackson (por las fechas)
            UsuarioEmpresaDTO dto = objectMapper.readValue(json, UsuarioEmpresaDTO.class);
            
            // Validaciones
            if (dto.getCorreo() == null || dto.getCorreo().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El correo es obligatorio");
                return;
            }
            
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El nombre es obligatorio");
                return;
            }
            
            if (dto.getFechaNacimiento() == null || dto.getFechaNacimiento().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha de nacimiento es obligatoria");
                return;
            }
            
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La contraseña es obligatoria");
                return;
            }
            
            if (dto.getIdEmpresa() == null || dto.getIdEmpresa() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID de empresa es obligatorio");
                return;
            }
            
            // Convertir DTO a modelo
            UsuarioEmpresa usuario = new UsuarioEmpresa();
            usuario.setCorreo(dto.getCorreo().trim());
            usuario.setNombre(dto.getNombre().trim());
            usuario.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
            usuario.setPassword(dto.getPassword());
            usuario.setIdEmpresa(dto.getIdEmpresa());
            
            // Crear usuario
            boolean exito = usuarioEmpresaDAO.crearUsuarioEmpresa(usuario);
            
            if (exito) {
                // Preparar respuesta con datos del usuario creado
                UsuarioEmpresaDTO respuestaDTO = new UsuarioEmpresaDTO();
                respuestaDTO.setIdUsuario(usuario.getIdUsuario());
                respuestaDTO.setCorreo(usuario.getCorreo());
                respuestaDTO.setNombre(usuario.getNombre());
                respuestaDTO.setFechaNacimiento(usuario.getFechaNacimiento().toString());
                respuestaDTO.setIdEmpresa(usuario.getIdEmpresa());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Usuario de empresa creado correctamente", respuestaDTO);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear usuario de empresa. Verifique que el correo no esté registrado.");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Actualizar usuario de empresa
     @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("PUT Actualizar Usuario Empresa - Path: " + pathInfo);
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
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
            
            // Verificar si es actualización de password
            if (pathParts.length >= 3 && pathParts[2].equals("password")) {
                actualizarPassword(request, response, idUsuario);
            } else {
                actualizarDatos(request, response, idUsuario);
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Eliminar usuario de empresa
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("DELETE Usuario Empresa - Path: " + pathInfo);
        
        try {
            if (pathInfo == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Debe especificar el usuario y empresa");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            
            if (pathParts.length < 4 || !pathParts[2].equals("empresa")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato: /usuarios-empresa/{idUsuario}/empresa/{idEmpresa}");
                return;
            }
            
            int idUsuario = Integer.parseInt(pathParts[1]);
            int idEmpresa = Integer.parseInt(pathParts[3]);
            
            boolean exito = usuarioEmpresaDAO.eliminarUsuarioEmpresa(idUsuario, idEmpresa);
            
            if (exito) {
                enviarRespuesta(response, HttpServletResponse.SC_OK, 
                    true, "Usuario eliminado correctamente", null);
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "No se pudo eliminar el usuario. Verifique que pertenezca a la empresa y no sea el único usuario.");
            }
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
     
    
    private void listarUsuariosPorEmpresa(HttpServletResponse response, int idEmpresa) 
            throws IOException {
        
        List<UsuarioEmpresa> usuarios = usuarioEmpresaDAO.obtenerUsuariosPorEmpresa(idEmpresa);
        
        // Convertir a DTOs
        List<UsuarioEmpresaDTO> usuariosDTO = new ArrayList<>();
        
        for (UsuarioEmpresa u : usuarios) {
            UsuarioEmpresaDTO dto = new UsuarioEmpresaDTO();
            dto.setIdUsuario(u.getIdUsuario());
            dto.setCorreo(u.getCorreo());
            dto.setNombre(u.getNombre());
            dto.setFechaNacimiento(u.getFechaNacimiento().toString());
            dto.setIdEmpresa(u.getIdEmpresa());
            usuariosDTO.add(dto);
        }
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Usuarios obtenidos correctamente", usuariosDTO);
    }
    
    private void obtenerUsuarioPorId(HttpServletResponse response, int idUsuario) 
            throws IOException {
        
        UsuarioEmpresa usuario = usuarioEmpresaDAO.obtenerUsuarioEmpresaPorId(idUsuario);
        
        if (usuario == null) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "Usuario no encontrado");
            return;
        }
        
        // Convertir a DTO
        UsuarioEmpresaDTO dto = new UsuarioEmpresaDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setCorreo(usuario.getCorreo());
        dto.setNombre(usuario.getNombre());
        dto.setFechaNacimiento(usuario.getFechaNacimiento().toString());
        dto.setIdEmpresa(usuario.getIdEmpresa());
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Usuario obtenido correctamente", dto);
    }
    
    private void actualizarDatos(HttpServletRequest request, HttpServletResponse response, 
                                 int idUsuario) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        UsuarioEmpresaDTO dto = objectMapper.readValue(json, UsuarioEmpresaDTO.class);
        
        UsuarioEmpresa usuario = new UsuarioEmpresa();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(dto.getNombre());
        usuario.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        
        boolean exito = usuarioEmpresaDAO.actualizarUsuarioEmpresa(usuario);
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Usuario actualizado correctamente", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar usuario");
        }
    }
    
    private void actualizarPassword(HttpServletRequest request, HttpServletResponse response, 
                                    int idUsuario) throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        UsuarioEmpresaDTO dto = objectMapper.readValue(json, UsuarioEmpresaDTO.class);
        
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La contraseña es obligatoria");
            return;
        }
        
        boolean exito = usuarioEmpresaDAO.actualizarPasswordUsuarioEmpresa(
            idUsuario, dto.getPassword());
        
        if (exito) {
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Contraseña actualizada correctamente", null);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar contraseña");
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
