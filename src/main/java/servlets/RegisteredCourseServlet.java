/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.RegisteredCourseDAO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/registeredCourses")
public class RegisteredCourseServlet {
    private static RegisteredCourseDAO regCrsDAO;

    public RegisteredCourseServlet() {
    }
    
    @GET
    @Path("/byStudent/{student_id}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public RegisteredCourseDAO getJson(@PathParam("student_id") String studentId) {
        regCrsDAO = new RegisteredCourseDAO(studentId);
        return regCrsDAO;
    }
}
