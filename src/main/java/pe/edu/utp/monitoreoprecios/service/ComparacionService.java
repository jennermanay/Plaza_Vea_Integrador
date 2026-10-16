package pe.edu.utp.monitoreoprecios.service;

import pe.edu.utp.monitoreoprecios.model.Producto;
import pe.edu.utp.monitoreoprecios.model.ResultadoComparacion;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ComparacionService {

    public ResultadoComparacion comparar(Producto producto, BigDecimal precioExterno) {
        if (producto == null || precioExterno == null) {
            throw new IllegalArgumentException("Producto y precio externo son obligatorios.");
        }
        if (producto.costo().compareTo(BigDecimal.ZERO) <= 0 ||
            producto.precioVenta().compareTo(BigDecimal.ZERO) <= 0 ||
            precioExterno.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Los precios y el costo deben ser mayores que cero.");
        }

        BigDecimal utilidadActual = producto.precioVenta().subtract(producto.costo());
        BigDecimal margenActual = porcentaje(utilidadActual, producto.precioVenta());

        BigDecimal utilidadSimulada = precioExterno.subtract(producto.costo());
        BigDecimal margenSimulado = porcentaje(utilidadSimulada, precioExterno);

        boolean alerta = precioExterno.compareTo(producto.precioVenta()) < 0;
        boolean posiblePerdida = precioExterno.compareTo(producto.costo()) < 0;

        return new ResultadoComparacion(
                utilidadActual.setScale(2, RoundingMode.HALF_UP),
                margenActual,
                utilidadSimulada.setScale(2, RoundingMode.HALF_UP),
                margenSimulado,
                alerta,
                posiblePerdida
        );
    }

    private BigDecimal porcentaje(BigDecimal utilidad, BigDecimal precio) {
        return utilidad
                .divide(precio, 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
