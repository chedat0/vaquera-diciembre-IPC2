/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.EmpresaDTO;
import dtos.VerificadorDTO;
import com.mycompany.vaqueras_ipc2.modelo.Empresa;
import servicios.EmpresaServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jeffm
 */
@WebServlet("/empresas/*")
public class EmpresaServlet extends HttpServlet {

    private EmpresaServicio empresaServicio;
    private ObjectMapper mapper;
    private Gson gson;

    @Override
    public void init() {
        empresaServicio = new EmpresaServicio();
        gson = new Gson();
        mapper = new ObjectMapper();
        System.out.println("EmpresaServlet inicializado correctamente");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            System.out.println("GET /empresas - PathInfo: " + pathInfo);

            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /empresas → Listar todas
                List<EmpresaDTO> empresas = empresaServicio.listarTodas();
                System.out.println("Empresas encontradas: " + empresas.size());

                // ENVIAR RESPUESTA AL FRONTEND
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresas obtenidas", empresas);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }

            String[] parts = pathInfo.split("/");
            String segment1 = parts.length > 1 ? parts[1] : "";

            if (segment1.equals("nombre")) {
                // GET /empresas/nombre?nombre=X → Buscar por nombre
                String nombreParam = request.getParameter("nombre");

                if (nombreParam == null || nombreParam.trim().isEmpty()) {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'nombre' requerido");
                    return;
                }

                List<EmpresaDTO> empresas = empresaServicio.buscarPorNombre(nombreParam);

                //  ENVIAR RESPUESTA
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresas encontradas", empresas);
                response.getWriter().write(gson.toJson(respuesta));

            } else {
                // GET /empresas/{id} → Buscar por ID
                try {
                    int id = Integer.parseInt(segment1);

                    if (id <= 0) {
                        enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser mayor a 0");
                        return;
                    }

                    EmpresaDTO empresa = empresaServicio.buscarPorId(id);

                    if (empresa != null) {
                        // ENVIAR RESPUESTA
                        VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa encontrada", empresa);
                        response.getWriter().write(gson.toJson(respuesta));
                    } else {
                        enviarError(response, HttpServletResponse.SC_NOT_FOUND, "Empresa no encontrada");
                    }

                } catch (NumberFormatException e) {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser un número válido");
                }
            }

        } catch (Exception e) {
            System.err.println("Error en doGet: " + e.getMessage());
            e.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            BufferedReader reader = request.getReader();
            Empresa empresa = gson.fromJson(reader, Empresa.class);

            // Validaciones
            if (empresa == null) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Datos de empresa inválidos", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }

            if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }

            if (empresa.getNombre().length() > 100) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre no puede exceder 100 caracteres", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }

            // Crear empresa
            boolean creada = empresaServicio.crear(empresa);

            if (creada) {
                System.out.println("Empresa creada: " + empresa.getNombre());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa creada exitosamente", empresa);
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al crear empresa", null);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(respuesta));
            }

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));

        } catch (Exception e) {
            System.err.println("Error en doPost: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID es requerido en la URL");
                return;
            }

            String[] parts = pathInfo.split("/");
            String idParam = parts.length > 1 ? parts[1] : "";

            Integer id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser un número válido");
                return;
            }

            BufferedReader reader = request.getReader();
            Empresa empresa = gson.fromJson(reader, Empresa.class);

            if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
                VerificadorDTO respuesta = new VerificadorDTO(false, "El nombre es requerido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
                return;
            }

            // Actualizar empresa
            boolean actualizada = empresaServicio.actualizar(empresa);

            if (actualizada) {
                System.out.println("Empresa actualizada: " + empresa.getNombre());
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa actualizada exitosamente", null);
                response.getWriter().write(gson.toJson(respuesta));
            } else {
                VerificadorDTO respuesta = new VerificadorDTO(false, "Error al actualizar empresa", null);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(respuesta));
            }

        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error: JSON inválido - " + e.getMessage());
            VerificadorDTO respuesta = new VerificadorDTO(false, "Formato JSON inválido", null);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(respuesta));

        } catch (Exception e) {
            System.err.println("Error en doPut: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID es requerido en la URL");
                return;
            }

            String[] parts = pathInfo.split("/");
            String idParam = parts.length > 1 ? parts[1] : "";

            try {
                Integer id = Integer.parseInt(idParam);

                if (id <= 0) {
                    enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser mayor a 0");
                    return;
                }

                boolean eliminada = empresaServicio.eliminar(id);

                if (eliminada) {
                    System.out.println("Empresa eliminada: ID " + id);
                    VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa eliminada", null);
                    response.getWriter().write(gson.toJson(respuesta));
                } else {
                    VerificadorDTO respuesta = new VerificadorDTO(false, "Error al eliminar empresa", null);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(gson.toJson(respuesta));
                }

            } catch (NumberFormatException e) {
                System.err.println("Error: ID inválido - " + idParam);
                VerificadorDTO respuesta = new VerificadorDTO(false, "ID debe ser un número válido", null);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(respuesta));
            }

        } catch (Exception e) {
            System.err.println("Error en doDelete: " + e.getMessage());
            e.printStackTrace();
            VerificadorDTO respuesta = new VerificadorDTO(false, "Error: " + e.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(respuesta));
        }
    }

    private void buscarPorId(String idParam, HttpServletResponse response) throws IOException {
        try {
            Integer id = Integer.parseInt(idParam);

            if (id <= 0) {
                enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser mayor a 0");
                return;
            }

            EmpresaDTO empresa = empresaServicio.buscarPorId(id);

            if (empresa != null) {
                VerificadorDTO respuesta = new VerificadorDTO(true, "Empresa encontrada", empresa);
                response.getWriter().write(mapper.writeValueAsString(respuesta));
            } else {
                enviarError(response, HttpServletResponse.SC_NOT_FOUND, "Empresa no encontrada");
            }

        } catch (NumberFormatException e) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser un número válido");
        }
    }

    private void enviarError(HttpServletResponse response, int status, String mensaje)
            throws IOException {
        VerificadorDTO respuesta = new VerificadorDTO(false, mensaje, null);
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(respuesta));
    }
}
