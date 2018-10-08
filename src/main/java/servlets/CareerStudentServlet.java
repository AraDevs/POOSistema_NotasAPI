/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerStudentDAO;
import hibernate.CareerStudent;
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
@Path("/careerStudents")
public class CareerStudentServlet {
    
    @GET
    @Path("/byStudent/{studentId}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerStudent> getCareerStudentByStudent (@PathParam("studentId") String studentId) {
        try {
            return new CareerStudentDAO().getCareerStudentList(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerStudentId}/careers")
    @Produces({MediaType.APPLICATION_JSON})
    public CareerStudent getCareerStudentt (@PathParam("careerStudentId") String careerStudentId) {
        try {
            return new CareerStudentDAO().getCareerStudent(Integer.parseInt(careerStudentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
