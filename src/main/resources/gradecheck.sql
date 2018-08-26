DROP DATABASE IF EXISTS gradecheck;

CREATE DATABASE gradecheck;

USE gradecheck;

-- Roles (permisos)
CREATE TABLE roles(
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
CREATE TABLE faculties(
   id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null UNIQUE,
   state bool default 1
);

-- Tipos de carrera
CREATE TABLE career_types(
   id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null UNIQUE,
   state bool default 1
);

-- Carreras
CREATE TABLE careers(
   id int not null AUTO_INCREMENT PRIMARY KEY,
   name varchar(50) not null UNIQUE,
   faculty_id int not null,
   career_type_id int not null,
   state bool default 1,
   FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE RESTRICT,
   FOREIGN KEY (career_type_id) REFERENCES career_types(id) ON DELETE RESTRICT
);

-- Materias
CREATE TABLE courses(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	name varchar(50) not null,
   course_code char(10) not null UNIQUE,
	semester enum('1','2','12') not null, -- Ciclo en el que está disponible
	inter bool default 1, -- Si está disponible en interciclo o no
   laboratory bool default 1, -- Si incluye laboratorio práctico
	uv int not null, -- Unidades valorativas
   prerequisite_id int, -- Materia prerrequisito
	state bool default 1,
	FOREIGN KEY (prerequisite_id) REFERENCES courses(id) ON DELETE RESTRICT
);

-- Tabla intermedia entre carreras y materias (a la vez, pensum)
CREATE TABLE careers_courses(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   career_id int not null,
   course_id int not null,
   plan int not null, -- El plan al que pertenece esta configuración del pensum (osea, el año)
   pensum_year enum('1', '2', '3', '4', '5'), -- Año relativo al pensum
   pensum_semester enum('1', '2'), -- Ciclo relativo al pensum
   state bool default 1,
   FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE RESTRICT,
   FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT
);

-- Evaluaciones
CREATE TABLE evaluations(
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
   FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT
);

-- Usuarios del sistema
CREATE TABLE users(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	name varchar(50) not null,
	surname varchar(50) not null,
	username char(10) not null UNIQUE,
	pass varchar(500) not null,
	phone varchar(8) not null UNIQUE,
	email varchar(255) not null UNIQUE,
	state bool default 1
);

-- Estudiantes
CREATE TABLE students(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   user_id int not null,
   state bool default 1,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Intermedia entre carreras y estudiantes (necesaria para quienes cursen carreras después de terminar otra)
CREATE TABLE careers_students(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   career_id int not null,
   student_id int not null,
	income_year int not null, -- El año en el que comenzó la carrera
   state bool default 1, -- Indica si es la carrera actual del estudiante, o si ya fue terminada
   FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE RESTRICT,
   FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT
);

-- Empleados (desde docentes hasta admins)
CREATE TABLE employees(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   user_id int not null,
   role_id int not null,
   state bool default 1,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
   FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- Intermedia entre materias y empleados docentes
CREATE TABLE courses_teachers(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   course_id int not null,
   employee_id int not null,
   state bool default 1,
   FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT,
   FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE RESTRICT
);

-- Intermedia entre estudiantes y materias impartidas por profesores
CREATE TABLE registered_courses(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	student_id int not null,
	course_teacher_id int not null,
   course_state enum('En curso', 'Aprobada', 'Reprobada', 'Retirada'),
   course_year int not null,
   semester enum('1', '2', 'Interciclo') not null,
	state bool default 1,
	FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
	FOREIGN KEY (course_teacher_id) REFERENCES courses_teachers(id) ON DELETE RESTRICT
);

-- Notas
CREATE TABLE grades(
	id int not null AUTO_INCREMENT PRIMARY KEY,
	grade float(4,2) not null,
   observations varchar(1000),
   registered_course_id int not null,
   evaluation_id int not null,
	state bool default 1,
	FOREIGN KEY (registered_course_id) REFERENCES registered_courses(id) ON DELETE RESTRICT,
	FOREIGN KEY (evaluation_id) REFERENCES evaluations(id) ON DELETE RESTRICT
);

-- Correcciones
CREATE TABLE corrections(
	id int not null AUTO_INCREMENT PRIMARY KEY,
   description varchar(1000) not null,
   file_path varchar(1000), -- Si se adjunta un archivo
   correction_state enum('Pendiente', 'Aprobada', 'Denegada') not null,
   grade_id int not null,
   state bool default 1,
   FOREIGN KEY (grade_id) REFERENCES grades(id) ON DELETE RESTRICT
);

INSERT INTO roles VALUES(null, 'Administrador', 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO roles VALUES(null, 'Docente', 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);

INSERT INTO faculties VALUES(null, 'Ingenieria', 1);
INSERT INTO faculties VALUES(null, 'Medicina', 1);
INSERT INTO faculties VALUES(null, 'Derecho', 1);

INSERT INTO career_types VALUES(null, 'Ingenieria', 1);
INSERT INTO career_types VALUES(null, 'Tecnico', 1);
INSERT INTO career_types VALUES(null, 'Maestria', 1);

INSERT INTO careers VALUES(null, 'Ingenieria en Ciencias de la Computacion', 1, 1, 1);
INSERT INTO careers VALUES(null, 'Ingenieria Civil', 1, 1, 1);
INSERT INTO careers VALUES(null, 'Ingenieria Industrial', 1, 1, 1);

INSERT INTO courses VALUES(null, 'Matematica I', 'MAT123', '12', 1, 0, 4, null, 1);
INSERT INTO courses VALUES(null, 'Matematica II', 'MAT456', '12', 1, 0, 4, 1, 1);
INSERT INTO courses VALUES(null, 'Matematica III', 'MAT789', '12', 1, 0, 4, 2, 1);
INSERT INTO courses VALUES(null, 'Expresion Oral y Escrita', 'EOE123', '12', 1, 0, 3, null, 1);

INSERT INTO careers_courses VALUES(null, 1, 1, 2017, '1', '1', 1);
INSERT INTO careers_courses VALUES(null, 1, 2, 2017, '1', '2', 1);
INSERT INTO careers_courses VALUES(null, 1, 3, 2017, '2', '2', 1);
INSERT INTO careers_courses VALUES(null, 1, 4, 2017, '1', '1', 1);

INSERT INTO evaluations VALUES(null, 'Trabajo de investigacion', 'Los estudiantes investigaran los tipos de funciones trigonometricas', 15, '1', 0, '2018-03-01', '2018-03-07', 1, 1);
INSERT INTO evaluations VALUES(null, 'Examen corto', 'Examen corto que cubre los temas vistos en las primeras dos semanas de clase', 25, '1', 0, '2018-02-21', '2018-02-28', 1, 1);
INSERT INTO evaluations VALUES(null, 'Examen parcial', 'Evaluacion de los contenidos vistos en el primer periodo', 60, '1', 0, '2018-03-14', '2018-03-21', 1, 1);
INSERT INTO evaluations VALUES(null, 'Trabajo de investigacion', 'Los estudiantes investigaran las aplicaciones de las integrales en la vida real', 15, '1', 0, '2018-03-01', '2018-03-07', 2, 1);
INSERT INTO evaluations VALUES(null, 'Examen corto', 'Examen corto que cubre los temas vistos en las primeras dos semanas de clase', 25, '1', 0, '2018-02-21', '2018-02-28', 2, 1);
INSERT INTO evaluations VALUES(null, 'Examen parcial', 'Evaluacion de los contenidos vistos en el primer periodo', 60, '1', 0, '2018-03-14', '2018-03-21', 2, 1);
INSERT INTO evaluations VALUES(null, 'Hipertextos', 'Cuadros resumen de los contenidos vistos semanalmente', 30, '1', 0, '2018-03-08', '2018-03-13', 4, 1);
INSERT INTO evaluations VALUES(null, 'Portafolio', 'Revisión de portafolio de actividades realizadas a lo largo del periodo', 30, '1', 0, '2018-03-08', '2018-03-13', 4, 1);
INSERT INTO evaluations VALUES(null, 'Examen parcial', 'Evaluacion de los contenidos vistos en el primer periodo', 40, '1', 0, '2018-03-14', '2018-03-21', 4, 1);

INSERT INTO users VALUES(null, 'David', 'Zometa', 'DZ20070001', 'root123', '77123566', 'zometa@gmail.com', 1);
INSERT INTO users VALUES(null, 'Claudia Marcela', 'Sanchez Palacios', 'CS20140001', 'mate123', '74157118', 'claudia@gmail.com', 1);
INSERT INTO users VALUES(null, 'Oscar Ernesto', 'Mendez Argueta', 'OM20170001', 'osca123', '72914572', 'oscar@gmail.com', 1);
INSERT INTO users VALUES(null, 'Kevin Roberto', 'Moran Garcia', 'KM20170002', 'kevi123', '76831222', 'kevin@gmail.com', 1);
INSERT INTO users VALUES(null, 'Raul Alberto', 'Alvarado Ramirez', 'RA20170003', 'raul123', '78910235', 'raul@gmail.com', 1);
INSERT INTO users VALUES(null, 'Iris', 'Irisita', 'II20100001', 'leng123', '71254618', 'iris@gmail.com', 1);

INSERT INTO students VALUES(null, 3, 1);
INSERT INTO students VALUES(null, 4, 1);
INSERT INTO students VALUES(null, 5, 1);

INSERT INTO careers_students VALUES(null, 1, 1, 2018, 1);
INSERT INTO careers_students VALUES(null, 1, 2, 2018, 1);
INSERT INTO careers_students VALUES(null, 1, 3, 2018, 1);

INSERT INTO employees VALUES(null, 1, 1, 1);
INSERT INTO employees VALUES(null, 2, 2, 1);
INSERT INTO employees VALUES(null, 6, 2, 1);

INSERT INTO courses_teachers VALUES(null, 1, 2, 1);
INSERT INTO courses_teachers VALUES(null, 2, 2, 1);
INSERT INTO courses_teachers VALUES(null, 4, 3, 1);

INSERT INTO registered_courses VALUES(null, 1, 1, 'En curso', 2018, '1', 1);
INSERT INTO registered_courses VALUES(null, 1, 3, 'En curso', 2018, '1', 1);
INSERT INTO registered_courses VALUES(null, 2, 1, 'Aprobada', 2017, '1', 1);
INSERT INTO registered_courses VALUES(null, 2, 2, 'En curso', 2018, '1', 1);

INSERT INTO grades VALUES(null, 7.5, null, 1, 1, 1);
INSERT INTO grades VALUES(null, 6, 'Entregó tarde', 1, 2, 1);
INSERT INTO grades VALUES(null, 7, null, 1, 3, 1);
INSERT INTO grades VALUES(null, 10, 'Felicitaciones', 2, 7, 1);
INSERT INTO grades VALUES(null, 8, null, 2, 8, 1);
INSERT INTO grades VALUES(null, 9.5, null, 2, 9, 1);
INSERT INTO grades VALUES(null, 10, null, 3, 1, 1);
INSERT INTO grades VALUES(null, 10, null, 3, 2, 1);
INSERT INTO grades VALUES(null, 10, null, 3, 3, 1);
INSERT INTO grades VALUES(null, 10, null, 4, 4, 1);
INSERT INTO grades VALUES(null, 10, null, 4, 5, 1);