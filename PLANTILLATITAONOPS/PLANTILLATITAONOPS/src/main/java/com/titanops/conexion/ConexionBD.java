package com.titanops.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static ConexionBD instancia;
    private Connection conexion;

    // Parametros del Session Pooler de Supabase (Compatible con IPv4)
    private static final String HOST = "aws-0-us-east-1.pooler.supabase.com";
    private static final String PUERTO = "5432";
    private static final String BASE_DATOS = "postgres";
    private static final String USUARIO = "postgres.geypjkymrzkqcvgvqfhn";
    private static final String PASSWORD = "TitanOps-Logistics-2026";

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS + "?sslmode=require";

    // 1. Constructor privado (Singleton)
    private ConexionBD() {
        conectar();
    }

    // 2. Metodo interno de conexion
    private void conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[DB] Conexion establecida exitosamente con Supabase.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] Driver PostgreSQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error al conectar con Supabase: " + e.getMessage());
        }
    }

    // 3. Acceso global a la unica instancia
    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // 4. Retorno de la conexion activa
    public Connection getConexion() {
        try {
            if (this.conexion == null || this.conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error verificando el estado de la conexion: " + e.getMessage());
        }
        return this.conexion;
    }

    // 5. Cierre seguro
    public void cerrarConexion() {
        if (this.conexion != null) {
            try {
                this.conexion.close();
                System.out.println("[DB] Conexion cerrada con exito.");
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Error cerrando conexion: " + e.getMessage());
            }
        }
    }

    // Metodo main de prueba inmediata
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexion...");
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn != null) {
            System.out.println("Prueba superada! El Singleton y la base de datos estan conectados.");
        } else {
            System.err.println("Fallo en la prueba de conexion.");
        }
    }
}