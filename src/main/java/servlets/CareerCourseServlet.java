/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerCourseDAO;
import dao.CareerDAO;
import dao.CourseDAO;
import helpers.DaoStatus;
import helpers.Helpers;
import hibernate.Career;
import hibernate.CareerCourse;
import hibernate.Course;
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
@Path("/careerCourses")
public class CareerCourseServlet {
    
    @GET
    @Path("/byCareer/{careerId}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerCourse> getCareerCourseByCareer (@PathParam("careerId") String careerId) {
        try {
            return new CareerCourseDAO().getCareerCourseList(Integer.parseInt(careerId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byCareer/{careerId}/courses/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerCourse> getActiveCareerCourseByCareer (@PathParam("careerId") String careerId) {
        try {
            return new CareerCourseDAO().getCareerCourseList(Integer.parseInt(careerId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerCourseId}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public CareerCourse getCareerCourse (@PathParam("careerCourseId") String careerCourseId) {
        try {
            return new CareerCourseDAO().getCareerCourse(Integer.parseInt(careerCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("careerId") String careerId, @FormParam("courseId") String courseId, 
            @FormParam("plan") String plan, @FormParam("pensumYear") String pensumYear, @FormParam("pensumSemester") String pensumSemester) {
        
        CareerCourseDAO carCrsDao = new CareerCourseDAO(false);
        
        String msg = "";
        if (careerId == null || careerId.equals("")) {
            msg += " Carrera\n";
        }
        if (courseId == null || courseId.equals("")) {
            msg += " Materia\n";
        }
        if (plan == null || plan.equals("")) {
            msg += " Plan\n";
        }
        if (pensumYear == null || pensumYear.equals("")) {
            msg += " Número de año\n";
        }
        if (pensumSemester == null || pensumSemester.equals("")) {
            msg += " Número de ciclo";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (Integer.parseInt(plan) <= Calendar.getInstance().get(Calendar.YEAR)) {
                msg = "Solo pueden hacerse registros de planes futuros.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            msg = "El plan debe ser un número.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(pensumYear.equals("1") || pensumYear.equals("2") || pensumYear.equals("3") || pensumYear.equals("4") || pensumYear.equals("5"))) {
            msg = "El número de año debe estar entre 1 y 5.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(pensumSemester.equals("1") || pensumSemester.equals("2"))) {
            msg = "El número de ciclo debe ser 1 o 2.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Career career = null;
        Course course = null;
        
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
            course = new CourseDAO().get(Integer.parseInt(courseId));
            if (course == null) {
                msg = "La materia especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!course.getState()) {
                msg = "La materia especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            List<CareerCourse> careerCourseList = carCrsDao.getCareerCourseByCareerPlan(Integer.parseInt(careerId), Integer.parseInt(plan));
            
            //Registro de careerCourse asociado al prerrequisito de la materia especificada, si lo posee
            CareerCourse prerrequisiteChild = null;
            
            //Cantidad de materias en el año/ciclo especificado
            int coursesCount = 0;
            
            //Iterando entre todas las materias de este pensum para realizar múltiples validaciones
            for (CareerCourse cc : careerCourseList) {
                if (cc.getCourse().getId() == Integer.parseInt(courseId)) {
                    msg = "La materia especificada ya es parte de este plan.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
                //Si la materia especificada tiene prerrequisito
                if (course.getCourse() != null) {
                    //Si la materia iterada es el prerrequisito de la especificada, se guardará para efectuar la validación después
                    if (cc.getCourse().getId() == course.getCourse().getId()) {
                        prerrequisiteChild = cc;
                    }
                }
                //Si la materia iterada esta en la misma columna en la que estaría la especificada
                if (cc.getPensumYear().equals(pensumYear) && cc.getPensumSemester().equals(pensumSemester)) {
                    coursesCount++;
                }
            }
            
            //Si no hay cupo en la columna
            if (coursesCount >= 7) {
                msg = "No pueden haber más materias en el año y ciclo especificado.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            //Si la materia especificada tiene prerrequisito
            if (course.getCourse() != null) {
                //Si el prerrequisito no está en el plan
                if (prerrequisiteChild == null) {
                    msg = "El prerrequisito de la materia debe estar presente en el plan.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
                //Si el prerrequisito está en el plan
                else {
                    //Si el prerrequisito no está antes que la materia en posición de pensum
                    if (Integer.parseInt(prerrequisiteChild.getPensumYear()) >= Integer.parseInt(pensumYear)) {
                        if (Integer.parseInt(prerrequisiteChild.getPensumSemester()) >= Integer.parseInt(pensumSemester)) {
                            msg = "El prerrequisito de la materia debe estar en una posición del plan que sea anterior a la de la materia.";
                            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            CareerCourse careerCourse = new CareerCourse();
            careerCourse.setCareer(career);
            careerCourse.setCourse(course);
            careerCourse.setPlan(Integer.parseInt(plan));
            careerCourse.setPensumYear(pensumYear);
            careerCourse.setPensumSemester(pensumSemester);
            careerCourse.setState(true);
            
            int status = carCrsDao.add(careerCourse);
            
            if (status == DaoStatus.OK) {
                msg = "Materia agregada al plan.";
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
        
        msg = "No se pudo agregar la materia al plan.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id) {
        
        String msg = "";
        CareerCourseDAO careerCourseDao = new CareerCourseDAO();
        
        CareerCourse careerCourse = null;
        
        try {
            careerCourse = careerCourseDao.getCareerCourseNiceWay(Integer.parseInt(id));
            
            if (careerCourse == null) {
                msg = "La materia registrada a desvincular no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            if (careerCourse.getPlan() <= Helpers.getCurrentYear()) {
                msg = "Sólo se pueden modificar planes futuros.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            //Lista de materias de la carrera
            List<CareerCourse> careerCourseList = careerCourseDao.getCareerCourseByCareerPlan(careerCourse.getCareer().getId(), careerCourse.getPlan()); 
            
            //Verificando si la materia que se quiere desvincular es el prerrequisito de otra que está
            //presente en el plan
            for (CareerCourse cc : careerCourseList) {
                if (cc.getCourse().getCourse() != null) {
                    if (cc.getCourse().getCourse().getCourseCode().equals(careerCourse.getCourse().getCourseCode())) {
                        msg = "No puede desvincular esta materia porque es prerrequisito de otra materia en este plan.";
                        return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = careerCourseDao.delete(careerCourse);
            
            if (status == DaoStatus.OK) {
                msg = "Materia desvinculada.";
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
        
        msg = "No se pudo desvincular la materia registrada.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
