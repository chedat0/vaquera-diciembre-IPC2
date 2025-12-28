CREATE DATABASE proyecto_vaqueras;
USE proyecto_vaqueras;

CREATE TABLE rol (
	id_rol INTEGER not null primary key auto_increment,
    nombre VARCHAR (50) not null UNIQUE
);

CREATE TABLE usuario (
	id_usuario INTEGER not null primary key auto_increment,
    nickname VARCHAR (50),
    correo VARCHAR (100) UNIQUE not null,
    fecha_nacimiento DATE,
    id_rol INTEGER,
    password VARCHAR(200) NOT NULL,
	telefono CHAR(8),
	pais VARCHAR(20),
	URL_avatar VARCHAR(300),
	biblioteca_publica BOOLEAN DEFAULT true,
    foreign key (id_rol) REFERENCES rol (id_rol)
);

CREATE TABLE empresa (
	id_empresa INTEGER not null primary key auto_increment,
    nombre VARCHAR (100),
    descripcion TEXT
);

CREATE TABLE usuario_empresa (
	id_usuario INTEGER not null,
    id_empresa INTEGER not null,
    primary key (id_usuario, id_empresa),
    foreign key (id_usuario) REFERENCES usuario(id_usuario),
    foreign key (id_empresa) REFERENCES empresa(id_empresa)
);

CREATE TABLE categoria (
	id_categoria INTEGER not null primary key auto_increment,
    nombre VARCHAR (50) not null UNIQUE,
    activado BOOLEAN default true
);

CREATE TABLE juego (
	id_juego INTEGER not null primary key auto_increment,
    id_empresa INTEGER not null,
    titulo VARCHAR(100) not null,
    descripcion TEXT,
    precio DECIMAL (10,2) not null,
    clasificacion_por_edad VARCHAR (20) not null,
    venta_activa BOOLEAN default true,
    requisitos_minimos TEXT,
	fecha_lanzamiento DATE,
    foreign key (id_empresa) REFERENCES empresa(id_empresa)
);

CREATE TABLE juego_categoria (
	id_juego INTEGER not null,
    id_categoria INTEGER not null,
    primary key (id_juego, id_categoria),
    foreign key (id_juego) REFERENCES juego(id_juego),
    foreign key (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE juego_imagen (
	id_imagen INTEGER not null primary key auto_increment,
    id_juego INTEGER not null,
    url_imagen VARCHAR (300) not null,
    img_portada BOOLEAN default false,
    foreign key (id_juego) REFERENCES juego(id_juego)
);

CREATE TABLE cartera (
	id_usuario INTEGER primary key,
    saldo DECIMAL (10,2) default 0,
    foreign key (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE transaccion (
	id_transaccion INTEGER primary key auto_increment,
    id_usuario INT not null,
    monto DECIMAL (10,2) not null,
    tipo ENUM ('RECARGA', 'COMPRA') not null,
    fecha DATE not null,
    foreign key (id_usuario) REFERENCES usuario(id_usuario) 
);

CREATE TABLE licencia (
	id_licencia INTEGER primary key auto_increment,
    id_usuario INTEGER not null,
    id_juego INTEGER not null,
    fecha_compra DATE not null,
    foreign key (id_usuario) REFERENCES usuario(id_usuario),
    foreign key (id_juego) REFERENCES juego(id_juego),
    unique (id_usuario, id_juego)
);

CREATE TABLE grupo_familiar (
	id_grupo INTEGER not null primary key auto_increment,
    nombre VARCHAR (100) not null,
    id_creador INTEGER not null,
    foreign key (id_creador) REFERENCES usuario (id_usuario)
);

CREATE TABLE grupo_miembro (
	id_grupo INTEGER not null,
    id_usuario INTEGER not null,
    rol VARCHAR(50) not null,
    primary key(id_grupo, id_usuario),
    foreign key (id_grupo) REFERENCES grupo_familiar(id_grupo),
    foreign key (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE instalacion_juego (
	id_usuario INTEGER not null,
    id_juego INTEGER not null,
    es_prestado BOOLEAN not null,
    estado ENUM ('INSTALADO', 'NO_INSTALADO') not null,
    fecha_estado DATE,
    primary key (id_usuario, id_juego),
    foreign key (id_usuario) REFERENCES usuario(id_usuario),
    foreign key (id_juego) REFERENCES juego (id_juego)
);

CREATE TABLE comentario (
	id_comentario INTEGER not null primary key auto_increment,
    id_juego INTEGER not null,
    id_usuario INTEGER not null,
    contenido TEXT not null,
    calificacion INTEGER CHECK (calificacion between 1 and 5),
    fecha DATE not null,
    visible BOOLEAN DEFAULT true,
    foreign key (id_juego) REFERENCES juego(id_juego),
    foreign key (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE respuesta (
	id_respuesta INTEGER not null primary key auto_increment,
    id_comentario INTEGER not null,
    id_usuario INTEGER not null,
    contenido TEXT not null,
    fecha DATE not null,
    foreign key (id_comentario) REFERENCES comentario(id_comentario),
    foreign key (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE comision_global (
	id_comision INTEGER not null primary key auto_increment,
    porcentaje DECIMAL(5,2) not null,
    fecha_inicio DATE not null
);

CREATE TABLE comision_empresa (
	id_empresa INTEGER primary key,
    porcentaje DECIMAL (5,2) not null,
    foreign key (id_empresa) REFERENCES empresa(id_empresa)
);

CREATE TABLE banner (
    id_banner INTEGER not null primary key auto_increment,
    id_juego INTEGER not null,
    posicion INTEGER not null,
    activo BOOLEAN default true,
    fecha_inicio DATE,
    fecha_fin DATE,
    foreign key (id_juego) REFERENCES juego(id_juego),
    unique (posicion)
);

CREATE TABLE invitacion_grupo (
    id_invitacion INTEGER primary key auto_increment,
    id_grupo INTEGER not null,
    id_usuario_invitado INTEGER not null,
    id_usuario_invitador INTEGER not null,
    estado ENUM('PENDIENTE', 'ACEPTADA', 'RECHAZADA') DEFAULT 'PENDIENTE',
    fecha_invitacion DATE NOT NULL,
    FOREIGN KEY (id_grupo) REFERENCES grupo_familiar(id_grupo),
    FOREIGN KEY (id_usuario_invitado) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_usuario_invitador) REFERENCES usuario(id_usuario)
);

INSERT INTO rol (nombre) VALUES ("ADMINISTRADOR"), ("EMPRESA"), ("GAMER");

SELECT * From rol;

ALTER TABLE juego
RENAME COLUMN descripcion TO requisitos_minimos;

SELECT * FROM juego;

use proyecto_vaqueras;
INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, telefono, pais, id_rol)
VALUES (
    'admin',
    'admin@vaqueras.com',
    '$2a$12$mn7n3aoovMG0MuTGhBv4JugK3t.4BLdA.c.TIPxjmTZexWB62c0Me', -- password: admin123
    '1998-10-09',
    '12345678',
    'Guatemala',
    1
);

select * from usuario;

use proyecto_vaqueras;

select * from juego;
INSERT INTO juego (id_empresa, titulo, requisitos_minimos, precio, clasificacion_por_edad, venta_activa, descripcion, fecha_lanzamiento)
VALUES (
    1,
    "Juego1",
    "Windows 10, 8GB RAM",
    19.99,
    "T",
    true,
    "Shooter multijugador clásico",
    '1999-11-22'
);

INSERT INTO juego (id_empresa, titulo, requisitos_minimos, precio, clasificacion_por_edad, venta_activa, descripcion, fecha_lanzamiento)
VALUES (
    1,
    "Juego2",
    "Windows 11, 8GB RAM",
    15.50,
    "E",
    true,
    "Shooter multijugador clásico",
    '2010-11-20'
);
INSERT INTO juego (id_empresa, titulo, requisitos_minimos, precio, clasificacion_por_edad, venta_activa, descripcion, fecha_lanzamiento)
VALUES (
    2,
    "Juego3",
    "Windows 7, 8GB RAM",
    10.00,
    "T",
    true,
    "Shooter multijugador clásico",
    '2018-10-12'
);
INSERT INTO juego (id_empresa, titulo, requisitos_minimos, precio, clasificacion_por_edad, venta_activa, descripcion, fecha_lanzamiento)
VALUES (
    1,
    "Juego4",
    "Windows 10, 16GB RAM",
    24.99,
    "E",
    true,
    "Shooter multijugador clásico",
    '2018-09-05'
);
INSERT INTO juego (id_empresa, titulo, requisitos_minimos, precio, clasificacion_por_edad, venta_activa, descripcion, fecha_lanzamiento)
VALUES (
    2,
    "Juego5",
    "Windows 11, 16GB RAM, tarjeta grafica",
    21.49,
    "T",
    true,
    "juego de aventura",
    '2025-09-10'
);

INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, telefono, pais, id_rol)
VALUES (
    'gamer1',
    'gamer1@vaqueras.com',
    '$2a$12$mn7n3aoovMG0MuTGhBv4JugK3t.4BLdA.c.TIPxjmTZexWB62c0Me', -- password: admin123
    '2006-09-10',
    '12345678',
    'Mexico',
    3
);

INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, telefono, pais, id_rol)
VALUES (
    'gamer2',
    'gamer2@vaqueras.com',
    '$2a$12$mn7n3aoovMG0MuTGhBv4JugK3t.4BLdA.c.TIPxjmTZexWB62c0Me', -- password: admin123
    '2006-09-10',
    '12345678',
    'Honduras',
    3
);

INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, telefono, pais, id_rol)
VALUES (
    'gamer3',
    'gamer3@vaqueras.com',
    '$2a$12$mn7n3aoovMG0MuTGhBv4JugK3t.4BLdA.c.TIPxjmTZexWB62c0Me', -- password: admin123
    '2008-07-11',
    '12345678',
    'Guatemala',
    3
);

INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, telefono, pais, id_rol)
VALUES (
    'empresa1',
    'empresa1@empresa.com',
    '$2a$12$mn7n3aoovMG0MuTGhBv4JugK3t.4BLdA.c.TIPxjmTZexWB62c0Me', -- password: admin123
    '2002-09-10',
    '12345678',
    'Mexico',
    2
);INSERT INTO usuario (nickname, correo, password, fecha_nacimiento, telefono, pais, id_rol)
VALUES (
    'empresa2',
    'gamer2@empresa.com',
    '$2a$12$mn7n3aoovMG0MuTGhBv4JugK3t.4BLdA.c.TIPxjmTZexWB62c0Me', -- password: admin123
    '2000-09-10',
    '12345678',
    'Guatemala',
    2
);

use proyecto_vaqueras;

ALTER TABLE juego_imagen 
    DROP COLUMN url_imagen;
    
ALTER TABLE juego_imagen 
    ADD COLUMN imagen LONGBLOB NOT NULL,
    ADD COLUMN nombre_archivo VARCHAR(100),
    ADD COLUMN tipo_mime VARCHAR(50) DEFAULT 'image/jpeg',
    ADD COLUMN tamano_bytes INT;
    
ALTER TABLE usuario
    DROP COLUMN URL_avatar;
    
ALTER TABLE usuario
    ADD COLUMN avatar MEDIUMBLOB,
    ADD COLUMN avatar_nombre VARCHAR(100),
    ADD COLUMN avatar_tipo VARCHAR(50);
    
ALTER TABLE transaccion 
    ADD COLUMN comision_aplicada DECIMAL(5,2) COMMENT 'Porcentaje de comisión aplicado',
    ADD COLUMN ganancia_empresa DECIMAL(10,2) COMMENT 'Monto que recibe la empresa',
    ADD COLUMN ganancia_plataforma DECIMAL(10,2) COMMENT 'Monto de comisión para la plataforma';
    
ALTER TABLE banner
    ADD COLUMN imagen MEDIUMBLOB,
    ADD COLUMN imagen_nombre VARCHAR(100),
    ADD COLUMN imagen_tipo VARCHAR(50);    

INSERT INTO comision_global (porcentaje, fecha_inicio) 
VALUES (15.00, CURDATE());