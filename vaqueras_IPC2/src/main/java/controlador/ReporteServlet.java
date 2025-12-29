/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import com.mycompany.vaqueras_ipc2.modelo.ConnectionMySQL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author jeffm
 */

@WebServlet ("/reportes/*")
public class ReporteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se especificó el tipo de reporte");
            return;
        }

        String tipoReporte = pathInfo.substring(1);

        try {
            generarReporte(tipoReporte, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte: " + e.getMessage());
        }
    }

    private void generarReporte(String tipoReporte, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ConnectionMySQL connmySQL = new ConnectionMySQL();
        Connection conn= null;
        InputStream jasperStream = null;

        try {
            conn = connmySQL.conectar();
            Map<String, Object> parametros = new HashMap<>();
            
            String archivoJasper = obtenerArchivoJasper(tipoReporte, request, parametros);

            // Cargar el archivo .jasper compilado
            jasperStream = getServletContext().getResourceAsStream("/WEB-INF/reportes/" + archivoJasper);
            if (jasperStream == null) {
                throw new Exception("No se encontró el archivo de reporte: " + archivoJasper);
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            // Llenar el reporte con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conn);
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + tipoReporte + ".pdf\"");

            // Exportar a PDF
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        } finally {
            if (jasperStream != null) {
                jasperStream.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private String obtenerArchivoJasper(String tipoReporte, HttpServletRequest request, Map<String, Object> parametros)
            throws Exception {

        switch (tipoReporte) {
            case "ganancias-globales":                
                parametros.put("FECHA_INICIO", Date.valueOf(request.getParameter("fechaInicio")));
                parametros.put("FECHA_FIN", Date.valueOf(request.getParameter("fechaFin")));
                return "R1_ganancias_globales.jasper";

            case "top-ventas-categoria":                
                parametros.put("FECHA_INICIO", Date.valueOf(request.getParameter("fechaInicio")));
                parametros.put("FECHA_FIN", Date.valueOf(request.getParameter("fechaFin")));
                return "R2_top_ventas_categoria.jasper";

            case "top-10-juegos":                
                return "R3_top_10_juegos.jasper";

            case "ranking-usuarios":                
                return "R4_ranking_usuarios.jasper";

            case "ventas-empresa":                
                parametros.put("EMPRESA_NOMBRE", request.getParameter("empresaNombre"));
                parametros.put("ID_EMPRESA", Integer.parseInt(request.getParameter("idEmpresa")));
                parametros.put("FECHA_INICIO", Date.valueOf(request.getParameter("fechaInicio")));
                parametros.put("FECHA_FIN", Date.valueOf(request.getParameter("fechaFin")));
                return "R5_ventas_propias_empresa.jasper";

            case "feedback-empresa":                
                parametros.put("EMPRESA_NOMBRE", request.getParameter("empresaNombre"));
                parametros.put("ID_EMPRESA", Integer.parseInt(request.getParameter("idEmpresa")));
                return "R6_feedback_empresa.jasper";

            case "top5-juegos-empresa":                
                parametros.put("EMPRESA_NOMBRE", request.getParameter("empresaNombre"));
                parametros.put("ID_EMPRESA", Integer.parseInt(request.getParameter("idEmpresa")));
                parametros.put("FECHA_INICIO", Date.valueOf(request.getParameter("fechaInicio")));
                parametros.put("FECHA_FIN", Date.valueOf(request.getParameter("fechaFin")));
                return "R7_top5_juegos_empresa.jasper";

            case "historial-gastos":                
                parametros.put("USUARIO_NICKNAME", request.getParameter("usuarioNickname"));
                parametros.put("ID_USUARIO", Integer.parseInt(request.getParameter("idUsuario")));
                parametros.put("FECHA_INICIO", Date.valueOf(request.getParameter("fechaInicio")));
                parametros.put("FECHA_FIN", Date.valueOf(request.getParameter("fechaFin")));
                return "R8_historial_gastos_usuario.jasper";

            case "analisis-biblioteca":                
                parametros.put("USUARIO_NICKNAME", request.getParameter("usuarioNickname"));
                parametros.put("ID_USUARIO", Integer.parseInt(request.getParameter("idUsuario")));
                return "R9_analisis_biblioteca_usuario.jasper";

            case "biblioteca-familiar":                
                parametros.put("USUARIO_NICKNAME", request.getParameter("usuarioNickname"));
                parametros.put("ID_USUARIO", Integer.parseInt(request.getParameter("idUsuario")));
                return "R10_uso_biblioteca_familiar.jasper";

            default:
                throw new Exception("Tipo de reporte no válido: " + tipoReporte);
        }
    }
}
