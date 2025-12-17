/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author jeffm
 */
public class ConnectionMySQL {
    private Connection conn;
    private String url = "jdbc:mysql://localhost:3306/proyecto_vaqueras";
    private String usuario = "root";
    private String password = "1234";

    public Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            conn = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexión exitosa");
            return conn;
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver: " + e.getMessage());
            return null;
        }
    }

    public void desconectar(Connection c) {
        if (c != null) {
            try {
                c.close();
                System.out.println("Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    public boolean testConnection (){
        Connection testConn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            testConn = DriverManager.getConnection(url,usuario,password);
            System.out.println("TEST DE CONEXIÓN: ÉXITO");
            return true;
        } catch (SQLException e) {
            System.out.println("Test de conexión falló. Error" + e.getMessage());
            return false;
        } catch (ClassNotFoundException e){
            System.err.println("Test de conexión: fallo. Driver no encontrado.");
            return false;
        } finally {
            if (testConn != null){
                try {
                    testConn.close();
                } catch (SQLException e){
                    
                }
            }
        }
    }
}
