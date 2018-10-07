/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseDAO;
import hibernate.Course;
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
@Path("/courses")
public class CourseServlet {
    private static CourseDAO courseDao;
    
    public CourseServlet() {
    }
    
    @GET
    @Path("/faculties/prerrequisite")
    @Produces({MediaType.APPLICATION_JSON})
    public CourseDAO getJson() {
        courseDao = new CourseDAO();
        return courseDao;
    }
    
    @GET
    @Path("/faculties/prerrequisite/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Course> getActiveCourses() {
        try {
            return new CourseDAO().getCourseList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("{courseId}/faculties/prerrequisite")
    @Produces({MediaType.APPLICATION_JSON})
    public Course getCourse(@PathParam("courseId") String courseId) {
        try {
            return new CourseDAO().getCourse(Integer.parseInt(courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
