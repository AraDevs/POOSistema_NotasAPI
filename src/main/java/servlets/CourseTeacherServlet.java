/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseTeacherDAO;
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
