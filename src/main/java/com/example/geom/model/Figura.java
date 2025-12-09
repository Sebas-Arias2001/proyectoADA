package com.example.geom.model;

import java.util.List;

/**
 * Representa una figura detectada: tipo, puntos constituyentes y area.
 */
public class Figura {
    private final TipoFigura tipo;
    private final List<Punto> puntos;
    private final double area;

    public Figura(TipoFigura tipo, List<Punto> puntos, double area) {
        this.tipo = tipo;
        this.puntos = puntos;
        this.area = area;
    }

    public TipoFigura getTipo() { return tipo; }
    public List<Punto> getPuntos() { return puntos; }
    public double getArea() { return area; }

    @Override
    public String toString() {
        return tipo + " area=" + String.format("%.2f", area) + " pts=" + puntos;
    }
}
