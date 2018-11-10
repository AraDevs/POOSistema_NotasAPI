/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EmployeeDAO;
import dao.RoleDAO;
import dao.UserDAO;
import dto.EmployeeTokenDTO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import helpers.Helpers;
import helpers.JWTHelper;
import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Employee;
import hibernate.Role;
import hibernate.User;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
    public List<Employee> getEmployees(@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
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
    public List<Employee> getActiveEmployees(@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            return new EmployeeDAO().getEmployeeList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{studentId: \\d+}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Employee> getEmployeeByStudent(@PathParam("studentId") String studentId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            return new EmployeeDAO().getEmployeeByStudent(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{employeeId: \\d+}/users/people/roles")
    @Produces({MediaType.APPLICATION_JSON})
    public Employee getEmployee(@PathParam("employeeId") String employeeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            Employee employee = new EmployeeDAO().getEmployee(Integer.parseInt(employeeId));
            employee.getUser().setImagePath(Helpers.downloadFileToString(employee.getUser().getImagePath()));
            return employee;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byRegisteredCourse/{regCourseId: \\d+}/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public Employee getEmployeeByRegisteredCourse(@PathParam("regCourseId") String regCourseId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            Employee employee = new EmployeeDAO().getEmployeeByRegisteredCourse(Integer.parseInt(regCourseId));
            employee.getUser().setImagePath(Helpers.downloadFileToString(employee.getUser().getImagePath()));
            return employee;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/{employeeId: \\d+}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public Employee getTeacher(@PathParam("employeeId") String employeeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            Employee employee = new EmployeeDAO().getTeacher(Integer.parseInt(employeeId));
            employee.getUser().setImagePath(Helpers.downloadFileToString(employee.getUser().getImagePath()));
            return employee;
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
            
            //Generando token 
            String token = new JWTHelper().createEmployeeJWT(employee);
            
            //Generando objeto custom para enviar devuelta al estudiante junto a su token
            EmployeeTokenDTO employeeWithToken = new EmployeeTokenDTO(employee, token);
            
            //Login exitoso
            return Response.status(Response.Status.ACCEPTED).entity(employeeWithToken).type(MediaType.APPLICATION_JSON).build();
            
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
    public Response create (@FormParam("userId") String userId, @FormParam("roleId") String roleId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.EMPLOYEE);
        
        EmployeeDAO empDao = new EmployeeDAO(false);
        
        String msg = "";
        if (userId == null || userId.equals("")) {
            msg += " Usuario\n";
        }
        if (roleId == null || roleId.equals("")) {
            msg += " Rol";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
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
            
            if (!user.getEmployees().isEmpty()) {
                msg = "El usuario especificado ya tiene un registro de empleado.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        Role role = null;
        try {
            role = new RoleDAO().get(Integer.parseInt(roleId));
            if (role == null) {
                msg = "El rol especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!role.getState()) {
                msg = "El rol especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            Employee employee = new Employee();
            employee.setUser(user);
            employee.setRole(role);
            employee.setState(true);
            
            int status = empDao.add(employee);
            
            if (status == DaoStatus.OK) {
                msg = "Empleado agregado.";
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
        
        msg = "No se pudo guardar el empleado.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("roleId") String roleId, @FormParam("state") String state, 
            @FormParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.EMPLOYEE);
        
        EmployeeDAO empDao = new EmployeeDAO(false);;
        
        String msg = "";
        if (roleId == null || roleId.equals("")) {
            msg += " Rol\n";
        }
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
        
        Role role = null;
        try {
            role = new RoleDAO().get(Integer.parseInt(roleId));
            if (role == null) {
                msg = "El rol especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!role.getState()) {
                msg = "El rol especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        Employee employee = null;
        
        try {
            employee = new EmployeeDAO().get(Integer.parseInt(id));
            if (employee == null) {
                msg = "El empleado especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            employee.setRole(role);
            employee.setState(Boolean.valueOf(state));
            
            int status = empDao.update(employee);
            
            if (status == DaoStatus.OK) {
                msg = "Empleado modificado.";
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
        
        msg = "No se pudo modificar el empleado.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.EMPLOYEE);
        
        String msg = "";
        EmployeeDAO employeeDao = new EmployeeDAO();
        
        Employee employee = null;
        
        try {
            employee = employeeDao.get(Integer.parseInt(id));
            
            if (employee == null) {
                msg = "El empleado a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if (employee.getId() == 1) {
                msg = "No se puede eliminar al primer usuario.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = employeeDao.delete(employee);
            
            if (status == DaoStatus.OK) {
                msg = "Empleado eliminado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El empleado no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar el empleado.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
