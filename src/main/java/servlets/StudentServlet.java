/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import hibernate.Student;
import dao.StudentDAO;
import dao.UserDAO;
import helpers.DaoStatus;
import helpers.Helpers;
import hibernate.User;
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
    @Path("byCourseTeacher/{courseTeacherId: \\d+}/users/people")
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
    @Path("/{studentId: \\d+}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public hibernate.Student getStudent (@PathParam("studentId") String studentId) {
        try {
            Student student = new StudentDAO().getStudent(Integer.parseInt(studentId));
            student.getUser().setImagePath(Helpers.downloadFileToString(student.getUser().getImagePath()));
            return student;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/users/{id: \\d+}/people")
    @Produces({MediaType.APPLICATION_JSON})
    public hibernate.Student getStudentByUserId (@PathParam("id") String userId) {
        try {
            Student student = new StudentDAO().getStudentByUser(Integer.parseInt(userId), StudentDAO.PERSON);
            student.getUser().setImagePath(Helpers.downloadFileToString(student.getUser().getImagePath()));
            return student;
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
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("userId") String userId) {
        
        stdDAO = new StudentDAO(false);
        
        String msg = "";
        if (userId == null || userId.equals("")) {
            msg = "Por favor especifique el usuario.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        User user = null;
        try {
            user = new UserDAO().getUserNiceWay(Integer.parseInt(userId));
            if (user == null) {
                msg = "El usuario especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!user.getState()) {
                msg = "El usuario especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if (!user.getStudents().isEmpty()) {
                msg = "El usuario especificado ya tiene un registro de estudiante.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            Student student = new Student();
            student.setUser(user);
            student.setState(true);
            
            int status = stdDAO.add(student);
            
            if (status == DaoStatus.OK) {
                msg = "Estudiante agregado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo guardar el estudiante.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("state") String state, @FormParam("id") String id) {
        
        stdDAO = new StudentDAO(false);
        
        String msg = "";
        if (state == null || state.equals("")) {
            msg += " Estado\n";
        }
        if (id == null || id.equals("")) {
            msg += " ID";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Student student = null;
        
        try {
            student = new StudentDAO().get(Integer.parseInt(id));
            if (student == null) {
                msg = "El estudiante especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            student.setState(Boolean.valueOf(state));
            
            int status = stdDAO.update(student);
            
            if (status == DaoStatus.OK) {
                msg = "Estudiante modificado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar el estudiante.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id) {
        
        String msg = "";
        StudentDAO studentDao = new StudentDAO();
        
        Student student = null;
        
        try {
            student = studentDao.get(Integer.parseInt(id));
            
            if (student == null) {
                msg = "El estudiante a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = studentDao.delete(student);
            
            if (status == DaoStatus.OK) {
                msg = "Estudiante eliminado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El estudiante no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar el estudiante.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
