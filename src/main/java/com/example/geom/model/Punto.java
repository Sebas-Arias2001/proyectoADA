package com.example.geom.model;

/**
 * Representa un punto en el plano cartesiano.
 * Ahora mutable: permite mover puntos en el canvas.
 */
public class Punto {

    private double x;
    private double y;

    public Punto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // ============================
    // GETTERS
    // ============================
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // ============================
    // SETTERS (necesarios para mover puntos)
    // ============================
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // ============================
    // OPERACIONES VECTORIALES
    // ============================
    public Punto menos(Punto otro) {
        return new Punto(this.x - otro.x, this.y - otro.y);
    }

    public double dot(Punto otro) {
        return this.x * otro.x + this.y * otro.y;
    }

    public double norma2() {
        return this.x * this.x + this.y * this.y;
    }

    public double distanciaA(Punto otro) {
        double dx = this.x - otro.x;
        double dy = this.y - otro.y;
        return Math.hypot(dx, dy);
    }

    // ============================
    // REPRESENTACIÓN TEXTO
    // ============================
    @Override
    public String toString() {
        return "(" + (int) x + "," + (int) y + ")";
    }
}
