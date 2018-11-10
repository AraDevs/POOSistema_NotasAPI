/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.RegisteredCourseDAO;
import dao.UnattendanceDAO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import helpers.Helpers;
import hibernate.RegisteredCourse;
import hibernate.Unattendance;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author kevin
 */
@Path("/unattendances")
public class UnattendanceServlet {
    
    private static UnattendanceDAO untDao;

    public UnattendanceServlet() {
    }
    
    @GET
    @Path("/byRegisteredCourse/{registeredCourseId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Unattendance> getUnattendanceByRegCourse (@PathParam("registeredCourseId") String registeredCourseId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.IS_STUDENT, FilterRequest.TEACH);
        try {
            return new UnattendanceDAO().getUnattendancesByRegCourse(Integer.parseInt(registeredCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{studentId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Unattendance> getUnattendanceByStudent(@PathParam("studentId") String studentId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.IS_STUDENT, FilterRequest.TEACH);
        try {
            return new UnattendanceDAO().getUnattendancesByStudent(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("registeredCourseId") String registeredCourseId, 
                            @FormParam("unattendanceDate") String unattendanceDate, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.TEACH);
        
        untDao = new UnattendanceDAO(false);
        
        String msg = "";
        if (registeredCourseId == null || registeredCourseId.equals("")) {
            msg += " Registro de estudiante\n";
        }
        if (unattendanceDate == null || unattendanceDate.equals("")) {
            msg += " Fecha de inasistencia\n";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        RegisteredCourse registeredCourse = null;
        
        try {
            registeredCourse = new RegisteredCourseDAO().getRegisteredCourse(Integer.parseInt(registeredCourseId));
            if (registeredCourse == null) {
                msg = "El registro de alumno especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!registeredCourse.getCourseState().equals("En curso")) {
                msg = "El registro de alumno especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        Date date;
        try {
            date = Date.valueOf(unattendanceDate);
        } catch (Exception e) {
            msg = "El formato de fecha enviado no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        
        try {
            if (untDao.getUnattendanceByRegCourseAndDate(Integer.parseInt(registeredCourseId), date) != null) {
                return Response.status(Response.Status.CONFLICT).entity("El alumno ya tiene una inasistencia registrada en la fecha indicada.").type(MediaType.TEXT_PLAIN).build();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if(cal.get(Calendar.YEAR) != Helpers.getCurrentYear() || !(Helpers.getDateSemester(date).equals(registeredCourse.getCourseTeacher().getSemester()))) {
                return Response.status(Response.Status.CONFLICT).entity("La fecha especificada corresponde a un ciclo diferente del actual.").type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            Unattendance unattendance = new Unattendance();
            unattendance.setRegisteredCourse(registeredCourse);
            unattendance.setUnattendanceDate(date);
            unattendance.setState(true);
            
            int status = untDao.add(unattendance);
            
            if (status == DaoStatus.OK) {
                msg = "Inasistencia agregada.";
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
        
        msg = "No se pudo guardar la inasistencia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.TEACH);
        
        String msg = "";
        UnattendanceDAO unattendanceDao = new UnattendanceDAO();
        
        Unattendance unattendance = null;
        
        try {
            unattendance = unattendanceDao.getUnattendance(Integer.parseInt(id));
            
            if (unattendance == null) {
                msg = "La inasistencia a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if (!unattendance.getRegisteredCourse().getCourseState().equals("En curso")) {
                msg = "No se puede eliminar la inasistencia porque la materia ya no está en curso.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = unattendanceDao.delete(unattendance);
            
            if (status == DaoStatus.OK) {
                msg = "Inasistencia eliminada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar la inasistencia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
