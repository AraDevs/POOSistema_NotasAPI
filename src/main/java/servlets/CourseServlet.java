/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseDAO;
import hibernate.Course;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/courses")
public class CourseServlet {
    private static CourseDAO courseDao;
    
    public CourseServlet() {
    }
    
    @GET
    @Path("/byRegisteredCourse/{regCourseId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Course getCoursesByRegisteredCourse(@PathParam("regCourseId") String regCourseId) {
        try {
            return new CourseDAO().getCourseByRegisteredCourse(Integer.parseInt(regCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
