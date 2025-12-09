package com.example.geom.data;

import com.example.geom.model.Punto;
import java.util.ArrayList;
import java.util.List;

public class CasosPrueba {

    public static List<List<Punto>> getCasos() {
        List<List<Punto>> casos = new ArrayList<>();

        // Caso 1: Cuadrado y Triangulos
        // (0,0), (4,0), (4,4), (0,4) -> Cuadrado
        // (0,0), (4,0), (0,3) -> Triangulo Rectangulo
        List<Punto> caso1 = new ArrayList<>();
        caso1.add(new Punto(0, 0));
        caso1.add(new Punto(4, 0));
        caso1.add(new Punto(4, 4));
        caso1.add(new Punto(0, 4));
        caso1.add(new Punto(0, 3));
        casos.add(caso1);

        // Caso 2: Rectangulo y Puntos extra
        // (1,1), (6,1), (6,4), (1,4) -> Rectangulo de 5x3
        // (2,2) -> Punto interno/extra
        List<Punto> caso2 = new ArrayList<>();
        caso2.add(new Punto(1, 1));
        caso2.add(new Punto(6, 1));
        caso2.add(new Punto(6, 4));
        caso2.add(new Punto(1, 4));
        caso2.add(new Punto(2, 2));
        caso2.add(new Punto(10, 10)); // Punto lejano
        casos.add(caso2);

        // Caso 3: Triangulo Acutángulo
        // Equilátero aproximado o uno claramente acutángulo
        // (0,0), (4,0), (2, 3.46)
        List<Punto> caso3 = new ArrayList<>();
        caso3.add(new Punto(0, 0));
        caso3.add(new Punto(4, 0));
        caso3.add(new Punto(2, 3.5)); // Aproximacion
        caso3.add(new Punto(5, 5));
        casos.add(caso3);

        // Caso 4: Varios Cuadrados
        // (0,0)-(2,0)-(2,2)-(0,2) y (3,3)-(5,3)-(5,5)-(3,5)
        List<Punto> caso4 = new ArrayList<>();
        caso4.add(new Punto(0, 0));
        caso4.add(new Punto(2, 0));
        caso4.add(new Punto(2, 2));
        caso4.add(new Punto(0, 2));

        caso4.add(new Punto(3, 3));
        caso4.add(new Punto(5, 3));
        caso4.add(new Punto(5, 5));
        caso4.add(new Punto(3, 5));
        casos.add(caso4);

        return casos;
    }
}
