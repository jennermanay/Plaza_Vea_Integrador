package pe.edu.utp.monitoreoprecios.model;

import java.math.BigDecimal;

public record Producto(
        Long id,
        String codigo,
        String nombre,
        BigDecimal costo,
        BigDecimal precioVenta
) {}
