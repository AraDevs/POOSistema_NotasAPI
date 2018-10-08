/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseTeacherDAO;
import hibernate.CourseTeacher;
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
@Path("/courseTeachers")
public class CourseTeacherServlet {

    public CourseTeacherServlet() {
    }
    
    @GET
    @Path("/byEmployee/{employeeId}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CourseTeacher> getCoursesByEmployee(@PathParam("employeeId") String employeeId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherList(Integer.parseInt(employeeId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byEmployee/{employeeId}/courses/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CourseTeacher> getActiveCoursesByEmployee(@PathParam("employeeId") String employeeId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherList(Integer.parseInt(employeeId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byCourse/{courseId}/employees/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CourseTeacher> getTeachersByCourse(@PathParam("courseId") String courseId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherByCourse(Integer.parseInt(courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{courseTeacherId}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public CourseTeacher getCoursesTeacher(@PathParam("courseTeacherId") String courseTeacherId) {
        try {
            return new CourseTeacherDAO().getCourseTeacher(Integer.parseInt(courseTeacherId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/passCount/byRegisteredCourse/{regCourseId}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getCoursesByRegisteredCourse(@PathParam("regCourseId") String regCourseId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherTendency(Integer.parseInt(regCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
