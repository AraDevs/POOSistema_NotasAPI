/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EmployeeDAO;
import dao.UserDAO;
import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Employee;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author kevin
 */
@Path("/employees")
public class EmployeeServlet {

    public EmployeeServlet() {
    }
    
    @GET
    @Path("/users/people/roles")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Employee> getEmployees() {
        try {
            return new EmployeeDAO().getEmployeeList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/users/people/roles/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Employee> getActiveEmployees() {
        try {
            return new EmployeeDAO().getEmployeeList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{studentId}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Employee> getEmployeeByStudent(@PathParam("studentId") String studentId) {
        try {
            return new EmployeeDAO().getEmployeeByStudent(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{employeeId}/users/people/roles")
    @Produces({MediaType.APPLICATION_JSON})
    public Employee getEmployee(@PathParam("employeeId") String employeeId) {
        try {
            return new EmployeeDAO().getEmployee(Integer.parseInt(employeeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byRegisteredCourse/{regCourseId}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public Employee getEmployeeByRegisteredCourse(@PathParam("regCourseId") String regCourseId) {
        try {
            return new EmployeeDAO().getEmployeeByRegisteredCourse(Integer.parseInt(regCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/{employeeId}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public Employee getTeacher(@PathParam("employeeId") String employeeId) {
        try {
            return new EmployeeDAO().getTeacher(Integer.parseInt(employeeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/login")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response login (@FormParam("username") String username, @FormParam("pass") String pass) {
        EmployeeDAO empDao = new EmployeeDAO(false);
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
            
            Employee employee = empDao.getEmployeeByUser(userId);
            if (employee == null) {
                msg = "Este servicio está disponible solo para empleados.";
                return Response.status(Response.Status.FORBIDDEN).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if(!(employee.getState() && employee.getUser().getState())) {
                msg = "Tu cuenta está desactivada.";
                return Response.status(Response.Status.FORBIDDEN).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            //Login exitoso
            return Response.status(Response.Status.ACCEPTED).entity(employee).type(MediaType.APPLICATION_JSON).build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo hacer login.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
}
