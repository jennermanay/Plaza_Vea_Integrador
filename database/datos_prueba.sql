INSERT INTO categoria(nombre, descripcion)
VALUES ('Abarrotes', 'Categoría de prueba')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO producto(id_categoria, codigo, nombre, marca, presentacion, costo, precio_venta)
SELECT id_categoria, 'DEMO-001', 'Producto de prueba', 'Marca Demo', 'Unidad', 80.00, 120.00
FROM categoria
WHERE nombre = 'Abarrotes'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO fuente_scraping(nombre, url_base, selectores, intervalo_minutos)
VALUES ('Example', 'https://example.com', '{"titulo":"title"}'::jsonb, 60)
ON CONFLICT (url_base) DO NOTHING;
