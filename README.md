# Rutas

## /Students
### /read
Método: GET<br>
Parámetros: Ninguno<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Lista de estudiantes
  * Estudiante
    * Usuario
    
### /read/{param}
Método: GET<br>
Parámetros: param(en url)<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Lista de estudiantes
  * Estudiante
    * Usuario
    
### /readByUser/{userId}
Método: GET<br>
Parámetros: userId(en url)<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Estudiante
    * Usuario
    
### /create
Método: POST<br>
Parámetros: name, surname, pass, passConfirm, email, phone<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student created with user id {userId} and student id {studentId}

Posibles errores:<br>
* 400
  * Must specify following parameters: {lista de parámetros}.
* 406
  * Given passwords do not match.
  
### /update
Método: PUT<br>
Parámetros: name, surname, pass(opcional), passConfirm(opcional), email, phone, state, userId<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student updated with user id {userId}

### /delete/{userId}
Método: DELETE<br>
Parámetros: userId(en url)<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student with user id {userId} deleted

### /login
Método: POST<br>
Parámetros: username, pass<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Estudiante logueado
  * Usuario
  
Posibles errores:<br>
* 400
  * Must specify username
  * Must specify password
  * Could not make login
* 401
  * Username or password not found
* 403
  * This service is meant for students only
  * Your user is deactivated
  
## /Faculties
### /read
Método: GET<br>
Parámetros: Ninguno<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Lista de facultades
  * Facultad
    
### /read/{param}
Método: GET<br>
Parámetros: param(en url)<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Lista de facultades
  * Facultad
    
### /readById/{id}
Método: GET<br>
Parámetros: id(en url)<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Facultad
    
### /create
Método: POST<br>
Parámetros: name<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty created with id {id}

### /update
Método: PUT<br>
Parámetros: name, state, id<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty updated with id {id}

### /delete/{id}
Método: DELETE<br>
Parámetros: id(en url)<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty with id {userId} deleted  

## /RegisteredCourses
### /read/{student_id}
Método: GET<br>
Parámetros: student_id<br>
Tipo de Response: JSON<br>
Response exitosa:<br>
* Lista de materias del estudiante
  * Relacion materia-estudiante
    * Relacion materia-docente
      * Materia
    * Lista de notas
      * Nota
        * Evaluacion a la que pertenece
