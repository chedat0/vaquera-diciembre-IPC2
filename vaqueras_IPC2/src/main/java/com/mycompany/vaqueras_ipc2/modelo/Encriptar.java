/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

import org.mindrot.jbcrypt.BCrypt;
/**
 *
 * @author jeffm
 */
public class Encriptar {
    
    //Se encripta la contraseña
    public static String hashPassword(String contraseñaPlana){
        return BCrypt.hashpw(contraseñaPlana, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String contraseñaPlana, String constraseñaHasheada){
        try {
            return BCrypt.checkpw(contraseñaPlana, constraseñaHasheada);
        } catch (Exception e) {
            return false;
        }
    }
}
