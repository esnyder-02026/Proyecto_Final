package com.hospital.emergencias.model;

/**
 *    Enum que gestiona los niveles de prioridad.
 */
public enum PriorityLevel {
    // a menor valor numérico, mayor prioridad médica
    CRITICO(1, "Crítico"), 
    ALTO(2, "Alto"),    
    MEDIO(3, "Medio"),   
    BAJO(4, "Bajo");    

    private final int valor;
    private final String nombreEs;

    // constructor del Enum
    PriorityLevel(int valor, String nombreEs) {
        this.valor = valor;
        this.nombreEs = nombreEs;
    }

    public int getValue() {
        return valor;
    }

    // este método servirá para mostrar el nombre bonito en el ComboBox
    @Override
    public String toString() {
        return nombreEs;
    }
}