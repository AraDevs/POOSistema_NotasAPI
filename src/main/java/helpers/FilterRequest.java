/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import io.jsonwebtoken.Claims;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author kevin
 */
public class FilterRequest {
    
    public static final String AND = "AND";
    public static final String OR = "OR";
    
    public static final String IS_STUDENT = "student";
    public static final String IS_EMPLOYEE = "employee";
    
    public static final String TEACH = "teach";
    public static final String USER = "manageUsers";
    public static final String STUDENT = "manageStudents";
    public static final String EMPLOYEE = "manageEmployees";
    public static final String FACULTY = "manageFaculties";
    public static final String CAREER = "manageCareers";
    public static final String COURSE = "manageCourses";
    public static final String PENSUM = "managePensums";
    public static final String EVALUATION = "manageEvaluations";
    public static final String ROLE = "manageRoles";
    
    private String operator; //Operador usado si se envía más de un permiso
    private Boolean authorized; //Determina si la operación será autorizada o no

    public FilterRequest(HttpHeaders headers, String operator, String... permissions) {
        
        String token = "";
        Claims tokenInfo;
        this.operator = operator;
        this.authorized = false;
        
        try {
            token = headers.getRequestHeader("token").get(0);
            if (token.equals("")) {
                throw new NotAuthenticatedException();
            }
            
            tokenInfo = new JWTHelper().parseJWT(token);
            
        } catch (Exception e) {
            throw new NotAuthenticatedException();
        }
        
        //Si no se especificaron permisos requeridos, la operación es autorizada
        if (permissions.length == 0) {
            authorized = true;
        }
        //Si se especificaron permisos, es necesario verificarlos
        else {
            //Si se comparará usando AND, el valor inicial del output debe ser verdadero para que funcione la evaluación
            if (operator.equals(AND)) {
                authorized = true;
            }
            //Iterando por cada uno de los permisos solicitados
            for (String permission : permissions) {
                if (permission.equals(IS_STUDENT)) {
                    authorized = evaluatePermission(tokenInfo.get("type").equals("student"));
                }
                else if (permission.equals(IS_EMPLOYEE)) {
                    authorized = evaluatePermission(tokenInfo.get("type").equals("employee"));
                }

                //Verificaciones que solo aplican si se trata de un empleado (osea, los permisos de los roles)
                if (tokenInfo.get("type").equals("employee")) {
                    if (permission.equals(TEACH) || permission.equals(USER) || permission.equals(STUDENT) || 
                            permission.equals(EMPLOYEE) || permission.equals(FACULTY) || permission.equals(CAREER) || 
                            permission.equals(COURSE) || permission.equals(PENSUM) || permission.equals(EVALUATION) || 
                            permission.equals(ROLE)) {
                        authorized = evaluatePermission(Boolean.valueOf(tokenInfo.get(permission).toString()));
                    }
                }
            }
        }
        
        if (!authorized) {
            throw new NotAuthorizedException();
        }
        
    }
    
    public FilterRequest(String token, String operator, String... permissions) {
        
        Claims tokenInfo;
        this.operator = operator;
        this.authorized = false;
        
        try {
            if (token == null || token.equals("")) {
                throw new NotAuthenticatedException();
            }
            
            tokenInfo = new JWTHelper().parseJWT(token);
            
        } catch (Exception e) {
            throw new NotAuthenticatedException();
        }
        
        //Si no se especificaron permisos requeridos, la operación es autorizada
        if (permissions.length == 0) {
            authorized = true;
        }
        //Si se especificaron permisos, es necesario verificarlos
        else {
            //Si se comparará usando AND, el valor inicial del output debe ser verdadero para que funcione la evaluación
            if (operator.equals(AND)) {
                authorized = true;
            }
            //Iterando por cada uno de los permisos solicitados
            for (String permission : permissions) {
                if (permission.equals(IS_STUDENT)) {
                    authorized = evaluatePermission(tokenInfo.get("type").equals("student"));
                }
                else if (permission.equals(IS_EMPLOYEE)) {
                    authorized = evaluatePermission(tokenInfo.get("type").equals("employee"));
                }

                //Verificaciones que solo aplican si se trata de un empleado (osea, los permisos de los roles)
                if (tokenInfo.get("type").equals("employee")) {
                    if (permission.equals(TEACH) || permission.equals(USER) || permission.equals(STUDENT) || 
                            permission.equals(EMPLOYEE) || permission.equals(FACULTY) || permission.equals(CAREER) || 
                            permission.equals(COURSE) || permission.equals(PENSUM) || permission.equals(EVALUATION) || 
                            permission.equals(ROLE)) {
                        authorized = evaluatePermission(Boolean.valueOf(tokenInfo.get(permission).toString()));
                    }
                }
            }
        }
        
        if (!authorized) {
            throw new NotAuthorizedException();
        }
        
    }
    
    private Boolean evaluatePermission(Boolean condition) {
        if (operator.equals(AND)) {
            return authorized && condition;
        }
        else { //operator.equals(OR)
            return authorized || condition;
        }
    }
    
    public class NotAuthenticatedException extends WebApplicationException {
        public NotAuthenticatedException() {
            super(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Debes proveer un token válido").type(MediaType.TEXT_PLAIN).build());
        }
    }
    
    public class NotAuthorizedException extends WebApplicationException {
        public NotAuthorizedException() {
            super(Response.status(Response.Status.FORBIDDEN)
                .entity("No tienes permiso para realizar esta acción").type(MediaType.TEXT_PLAIN).build());
        }
    }
    
}
