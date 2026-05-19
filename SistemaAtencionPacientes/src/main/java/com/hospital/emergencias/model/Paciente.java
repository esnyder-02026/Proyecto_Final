package com.hospital.emergencias.model;

import java.time.LocalDateTime;

// Implementamos Comparable para que la PriorityQueue sepa cómo ordenar
public class Paciente implements Comparable<Paciente> {
    private String nombreCompleto;
    private int edad;
    private String dpi;
    private String sintomas;
    private PriorityLevel prioridad;
    private LocalDateTime horaIngreso;

    public Paciente(String nombreCompleto, int edad, String dpi, String sintomas, PriorityLevel prioridad) {
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.dpi = dpi;
        this.sintomas = sintomas;
        this.prioridad = prioridad;
        this.horaIngreso = LocalDateTime.now();
    }

    // logica de comparacion (Para que la cola funcione)
    @Override
    public int compareTo(Paciente otro) {
        // 1. Comparar por nivel de prioridad
        int res = Integer.compare(this.prioridad.getValue(), otro.prioridad.getValue());
        
        // 2. Si tienen la misma prioridad, el que llegó primero va antes
        if (res == 0) {
            return this.horaIngreso.compareTo(otro.horaIngreso);
        }
        return res;
    }

    // GETTERS son necesarios para que el Controller lea los datos y los guarde en la BD
    
    public String getNombreCompleto() { 
        return nombreCompleto; 
    }

    public int getEdad() { 
        return edad; 
    }

    public String getDpi() { 
        return dpi; 
    }

    public String getSintomas() { 
        return sintomas; 
    }

    public PriorityLevel getPrioridad() { 
        return prioridad; 
    }

    public LocalDateTime getHoraIngreso() { 
        return horaIngreso; 
    }
}
