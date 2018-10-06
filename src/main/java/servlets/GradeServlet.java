/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.GradeDAO;
import hibernate.Grade;
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
@Path("/grades")
public class GradeServlet {

    private static GradeDAO gradeDao;
    
    public GradeServlet() {
    }
    
    @GET
    @Path("/byRegisteredCourse/{regCourseId}/byEvaluation/{evalId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Grade getGrade(@PathParam("regCourseId") String regCourseId,
                                                 @PathParam("evalId") String evalId) {
        try {
            return new GradeDAO().getGrade(Integer.parseInt(regCourseId), Integer.parseInt(evalId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
