/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EmployeeDAO;
import hibernate.Employee;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/employees")
public class EmployeeServlet {

    public EmployeeServlet() {
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
}
