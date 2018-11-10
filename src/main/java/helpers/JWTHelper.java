/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import hibernate.Employee;
import hibernate.Student;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author kevin
 */
public class JWTHelper {
    
    private final String secretKey = "xK1ekOyj5eEGl2HHt7lJ2FKYrlMHNPcT2uBzyrqI1yEcCJBV7woI8G6FgfLSuFw"; 
    
    //Crea un token para estudiantes
    public String createStudentJWT(Student student) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(student.getId().toString())
                                    .claim("userId", student.getUser().getId().toString())
                                    .claim("name", student.getUser().getPerson().getName())
                                    .claim("surname", student.getUser().getPerson().getSurname())
                                    .claim("username", student.getUser().getUsername())
                                    .claim("email", student.getUser().getPerson().getEmail())
                                    .claim("phone", student.getUser().getPerson().getPhone())
                                    .claim("address", student.getUser().getPerson().getAddress())
                                    .claim("dui", student.getUser().getPerson().getDui())
                                    .claim("type", "student")
                                    .setIssuedAt(now)
                                    .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        long expMillis = nowMillis + 3600000; //One hour from generation time
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);
        

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
    
    //Crea un token para empleados
    public String createEmployeeJWT(Employee employee) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(employee.getId().toString())
                                    .claim("userId", employee.getUser().getId().toString())
                                    .claim("name", employee.getUser().getPerson().getName())
                                    .claim("surname", employee.getUser().getPerson().getSurname())
                                    .claim("username", employee.getUser().getUsername())
                                    .claim("email", employee.getUser().getPerson().getEmail())
                                    .claim("phone", employee.getUser().getPerson().getPhone())
                                    .claim("address", employee.getUser().getPerson().getAddress())
                                    .claim("dui", employee.getUser().getPerson().getDui())
                                    .claim("type", "employee")
                                    .claim("role", employee.getRole().getRole())
                                    .claim("teach", employee.getRole().getTeach())
                                    .claim("manageUsers", employee.getRole().getManageUsers())
                                    .claim("manageStudents", employee.getRole().getManageStudents())
                                    .claim("manageEmployees", employee.getRole().getManageEmployees())
                                    .claim("manageFaculties", employee.getRole().getManageFaculties())
                                    .claim("manageCareers", employee.getRole().getManageCareers())
                                    .claim("manageCourses", employee.getRole().getManageCourses())
                                    .claim("managePensums", employee.getRole().getManagePensums())
                                    .claim("manageEvaluations", employee.getRole().getManageEvaluations())
                                    .claim("manageRoles", employee.getRole().getManageRoles())
                                    .setIssuedAt(now)
                                    .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        long expMillis = nowMillis + 3600000; //One hour from generation time
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);
        

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
    
    public Claims parseJWT(String jwt) throws Exception {
 
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()         
           .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
           .parseClaimsJws(jwt).getBody();
        
        return claims;
    }
}
