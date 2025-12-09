package com.example.geom.algoritmos;

import com.example.geom.model.Figura;
import com.example.geom.model.Punto;
import com.example.geom.model.TipoFigura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Detector de figuras geométricas usando combinaciones de puntos.
 */
public class DetectorFiguras {

    public static List<Figura> detectarFiguras(List<Punto> puntos) {
        List<Figura> resultado = new ArrayList<>();

        int n = puntos.size();

        // Detectar triángulos (combinaciones de 3 puntos)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    Punto a = puntos.get(i);
                    Punto b = puntos.get(j);
                    Punto c = puntos.get(k);
                    if (!colineales(a, b, c)) {
                        if (esTrianguloRectangulo(a, b, c)) {
                            double area = CalculoArea.areaTriangulo(a, b, c);
                            resultado.add(new Figura(TipoFigura.TRIANGULO_RECTANGULO, List.of(a, b, c), area));
                        } else if (esTrianguloAcutangulo(a, b, c)) {
                            double area = CalculoArea.areaTriangulo(a, b, c);
                            resultado.add(new Figura(TipoFigura.TRIANGULO_ACUTANGULO, List.of(a, b, c), area));
                        }
                    }
                }
            }
        }

        // Detectar cuadriláteros (combinaciones de 4 puntos)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int l = k + 1; l < n; l++) {
                        Punto p1 = puntos.get(i);
                        Punto p2 = puntos.get(j);
                        Punto p3 = puntos.get(k);
                        Punto p4 = puntos.get(l);

                        List<Punto> combo = List.of(p1, p2, p3, p4);
                        if (esRectanguloOrdenado(combo)) {
                            List<Punto> orden = ordenarConvexo(combo);
                            double area = CalculoArea.areaCuadrilateroPorTriangulos(orden.get(0), orden.get(1), orden.get(2), orden.get(3));
                            if (esCuadrado(orden)) {
                                resultado.add(new Figura(TipoFigura.CUADRADO, orden, area));
                            } else {
                                resultado.add(new Figura(TipoFigura.RECTANGULO, orden, area));
                            }
                        }
                    }
                }
            }
        }

        // Ordenar resultado por área (menor a mayor)
        Collections.sort(resultado, (a, b) -> Double.compare(a.getArea(), b.getArea()));

        return resultado;
    }

    private static boolean colineales(Punto a, Punto b, Punto c) {
        double area2 = Math.abs((b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX()));
        return Math.abs(area2) < 1e-9;
    }

    public static boolean esTrianguloRectangulo(Punto a, Punto b, Punto c) {
        Punto ab = b.menos(a), ac = c.menos(a);
        Punto ba = a.menos(b), bc = c.menos(b);
        Punto ca = a.menos(c), cb = b.menos(c);

        return casiCero(ab.dot(ac)) || casiCero(ba.dot(bc)) || casiCero(ca.dot(cb));
    }

    public static boolean esTrianguloAcutangulo(Punto a, Punto b, Punto c) {
        Punto ab = b.menos(a), ac = c.menos(a);
        Punto ba = a.menos(b), bc = c.menos(b);
        Punto ca = a.menos(c), cb = b.menos(c);

        return (ab.dot(ac) > 0) && (ba.dot(bc) > 0) && (ca.dot(cb) > 0);
    }

    private static boolean esRectanguloOrdenado(List<Punto> combo) {
        List<Punto> ord = ordenarConvexo(combo);
        if (ord == null) return false;

        Punto v0 = ord.get(1).menos(ord.get(0));
        Punto v1 = ord.get(2).menos(ord.get(1));
        Punto v2 = ord.get(3).menos(ord.get(2));
        Punto v3 = ord.get(0).menos(ord.get(3));

        // comprobación ángulos rectos por producto punto
        boolean angulosRectos = casiCero(v0.dot(v1)) && casiCero(v1.dot(v2)) && casiCero(v2.dot(v3)) && casiCero(v3.dot(v0));

        double area = CalculoArea.areaCuadrilateroPorTriangulos(ord.get(0), ord.get(1), ord.get(2), ord.get(3));
        return angulosRectos && area > 1e-9;
    }

    private static boolean esCuadrado(List<Punto> orden) {
        Punto a = orden.get(0), b = orden.get(1), c = orden.get(2), d = orden.get(3);
        double d1 = a.distanciaA(b);
        double d2 = b.distanciaA(c);
        double d3 = c.distanciaA(d);
        double d4 = d.distanciaA(a);
        boolean ladosIguales = casiIgual(d1, d2) && casiIgual(d2, d3) && casiIgual(d3, d4);
        boolean anguloRecto = casiCero((b.menos(a)).dot(c.menos(b))) && casiCero((c.menos(b)).dot(d.menos(c)));
        return ladosIguales && anguloRecto;
    }

    private static List<Punto> ordenarConvexo(List<Punto> puntos) {
        if (puntos.size() != 4) return null;
        double cx = 0, cy = 0;
        for (Punto p : puntos) {
            cx += p.getX();
            cy += p.getY();
        }
        cx /= 4;
        cy /= 4;
        final double centerX = cx, centerY = cy;
        List<Punto> copia = new ArrayList<>(puntos);
        copia.sort((p1, p2) -> {
            double ang1 = Math.atan2(p1.getY() - centerY, p1.getX() - centerX);
            double ang2 = Math.atan2(p2.getY() - centerY, p2.getX() - centerX);
            return Double.compare(ang1, ang2);
        });
        return copia;
    }

    private static boolean casiCero(double v) {
        return Math.abs(v) < 1e-8;
    }

    private static boolean casiIgual(double a, double b) {
        return Math.abs(a - b) < 1e-6;
    }
}
