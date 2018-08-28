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
  * Must specify following parameters: {parameter list}.
  * Could not insert the student
* 406
  * Given passwords do not match.
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}
  
### /update
Método: PUT<br>
Parámetros: name, surname, pass(opcional), passConfirm(opcional), email, phone, state, userId<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student updated with user id {userId}

Posibles errores:<br>
* 400
  * Must specify following parameters: {parameter list}.
  * Could not update the student
* 404
  * Given userId does not exist.
* 406
  * Given passwords do not match.
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}

### /delete/{userId}
Método: DELETE<br>
Parámetros: userId(en url)<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student with user id {userId} deleted

Posibles errores:<br>
* 400
  * Could not delete the student
* 404
  * Given userId does not exist.
* 409
  * Cannot delete record, parent row conflict
  * SQL Unhandled Error: {sql error code}

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

Posibles errores:<br>
* 400
  * Must specify faculty name
  * Could not insert the faculty
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}

### /update
Método: PUT<br>
Parámetros: name, state, id<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty updated with id {id}

Posibles errores:<br>
* 400
  * Must specify following parameters: {parameter list}.
  * Could not update the faculty
* 404
  * Given id does not exist.
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}

### /delete/{id}
Método: DELETE<br>
Parámetros: id(en url)<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty with id {userId} deleted  

Posibles errores:<br>
* 400
  * Could not delete the faculty
* 404
  * Given id does not exist.
* 409
  * Cannot delete record, parent row conflict
  * SQL Unhandled Error: {sql error code}

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
