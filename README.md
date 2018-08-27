# Rutas

## /Student
### /read
Método: GET<br>
Parámetros: Ninguno<br>
Response exitosa:<br>
* Lista de estudiantes
  * Estudiante
    * Usuario

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
