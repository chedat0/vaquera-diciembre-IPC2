/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.CarteraDAO;
import daos.TransaccionDAO;
import dtos.CarteraDTO;
import dtos.RecargaDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Cartera;
import com.mycompany.vaqueras_ipc2.modelo.Transaccion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;
/**
 *
 * @author jeffm
 */
@WebServlet ("/cartera/*")
public class CarteraServlet extends HttpServlet{
    private CarteraDAO carteraDAO;
    private TransaccionDAO transaccionDAO;
    private ObjectMapper objectMapper;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        carteraDAO = new CarteraDAO();
        transaccionDAO = new TransaccionDAO();                
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());               
        gson = new Gson();
        
        System.out.println("CarteraServlet inicializado");
    }
    
    //Consultar saldo cartera
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("GET Cartera - Path: " + pathInfo);
        
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
            
            Cartera cartera = carteraDAO.obtenerCartera(idUsuario);
            
            if (cartera == null) {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                    "Cartera no encontrada para el usuario");
                return;
            }            
            
            CarteraDTO dto = new CarteraDTO();
            dto.setIdUsuario(cartera.getIdUsuario());
            dto.setSaldo(cartera.getSaldo());
            
            enviarRespuesta(response, HttpServletResponse.SC_OK, 
                true, "Saldo obtenido correctamente", dto);
            
        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }
    
    //Recargar saldo
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("POST Cartera - Path: " + pathInfo);
        
        try {
            if (pathInfo != null && pathInfo.equals("/recargar")) {
                recargarSaldo(request, response);
            } else if (pathInfo != null && pathInfo.equals("/crear")) {
                crearCartera(request, response);
            } else {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Endpoint no válido. Use /cartera/recargar o /cartera/crear");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Cartera: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    //Crea cartera para un usuario
    private void crearCartera(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        CarteraDTO dto = gson.fromJson(json, CarteraDTO.class);
        
        if (dto.getIdUsuario() == null || dto.getIdUsuario() <= 0) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El ID de usuario es obligatorio");
            return;
        }
        
        // Verificar si ya existe
        if (carteraDAO.existeCartera(dto.getIdUsuario())) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El usuario ya tiene una cartera");
            return;
        }
        
        boolean exito = carteraDAO.crearCartera(dto.getIdUsuario());
        
        if (exito) {
            CarteraDTO respuestaDTO = new CarteraDTO();
            respuestaDTO.setIdUsuario(dto.getIdUsuario());
            respuestaDTO.setSaldo(0.0);
            
            enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                true, "Cartera creada correctamente", respuestaDTO);
        } else {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al crear la cartera");
        }
    }
    
    // Recarga saldo de la cartera de un usuario
    private void recargarSaldo(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining());
        
        System.out.println("JSON recibido: " + json);
                
        RecargaDTO dto = objectMapper.readValue(json, RecargaDTO.class);
        
        // Validaciones
        if (dto.getIdUsuario() == null || dto.getIdUsuario() <= 0) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El ID de usuario es obligatorio");
            return;
        }
        
        if (dto.getMonto() == null || dto.getMonto() <= 0) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "El monto debe ser mayor a cero");
            return;
        }
        
        if (dto.getFecha() == null || dto.getFecha().trim().isEmpty()) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                "La fecha es obligatoria");
            return;
        }
        
        // Verificar que la cartera existe
        if (!carteraDAO.existeCartera(dto.getIdUsuario())) {
            enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                "La cartera no existe para este usuario");
            return;
        }
        
        // Incrementar saldo
        boolean saldoActualizado = carteraDAO.incrementarSaldo(
            dto.getIdUsuario(), 
            dto.getMonto()
        );
        
        if (!saldoActualizado) {
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al actualizar el saldo");
            return;
        }
        
        // Registrar transacción de RECARGA
        Transaccion transaccion = new Transaccion(
            dto.getIdUsuario(),
            dto.getMonto(),
            Transaccion.TipoTransaccion.RECARGA,
            LocalDate.parse(dto.getFecha())
        );
        
        boolean transaccionRegistrada = transaccionDAO.registrarTransaccion(transaccion);
        
        if (!transaccionRegistrada) {
            System.err.println("Saldo actualizado pero transacción no registrada");
        }
        
        // Obtener nuevo saldo
        Cartera cartera = carteraDAO.obtenerCartera(dto.getIdUsuario());
        
        CarteraDTO respuestaDTO = new CarteraDTO();
        respuestaDTO.setIdUsuario(cartera.getIdUsuario());
        respuestaDTO.setSaldo(cartera.getSaldo());
        
        enviarRespuesta(response, HttpServletResponse.SC_OK, 
            true, "Recarga realizada correctamente", respuestaDTO);
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
