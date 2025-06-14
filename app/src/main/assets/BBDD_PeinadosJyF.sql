-- 1. Tabla de Roles
CREATE TABLE Roles (
    ID_Rol INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre_Rol TEXT NOT NULL UNIQUE,
    Descripcion TEXT
);

-- 2. Tabla de Usuarios
CREATE TABLE Usuarios (
    ID_Usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    Email TEXT NOT NULL UNIQUE,
    Contrasena_Hash TEXT NOT NULL,
    DNI_NIE_Pasaporte TEXT UNIQUE,
    ID_Rol INTEGER NOT NULL,
    Fecha_Registro TEXT NOT NULL DEFAULT (date('now')),
    FOREIGN KEY (ID_Rol) REFERENCES Roles(ID_Rol)
);

-- 3. Tabla de Empleados
CREATE TABLE Empleados (
    ID_Empleado INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre TEXT NOT NULL,
    Apellido1 TEXT,
    Apellido2 TEXT,
    Email TEXT UNIQUE,
    Telefono TEXT,
    Fecha_Inicio_Contrato TEXT NOT NULL,
    Fecha_Fin_Contrato TEXT,
    ID_Usuario INTEGER UNIQUE,
    Notas_Internas TEXT,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

-- 4. Tabla de Clientes
CREATE TABLE Clientes (
    ID_Cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre TEXT NOT NULL,
    Apellido1 TEXT,
    Apellido2 TEXT,
    Email TEXT,
    Telefono TEXT,
    Fecha_Nacimiento TEXT,
    ID_Usuario INTEGER UNIQUE,
    Preferencias_Servicio TEXT,
    Alergias TEXT,
    Notas TEXT,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

-- 5. Tabla de Servicios
CREATE TABLE Servicios (
    ID_Servicio INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre_Servicio TEXT NOT NULL UNIQUE,
    Descripcion TEXT,
    Duracion_Estimada_Minutos INTEGER NOT NULL,
    Precio REAL NOT NULL
);

-- 6. Tabla de Citas
CREATE TABLE Citas (
    ID_Cita INTEGER PRIMARY KEY AUTOINCREMENT,
    ID_Cliente INTEGER NOT NULL,
    ID_Empleado INTEGER NOT NULL,
    ID_Servicio INTEGER NOT NULL,
    Fecha_Cita TEXT NOT NULL,
    Hora_Inicio TEXT NOT NULL,
    Hora_Fin_Estimada TEXT NOT NULL,
    Hora_Fin_Real TEXT,
    Estado_Cita TEXT NOT NULL DEFAULT 'Pendiente',
    Notas_Cita TEXT,
    Fecha_Creacion TEXT NOT NULL DEFAULT (datetime('now')),
    Fecha_Actualizacion TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (ID_Cliente) REFERENCES Clientes(ID_Cliente),
    FOREIGN KEY (ID_Empleado) REFERENCES Empleados(ID_Empleado),
    FOREIGN KEY (ID_Servicio) REFERENCES Servicios(ID_Servicio)
);

-- 7. Tabla de Horarios_Disponibles_Empleado
CREATE TABLE Horarios_Disponibles_Empleado (
    ID_Disponibilidad INTEGER PRIMARY KEY AUTOINCREMENT,
    ID_Empleado INTEGER NOT NULL,
    Dia_Semana TEXT NOT NULL,
    Hora_Inicio_Bloque TEXT NOT NULL,
    Hora_Fin_Bloque TEXT NOT NULL,
    Fecha_Aplicacion_Desde TEXT NOT NULL DEFAULT (date('now')),
    Fecha_Aplicacion_Hasta TEXT,
    FOREIGN KEY (ID_Empleado) REFERENCES Empleados(ID_Empleado)
);

-- Inserts de datos iniciales
-- ROLES
INSERT INTO Roles (Nombre_Rol, Descripcion) VALUES ('Administrador', 'Puede ver y gestionar todas las citas del sistema.');
INSERT INTO Roles (Nombre_Rol, Descripcion) VALUES ('Cliente', 'Puede crear y gestionar sus propias citas.');
INSERT INTO Roles (Nombre_Rol, Descripcion) VALUES ('Empleado', 'Puede crear y gestionar sus propias citas.');

-- USUARIOS
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('felix.tataje@gmail.com', 'pass_felix', '12345678A', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Administrador'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('jacqueline.garcia@gmail.com', 'pass_jacqueline', '98765432B', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Administrador'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('laura.fernandez.emp@gmail.com', 'pass_laura_emp', '12345678X', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Empleado'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('miguel.santos.emp@gmail.com', 'pass_miguel_emp', '87654321Y', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Empleado'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('ana.lopez@gmail.com', 'pass_ana', '11111111C', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Cliente'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('juan.perez@gmail.com', 'pass_juan', '22222222D', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Cliente'));


-- CLIENTES
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Felix', 'Tataje', NULL, 'felix.tataje@gmail.com', '600111222', '1985-05-15', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'felix.tataje@gmail.com'), 'Cortes modernos', NULL, 'Es el dueño.');
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Jacqueline', 'Garcia', NULL, 'jacqueline.garcia@gmail.com', '654321098', '1980-03-20', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'jacqueline.garcia@gmail.com'), 'Coloración y cortes', NULL, 'Es la dueña.');
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Ana', 'Lopez', 'Martinez', 'ana.lopez@gmail.com', '611111111', '1992-03-10', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'ana.lopez@gmail.com'), 'Cortes de flequillo', NULL, NULL);
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Juan', 'Perez', 'Diaz', 'juan.perez@gmail.com', '622222222', '1988-07-25', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'juan.perez@gmail.com'), 'Cortes clásicos', NULL, 'Prefiere citas por la mañana.');
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Laura', 'Fernandez', 'Vega', 'laura.fernandez.emp@gmail.com', '612345678', '1990-03-20', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Cortes de señora', NULL, 'Es empleada.');
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Miguel', 'Santos', 'Marin', 'miguel.santos.emp@gmail.com', '687654321', '1988-07-10', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'miguel.santos.emp@gmail.com'), 'Arreglo de barba', NULL, 'Es empleado.');

-- EMPLEADOS
INSERT INTO Empleados (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Inicio_Contrato, Fecha_Fin_Contrato, ID_Usuario, Notas_Internas)
VALUES ('Laura', 'Fernandez', 'Vega', 'laura.fernandez.emp@gmail.com', '612345678', '2023-01-10', NULL, (SELECT ID_Usuario FROM Usuarios WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Especialista en coloración.');
INSERT INTO Empleados (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Inicio_Contrato, Fecha_Fin_Contrato, ID_Usuario, Notas_Internas)
VALUES ('Miguel', 'Santos', 'Martinez', 'miguel.santos.emp@gmail.com', '698765432', '2023-02-15', NULL, (SELECT ID_Usuario FROM Usuarios WHERE Email = 'miguel.santos.emp@gmail.com'), 'Especialista en cortes masculinos.');

-- HORARIOS
-- Horarios para Laura (Turno mañana y tarde)
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Lunes', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Lunes', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Martes', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Martes', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Miércoles', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Miércoles', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Jueves', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Jueves', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Viernes', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Viernes', '14:00', '18:00', '2024-01-01', NULL);

-- Horarios para Miguel (Turno mañana y tarde)
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Lunes', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Lunes', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Martes', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Martes', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Miércoles', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Miércoles', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Jueves', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Jueves', '14:00', '18:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Viernes', '09:00', '13:00', '2024-01-01', NULL);
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'miguel.santos.emp@gmail.com'), 'Viernes', '14:00', '18:00', '2024-01-01', NULL);

-- SERVICIOS
INSERT INTO Servicios (Nombre_Servicio, Descripcion, Duracion_Estimada_Minutos, Precio) VALUES ('Corte Simple', 'Corte de pelo básico y rápido.', 30, 15.00);
INSERT INTO Servicios (Nombre_Servicio, Descripcion, Duracion_Estimada_Minutos, Precio) VALUES ('Corte Complejo', 'Corte de pelo con peinado y estilismo.', 45, 25.00);
INSERT INTO Servicios (Nombre_Servicio, Descripcion, Duracion_Estimada_Minutos, Precio) VALUES ('Coloración', 'Aplicación de coloración y mechas.', 90, 50.00);
INSERT INTO Servicios (Nombre_Servicio, Descripcion, Duracion_Estimada_Minutos, Precio) VALUES ('Corte + Coloración', 'Corte y coloración en una sola cita.', 120, 65.00);
INSERT INTO Servicios (Nombre_Servicio, Descripcion, Duracion_Estimada_Minutos, Precio) VALUES ('Peinado Especial', 'Peinado para ocasiones especiales.', 60, 35.00);
