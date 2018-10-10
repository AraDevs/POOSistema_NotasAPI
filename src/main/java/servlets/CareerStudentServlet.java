/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerDAO;
import dao.CareerStudentDAO;
import dao.StudentDAO;
import helpers.DaoStatus;
import hibernate.Career;
import hibernate.CareerStudent;
import hibernate.Student;
import java.util.Calendar;
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

/**
 *
 * @author kevin
 */
@Path("/careerStudents")
public class CareerStudentServlet {
    
    @GET
    @Path("/byStudent/{studentId}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerStudent> getCareerStudentByStudent (@PathParam("studentId") String studentId) {
        try {
            return new CareerStudentDAO().getCareerStudentList(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerStudentId}/careers")
    @Produces({MediaType.APPLICATION_JSON})
    public CareerStudent getCareerStudentt (@PathParam("careerStudentId") String careerStudentId) {
        try {
            return new CareerStudentDAO().getCareerStudent(Integer.parseInt(careerStudentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("careerId") String careerId, @FormParam("studentId") String studentId) {
        
        CareerStudentDAO carStdDao = new CareerStudentDAO(false);
        
        String msg = "";
        if (careerId == null || careerId.equals("")) {
            msg += " Carrera\n";
        }
        if (studentId == null || studentId.equals("")) {
            msg += " Estudiante";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Career career = null;
        Student student = null;
        
        try {
            career = new CareerDAO().get(Integer.parseInt(careerId));
            if (career == null) {
                msg = "La carrera especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!career.getState()) {
                msg = "La carrera especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            student = new StudentDAO().getStudentWithCareerStudent(Integer.parseInt(studentId));
            if (student == null) {
                msg = "El estudiante especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!student.getState()) {
                msg = "El estudiante especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            for (Object cs : student.getCareerStudents()) {
                if (((CareerStudent) cs).getCareerState().equals("En curso")) {
                    msg = "El estudiante especificado ya tiene una carrera en curso.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            CareerStudent careerStudent = new CareerStudent();
            careerStudent.setStudent(student);
            careerStudent.setCareer(career);
            careerStudent.setCareerState("En curso");
            careerStudent.setIncomeYear(Calendar.getInstance().get(Calendar.YEAR));
            careerStudent.setState(true);
            
            int status = carStdDao.add(careerStudent);
            
            if (status == DaoStatus.OK) {
                msg = "Carrera registrada.";
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
        
        msg = "No se pudo registrar la carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    /*
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("careerState") String careerState, @FormParam("state") String state, 
                            @FormParam("id") String id) {
        
        CareerStudentDAO carStdDao = new CareerStudentDAO(false);
        
        String msg = "";
        if (careerState == null || careerState.equals("")) {
            msg += " Estado de carrera\n";
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
        
        if (!(careerState.equals("En curso") || careerState.equals("Egresado") || careerState.equals("Abandonado"))) {
            msg = "El valor especificado para el estado de la carrera no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Faculty faculty = null;
        CareerType careerType = null;
        
        try {
            faculty = new FacultyDAO().get(Integer.parseInt(facultyId));
            if (faculty == null) {
                msg = "La facultad especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!faculty.getState()) {
                msg = "La facultad especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            careerType = new CareerTypeDAO().get(Integer.parseInt(careerTypeId));
            if (careerType == null) {
                msg = "El tipo de carrera especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!careerType.getState()) {
                msg = "El tipo de carrera especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        Career career = null;
        
        try {
            career = carDao.get(Integer.parseInt(id));
            if (career == null) {
                msg = "La carrera a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            career.setName(name);
            career.setFaculty(faculty);
            career.setCareerType(careerType);
            career.setState(Boolean.valueOf(state));
            
            int status = carDao.add(career);
            
            if (status == DaoStatus.OK) {
                msg = "Carrera modificada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre de la carrera ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar la carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }*/
}
