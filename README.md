# Rutas

## /Student
### /read
Método: GET<br>
Parámetros: Ninguno<br>
Response exitosa:<br>
* Lista de estudiantes
  * Estudiante
    * Usuario
    
### /read/{param}
Método: GET<br>
Parámetros: param(en url)<br>
Response exitosa:<br>
* Lista de estudiantes
  * Estudiante
    * Usuario
    
### /readByUser/{userId}
Método: GET<br>
Parámetros: userId(en url)<br>
Response exitosa:<br>
* Estudiante
    * Usuario
    
### /create
Método: POST<br>
Parámetros: name, surname, pass, passConfirm, email, phone<br>
Response exitosa:<br>
* Student created with user id {userId} and student id {studentId}

### /update
Método: PUT<br>
Parámetros: name, surname, pass(opcional), passConfirm(opcional), email, phone, userId<br>
Response exitosa:<br>
* Student updated with user id {userId}

### /delete/{userId}
Método: DELETE<br>
Parámetros: userId(en url)<br>
Response exitosa:<br>
* Student with user id {userId} deleted

### /login
Método: POST<br>
Parámetros: username, pass<br>
Response exitosa:<br>
* Estudiante logueado
  * Usuario
  
## /RegisteredCourse
### /read/{student_id}
Método: GET<br>
Parámetros: student_id<br>
Response exitosa:<br>
* Lista de materias del estudiante
  * Relacion materia-estudiante
    * Relacion materia-docente
      * Materia
    * Lista de notas
      * Nota
        * Evaluacion a la que pertenece
