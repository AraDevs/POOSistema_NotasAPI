/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.RegisteredCourseDAO;
import hibernate.RegisteredCourse;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author kevin
 */
@Path("/registeredCourses")
public class RegisteredCourseServlet {
    private static RegisteredCourseDAO regCrsDAO;

    public RegisteredCourseServlet() {
    }
    
    @GET
    @Path("/byStudent/{student_id}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getRegisteredCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseList(Integer.parseInt(studentId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{student_id}/full/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getActiveRegisteredCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseList(Integer.parseInt(studentId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/byStudent/{student_id}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getRegisteredCoursesWithCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseWithCourse(Integer.parseInt(studentId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/byStudent/{student_id}/courses/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getActiveRegisteredCoursesWithCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseWithCourse(Integer.parseInt(studentId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
