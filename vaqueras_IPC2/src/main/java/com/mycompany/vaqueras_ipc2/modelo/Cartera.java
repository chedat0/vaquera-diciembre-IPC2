/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vaqueras_ipc2.modelo;

/**
 *
 * @author jeffm
 */
public class Cartera {
    private int idUsuario;
    private double saldo;

    public Cartera() {        
    }

    public Cartera(int idUsuario, double saldo) {
        this.idUsuario = idUsuario;
        this.saldo = saldo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
   
}
