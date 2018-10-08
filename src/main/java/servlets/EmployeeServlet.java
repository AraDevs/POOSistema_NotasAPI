/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EmployeeDAO;
import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Employee;
import java.util.List;
import javax.ws.rs.GET;
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
}
