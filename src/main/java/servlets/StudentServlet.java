/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Student;
import controllers.StudentController;
import controllers.UserController;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author kevin
 */
@Path("/")
public class StudentServlet {
    private static StudentController stdCtrl;

    public StudentServlet() {
    }
    
    @GET
    @Path("/read")
    @Produces({MediaType.APPLICATION_JSON})
    public StudentController getJson() {
        stdCtrl = new StudentController();
        return stdCtrl;
    }
    
    @POST
    @Path("/login")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response login (@FormParam("username") String username, @FormParam("pass") String pass) {
        stdCtrl = new StudentController(false);
        String msg;
        
        if (username == null) {
            msg = "Must specify username";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (pass == null) {
            msg = "Must specify password";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            int userId = new UserController().login(username, pass);
            if (userId == 0) {
                msg = "Username or password not found";
                return Response.status(Response.Status.UNAUTHORIZED).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            Student student = stdCtrl.getStudentByUser(userId);
            if (student == null) {
                msg = "This service is meant for students only";
                return Response.status(Response.Status.FORBIDDEN).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if(!(student.getState() && student.getUser().getState())) {
                msg = "Your user is deactivated";
                return Response.status(Response.Status.FORBIDDEN).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            //Login exitoso
            return Response.status(Response.Status.ACCEPTED).entity(student).type(MediaType.APPLICATION_JSON).build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not make login";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
}
