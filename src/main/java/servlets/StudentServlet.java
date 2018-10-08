/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import hibernate.Student;
import controllers.StudentController;
import controllers.UserController;
import dao.StudentDAO;
import dao.UserDAO;
import helpers.Helpers;
import java.util.Calendar;
import java.util.List;
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
@Path("/students")
public class StudentServlet {
    private static StudentDAO stdDAO;

    public StudentServlet() {
    }
    
    @GET
    @Path("/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Student> getStudentList () {
        try {
            return new StudentDAO().getStudentList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/users/people/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Student> getActiveStudentList () {
        try {
            return new StudentDAO().getStudentList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("byCourseTeacher/{courseTeacherId}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Student> getActiveStudentList (@PathParam("courseTeacherId") String courseTeacherId) {
        try {
            return new StudentDAO().getStudentsByCourseTeacher(Integer.parseInt(courseTeacherId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/read/{param}")
    @Produces({MediaType.APPLICATION_JSON})
    public StudentDAO getJson (@PathParam("param") String param) {
        stdDAO = new StudentDAO(param);
        return stdDAO;
    }
    
    @GET
    @Path("/{studentId}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public hibernate.Student getStudent (@PathParam("studentId") String studentId) {
        try {
            return new StudentDAO().getStudent(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/users/{id}/people")
    @Produces({MediaType.APPLICATION_JSON})
    public hibernate.Student getStudentByUserId (@PathParam("id") String userId) {
        try {
            return new StudentDAO().getStudentByUser(Integer.parseInt(userId), StudentDAO.PERSON);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/login")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response login (@FormParam("username") String username, @FormParam("pass") String pass) {
        stdDAO = new StudentDAO(false);
        String msg;
        
        if (username == null) {
            msg = "Ingrese el nombre de usuario.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (pass == null) {
            msg = "Ingrese la contraseña.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            int userId = new UserDAO().login(username, pass);
            if (userId == 0) {
                msg = "Nombre de usuario y contraseña no concuerdan.";
                return Response.status(Response.Status.UNAUTHORIZED).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            Student student = stdDAO.getStudentByUser(userId, StudentDAO.PERSON);
            if (student == null) {
                msg = "Este servicio está disponible solo para estudiantes.";
                return Response.status(Response.Status.FORBIDDEN).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if(!(student.getState() && student.getUser().getState())) {
                msg = "Tu cuenta está desactivada.";
                return Response.status(Response.Status.FORBIDDEN).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            //Login exitoso
            return Response.status(Response.Status.ACCEPTED).entity(student).type(MediaType.APPLICATION_JSON).build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo hacer login.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    /*
    @POST
    @Path("/create")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("surname") String surname, 
            @FormParam("pass") String pass, @FormParam("passConfirm") String passConfirm, 
            @FormParam("phone") String phone, @FormParam("email") String email) {
        
        stdDAO = new StudentController(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " name";
        }
        if (surname == null || surname.equals("")) {
            msg += " surname";
        }
        if (pass == null || pass.equals("")) {
            msg += " pass";
        }
        if (passConfirm == null || passConfirm.equals("")) {
            msg += " passConfirm";
        }
        if (phone == null || phone.equals("")) {
            msg += " phone";
        }
        if (email == null || email.equals("")) {
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
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(userId)).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(userId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            String studentId = stdDAO.add(userId);
            if (!Helpers.isInt(studentId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(studentId)).type(MediaType.TEXT_PLAIN).build();
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
            @FormParam("phone") String phone, @FormParam("email") String email, @FormParam("state") String state,
            @FormParam("userId") String userId) {
        
        stdDAO = new StudentController(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " name";
        }
        if (surname == null || surname.equals("")) {
            msg += " surname";
        }
        if (phone == null || phone.equals("")) {
            msg += " phone";
        }
        if (email == null || email.equals("")) {
            msg += " email";
        }
        if (state == null || state.equals("")) {
            msg += " state";
        }
        if (userId == null || userId.equals("")) {
            msg += " userId";
        }
        
        if (!msg.equals("")) {
            msg = "Must specify following parameters:" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if ((pass != null && !pass.equals("")) ^ (passConfirm != null && !passConfirm.equals(""))) {
            msg = "Must specify both pass and passConfirm, or not specify any of them.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (pass != null && !(pass.equals(passConfirm))) {
            msg = "Given passwords do not match.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        try {
            if (new UserController().getUserById(Integer.parseInt(userId)) == null) {
                msg = "Given userId does not exist.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        
        try {
            String updUserId = new UserController().update(name, surname, pass, phone, email, state, userId);
            if (!Helpers.isInt(updUserId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(updUserId)).type(MediaType.TEXT_PLAIN).build();
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
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        try {
            String dltUserId = new StudentController().delete(userId);
            if (!Helpers.isInt(dltUserId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(dltUserId)).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(dltUserId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            dltUserId = new UserController().delete(userId);
            if (!Helpers.isInt(dltUserId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(dltUserId)).type(MediaType.TEXT_PLAIN).build();
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
        
    }*/
}
