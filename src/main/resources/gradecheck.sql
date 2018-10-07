DROP DATABASE IF EXISTS gradecheck;

CREATE DATABASE gradecheck;

USE gradecheck;

-- Roles (permisos)
CREATE TABLE `role`(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	role varchar(50) not null UNIQUE,
	teach bool default 0,
	manage_users bool default 0,
	manage_students bool default 0,
	manage_employees bool default 0,
	manage_faculties bool default 0,
	manage_careers bool default 0,
	manage_courses bool default 0,
	manage_pensums bool default 0,
	manage_evaluations bool default 0,
	manage_roles bool default 0,
	state bool default 1
);

-- Facultades
CREATE TABLE faculty(
   id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null UNIQUE,
   state bool default 1
);

-- Tipos de carrera
CREATE TABLE career_type(
   id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null UNIQUE,
   state bool default 1
);

-- Carreras
CREATE TABLE career(
   id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null UNIQUE,
   faculty_id int not null,
   career_type_id int not null,
   state bool default 1,
   FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE RESTRICT,
   FOREIGN KEY (career_type_id) REFERENCES career_type(id) ON DELETE RESTRICT
);

-- Materias
CREATE TABLE course(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	name varchar(50) not null,
   course_code char(10) not null UNIQUE,
	semester enum('1','2','12') not null, -- Ciclo en el que está disponible
	inter bool default 1, -- Si está disponible en interciclo o no
   laboratory bool default 1, -- Si incluye laboratorio práctico
	uv int not null, -- Unidades valorativas
   prerequisite_id int, -- Materia prerrequisito
   faculty_id int, -- Facultad a la que pertenece
	state bool default 1,
	FOREIGN KEY (prerequisite_id) REFERENCES course(id) ON DELETE RESTRICT,
	FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE RESTRICT
);

-- Tabla intermedia entre carreras y materias (a la vez, pensum)
CREATE TABLE career_course(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   career_id int not null,
   course_id int not null,
   plan int not null, -- El plan al que pertenece esta configuración del pensum (osea, el año)
   pensum_year enum('1', '2', '3', '4', '5'), -- Año relativo al pensum
   pensum_semester enum('1', '2'), -- Ciclo relativo al pensum
   state bool default 1,
   FOREIGN KEY (career_id) REFERENCES career(id) ON DELETE RESTRICT,
   FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE RESTRICT
);

-- Evaluaciones
CREATE TABLE evaluation(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null,
   description varchar(1000) not null,
   percentage int not null,
   period enum('1','2','3'), -- El período al que pertenece
   laboratory bool default 0, -- Si pertenece a laboratorio o no
   start_date date not null,
   end_date date not null,
   course_id int not null,
   state bool default 1,
   FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE RESTRICT
);

-- Personas
CREATE TABLE person(
        id int not null AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) not null,
    surname varchar(50) not null,
    phone varchar(8) not null,
    email varchar(255) not null UNIQUE,
    dui char(10) not null UNIQUE,
    address varchar(500) not null,
    state bool default 1
);

-- Usuarios del sistema
CREATE TABLE `user`(
	id int not null AUTO_INCREMENT PRIMARY KEY,
    username char(10) not null UNIQUE,
    pass varchar(500) not null,
    image_path varchar(1000) not null,
    person_id int not null,
    state bool default 1,
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE RESTRICT    
);

-- Estudiantes
CREATE TABLE student(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   user_id int not null,
   state bool default 1,
   FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE RESTRICT
);

-- Intermedia entre carreras y estudiantes (necesaria para quienes cursen carreras después de terminar otra)
CREATE TABLE career_student(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   career_id int not null,
   student_id int not null,
	income_year int not null, -- El año en el que comenzó la carrera
   career_state enum('En curso', 'Egresado', 'Abandonado') not null,
   state bool default 1,
   FOREIGN KEY (career_id) REFERENCES career(id) ON DELETE RESTRICT,
   FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE RESTRICT
);

-- Empleados (desde docentes hasta admins)
CREATE TABLE employee(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   user_id int not null,
   role_id int not null,
   state bool default 1,
   FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE RESTRICT,
   FOREIGN KEY (role_id) REFERENCES `role`(id) ON DELETE RESTRICT
);

-- Intermedia entre materias y empleados docentes
CREATE TABLE course_teacher(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   course_id int not null,
   employee_id int not null,
   course_year int not null,
   semester enum('1', '2', 'Interciclo') not null,
   class_count int unsigned not null, -- Cantidad de clases 
   state bool default 1,
   FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE RESTRICT,
   FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE RESTRICT
);

-- Intermedia entre estudiantes y materias impartidas por profesores
CREATE TABLE registered_course(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	student_id int not null,
	course_teacher_id int not null,
   course_state enum('En curso', 'Aprobada', 'Reprobada', 'Retirada'),
	state bool default 1,
	FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE RESTRICT,
	FOREIGN KEY (course_teacher_id) REFERENCES course_teacher(id) ON DELETE RESTRICT
);

CREATE TABLE unattendance(
        id int not null AUTO_INCREMENT PRIMARY KEY,
    registered_course_id int not null,
    unattendance_date date,
    state bool default 1,
    FOREIGN KEY (registered_course_id) REFERENCES registered_course(id) ON DELETE RESTRICT
);

-- Notas
CREATE TABLE grade(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	grade float(4,2) not null,
   observations varchar(1000),
   registered_course_id int not null,
   evaluation_id int not null,
	state bool default 1,
	FOREIGN KEY (registered_course_id) REFERENCES registered_course(id) ON DELETE RESTRICT,
	FOREIGN KEY (evaluation_id) REFERENCES evaluation(id) ON DELETE RESTRICT
);

-- Correcciones
CREATE TABLE correction(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   description varchar(1000) not null,
   file_path varchar(1000), -- Si se adjunta un archivo
   correction_state enum('Pendiente', 'Aprobada', 'Denegada') not null,
   grade_id int not null,
   state bool default 1,
   FOREIGN KEY (grade_id) REFERENCES grade(id) ON DELETE RESTRICT
);

INSERT INTO `role` VALUES(null, 'Administrador', 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `role` VALUES(null, 'Docente', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);

INSERT INTO faculty VALUES(null, 'Ingenieria', 1);
INSERT INTO faculty VALUES(null, 'Medicina', 1);
INSERT INTO faculty VALUES(null, 'Derecho', 1);

INSERT INTO career_type VALUES(null, 'Ingenieria', 1);
INSERT INTO career_type VALUES(null, 'Tecnico', 1);
INSERT INTO career_type VALUES(null, 'Maestria', 1);

INSERT INTO career VALUES(null, 'Ingenieria en Ciencias de la Computacion', 1, 1, 1);
INSERT INTO career VALUES(null, 'Ingenieria Civil', 1, 1, 1);
INSERT INTO career VALUES(null, 'Ingenieria Industrial', 1, 1, 1);

INSERT INTO course VALUES(null, 'Matematica I', 'MAT123', '12', 1, 0, 4, null, 1, 1);
INSERT INTO course VALUES(null, 'Matematica II', 'MAT456', '12', 1, 0, 4, 1, 1, 1);
INSERT INTO course VALUES(null, 'Matematica III', 'MAT789', '12', 1, 0, 4, 2, 1, 1);
INSERT INTO course VALUES(null, 'Expresion Oral y Escrita', 'EOE123', '12', 1, 0, 3, null, 1, 1);

INSERT INTO career_course VALUES(null, 1, 1, 2017, '1', '1', 1);
INSERT INTO career_course VALUES(null, 1, 2, 2017, '1', '2', 1);
INSERT INTO career_course VALUES(null, 1, 3, 2017, '2', '2', 1);
INSERT INTO career_course VALUES(null, 1, 4, 2017, '1', '1', 1);

INSERT INTO evaluation VALUES(null, 'Trabajo de investigacion', 'Los estudiantes investigaran los tipos de funciones trigonometricas', 15, '1', 0, '2018-03-01', '2018-03-07', 1, 1);
INSERT INTO evaluation VALUES(null, 'Examen corto', 'Examen corto que cubre los temas vistos en las primeras dos semanas de clase', 25, '1', 0, '2018-02-21', '2018-02-28', 1, 1);
INSERT INTO evaluation VALUES(null, 'Examen parcial', 'Evaluacion de los contenidos vistos en el primer periodo', 60, '1', 0, '2018-03-14', '2018-03-21', 1, 1);
INSERT INTO evaluation VALUES(null, 'Trabajo de investigacion', 'Los estudiantes investigaran las aplicaciones de las integrales en la vida real', 15, '1', 0, '2018-03-01', '2018-03-07', 2, 1);
INSERT INTO evaluation VALUES(null, 'Examen corto', 'Examen corto que cubre los temas vistos en las primeras dos semanas de clase', 25, '1', 0, '2018-02-21', '2018-02-28', 2, 1);
INSERT INTO evaluation VALUES(null, 'Examen parcial', 'Evaluacion de los contenidos vistos en el primer periodo', 60, '1', 0, '2018-03-14', '2018-03-21', 2, 1);
INSERT INTO evaluation VALUES(null, 'Hipertextos', 'Cuadros resumen de los contenidos vistos semanalmente', 30, '1', 0, '2018-03-08', '2018-03-13', 4, 1);
INSERT INTO evaluation VALUES(null, 'Portafolio', 'Revisión de portafolio de actividades realizadas a lo largo del periodo', 30, '1', 0, '2018-03-08', '2018-03-13', 4, 1);
INSERT INTO evaluation VALUES(null, 'Examen parcial', 'Evaluacion de los contenidos vistos en el primer periodo', 40, '1', 0, '2018-03-14', '2018-03-21', 4, 1);

INSERT INTO person VALUES(null, 'David', 'Zometa', '77123566', 'zometa@gmail.com', '05877982-1', 'Colonia Miralvalle, Casa #10, San Salvador', 1);
INSERT INTO person VALUES(null, 'Claudia Marcela', 'Sanchez Palacios', '74157118', 'claudia@gmail.com', '05977982-4', 'Colonia América, Casa #22, San Salvador', 1);
INSERT INTO person VALUES(null, 'Oscar Ernesto', 'Mendez Argueta', '72914572', 'oscar@gmail.com', '95877902-1', 'Colonia Australia, Casa #14, Mejicanos', 1);
INSERT INTO person VALUES(null, 'Kevin Roberto', 'Moran Garcia', '76831222', 'kevin@gmail.com', '05878982-8', 'Colonia Monteverde, Casa #8, Ayutuxtepeque', 1);
INSERT INTO person VALUES(null, 'Raul Alberto', 'Alvarado Ramirez', '78910235', 'raul@gmail.com', '05867982-6', 'Colonia Campanera, Casa #18, Soyapango', 1);
INSERT INTO person VALUES(null, 'Iris', 'Irisita', '71254618', 'iris@gmail.com', '15877982-2', 'Colonia Miralvalle, Casa #11, San Salvador', 1);

INSERT INTO `user` VALUES(null, 'DZ20070001', 'root123', '', 1, 1);
INSERT INTO `user` VALUES(null, 'CS20140001', 'mate123', '', 2, 1);
INSERT INTO `user` VALUES(null, 'OM20170001', 'osca123', '', 3, 1);
INSERT INTO `user` VALUES(null, 'KM20170002', 'kevi123', '', 4, 1);
INSERT INTO `user` VALUES(null, 'RA20170003', 'raul123', '', 5, 1);
INSERT INTO `user` VALUES(null, 'II20100001', 'leng123', '', 6, 1);

INSERT INTO student VALUES(null, 3, 1);
INSERT INTO student VALUES(null, 4, 1);
INSERT INTO student VALUES(null, 5, 1);

INSERT INTO career_student VALUES(null, 1, 1, 2018, 1, 1);
INSERT INTO career_student VALUES(null, 1, 2, 2018, 1, 1);
INSERT INTO career_student VALUES(null, 1, 3, 2018, 1, 1);

INSERT INTO employee VALUES(null, 1, 1, 1);
INSERT INTO employee VALUES(null, 2, 2, 1);
INSERT INTO employee VALUES(null, 6, 2, 1);

INSERT INTO course_teacher VALUES(null, 1, 2, 2017, '1', 30, 1);
INSERT INTO course_teacher VALUES(null, 1, 2, 2018, '1', 30, 1);
INSERT INTO course_teacher VALUES(null, 2, 2, 2018, '1', 30, 1);
INSERT INTO course_teacher VALUES(null, 4, 3, 2018, '1', 30, 1);

INSERT INTO registered_course VALUES(null, 1, 2, 'En curso', 1);
INSERT INTO registered_course VALUES(null, 1, 4, 'En curso', 1);
INSERT INTO registered_course VALUES(null, 2, 1, 'Aprobada', 1);
INSERT INTO registered_course VALUES(null, 2, 3, 'En curso', 1);

INSERT INTO grade VALUES(null, 7.5, null, 1, 1, 1);
INSERT INTO grade VALUES(null, 6, 'Entregó tarde', 1, 2, 1);
INSERT INTO grade VALUES(null, 7, null, 1, 3, 1);
INSERT INTO grade VALUES(null, 10, 'Felicitaciones', 2, 7, 1);
INSERT INTO grade VALUES(null, 8, null, 2, 8, 1);
INSERT INTO grade VALUES(null, 9.5, null, 2, 9, 1);
INSERT INTO grade VALUES(null, 10, null, 3, 1, 1);
INSERT INTO grade VALUES(null, 10, null, 3, 2, 1);
INSERT INTO grade VALUES(null, 10, null, 3, 3, 1);
INSERT INTO grade VALUES(null, 10, null, 4, 4, 1);
INSERT INTO grade VALUES(null, 10, null, 4, 5, 1);