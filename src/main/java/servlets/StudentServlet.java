/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.Student;
import controllers.StudentController;
import controllers.UserController;
import helpers.Helpers;
import java.util.Calendar;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    
    @GET
    @Path("/read/{param}")
    @Produces({MediaType.APPLICATION_JSON})
    public StudentController getJson (@PathParam("param") String param) {
        stdCtrl = new StudentController(param);
        return stdCtrl;
    }
    
    @GET
    @Path("/readByUser/{userId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Student getStudentById (@PathParam("userId") String userId) {
        try {
            return new StudentController().getStudentByUser(Integer.parseInt(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    
    @POST
    @Path("/create")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("surname") String surname, 
            @FormParam("pass") String pass, @FormParam("passConfirm") String passConfirm, 
            @FormParam("phone") String phone, @FormParam("email") String email) {
        
        stdCtrl = new StudentController(false);
        
        String msg = "";
        if (name == null) {
            msg += " name";
        }
        if (surname == null) {
            msg += " surname";
        }
        if (pass == null) {
            msg += " pass";
        }
        if (passConfirm == null) {
            msg += " passConfirm";
        }
        if (phone == null) {
            msg += " phone";
        }
        if (email == null) {
            msg += " email";
        }
        
        if (!msg.equals("")) {
            msg = "Must specify following parameters:" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(pass.equals(passConfirm))) {
            msg = "Given passwords do not match.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        //Generando username
        String firstLetter = name.substring(0, 1).toUpperCase();
        String secondLetter = surname.substring(0, 1).toUpperCase();
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int index = 0;
        try {
            index = new UserController().getYearIndex();
        } catch (Exception e) {
            msg = e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        String username = firstLetter + secondLetter + year + String.format("%04d", index + 1);
        
        try {
            String userId = new UserController().add(name, surname, username, pass, phone, email);
            if (!Helpers.isInt(userId)) {
                return Response.status(Response.Status.CONFLICT).entity(userId).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(userId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            String studentId = new StudentController().add(userId);
            if (!Helpers.isInt(studentId)) {
                return Response.status(Response.Status.CONFLICT).entity(studentId).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(studentId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            msg = "Student created with user id " + userId + " and student id " + studentId;
            return Response.ok(msg, "text/plain").build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not insert the student";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    
    
    @PUT
    @Path("/update")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("surname") String surname, 
            @FormParam("pass") String pass, @FormParam("passConfirm") String passConfirm, 
            @FormParam("phone") String phone, @FormParam("email") String email, @FormParam("userId") String userId) {
        
        stdCtrl = new StudentController(false);
        
        String msg = "";
        if (name == null) {
            msg += " name";
        }
        if (surname == null) {
            msg += " surname";
        }
        if (phone == null) {
            msg += " phone";
        }
        if (email == null) {
            msg += " email";
        }
        if (userId == null) {
            msg += " userId";
        }
        
        if (!msg.equals("")) {
            msg = "Must specify following parameters:" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (pass != null && !(pass.equals(passConfirm))) {
            msg = "Given passwords do not match.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        try {
            if (new UserController().getUserById(Integer.parseInt(userId)) == null) {
                msg = "Given userId does not exist.";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        
        try {
            String updUserId = new UserController().update(name, surname, pass, phone, email, userId);
            if (!Helpers.isInt(updUserId)) {
                return Response.status(Response.Status.CONFLICT).entity(userId).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(updUserId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            msg = "Student updated with user id " + userId;
            return Response.ok(msg, "text/plain").build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not update the student";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    
    @DELETE
    @Path("/delete/{userId: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete (@PathParam("userId") String userId) {
        
        String msg = "";
        
        try {
            if (new UserController().getUserById(Integer.parseInt(userId)) == null) {
                msg = "Given userId does not exist.";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        try {
            String dltUserId = new StudentController().delete(userId);
            if (!Helpers.isInt(dltUserId)) {
                msg = "Cannot delete a parent row";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(dltUserId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            dltUserId = new UserController().delete(userId);
            if (!Helpers.isInt(dltUserId)) {
                msg = "Cannot delete a parent row";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(dltUserId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            msg = "Student with user id " + dltUserId + " deleted";
            return Response.ok(msg, "text/plain").build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not delete the student";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
