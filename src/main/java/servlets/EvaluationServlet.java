/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EvaluationDAO;
import hibernate.Evaluation;
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
@Path("/evaluations")
public class EvaluationServlet {
    private static EvaluationDAO evalDao;
    
    public EvaluationServlet() {
    }
    
    @GET
    @Path("/byRegisteredCourse/{regCourseId}/grades")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Evaluation> getEvaluationsByRegisteredCourse(@PathParam("regCourseId") String regCourseId) {
        try {
            return new EvaluationDAO().getEvaluationsByRegCourseWithGrade(Integer.parseInt(regCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
