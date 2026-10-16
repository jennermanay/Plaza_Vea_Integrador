package pe.edu.utp.monitoreoprecios.model;

import java.math.BigDecimal;

public record ResultadoComparacion(
        BigDecimal utilidadActual,
        BigDecimal margenActual,
        BigDecimal utilidadSimulada,
        BigDecimal margenSimulado,
        boolean alerta,
        boolean posiblePerdida
) {}
