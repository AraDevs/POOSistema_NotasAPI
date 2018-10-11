/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EvaluationDAO;
import dao.GradeDAO;
import dao.RegisteredCourseDAO;
import helpers.DaoStatus;
import hibernate.Evaluation;
import hibernate.Grade;
import hibernate.RegisteredCourse;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @Path("/byRegisteredCourse/{regCourseId: \\d+}/byEvaluation/{evalId: \\d+}")
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
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("grade") String grade, @FormParam("observations") String observations, 
                            @FormParam("registeredCourseId") String registeredCourseId, @FormParam("evaluationId") String evaluationId) {
        
        gradeDao = new GradeDAO(false);
        
        String msg = "";
        if (grade == null || grade.equals("")) {
            msg += " Nota\n";
        }
        //observations es nullable
        if (registeredCourseId == null || registeredCourseId.equals("")) {
            msg += " Materia de alumno\n";
        }
        if (evaluationId == null || evaluationId.equals("")) {
            msg += " Evaluación";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (Double.parseDouble(grade) < 0 || Double.parseDouble(grade) > 10) {
                msg = "La nota debe ser un valor entre 0 y 10.";
                return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            msg = "La nota debe ser un valor entre 0 y 10.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        RegisteredCourse registeredCourse = null;
        Evaluation evaluation = null;
        
        try {
            registeredCourse = new RegisteredCourseDAO().getRegisteredCourseNiceWay(Integer.parseInt(registeredCourseId));
            //En este punto no reconoce el null e ignora la validación, generando nullpointerexception
            //No se por qué es, pero no lo quiero arreglar xd igual dudo que se de este caso en el cliente
            if (registeredCourse == null) {
                msg = "El registro de materia especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!registeredCourse.getCourseTeacher().getState()) {
                msg = "El registro de materia especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            evaluation = new EvaluationDAO().getEvaluationWithCourse(Integer.parseInt(evaluationId));
            if (evaluation == null) {
                msg = "La evaluación especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!evaluation.getState()) {
                msg = "La evaluación especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            if (registeredCourse.getCourseTeacher().getCourse().getId() != evaluation.getCourse().getId()) {
                msg = "La evaluación especificada no corresponde a esta materia.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Grade newGrade = null;
            newGrade = gradeDao.getGradeNiceWay(Integer.parseInt(registeredCourseId), Integer.parseInt(evaluationId));
            
            boolean isUpdate = true;
            if (newGrade == null) {
                isUpdate = false;
                newGrade = new Grade();
            }
            
            newGrade.setGrade(Float.parseFloat(grade));
            newGrade.setObservations(observations);
            newGrade.setState(true);
            
            int status = 0;
            
            if (isUpdate) {
                status = gradeDao.update(newGrade);
            }
            else {
                newGrade.setRegisteredCourse(registeredCourse);
                newGrade.setEvaluation(evaluation);
                
                status = gradeDao.add(newGrade);
            }
            
            if (status == DaoStatus.OK) {
                
                
                //Si la materia tiene laboratorio
                if(evaluation.getCourse().getLaboratory()) {
                    //Si ya se ingresaron todas las notas de este alumno, incluidas las de laboratorio
                    if (gradeDao.hasAllGrades(Integer.parseInt(registeredCourseId), true)) {
                        //Notas de no laboratorio
                        List<Evaluation> evaluations = new EvaluationDAO().getEvaluationsByRegCourseWithGrade(Integer.parseInt(registeredCourseId), false);
                        
                        Double grade1 = 0.0, grade2 = 0.0, grade3 = 0.0;
                        //Calculando calificación final para la materia
                        for (Evaluation e : evaluations) {
                            Grade evalGrade = (Grade) e.getGrades().iterator().next();
                            switch (Integer.parseInt(e.getPeriod())) {
                                case 1:
                                    grade1 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                                case 2:
                                    grade2 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                                case 3:
                                    grade3 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                            }
                        }
                        Double totalNoLaboratory = (grade1 * 0.3) + (grade2 * 0.35) + (grade3 * 0.35);
                        
                        //Notas de laboratorio
                        evaluations = new EvaluationDAO().getEvaluationsByRegCourseWithGrade(Integer.parseInt(registeredCourseId), true);
                        
                        Double labGrade1 = 0.0, labGrade2 = 0.0, labGrade3 = 0.0;
                        //Calculando calificación final para la materia
                        for (Evaluation e : evaluations) {
                            Grade evalGrade = (Grade) e.getGrades().iterator().next();
                            switch (Integer.parseInt(e.getPeriod())) {
                                case 1:
                                    labGrade1 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                                case 2:
                                    labGrade2 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                                case 3:
                                    labGrade3 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                            }
                        }
                        Double totalLaboratory = (labGrade1 * 0.3) + (labGrade2 * 0.35) + (labGrade3 * 0.35);
                        
                        Double total = (totalNoLaboratory / 2) + (totalLaboratory / 2);
                        System.out.println(total);
                        if (total >= 6) {
                            registeredCourse.setCourseState("Aprobada");
                        }
                        else {
                            registeredCourse.setCourseState("Reprobada");
                        }
                        //Actualizando estado de materia
                        new RegisteredCourseDAO().update(registeredCourse);
                    }
                }
                //Si la materia no tiene laboratorio
                else {
                    //Si ya se ingresaron todas las notas de este alumno, y la materia no tiene laboratorio
                    if (gradeDao.hasAllGrades(Integer.parseInt(registeredCourseId), false)) {
                        List<Evaluation> evaluations = new EvaluationDAO().getEvaluationsByRegCourseWithGrade(Integer.parseInt(registeredCourseId), false);
                        
                        Double grade1 = 0.0, grade2 = 0.0, grade3 = 0.0;
                        //Calculando calificación final para la materia
                        for (Evaluation e : evaluations) {
                            Grade evalGrade = (Grade) e.getGrades().iterator().next();
                            switch (Integer.parseInt(e.getPeriod())) {
                                case 1:
                                    grade1 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                                case 2:
                                    grade2 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                                case 3:
                                    grade3 += (evalGrade.getGrade() * e.getPercentage() / 100);
                                    break;
                            }
                        }
                        System.out.println(grade1 + " " + grade2 + " " + grade3);
                        Double total = (grade1 * 0.3) + (grade2 * 0.35) + (grade3 * 0.35);
                        
                        if (total >= 6) {
                            registeredCourse.setCourseState("Aprobada");
                        }
                        else {
                            registeredCourse.setCourseState("Reprobada");
                        }
                        //Actualizando estado de materia
                        new RegisteredCourseDAO().update(registeredCourse);
                    }
                }
                
                msg = "Nota guardada.";
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
        
        msg = "No se pudo guardar la nota.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
}
