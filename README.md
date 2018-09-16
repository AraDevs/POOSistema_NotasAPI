# Rutas

## /Students
### /Students/read
Método: GET<br>
Descripción: Devuelve la lista de todos los estudiantes<br>
Parámetros: Ninguno<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto:</summary><p>
 
```json
{
    "student": [
        {
            "@type": "student",
            "id": "1",
            "state": "true",
            "user": {
                "email": "oscar@gmail.com",
                "id": "3",
                "name": "Oscar Ernesto",
                "pass": "osca123",
                "phone": "72914572",
                "state": "true",
                "surname": "Mendez Argueta",
                "username": "OM20170001"
            }
        },
        {
            "@type": "student",
            "id": "2",
            "state": "true",
            "user": {
                "email": "kevin@gmail.com",
                "id": "4",
                "name": "Kevin Roberto",
                "pass": "kevi123",
                "phone": "76831222",
                "state": "true",
                "surname": "Moran Garcia",
                "username": "KM20170002"
            }
        },
        {
            "@type": "student",
            "id": "3",
            "state": "true",
            "user": {
                "email": "raul@gmail.com",
                "id": "5",
                "name": "Raul Alberto",
                "pass": "raul123",
                "phone": "78910235",
                "state": "true",
                "surname": "Alvarado Ramirez",
                "username": "RA20170003"
            }
        }
    ]
}
``` 

</p></details>
 
### /Students/read/{param}
Método: GET<br>
Descripción: Devuelve una lista de estudiantes cuyo nombre, apellido, username, email o teléfono contengan la cadena especificada en 'param'<br>
Parámetros: param(en url)<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto (param = 'erto'):</summary><p>
 
```json
{
    "student": [
        {
            "@type": "student",
            "id": "2",
            "state": "true",
            "user": {
                "email": "kevin@gmail.com",
                "id": "4",
                "name": "Kevin Roberto",
                "pass": "kevi123",
                "phone": "76831222",
                "state": "true",
                "surname": "Moran Garcia",
                "username": "KM20170002"
            }
        },
        {
            "@type": "student",
            "id": "3",
            "state": "true",
            "user": {
                "email": "raul@gmail.com",
                "id": "5",
                "name": "Raul Alberto",
                "pass": "raul123",
                "phone": "78910235",
                "state": "true",
                "surname": "Alvarado Ramirez",
                "username": "RA20170003"
            }
        }
    ]
}
``` 

</p></details>
    
### /Students/readByUser/{userId}
Método: GET<br>
Descripción: Devuelve un estudiante, dado su id de usuario (Si el id no corresponde a un estudiante, no devuelve nada)<br>
Parámetros: userId(en url)<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto (userId = '3'):</summary><p>
 
```json
{
    "id": "1",
    "state": "true",
    "user": {
        "email": "oscar@gmail.com",
        "id": "3",
        "name": "Oscar Ernesto",
        "pass": "osca123",
        "phone": "72914572",
        "state": "true",
        "surname": "Mendez Argueta",
        "username": "OM20170001"
    }
}
```

</p></details>
    
### /Students/create
Método: POST<br>
Descripción: Crea un nuevo usuario del tipo estudiante<br>
Parámetros: name, surname, pass, passConfirm, email, phone<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student created with user id {userId} and student id {studentId}

<details><summary>Posibles errores:</summary><p>
 
* 400
  * Must specify following parameters: {parameter list}.
  * Could not insert the student
* 406
  * Given passwords do not match.
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}
  
</p></details>
  
### /Students/update
Método: PUT<br>
Descripción: Modifica el usuario y estudiante que contenga el id de usuario especificado<br>
Parámetros: name, surname, pass(opcional), passConfirm(opcional), email, phone, state, userId<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student updated with user id {userId}

<details><summary>Posibles errores:</summary><p>
 
* 400
  * Must specify following parameters: {parameter list}.
  * Must specify both pass and passConfirm, or not specify any of them.
  * Could not update the student
* 404
  * Given userId does not exist.
* 406
  * Given passwords do not match.
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}
  
</p></details>

### /Students/delete/{userId}
Método: DELETE<br>
Descripción: Elimina el estudiante y usuario con el id de usuario especificado<br>
Parámetros: userId(en url)<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Student with user id {userId} deleted

<details><summary>Posibles errores:</summary><p>

* 400
  * Could not delete the student
* 404
  * Given userId does not exist.
* 409
  * Cannot delete record, parent row conflict
  * SQL Unhandled Error: {sql error code}

</p></details>

### /Students/login
Método: POST<br>
Descripción: Verifica que el nombre de usuario y contraseña proporcinados correspondan a un estudiante, en cuyo caso devuelve el objeto del mismo. Caso contrario, devuelve un error.
Parámetros: username, pass<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto (username = 'OM20170001', pass = 'osca123'):</summary><p>
 
```json
{
    "id": "1",
    "state": "true",
    "user": {
        "email": "oscar@gmail.com",
        "id": "3",
        "name": "Oscar Ernesto",
        "pass": "osca123",
        "phone": "72914572",
        "state": "true",
        "surname": "Mendez Argueta",
        "username": "OM20170001"
    }
}
```

</p></details>
  
<details><summary>Posibles errores:</summary><p>
 
* 400
  * Must specify username
  * Must specify password
  * Could not make login
* 401
  * Username or password not found
* 403
  * This service is meant for students only
  * Your user is deactivated
  
</p></details>
  
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
