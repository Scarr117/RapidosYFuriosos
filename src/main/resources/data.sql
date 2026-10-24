-- Datos de prueba para desarrollo local.
-- Como la BD es H2 en memoria (se recrea vacía en cada arranque, ver application.yml
-- hibernate.ddl-auto: create-drop), este script se ejecuta automáticamente después de
-- que Hibernate crea las tablas, para poder probar las pantallas sin capturar todo a mano.
--
-- Usa subconsultas para ligar las llaves foráneas (en vez de IDs fijos) para no interferir
-- con el contador interno de las columnas IDENTITY.

-- Clientes de prueba
INSERT INTO CLIENTE (nombre, telefono) VALUES ('Juan Pérez', '5512345678');
INSERT INTO CLIENTE (nombre, telefono) VALUES ('María López', '5598765432');
INSERT INTO CLIENTE (nombre, telefono) VALUES ('Carlos Slim', '5511223344');

-- Vehículos de prueba, ligados a los clientes de arriba
INSERT INTO VEHICULO (marca, modelo, placas, anio, kilometraje, id_cliente)
  VALUES ('Nissan', 'Versa', 'ABC-123', 2022, 35000.0, (SELECT id_cliente FROM CLIENTE WHERE nombre = 'Juan Pérez'));
INSERT INTO VEHICULO (marca, modelo, placas, anio, kilometraje, id_cliente)
  VALUES ('Toyota', 'Hilux', 'XYZ-789', 2023, 12000.0, (SELECT id_cliente FROM CLIENTE WHERE nombre = 'María López'));
  INSERT INTO VEHICULO (marca, modelo, placas, anio, kilometraje, id_cliente)
VALUES ('Toyota', 'Corolla', 'AWX-789', 2020, 50000.0, (SELECT id_cliente FROM CLIENTE WHERE nombre = 'Carlos Slim'));



-- Refacciones de prueba (HU-12), con datos de la última remesa recibida (HU-31)
INSERT INTO REFACCION (nombre, precio, existencia, proveedor, fecha_recepcion, fecha_hora_registro)
  VALUES ('Balatas delanteras', 180.0, 20, 'Refaccionaria Central', '2026-05-01', '2026-05-01 09:00:00');
INSERT INTO REFACCION (nombre, precio, existencia, proveedor, fecha_recepcion, fecha_hora_registro)
  VALUES ('Filtro Aceite', 95.0, 15, 'AutoPartes del Valle', '2026-05-10', '2026-05-10 11:00:00');

-- Distribuidores de prueba (HU-25)
INSERT INTO DISTRIBUIDOR (nombre, telefono, correo, direccion, tipo_refaccion)
  VALUES ('Refaccionaria Central', '5511112222', 'ventas@refaccentral.com', 'Av. Reforma 123, CDMX', 'frenos');
INSERT INTO DISTRIBUIDOR (nombre, telefono, correo, direccion, tipo_refaccion)
  VALUES ('AutoPartes del Valle', '5533334444', 'contacto@autopartesvalle.com', 'Calle Hidalgo 45, CDMX', 'motor');
INSERT INTO DISTRIBUIDOR (nombre, telefono, correo, direccion, tipo_refaccion)
  VALUES ('Distribuidora Norte', '5555556666', 'info@distnorte.com', 'Blvd. Norte 900, CDMX', 'suspensión');

-- Historial de servicio de ejemplo, ligado al vehículo con placas ABC-123 (HU-29)
INSERT INTO SERVICIO (fecha, descripcion, piezas_utilizadas, costo_mano_obra, observaciones, fecha_hora_registro, vehiculo_id_vehiculo)
  VALUES ('2026-06-01', 'Cambio de balatas delanteras', 'Balatas delanteras', 350.0, 'Cliente reportó ruido al frenar', '2026-06-01 10:30:00', (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'ABC-123'));

-- Reparaciones de prueba para que los vehículos aparezcan en la tabla de entregas

-- 1. Reparación terminada para el Nissan Versa de Juan Pérez
INSERT INTO REPARACION (
    fecha_inicio, 
    fecha_fin, 
    estatus_servicio, 
    observaciones_tecnicas, 
    garantia_meses, 
    condiciones_garantia, 
    id_vehiculo) VALUES (
    '2026-07-15 09:00:00',
    '2026-07-20 14:30:00',
    'En espera',
    'Cambio de aceite, filtros, limpieza de inyectores y revisión de frenos.',
    3,
    'La garantía aplica solo para los componentes instalados en este servicio.',
    (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'ABC-123')
  );

  -- 2. Reparación terminada para la Toyota Hilux de María López
INSERT INTO REPARACION (
    fecha_inicio, 
    fecha_fin, 
    estatus_servicio, 
    observaciones_tecnicas, 
    garantia_meses, 
    condiciones_garantia, 
    id_vehiculo) VALUES (
    '2026-07-18 11:00:00',
    '2026-07-20 16:00:00',
    'Listo para entrega',
    'Alineación, balanceo, rotación de llantas y cambio de amortiguadores traseros.',
    6,
    'Garantía directa con proveedor en el caso de los amortiguadores.',
    (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'XYZ-789')
  );
-- ==========================================
-- Pedidos de prueba (HU-30)
-- ==========================================

-- 1. Pedido asociado a una reparación específica (Para el Nissan Versa)
INSERT INTO PEDIDO (fecha_pedido, estado_pedido, cantidad, id_distribuidor, id_refaccion, id_reparacion)
VALUES (
    '2026-07-25', 
    'En espera', 
    2, 
    (SELECT id_distribuidor FROM DISTRIBUIDOR WHERE nombre = 'Refaccionaria Central'), 
    (SELECT id_refaccion FROM REFACCION WHERE nombre = 'Balatas delanteras'),
    (SELECT id_reparacion FROM REPARACION WHERE id_vehiculo = (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'ABC-123'))
);

-- 2. Pedido para inventario local (Sin reparación asignada, valor NULL)
INSERT INTO PEDIDO (fecha_pedido, estado_pedido, cantidad, id_distribuidor, id_refaccion, id_reparacion)
VALUES (
    '2026-07-24', 
    'Entregado', 
    5, 
    (SELECT id_distribuidor FROM DISTRIBUIDOR WHERE nombre = 'AutoPartes del Valle'), 
    (SELECT id_refaccion FROM REFACCION WHERE nombre = 'Filtro Aceite'),
    NULL
);

-- 3. Pedido cancelado asociado a la Toyota Hilux
INSERT INTO PEDIDO (fecha_pedido, estado_pedido, cantidad, id_distribuidor, id_refaccion, id_reparacion)
VALUES (
    '2026-07-20', 
    'Cancelado', 
    1, 
    (SELECT id_distribuidor FROM DISTRIBUIDOR WHERE nombre = 'Distribuidora Norte'), 
    (SELECT id_refaccion FROM REFACCION WHERE nombre = 'Balatas delanteras'),
    (SELECT id_reparacion FROM REPARACION WHERE id_vehiculo = (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'XYZ-789'))
);

-- ==========================================
-- Citas agendadas de prueba (HU-01)
-- ==========================================
INSERT INTO CITA (fecha, hora, estado, id_cliente, vehiculo_id)
VALUES (
    CURRENT_DATE,
    '09:00:00',
    'Pendiente',
    (SELECT id_cliente FROM CLIENTE WHERE nombre = 'Juan Pérez'),
    (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'ABC-123')
);

INSERT INTO CITA (fecha, hora, estado, id_cliente, vehiculo_id)
VALUES (
    CURRENT_DATE,
    '10:30:00',
    'Pendiente',
    (SELECT id_cliente FROM CLIENTE WHERE nombre = 'María López'),
    (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'XYZ-789')
);

INSERT INTO CITA (fecha, hora, estado, id_cliente, vehiculo_id)
VALUES (
    DATEADD('DAY', 7, CURRENT_DATE),
    '14:00:00',
    'Pendiente',
    (SELECT id_cliente FROM CLIENTE WHERE nombre = 'Carlos Slim'),
    (SELECT id_vehiculo FROM VEHICULO WHERE placas = 'AWX-789')
);
