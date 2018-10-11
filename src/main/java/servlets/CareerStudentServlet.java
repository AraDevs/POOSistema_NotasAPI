/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerCourseDAO;
import dao.CareerDAO;
import dao.CareerStudentDAO;
import dao.RegisteredCourseDAO;
import dao.StudentDAO;
import helpers.DaoStatus;
import hibernate.Career;
import hibernate.CareerCourse;
import hibernate.CareerStudent;
import hibernate.Course;
import hibernate.RegisteredCourse;
import hibernate.Student;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ws.rs.DELETE;
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
    @Path("/byStudent/{studentId: \\d+}/full")
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
    @Path("/{careerStudentId: \\d+}/careers")
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
            student = new StudentDAO().getStudentWithCareers(Integer.parseInt(studentId));
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
                if (((CareerStudent) cs).getCareerState().equals("Egresado") && ((CareerStudent) cs).getCareer().getId() == Integer.parseInt(careerId)) {
                    msg = "El estudiante ya ha egresado de esta carrera.";
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
            
            //Antes de guardar, se debe verificar que la carrera elegida tenga materias en el pensum
            CareerCourseDAO carCrsDao = new CareerCourseDAO();
            int plan = carCrsDao.getPlan(careerStudent);
            //Lista de materias que deben ser aprobadas para egresar
            List<CareerCourse> pensum = carCrsDao.getCareerCourseByCareerPlan(careerStudent.getCareer().getId(), plan);
            
            if (pensum.isEmpty()) {
                msg = "La carrera especificada no tiene ninguna materia en su pensum.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
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
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("careerState") String careerState, @FormParam("id") String id) {
        
        CareerStudentDAO carStdDao = new CareerStudentDAO(false);
        
        String msg = "";
        if (careerState == null || careerState.equals("")) {
            msg += " Estado de carrera\n";
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
        
        CareerStudent careerStudent = null;
        
        try {
            careerStudent = carStdDao.getCareerStudentWithParents(Integer.parseInt(id));
            if (careerStudent == null) {
                msg = "El registro de carrera especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if (careerState.equals("En curso")) {
                //Verificando si no hay otra carrera en curso
                for (CareerStudent cs : carStdDao.getCareerStudentList(careerStudent.getStudent().getId())) {
                    if (cs.getCareerState().equals("En curso") && cs.getId() != Integer.parseInt(id)) {
                        msg = "El estudiante especificado ya tiene una carrera en curso.";
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();                    
                    }
                }
            }
            //Si se ha definido la carrera como "Egresada" o "Abandonada", es necesario comprobarlo
            else {
                //Lista de materias aprobadas por el estudiante
                List<RegisteredCourse> approvedCourses = new RegisteredCourseDAO().getRegisteredCourseList(careerStudent.getStudent().getId(), true);
                //Lista de codigos de las materias aprobadas
                List<String> approvedCourseCodes = new ArrayList<String>();
                for (RegisteredCourse rc : approvedCourses) {
                    approvedCourseCodes.add(rc.getCourseTeacher().getCourse().getCourseCode());
                }
                
                CareerCourseDAO carCrsDao = new CareerCourseDAO();
                int plan = carCrsDao.getPlan(careerStudent);
                //Lista de materias que deben ser aprobadas para egresar
                List<CareerCourse> pensum = carCrsDao.getCareerCourseByCareerPlan(careerStudent.getCareer().getId(), plan);
                
                int approvedCourseCount = 0;
                //Verificando si cada materia ha sido aprobada
                for (CareerCourse cc : pensum) {
                    if (approvedCourseCodes.contains(cc.getCourse().getCourseCode())) {
                        approvedCourseCount++;
                    }
                }
                
                Boolean complete = false;
                
                //Si todas fueron aprobadas
                if (approvedCourseCount == pensum.size()) {
                    complete = true;
                }
                
                //Si se definió como egresado pero no ha pasado todas las materias
                if (careerState.equals("Egresado") && !complete) {
                    msg = "El estudiante no puede ser egresado, porque aún tiene materias pendientes de aprobar en esta carrera.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();  
                }
                
                //Si se definió como abandonado pero pasó todas las materias
                if (careerState.equals("Abandonado") && complete) {
                    msg = "El estudiante no puede haber abandonado, porque ya ha aprobado todas las materias de esta carrera.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();  
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            careerStudent.setCareerState(careerState);
            
            int status = carStdDao.update(careerStudent);
            
            if (status == DaoStatus.OK) {
                msg = "Registro de carrera modificado.";
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
        
        msg = "No se pudo modificar el registro de carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id) {
        
        String msg = "";
        CareerStudentDAO careerStudentDao = new CareerStudentDAO();
        
        CareerStudent careerStudent = null;
        
        try {
            careerStudent = careerStudentDao.getCareerStudentWithParents(Integer.parseInt(id));
            
            if (careerStudent == null) {
                msg = "La carrera registrada a desvicular no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            CareerCourseDAO carCrsDao = new CareerCourseDAO();
            
            int plan = carCrsDao.getPlan(careerStudent);
            //Materias de la carrera
            List<CareerCourse> careerCourses = carCrsDao.getCareerCourseByCareerPlan(careerStudent.getCareer().getId(), plan);
            //Materias cursadas por el estudiante
            List<RegisteredCourse> registeredCourses = new RegisteredCourseDAO().getRegisteredCourseList(careerStudent.getStudent().getId(), false);
            
            //Lista de códigos de materias cursadas por el estudiante
            ArrayList<String> courseCodes = new ArrayList<String>();
            for (RegisteredCourse rc : registeredCourses) {
                courseCodes.add(rc.getCourseTeacher().getCourse().getCourseCode());
            }
            
            //Verificando si alguna de las materias del pensum ha sido cursada por el estudiante
            //Si es así, se denega la operación
            for (CareerCourse cc : careerCourses) {
                if (courseCodes.contains(cc.getCourse().getCourseCode())) {
                    return Response.status(Response.Status.CONFLICT).entity("La carrera registrada no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = careerStudentDao.delete(careerStudent);
            
            if (status == DaoStatus.OK) {
                msg = "Carrera desvinculada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo desvincular la carrera registrada.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
