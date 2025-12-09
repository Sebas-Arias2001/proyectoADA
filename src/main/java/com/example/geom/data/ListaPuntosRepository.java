package com.example.geom.data;

import com.example.geom.model.Punto;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio estático con listas de puntos para pruebas.
 * El enunciado pide mínimo 4 listas con mínimo 4 puntos cada una.
 */
public class ListaPuntosRepository {

    public static List<List<Punto>> getListasEjemplo() {
        List<List<Punto>> listas = new ArrayList<>();

        // Lista 1 - los puntos del ejemplo Figura 2 del PDF
        listas.add(List.of(
                new Punto(1,1),
                new Punto(1,5),
                new Punto(5,1),
                new Punto(5,-2)
        ));

        // Lista 2 - forma un cuadrado de 4x4
        listas.add(List.of(
                new Punto(0,0),
                new Punto(0,4),
                new Punto(4,0),
                new Punto(4,4)
        ));

        // Lista 3 - rectángulo 4x2 más puntos extras
        listas.add(List.of(
                new Punto(-1,-1),
                new Punto(-1,1),
                new Punto(3,-1),
                new Punto(3,1),
                new Punto(0,0) // punto extra
        ));

        // Lista 4 - triángulos posibles
        listas.add(List.of(
                new Punto(0,0),
                new Punto(3,0),
                new Punto(0,4),
                new Punto(1,1),
                new Punto(2,2)
        ));

        // Puedes añadir más listas para pruebas
        return listas;
    }
}
