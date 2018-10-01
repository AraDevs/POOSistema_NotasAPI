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
### /Faculties/read
Método: GET<br>
Descripción: Devuelve la lista de todas las facultades registradas<br>
Parámetros: Ninguno<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto:</summary><p>
 
```json
{
    "faculty": [
        {
            "@type": "faculty",
            "id": "1",
            "name": "Ingenieria",
            "state": "true"
        },
        {
            "@type": "faculty",
            "id": "2",
            "name": "Medicina",
            "state": "true"
        },
        {
            "@type": "faculty",
            "id": "3",
            "name": "Derecho",
            "state": "true"
        }
    ]
}
```

</p></details>
    
### /Faculties/read/{param}
Método: GET<br>
Descripción: Devuelve una lista de facultades cuyo nombre contenga la cadena especificada en 'param'<br>
Parámetros: param(en url)<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto (param = 'i'):</summary><p>
 
```json
{
    "faculty": [
        {
            "@type": "faculty",
            "id": "1",
            "name": "Ingenieria",
            "state": "true"
        },
        {
            "@type": "faculty",
            "id": "2",
            "name": "Medicina",
            "state": "true"
        }
    ]
}
```

</p></details>
    
### /Faculties/readById/{id}
Método: GET<br>
Descripción: Devuelve una facultad, dado su id<br>
Parámetros: id(en url)<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto (id = '1'):</summary><p>
 
```json
{
    "id": "1",
    "name": "Ingenieria",
    "state": "true"
}
```

</p></details>
    
### /Faculties/create
Método: POST<br>
Descripción: Crea una nueva facultad<br>
Parámetros: name<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty created with id {id}

<details><summary>Posibles errores:</summary><p>
 
* 400
  * Must specify faculty name
  * Could not insert the faculty
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}
 
</p></details>

### /Faculties/update
Método: PUT<br>
Descripción: Modifica la facultad con el id dado<br>
Parámetros: name, state, id<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty updated with id {id}

<details><summary>Posibles errores:</summary><p>
 
* 400
  * Must specify following parameters: {parameter list}.
  * Could not update the faculty
* 404
  * Given id does not exist.
* 409
  * Duplicated value. Could not complete operation
  * SQL Unhandled Error: {sql error code}

</p></details>

### /Faculties/delete/{id}
Método: DELETE<br>
Descripción: Elimina la facultad con el id dado<br>
Parámetros: id(en url)<br>
Tipo de Response: Plain text<br>
Response exitosa:<br>
* Faculty with id {userId} deleted  

<details><summary>Posibles errores:</summary><p>
 
* 400
  * Could not delete the faculty
* 404
  * Given id does not exist.
* 409
  * Cannot delete record, parent row conflict
  * SQL Unhandled Error: {sql error code}
  
</p></details>

## /RegisteredCourses
### /RegisteredCourses/read/{student_id}
Método: GET<br>
Descripción: Devuelve una lista conteniendo todas las materias inscritas por el estudiante cuyo id de estudiante haya sido proporcionado, especificando las evaluaciones, notas, y docentes de cada materia.
Parámetros: student_id<br>
Tipo de Response: JSON<br>
<details><summary>Ejemplo de JSON devuelto (student_id = '1'):</summary><p>

```json
{
    "registeredCourse": [
        {
            "@type": "registeredCourse",
            "courseState": "En curso",
            "courseTeacher": {
                "course": {
                    "courseCode": "MAT123",
                    "id": "1",
                    "inter": "true",
                    "laboratory": "false",
                    "name": "Matematica I",
                    "semester": "12",
                    "state": "false",
                    "uv": "4"
                },
                "id": "1",
                "state": "true"
            },
            "courseYear": "2018",
            "gradeList": [
                {
                    "evaluation": {
                        "description": "Los estudiantes investigaran los tipos de funciones trigonometricas",
                        "endDate": "2018-03-06T18:00:00-06:00",
                        "id": "1",
                        "laboratory": "false",
                        "name": "Trabajo de investigacion",
                        "percentage": "15",
                        "period": "1",
                        "startDate": "2018-02-28T18:00:00-06:00",
                        "state": "true"
                    },
                    "grade": "7.5",
                    "id": "1",
                    "state": "true"
                },
                {
                    "evaluation": {
                        "description": "Examen corto que cubre los temas vistos en las primeras dos semanas de clase",
                        "endDate": "2018-02-27T18:00:00-06:00",
                        "id": "2",
                        "laboratory": "false",
                        "name": "Examen corto",
                        "percentage": "25",
                        "period": "1",
                        "startDate": "2018-02-20T18:00:00-06:00",
                        "state": "true"
                    },
                    "grade": "6.0",
                    "id": "2",
                    "observations": "Entregó tarde",
                    "state": "true"
                },
                {
                    "evaluation": {
                        "description": "Evaluacion de los contenidos vistos en el primer periodo",
                        "endDate": "2018-03-20T18:00:00-06:00",
                        "id": "3",
                        "laboratory": "false",
                        "name": "Examen parcial",
                        "percentage": "60",
                        "period": "1",
                        "startDate": "2018-03-13T18:00:00-06:00",
                        "state": "true"
                    },
                    "grade": "7.0",
                    "id": "3",
                    "state": "true"
                }
            ],
            "id": "1",
            "semester": "1",
            "state": "true"
        },
        {
            "@type": "registeredCourse",
            "courseState": "En curso",
            "courseTeacher": {
                "course": {
                    "courseCode": "EOE123",
                    "id": "4",
                    "inter": "true",
                    "laboratory": "false",
                    "name": "Expresion Oral y Escrita",
                    "semester": "12",
                    "state": "false",
                    "uv": "3"
                },
                "id": "3",
                "state": "true"
            },
            "courseYear": "2018",
            "gradeList": [
                {
                    "evaluation": {
                        "description": "Cuadros resumen de los contenidos vistos semanalmente",
                        "endDate": "2018-03-12T18:00:00-06:00",
                        "id": "7",
                        "laboratory": "false",
                        "name": "Hipertextos",
                        "percentage": "30",
                        "period": "1",
                        "startDate": "2018-03-07T18:00:00-06:00",
                        "state": "true"
                    },
                    "grade": "10.0",
                    "id": "4",
                    "observations": "Felicitaciones",
                    "state": "true"
                },
                {
                    "evaluation": {
                        "description": "Revisión de portafolio de actividades realizadas a lo largo del periodo",
                        "endDate": "2018-03-12T18:00:00-06:00",
                        "id": "8",
                        "laboratory": "false",
                        "name": "Portafolio",
                        "percentage": "30",
                        "period": "1",
                        "startDate": "2018-03-07T18:00:00-06:00",
                        "state": "true"
                    },
                    "grade": "8.0",
                    "id": "5",
                    "state": "true"
                },
                {
                    "evaluation": {
                        "description": "Evaluacion de los contenidos vistos en el primer periodo",
                        "endDate": "2018-03-20T18:00:00-06:00",
                        "id": "9",
                        "laboratory": "false",
                        "name": "Examen parcial",
                        "percentage": "40",
                        "period": "1",
                        "startDate": "2018-03-13T18:00:00-06:00",
                        "state": "true"
                    },
                    "grade": "9.5",
                    "id": "6",
                    "state": "true"
                }
            ],
            "id": "2",
            "semester": "1",
            "state": "true"
        }
    ]
}
```

</p></details>
