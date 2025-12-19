/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import daos.EmpresaDAO;
import dtos.EmpresaDTO;
import com.mycompany.vaqueras_ipc2.modelo.Empresa;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author jeffm
 */
public class EmpresaServicio {
    private EmpresaDAO empresaDAO;
    
    public EmpresaServicio() {
        this.empresaDAO = new EmpresaDAO();
    }
    
    public List<EmpresaDTO> listarTodas() {
        List<Empresa> empresas = empresaDAO.listarTodas();
        return convertirADTOs(empresas);
    }
    
    public EmpresaDTO buscarPorId(Integer idEmpresa) {
        Empresa empresa = empresaDAO.buscarPorId(idEmpresa);
        return empresa != null ? convertirADTO(empresa) : null;
    }
    
    public boolean crear(Empresa empresa) {
        return empresaDAO.crear(empresa);
    }
    
    public boolean actualizar(Empresa empresa) {
        return empresaDAO.actualizar(empresa);
    }
    
    public boolean eliminar(Integer idEmpresa) {
        return empresaDAO.eliminar(idEmpresa);
    }
    
    public List<EmpresaDTO> buscarPorNombre(String nombre) {
        List<Empresa> empresas = empresaDAO.buscarPorNombre(nombre);
        return convertirADTOs(empresas);
    }
    
    private EmpresaDTO convertirADTO(Empresa empresa) {
        return new EmpresaDTO(
            empresa.getIdEmpresa(),
            empresa.getNombre(),
            empresa.getDescripcion()
        );
    }
    
    private List<EmpresaDTO> convertirADTOs(List<Empresa> empresas) {
        List<EmpresaDTO> dtos = new ArrayList<>();
        for (Empresa empresa : empresas) {
            dtos.add(convertirADTO(empresa));
        }
        return dtos;
    }
}
