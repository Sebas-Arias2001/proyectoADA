package com.example.geom.algoritmos;

import com.example.geom.model.Punto;

/**
 * Métodos para calcular área de triángulos y polígonos simples.
 */
public class CalculoArea {

    /**
     * Área de triángulo dados 3 puntos (fórmula de la mitad del producto cruzado).
     */
    public static double areaTriangulo(Punto a, Punto b, Punto c) {
        // |(b - a) x (c - a)| / 2
        double x1 = b.getX() - a.getX();
        double y1 = b.getY() - a.getY();
        double x2 = c.getX() - a.getX();
        double y2 = c.getY() - a.getY();
        double cross = Math.abs(x1 * y2 - y1 * x2);
        return cross / 2.0;
    }

    /**
     * Área de un paralelogramo dado por 4 puntos asumiendo orden a-b-c-d,
     * para rectángulos/squares usamos dividir en dos triángulos.
     */
    public static double areaCuadrilateroPorTriangulos(Punto a, Punto b, Punto c, Punto d) {
        // dividir en triángulos (a,b,c) y (a,c,d)
        return areaTriangulo(a, b, c) + areaTriangulo(a, c, d);
    }
}
