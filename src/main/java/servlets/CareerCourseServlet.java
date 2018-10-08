/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerCourseDAO;
import hibernate.CareerCourse;
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
@Path("/careerCourses")
public class CareerCourseServlet {
    
    @GET
    @Path("/byCareer/{careerId}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerCourse> getCareerCourseByCareer (@PathParam("careerId") String careerId) {
        try {
            return new CareerCourseDAO().getCareerCourseList(Integer.parseInt(careerId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byCareer/{careerId}/courses/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerCourse> getActiveCareerCourseByCareer (@PathParam("careerId") String careerId) {
        try {
            return new CareerCourseDAO().getCareerCourseList(Integer.parseInt(careerId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerCourseId}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public CareerCourse getCareerCourse (@PathParam("careerCourseId") String careerCourseId) {
        try {
            return new CareerCourseDAO().getCareerCourse(Integer.parseInt(careerCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
