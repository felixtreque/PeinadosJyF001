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
INSERT INTO Roles (Nombre_Rol, Descripcion) VALUES ('Administrador', 'Control total del sistema de gestión de peluquería.');
INSERT INTO Roles (Nombre_Rol, Descripcion) VALUES ('Cliente', 'Usuario regular que puede reservar y gestionar sus propias citas.');

-- USUARIOS
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('felix.tataje@gmail.com', 'pass_felix', '12345678A', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Administrador'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('jacqueline.garcia@gmail.com', 'pass_jacqueline', '98765432B', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Administrador'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('laura.fernandez.emp@gmail.com', 'pass_laura_emp', '12345678X', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Cliente'));
INSERT INTO Usuarios (Email, Contrasena_Hash, DNI_NIE_Pasaporte, ID_Rol) VALUES ('miguel.santos.emp@gmail.com', 'pass_miguel_emp', '87654321Y', (SELECT ID_Rol FROM Roles WHERE Nombre_Rol = 'Cliente'));

-- CLIENTES
INSERT INTO Clientes (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Nacimiento, ID_Usuario, Preferencias_Servicio, Alergias, Notas)
VALUES ('Felix', 'Tataje', NULL, 'felix.tataje@gmail.com', '600111222', '1985-05-15', (SELECT ID_Usuario FROM Usuarios WHERE Email = 'felix.tataje@gmail.com'), 'Cortes modernos', NULL, 'Es el dueño.');

-- EMPLEADOS
INSERT INTO Empleados (Nombre, Apellido1, Apellido2, Email, Telefono, Fecha_Inicio_Contrato, Fecha_Fin_Contrato, ID_Usuario, Notas_Internas)
VALUES ('Laura', 'Fernandez', 'Vega', 'laura.fernandez.emp@gmail.com', '612345678', '2023-01-10', NULL, (SELECT ID_Usuario FROM Usuarios WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Especialista en coloración.');

-- HORARIOS
INSERT INTO Horarios_Disponibles_Empleado (ID_Empleado, Dia_Semana, Hora_Inicio_Bloque, Hora_Fin_Bloque, Fecha_Aplicacion_Desde, Fecha_Aplicacion_Hasta)
VALUES ((SELECT ID_Empleado FROM Empleados WHERE Email = 'laura.fernandez.emp@gmail.com'), 'Lunes', '09:00', '13:00', '2024-01-01', NULL);

-- SERVICIOS
INSERT INTO Servicios (Nombre_Servicio, Descripcion, Duracion_Estimada_Minutos, Precio) VALUES ('Corte Simple', 'Corte de pelo básico y rápido.', 30, 15.00);
