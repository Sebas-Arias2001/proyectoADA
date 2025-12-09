package com.example.geom;

import com.example.geom.algoritmos.DetectorFiguras;
import com.example.geom.model.Figura;
import com.example.geom.model.Punto;
import com.example.geom.ui.GraphCanvas;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private GraphCanvas canvas;
    private final List<Punto> puntosUsuario = new ArrayList<>();

    private TableView<Punto> tablaPuntos;
    private TableView<Figura> tablaFiguras;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Proyecto Geometría | Plano + Figuras");

        // =============================
        // PANEL IZQUIERDO (CONTROL)
        // =============================
        VBox left = new VBox(15);
        left.setPadding(new Insets(15));
        left.setPrefWidth(350);

        // Campos para agregar puntos
        TextField txtX = new TextField();
        txtX.setPromptText("X");
        txtX.setPrefWidth(70);

        TextField txtY = new TextField();
        txtY.setPromptText("Y");
        txtY.setPrefWidth(70);

        Button btnAgregar = new Button("Agregar punto");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");
        Button btnDetectar = new Button("Detectar figuras");
        Button btnMostrarFigura = new Button("Mostrar figura seleccionada");

        // fila de inputs y botón
        HBox filaAdd = new HBox(10, txtX, txtY, btnAgregar);
        filaAdd.setAlignment(Pos.CENTER_LEFT);

        // =============================
        // TABLA PUNTOS
        // =============================
        tablaPuntos = new TableView<>();
        tablaPuntos.setPrefHeight(220);

        TableColumn<Punto, String> colX = new TableColumn<>("X");
        colX.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getX())));
        colX.setPrefWidth(70);

        TableColumn<Punto, String> colY = new TableColumn<>("Y");
        colY.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getY())));
        colY.setPrefWidth(70);

        tablaPuntos.getColumns().addAll(colX, colY);

        HBox filaAcciones = new HBox(10, btnEliminar, btnLimpiar);
        filaAcciones.setAlignment(Pos.CENTER_LEFT);

        // =============================
        // TABLA FIGURAS
        // =============================
        tablaFiguras = new TableView<>();
        tablaFiguras.setPrefHeight(250);

        TableColumn<Figura, String> colTipo = new TableColumn<>("Figura");
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colTipo.setPrefWidth(120);

        TableColumn<Figura, String> colArea = new TableColumn<>("Área");
        colArea.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getArea())));
        colArea.setPrefWidth(90);

        TableColumn<Figura, String> colPts = new TableColumn<>("Puntos");
        colPts.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPuntos().toString()));
        colPts.setPrefWidth(200);

        tablaFiguras.getColumns().addAll(colTipo, colArea, colPts);

        // =============================
        // ZOOM (slider)
        // =============================
        Slider zoomSlider = new Slider(20, 90, 40);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setBlockIncrement(10);

        // =============================
        // ARMAR PANEL IZQUIERDO
        // =============================
        left.getChildren().addAll(
                new Label("Agregar puntos manualmente"),
                filaAdd,
                tablaPuntos,
                filaAcciones,
                new Label("Figuras detectadas"),
                tablaFiguras,
                btnDetectar,
                btnMostrarFigura,
                new Label("Zoom"),
                zoomSlider
        );

        // =============================
        // CANVAS (Plano cartesiano)
        // =============================
        canvas = new GraphCanvas(850, 650);
        canvas.setStyle("-fx-border-color: #CCC; -fx-background-color: white;");
        canvas.setOnPointsChanged(p -> tablaPuntos.setItems(FXCollections.observableArrayList(p)));

        // =============================
        // EVENTOS
        // =============================

        // Agregar punto manual
        btnAgregar.setOnAction(e -> {
            try {
                double x = Double.parseDouble(txtX.getText());
                double y = Double.parseDouble(txtY.getText());

                Punto p = new Punto(x, y);
                puntosUsuario.add(p);

                tablaPuntos.setItems(FXCollections.observableArrayList(puntosUsuario));
                canvas.drawPoints(puntosUsuario);
                txtX.clear();
                txtY.clear();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Coordenadas inválidas").showAndWait();
            }
        });

        // Eliminar punto seleccionado
        btnEliminar.setOnAction(e -> {
            Punto p = tablaPuntos.getSelectionModel().getSelectedItem();
            if (p == null) {
                new Alert(Alert.AlertType.INFORMATION, "Seleccione un punto primero").showAndWait();
                return;
            }
            puntosUsuario.remove(p);
            tablaPuntos.setItems(FXCollections.observableArrayList(puntosUsuario));
            canvas.drawPoints(puntosUsuario);
        });

        // Limpiar puntos
        btnLimpiar.setOnAction(e -> {
            puntosUsuario.clear();
            tablaPuntos.setItems(FXCollections.observableArrayList());
            tablaFiguras.setItems(FXCollections.observableArrayList());
            canvas.drawPoints(puntosUsuario);
        });

        // Detectar figuras
        btnDetectar.setOnAction(e -> {
            if (puntosUsuario.size() < 3) {
                new Alert(Alert.AlertType.WARNING, "Debes tener al menos 3 puntos").showAndWait();
                return;
            }
            List<Figura> figs = DetectorFiguras.detectarFiguras(puntosUsuario);
            tablaFiguras.setItems(FXCollections.observableArrayList(figs));
        });

        // Mostrar figura seleccionada — AHORA PERMANECE EN PANTALLA
        btnMostrarFigura.setOnAction(e -> {
            Figura f = tablaFiguras.getSelectionModel().getSelectedItem();
            if (f == null) {
                new Alert(Alert.AlertType.INFORMATION, "Seleccione una figura primero").showAndWait();
                return;
            }
            canvas.drawFigura(f);  // <-- ya no se borra con hover ni mouse move
        });

        // Zoom
        zoomSlider.valueProperty().addListener((obs, oldV, newV) -> canvas.setScale(newV.doubleValue()));

        // =============================
        // LAYOUT GENERAL
        // =============================
        BorderPane root = new BorderPane();
        root.setLeft(left);
        root.setCenter(canvas);

        Scene scene = new Scene(root, 1300, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
