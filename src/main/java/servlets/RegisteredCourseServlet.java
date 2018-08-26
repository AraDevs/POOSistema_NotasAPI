/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import controllers.RegisteredCourseController;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/")
public class RegisteredCourseServlet {
    private static RegisteredCourseController regCrsCtrl;

    public RegisteredCourseServlet() {
    }
    
    @GET
    @Path("/read/{student_id}")
    @Produces({MediaType.APPLICATION_JSON})
    public RegisteredCourseController getJson(@PathParam("student_id") String studentId) {
        regCrsCtrl = new RegisteredCourseController(studentId);
        return regCrsCtrl;
    }
}
