package com.example.geom.ui;

import com.example.geom.model.Figura;
import com.example.geom.model.Punto;
import com.example.geom.model.TipoFigura;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.function.Consumer;

/**
 * Canvas avanzado:
 * ✔ Doble clic izquierdo → agregar punto
 * ✔ Clic izquierdo + arrastrar → mover punto
 * ✔ Doble clic derecho → eliminar punto
 * ✔ Pan con clic derecho sostenido
 * ✔ Zoom centrado en el mouse
 * ✔ Figura permanece dibujada siempre
 * ✔ Etiquetas limpias (3,2) o (1.25, 3.11)
 */
public class GraphCanvas extends Canvas {

    private double scale = 40;
    private double originX, originY;

    private List<Punto> puntos;

    private Punto selected = null;
    private Punto hover = null;

    private boolean draggingPoint = false;
    private boolean panning = false;

    private double lastX, lastY;

    private Figura figuraActual = null;   // <-- NUEVO: guardar figura para que no desaparezca

    private Consumer<List<Punto>> onPointsChanged;

    public GraphCanvas(double width, double height) {
        super(width, height);
        originX = width / 2;
        originY = height / 2;

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());

        setOnMouseClicked(this::onClick);
        setOnMousePressed(this::onPress);
        setOnMouseDragged(this::onDrag);
        setOnMouseReleased(e -> {
            draggingPoint = false;
            panning = false;
        });

        setOnMouseMoved(this::onMove);
        addEventFilter(ScrollEvent.SCROLL, this::onScroll);

        redraw();
    }

    // ======================================================
    // CLICK EVENTS
    // ======================================================
    private void onClick(MouseEvent e) {

        // === Doble clic izquierdo → agregar punto ===
        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
            double cx = (e.getX() - originX) / scale;
            double cy = (originY - e.getY()) / scale;

            Punto nuevo = new Punto(cx, cy);
            puntos.add(nuevo);
            notifyPointsChanged();
            redraw();
            return;
        }

        // === Doble clic derecho → eliminar punto ===
        if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 2) {
            Punto n = findNearest(e.getX(), e.getY());
            if (n != null) {
                puntos.remove(n);
                if (selected == n) selected = null;
                hover = null;
                notifyPointsChanged();
            }
            redraw();
        }
    }

    // PRESSED
    private void onPress(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();

        if (e.getButton() == MouseButton.PRIMARY) {

            // mover punto con clic izquierdo
            Punto n = findNearest(e.getX(), e.getY());
            if (n != null) {
                selected = n;
                draggingPoint = true;
            }

        } else if (e.getButton() == MouseButton.SECONDARY) {
            // pan con clic derecho sostenido
            panning = true;
        }

        redraw();
    }

    // DRAG
    private void onDrag(MouseEvent e) {

        double dx = e.getX() - lastX;
        double dy = e.getY() - lastY;
        lastX = e.getX();
        lastY = e.getY();

        // mover punto
        if (draggingPoint && selected != null) {
            double cx = (e.getX() - originX) / scale;
            double cy = (originY - e.getY()) / scale;

            selected.setX(cx);
            selected.setY(cy);

            notifyPointsChanged();
            redraw();
            return;
        }

        // pan
        if (panning) {
            originX += dx;
            originY += dy;
            redraw();
        }
    }

    // HOVER
    private void onMove(MouseEvent e) {
        hover = findNearest(e.getX(), e.getY());
        redraw();
    }

    // ZOOM
    private void onScroll(ScrollEvent e) {
        double factor = e.getDeltaY() > 0 ? 1.12 : 0.88;
        double oldScale = scale;
        scale *= factor;

        double mx = e.getX();
        double my = e.getY();

        double cx = (mx - originX) / oldScale;
        double cy = (originY - my) / oldScale;

        originX = mx - cx * scale;
        originY = my + cy * scale;

        redraw();
    }

    // ======================================================
    // FIND NEAREST POINT
    // ======================================================
    private Punto findNearest(double mx, double my) {
        Punto n = null;
        double min = 10;

        for (Punto p : puntos) {
            double px = originX + p.getX() * scale;
            double py = originY - p.getY() * scale;
            double d = Math.hypot(mx - px, my - py);

            if (d < min) {
                min = d;
                n = p;
            }
        }
        return n;
    }

    // ======================================================
    // REDRAW
    // ======================================================
    private void redraw() {
        GraphicsContext g = getGraphicsContext2D();

        drawAxes(g);
        drawPoints(g);

        // DIBUJAR FIGURA SI EXISTE
        if (figuraActual != null) {
            drawFiguraInterno(g, figuraActual);
        }
    }

    private void drawAxes(GraphicsContext g) {
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setStroke(Color.LIGHTGRAY);
        for (double x = originX % scale; x < getWidth(); x += scale)
            g.strokeLine(x, 0, x, getHeight());
        for (double y = originY % scale; y < getHeight(); y += scale)
            g.strokeLine(0, y, getWidth(), y);

        g.setStroke(Color.GRAY);
        g.setLineWidth(2);
        g.strokeLine(0, originY, getWidth(), originY);
        g.strokeLine(originX, 0, originX, getHeight());

        g.setFill(Color.BLACK);
        g.setFont(Font.font(10));

        // X labels
        int min = (int) Math.floor((0 - originX) / scale);
        int max = (int) Math.ceil((getWidth() - originX) / scale);

        for (int i = min; i <= max; i++) {
            double px = originX + i * scale;
            g.fillText(format(i), px + 2, originY + 12);
        }

        // Y labels
        int ymin = (int) Math.floor((originY - getHeight()) / scale);
        int ymax = (int) Math.ceil(originY / scale);

        for (int j = ymin; j <= ymax; j++) {
            double py = originY - j * scale;
            g.fillText(format(j), originX + 4, py - 2);
        }
    }

    private void drawPoints(GraphicsContext g) {
        if (puntos == null) return;

        g.setFont(Font.font(12));

        for (Punto p : puntos) {
            double px = originX + p.getX() * scale;
            double py = originY - p.getY() * scale;

            if (p == hover) {
                g.setFill(Color.YELLOW);
                g.fillOval(px - 6, py - 6, 12, 12);
            }

            if (p == selected) {
                g.setFill(Color.RED);
                g.fillOval(px - 7, py - 7, 14, 14);
            }

            g.setFill(Color.BLUE);
            g.fillOval(px - 4, py - 4, 8, 8);

            g.setFill(Color.BLACK);
            g.fillText("(" + format(p.getX()) + ", " + format(p.getY()) + ")", px + 8, py - 8);
        }
    }

    private String format(double v) {
        if (Math.abs(v - Math.round(v)) < 0.0001)
            return String.valueOf((int) Math.round(v));

        return String.format("%.2f", v);
    }

    // ======================================================
    // API PUBLICA
    // ======================================================
    public void drawPoints(List<Punto> pts) {
        this.puntos = pts;
        redraw();
    }

    public void drawFigura(Figura f) {
        this.figuraActual = f;  // <-- guardar figura permanentemente
        redraw();
    }

    private void drawFiguraInterno(GraphicsContext g, Figura f) {

        Color c = (f.getTipo() == TipoFigura.CUADRADO ? Color.DARKGREEN :
                (f.getTipo() == TipoFigura.RECTANGULO ? Color.DARKORANGE :
                        Color.DARKRED));

        g.setStroke(c);
        g.setLineWidth(2);

        List<Punto> pts = f.getPuntos();
        for (int i = 0; i < pts.size(); i++) {
            Punto a = pts.get(i);
            Punto b = pts.get((i + 1) % pts.size());

            g.strokeLine(
                    originX + a.getX() * scale, originY - a.getY() * scale,
                    originX + b.getX() * scale, originY - b.getY() * scale
            );
        }
    }

    public void setOnPointsChanged(Consumer<List<Punto>> c) {
        this.onPointsChanged = c;
    }

    private void notifyPointsChanged() {
        if (onPointsChanged != null) onPointsChanged.accept(puntos);
    }

    public void setScale(double s) {
        this.scale = s;
        redraw();
    }

}
