/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseTeacherDAO;
import dao.RegisteredCourseDAO;
import dao.StudentDAO;
import helpers.DaoStatus;
import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.RegisteredCourse;
import hibernate.Student;
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
@Path("/registeredCourses")
public class RegisteredCourseServlet {
    private static RegisteredCourseDAO regCrsDAO;

    public RegisteredCourseServlet() {
    }
    
    @GET
    @Path("/byCourseTeacher/{courseTeacherId: \\d+}/students/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getRegisteredCoursesByCourseTeacher(@PathParam("courseTeacherId") String courseTeacherId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseByCourseTeacher(Integer.parseInt(courseTeacherId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{registeredCourseId: \\d+}/courses/teachers")
    @Produces({MediaType.APPLICATION_JSON})
    public RegisteredCourse getRegisteredCourse(@PathParam("registeredCourseId") String registeredCourseId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourse(Integer.parseInt(registeredCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{student_id: \\d+}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getRegisteredCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseListWithGrades(Integer.parseInt(studentId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{student_id: \\d+}/full/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getActiveRegisteredCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseListWithGrades(Integer.parseInt(studentId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/byStudent/{student_id: \\d+}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getRegisteredCoursesWithCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseWithCourse(Integer.parseInt(studentId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{student_id: \\d+}/courses/teachers")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getRegisteredCoursesWithCoursesAndTeachers(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseWithCourseAndTeacher(Integer.parseInt(studentId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @GET
    @Path("/byStudent/{student_id: \\d+}/courses/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<RegisteredCourse> getActiveRegisteredCoursesWithCourses(@PathParam("student_id") String studentId) {
        try {
            return new RegisteredCourseDAO().getRegisteredCourseWithCourse(Integer.parseInt(studentId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("studentId") String studentId, @FormParam("courseTeacherId") String courseTeacherId) {
        
        regCrsDAO = new RegisteredCourseDAO(false);
        
        String msg = "";
        if (studentId == null || studentId.equals("")) {
            msg += " Estudiante\n";
        }
        if (courseTeacherId == null || courseTeacherId.equals("")) {
            msg += " Clase";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Student student = null;
        CourseTeacher courseTeacher = null;
        
        try {
            student = new StudentDAO().getStudent(Integer.parseInt(studentId));
            if (student == null) {
                msg = "El estudiante especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!student.getState()) {
                msg = "El estudiante especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            courseTeacher = new CourseTeacherDAO().getCourseTeacherWithParentsNiceWay(Integer.parseInt(courseTeacherId));
            if (courseTeacher == null) {
                msg = "La clase especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!courseTeacher.getState()) {
                msg = "La clase especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            //Verificando si la clase elegida está disponible para el estudiante especificado
            List<Course> availableCourses = new CourseServlet().getAvailableCourses(studentId);
            boolean available = false;
            
            for (Course c : availableCourses) {
                //Si se encontró en la lista de disponibles
                if (c.getCourseCode().equals(courseTeacher.getCourse().getCourseCode())) {
                    available = true;
                }
            }
            
            //Si no está disponible
            if (!available) {
                msg = "El estudiante especificado no puede ingresar esta materia.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            
            //Verificando que el docente y estudiante sean personas diferentes
            if (courseTeacher.getEmployee().getUser().getId() == student.getUser().getId()) {
                msg = "El docente de la clase especificada y el estudiante especificado son la misma persona.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            RegisteredCourse registeredCourse = new RegisteredCourse();
            registeredCourse.setStudent(student);
            registeredCourse.setCourseTeacher(courseTeacher);
            registeredCourse.setCourseState("En curso");
            registeredCourse.setState(true);
            
            int status = regCrsDAO.add(registeredCourse);
            
            if (status == DaoStatus.OK) {
                msg = "Clase registrada.";
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
        
        msg = "No se pudo registrar la clase.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("courseState") String courseState, @FormParam("id") String id) {
        
        regCrsDAO = new RegisteredCourseDAO(false);
        
        String msg = "";
        if (courseState == null || courseState.equals("")) {
            msg += " Estado de materia.\n";
        }
        if (id == null || id.equals("")) {
            msg += " ID";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(courseState.equals("En curso") || courseState.equals("Retirada"))) {
            msg = "El estado de materia especificado no es válido.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        RegisteredCourse registeredCourse = null;
        
        try {
            registeredCourse = regCrsDAO.getRegisteredCourseNiceWay(Integer.parseInt(id));
            if (registeredCourse == null) {
                msg = "El registro especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            //Si corresponde a una clase que está deshabilitada, no se puede modificar
            if (!registeredCourse.getCourseTeacher().getState()) {
                msg = "El registro especificado corresponde a una clase que no está disponible.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            //Si la materia fue finalizada (aprobada o reprobada), no se puede modificar
            if (registeredCourse.getCourseState().equals("Aprobada") || registeredCourse.getCourseState().equals("Reprobada")) {
                msg = "La materia ya ha sido finalizada, por lo que no está disponible.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            registeredCourse.setCourseState(courseState);
            
            int status = regCrsDAO.update(registeredCourse);
            
            if (status == DaoStatus.OK) {
                msg = "Estado de materia actualizado.";
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
        
        msg = "No se pudo modificar el estado de la materia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id) {
        
        String msg = "";
        RegisteredCourseDAO registeredCourseDao = new RegisteredCourseDAO();
        
        RegisteredCourse registeredCourse = null;
        
        try {
            registeredCourse = registeredCourseDao.get(Integer.parseInt(id));
            
            if (registeredCourse == null) {
                msg = "El registro de materia a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = registeredCourseDao.delete(registeredCourse);
            
            if (status == DaoStatus.OK) {
                msg = "Registro de materia eliminado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El registro de materia no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar el registro de materia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
