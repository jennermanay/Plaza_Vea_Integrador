package pe.edu.utp.monitoreoprecios;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pe.edu.utp.monitoreoprecios.config.Database;
import pe.edu.utp.monitoreoprecios.model.Producto;
import pe.edu.utp.monitoreoprecios.scraper.JsoupExtractor;
import pe.edu.utp.monitoreoprecios.service.ComparacionService;

import java.math.BigDecimal;
import java.sql.Connection;

public class App extends Application {

    private final TextArea salida = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Monitoreo competitivo de precios - UTP");

        Label titulo = new Label("Sistema de Monitoreo Competitivo de Precios");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField costo = new TextField("80.00");
        TextField precioPropio = new TextField("120.00");
        TextField precioExterno = new TextField("105.00");

        GridPane datos = new GridPane();
        datos.setHgap(10);
        datos.setVgap(10);
        datos.addRow(0, new Label("Costo:"), costo);
        datos.addRow(1, new Label("Precio propio:"), precioPropio);
        datos.addRow(2, new Label("Precio externo:"), precioExterno);

        Button btnComparar = new Button("Probar comparación");
        Button btnBD = new Button("Probar PostgreSQL");
        Button btnScraping = new Button("Probar scraping");

        TextField url = new TextField("https://example.com");
        url.setPrefColumnCount(40);

        HBox acciones = new HBox(10, btnComparar, btnBD);
        HBox scraping = new HBox(10, new Label("URL:"), url, btnScraping);

        salida.setEditable(false);
        salida.setPrefRowCount(16);

        btnComparar.setOnAction(e -> {
            try {
                Producto producto = new Producto(
                        1L, "DEMO-001", "Producto de prueba",
                        new BigDecimal(costo.getText()),
                        new BigDecimal(precioPropio.getText())
                );
                var r = new ComparacionService().comparar(
                        producto, new BigDecimal(precioExterno.getText())
                );
                salida.setText(
                        "COMPARACIÓN\n" +
                        "Costo: S/ " + producto.costo() + "\n" +
                        "Precio propio: S/ " + producto.precioVenta() + "\n" +
                        "Precio externo: S/ " + precioExterno.getText() + "\n\n" +
                        "Utilidad actual: S/ " + r.utilidadActual() + "\n" +
                        "Margen actual: " + r.margenActual() + " %\n" +
                        "Utilidad simulada: S/ " + r.utilidadSimulada() + "\n" +
                        "Margen simulado: " + r.margenSimulado() + " %\n" +
                        "¿Genera alerta?: " + (r.alerta() ? "SÍ" : "NO") + "\n" +
                        "¿Posible pérdida?: " + (r.posiblePerdida() ? "SÍ" : "NO")
                );
            } catch (Exception ex) {
                salida.setText("Error: " + ex.getMessage());
            }
        });

        btnBD.setOnAction(e -> {
            try (Connection c = Database.getConnection()) {
                salida.setText("Conexión PostgreSQL OK.\nBase: " + c.getCatalog());
            } catch (Exception ex) {
                salida.setText(
                        "No se pudo conectar a PostgreSQL.\n" +
                        "Revisa src/main/resources/application.properties\n\n" +
                        ex.getMessage()
                );
            }
        });

        btnScraping.setOnAction(e -> {
            salida.setText("Consultando " + url.getText() + " ...");
            btnScraping.setDisable(true);

            Task<String> task = new Task<>() {
                @Override
                protected String call() throws Exception {
                    var resultado = new JsoupExtractor().extraerTitulo(url.getText());
                    return "SCRAPING OK\nURL: " + url.getText() + "\nTítulo HTML: " + resultado;
                }
            };

            task.setOnSucceeded(ev -> {
                salida.setText(task.getValue());
                btnScraping.setDisable(false);
            });
            task.setOnFailed(ev -> {
                salida.setText("Error de scraping:\n" + task.getException().getMessage());
                btnScraping.setDisable(false);
            });

            Thread hilo = new Thread(task, "scraping-task");
            hilo.setDaemon(true);
            hilo.start();
        });

        VBox root = new VBox(16, titulo, datos, acciones, new Separator(), scraping, salida);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 760, 560));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
