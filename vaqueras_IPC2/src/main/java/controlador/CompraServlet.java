/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import daos.*;
import dtos.CompraDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jeffm
 */

@WebServlet ("/compras/*")
public class CompraServlet extends HttpServlet{
    private CarteraDAO carteraDAO;
    private LicenciaDAO licenciaDAO;
    private TransaccionDAO transaccionDAO;
    private ComisionDAO comisionDAO;
    private JuegoDAO juegoDAO;
    private UsuarioDAO usuarioDAO;
    private ObjectMapper objectMapper;
    private Gson gson;
    
    
    @Override
    public void init() throws ServletException {
        super.init();
        carteraDAO = new CarteraDAO();
        licenciaDAO = new LicenciaDAO();
        transaccionDAO = new TransaccionDAO();
        comisionDAO = new ComisionDAO();
        juegoDAO = new JuegoDAO();
        usuarioDAO = new UsuarioDAO();             
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());             
        gson = new Gson();
        
        System.out.println("CompraServlet inicializado");
    }
    
    //Realizar compras de juegos
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POST Compra de Juego");
        
        try {            
            BufferedReader reader = request.getReader();
            String json = reader.lines().collect(Collectors.joining());
            
            System.out.println("JSON recibido: " + json);
                       
            CompraDTO dto = objectMapper.readValue(json, CompraDTO.class);
            
            // validaciones
            
            if (dto.getIdUsuario() == null || dto.getIdUsuario() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID de usuario es obligatorio");
                return;
            }
            
            if (dto.getIdJuego() == null || dto.getIdJuego() <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El ID de juego es obligatorio");
                return;
            }
            
            if (dto.getFechaCompra() == null || dto.getFechaCompra().trim().isEmpty()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "La fecha de compra es obligatoria");
                return;
            }
            
            LocalDate fechaCompra = LocalDate.parse(dto.getFechaCompra());
            
            //Obtener datos del juego
            
            Juego juego = juegoDAO.buscarPorId(dto.getIdJuego());
            
            if (juego == null) {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                    "El juego no existe");
                return;
            }
            
            // Juego debe tener venta activa
            
            if (!juego.getVentaActiva()) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El juego no está disponible para la venta");
                return;
            }
            
            //  Usuario no debe tener ya la licencia
            
            if (licenciaDAO.tieneLicencia(dto.getIdUsuario(), dto.getIdJuego())) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El usuario ya posee este juego");
                return;
            }
            
            // Obtener datos del usuario
            
            Usuario usuario = usuarioDAO.obtenerUsuarioPorId(dto.getIdUsuario());
            
            if (usuario == null) {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, 
                    "El usuario no existe");
                return;
            }
            
            //  Validación de edad
            
            if (usuario.getFechaNacimiento() == null) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "El usuario no tiene fecha de nacimiento registrada");
                return;
            }
            
            if (!validarEdadParaCompra(usuario.getFechaNacimiento(), juego.getClasificacionPorEdad())) {
                int edad = calcularEdad(usuario.getFechaNacimiento());
                enviarError(response, HttpServletResponse.SC_FORBIDDEN, 
                    "El usuario no cumple con la edad mínima requerida. " +
                    "Edad actual: " + edad + " años. " +
                    "Clasificación del juego: " + juego.getClasificacionPorEdad());
                return;
            }
            
            // Usuario debe tener saldo suficiente
            
            if (!carteraDAO.tieneSaldoSuficiente(dto.getIdUsuario(), juego.getPrecio())) {
                Cartera cartera = carteraDAO.obtenerCartera(dto.getIdUsuario());
                double faltante = juego.getPrecio() - cartera.getSaldo();
                
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Saldo insuficiente. Precio del juego: $" + juego.getPrecio() + 
                    " | Saldo actual: $" + cartera.getSaldo() + 
                    " | Faltante: $" + faltante);
                return;
            }
            
            // CÁLCULO DE COMISIONES
            
            double porcentajeComision = comisionDAO.obtenerComisionAplicable(juego.getIdEmpresa());
            double[] ganancias = comisionDAO.calcularGanancias(juego.getPrecio(), porcentajeComision);
            
            double gananciaEmpresa = ganancias[0];
            double gananciaPlataforma = ganancias[1];
            
            System.out.println("Comisión aplicada: " + porcentajeComision + "%");
            System.out.println("Ganancia empresa: $" + gananciaEmpresa);
            System.out.println("Ganancia plataforma: $" + gananciaPlataforma);
            
            // PROCESO DE COMPRA (TODO EN TRANSACCIÓN)
            
            boolean compraExitosa = realizarCompraCompleta(
                dto.getIdUsuario(),
                dto.getIdJuego(),
                fechaCompra,
                juego.getPrecio(),
                porcentajeComision,
                gananciaEmpresa,
                gananciaPlataforma
            );
            
            if (compraExitosa) {
                // Obtener nuevo saldo
                Cartera cartera = carteraDAO.obtenerCartera(dto.getIdUsuario());
                
                Map<String, Object> datosRespuesta = new HashMap<>();
                datosRespuesta.put("juego", juego.getTitulo());
                datosRespuesta.put("precio", juego.getPrecio());
                datosRespuesta.put("comisionAplicada", porcentajeComision);
                datosRespuesta.put("nuevoSaldo", cartera.getSaldo());
                datosRespuesta.put("fechaCompra", fechaCompra.toString());
                
                enviarRespuesta(response, HttpServletResponse.SC_CREATED, 
                    true, "Compra realizada exitosamente", datosRespuesta);
            } else {
                enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al procesar la compra. No se realizaron cargos.");
            }
            
        } catch (Exception e) {
            System.err.println("Error en POST Compra: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar la compra: " + e.getMessage());
        }                
    }
    
    //Realiza la compra en una transaccion
     private boolean realizarCompraCompleta(int idUsuario, int idJuego, LocalDate fechaCompra,
                                          double precio, double comision, 
                                          double gananciaEmpresa, double gananciaPlataforma) {
        
        ConnectionMySQL connMySQL = new ConnectionMySQL();
        Connection conn = null;
        
        try {
            conn = connMySQL.conectar();
            conn.setAutoCommit(false);
            
            System.out.println("Iniciando transacción de compra...");
            
            // Descontar saldo de la cartera
            boolean saldoDescontado = carteraDAO.decrementarSaldo(idUsuario, precio);
            
            if (!saldoDescontado) {
                System.err.println("Error al descontar saldo");
                conn.rollback();
                return false;
            }
            
            // Crear licencia
            Licencia licencia = new Licencia(idUsuario, idJuego, fechaCompra);
            boolean licenciaCreada = licenciaDAO.crearLicencia(licencia);
            
            if (!licenciaCreada) {
                System.err.println("Error al crear licencia");
                conn.rollback();
                return false;
            }
            
            // Registrar transacción de COMPRA
            Transaccion transaccion = new Transaccion(
                idUsuario,
                precio,
                Transaccion.TipoTransaccion.COMPRA,
                fechaCompra
            );
            
            transaccion.setComisionAplicada(comision);
            transaccion.setGananciaEmpresa(gananciaEmpresa);
            transaccion.setGananciaPlataforma(gananciaPlataforma);
            
            boolean transaccionRegistrada = transaccionDAO.registrarTransaccion(transaccion);
            
            if (!transaccionRegistrada) {
                System.err.println("Error al registrar transacción");
                conn.rollback();
                return false;
            }
            
            // Si todo salio bien entonces se confirma la transacción
            conn.commit();
            System.out.println("Compra completada exitosamente");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error en transacción de compra: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Rollback realizado");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            return false;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
     
    //Valida la edad requerida para la compra
    private boolean validarEdadParaCompra(LocalDate fechaNacimiento, String clasificacion) {
        int edad = calcularEdad(fechaNacimiento);
        
        switch (clasificacion.toUpperCase()) {
            case "E":
                return true; // Sin restriccion
            case "T":
                return edad >= 12; // adolescentes
            case "M":
                return edad >= 16; // jovenes
            case "AO":
                return edad >= 18; // juegos solo para adultos
            default:
                System.err.println(" Clasificación desconocida: " + clasificacion);
                return false;
        }
    } 
    
    //Calcula edad
     private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
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
