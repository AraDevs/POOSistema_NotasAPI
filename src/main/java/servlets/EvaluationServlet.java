/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseDAO;
import dao.EvaluationDAO;
import helpers.DaoStatus;
import hibernate.Course;
import hibernate.Evaluation;
import hibernate.Grade;
import java.sql.Date;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlSeeAlso;

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
    @Path("/byCourse/{courseId}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Evaluation> getEvaluations(@PathParam("courseId") String courseId) {
        try {
            return new EvaluationDAO().getEvaluationByCourse(Integer.parseInt(courseId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byCourse/{courseId}/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Evaluation> getActiveEvaluations(@PathParam("courseId") String courseId) {
        try {
            return new EvaluationDAO().getEvaluationByCourse(Integer.parseInt(courseId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{evaluationId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Evaluation getEvaluation(@PathParam("evaluationId") String evaluationId) {
        try {
            return new EvaluationDAO().getEvaluation(Integer.parseInt(evaluationId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{evaluationId}/byRegisteredCourse/{registeredCourseId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Evaluation getEvaluationWithGrade(@PathParam("evaluationId") String evaluationId, 
                                             @PathParam("registeredCourseId") String registeredCourseId) {
        try {
            return new EvaluationDAO().getEvaluationWithGrade(Integer.parseInt(evaluationId), Integer.parseInt(registeredCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("description") String description, 
            @FormParam("percentage") String percentage, @FormParam("period") String period, 
            @FormParam("laboratory") String laboratory, @FormParam("startDate") String startDate, 
            @FormParam("endDate") String endDate, @FormParam("courseId") String courseId) {
        
        evalDao = new EvaluationDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de evaluación\n";
        }
        if (description == null || description.equals("")) {
            msg += " Descripción\n";
        }
        if (percentage == null || percentage.equals("")) {
            msg += " Porcentaje\n";
        }
        if (period == null || period.equals("")) {
            msg += " Período\n";
        }
        if (laboratory == null || laboratory.equals("")) {
            msg += " Posee laboratorio\n";
        }
        if (startDate == null || startDate.equals("")) {
            msg += " Fecha de inicio\n";
        }
        if (endDate == null || endDate.equals("")) {
            msg += " Fecha de finalización\n";
        }
        if (courseId == null || courseId.equals("")) {
            msg += " Materia";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (Date.valueOf(startDate).after(Date.valueOf(endDate))) {
            msg = "La fecha de inicio y finalización no concuerdan.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (Integer.parseInt(percentage) < 1) {
            msg = "El porcentaje debe ser un entero entre 1 y 100.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(period.equals("1") || period.equals("2") || period.equals("3"))) {
            msg = "El valor especificado para el período no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Course course = null;
        
        try {
            course = new CourseDAO().get(Integer.parseInt(courseId));
            if (course == null) {
                msg = "La materia especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!course.getState()) {
                msg = "La materia especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        if (Boolean.valueOf(laboratory) && !(course.getLaboratory())) {
            msg = "Se especificó la evaluación como parte de laboratorio, pero la materia seleccionada no posee laboratorio.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            Evaluation evaluation = new Evaluation();
            evaluation.setName(name);
            evaluation.setDescription(description);
            evaluation.setPercentage(Integer.parseInt(percentage));
            evaluation.setPeriod(period);
            evaluation.setLaboratory(Boolean.valueOf(laboratory));
            evaluation.setStartDate(Date.valueOf(startDate));
            evaluation.setEndDate(Date.valueOf(endDate));
            evaluation.setCourse(course);
            evaluation.setState(true);
            
            if (!(evalDao.isPercentageConsistent(evaluation))) {
                msg = "El porcentaje especificado constituye un total mayor al 100% para el período especificado.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            int status = evalDao.add(evaluation);
            
            if (status == DaoStatus.OK) {
                msg = "Evaluación agregada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo guardar la evaluación.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("description") String description, 
            @FormParam("percentage") String percentage, @FormParam("period") String period, 
            @FormParam("laboratory") String laboratory, @FormParam("startDate") String startDate, 
            @FormParam("endDate") String endDate, @FormParam("courseId") String courseId, 
            @FormParam("state") String state, @FormParam("id") String id) {
        
        evalDao = new EvaluationDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de evaluación\n";
        }
        if (description == null || description.equals("")) {
            msg += " Descripción\n";
        }
        if (percentage == null || percentage.equals("")) {
            msg += " Porcentaje\n";
        }
        if (period == null || period.equals("")) {
            msg += " Período\n";
        }
        if (laboratory == null || laboratory.equals("")) {
            msg += " Posee laboratorio\n";
        }
        if (startDate == null || startDate.equals("")) {
            msg += " Fecha de inicio\n";
        }
        if (endDate == null || endDate.equals("")) {
            msg += " Fecha de finalización\n";
        }
        if (courseId == null || courseId.equals("")) {
            msg += " Materia\n";
        }
        if (state == null || state.equals("")) {
            msg += " Estado\n";
        }
        if (id == null || id.equals("")) {
            msg += " ID";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (Date.valueOf(startDate).after(Date.valueOf(endDate))) {
            msg = "La fecha de inicio y finalización no concuerdan.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (Integer.parseInt(percentage) < 1) {
            msg = "El porcentaje debe ser un entero entre 1 y 100.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(period.equals("1") || period.equals("2") || period.equals("3"))) {
            msg = "El valor especificado para el período no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Course course = null;
        
        try {
            course = new CourseDAO().get(Integer.parseInt(courseId));
            if (course == null) {
                msg = "La materia especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!course.getState()) {
                msg = "La materia especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        if (Boolean.valueOf(laboratory) && !(course.getLaboratory())) {
            msg = "Se especificó la evaluación como parte de laboratorio, pero la materia seleccionada no posee laboratorio.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Evaluation evaluation = null;
        
        try {
            evaluation = new EvaluationDAO().get(Integer.parseInt(id));
            if (course == null) {
                msg = "La evaluación a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        try {
            evaluation.setName(name);
            evaluation.setDescription(description);
            evaluation.setPercentage(Integer.parseInt(percentage));
            evaluation.setPeriod(period);
            evaluation.setLaboratory(Boolean.valueOf(laboratory));
            evaluation.setStartDate(Date.valueOf(startDate));
            evaluation.setEndDate(Date.valueOf(endDate));
            evaluation.setCourse(course);
            evaluation.setState(Boolean.valueOf(state));
            
            if (!(evalDao.isPercentageConsistent(evaluation))) {
                msg = "El porcentaje especificado constituye un total mayor al 100% para el período especificado.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            int status = evalDao.update(evaluation);
            
            if (status == DaoStatus.OK) {
                msg = "Evaluación modificada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar la evaluación.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
}
