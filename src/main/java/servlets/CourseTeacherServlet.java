/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CourseDAO;
import dao.CourseTeacherDAO;
import dao.EmployeeDAO;
import dao.RegisteredCourseDAO;
import helpers.DaoStatus;
import helpers.Helpers;
import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Employee;
import hibernate.RegisteredCourse;
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
@Path("/courseTeachers")
public class CourseTeacherServlet {

    public CourseTeacherServlet() {
    }
    
    @GET
    @Path("/byEmployee/{employeeId: \\d+}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CourseTeacher> getCoursesByEmployee(@PathParam("employeeId") String employeeId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherList(Integer.parseInt(employeeId), false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byEmployee/{employeeId: \\d+}/courses/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CourseTeacher> getActiveCoursesByEmployee(@PathParam("employeeId") String employeeId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherList(Integer.parseInt(employeeId), true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byCourse/{courseId: \\d+}/employees/users/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CourseTeacher> getTeachersByCourse(@PathParam("courseId") String courseId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherByCourse(Integer.parseInt(courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{courseTeacherId: \\d+}/courses")
    @Produces({MediaType.APPLICATION_JSON})
    public CourseTeacher getCoursesTeacher(@PathParam("courseTeacherId") String courseTeacherId) {
        try {
            return new CourseTeacherDAO().getCourseTeacher(Integer.parseInt(courseTeacherId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/passCount/byRegisteredCourse/{regCourseId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getCoursesByRegisteredCourse(@PathParam("regCourseId") String regCourseId) {
        try {
            return new CourseTeacherDAO().getCourseTeacherTendency(Integer.parseInt(regCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("courseId") String courseId, @FormParam("employeeId") String employeeId, 
            @FormParam("courseYear") String courseYear, @FormParam("semester") String semester, @FormParam("classCount") String classCount) {
        
        CourseTeacherDAO crsTchDao = new CourseTeacherDAO(false);
        
        String msg = "";
        if (courseId == null || courseId.equals("")) {
            msg += " Materia\n";
        }
        if (employeeId == null || employeeId.equals("")) {
            msg += " Empleado\n";
        }
        if (courseYear == null || courseYear.equals("")) {
            msg += " Año\n";
        }
        if (semester == null || semester.equals("")) {
            msg += " Ciclo\n";
        }
        if (classCount == null || classCount.equals("")) {
            msg += " Cantidad de sesiones";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Course course = null;
        Employee employee = null;
        
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
            employee = new EmployeeDAO().getEmployee(Integer.parseInt(employeeId));
            if (employee == null) {
                msg = "El empleado especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!employee.getState()) {
                msg = "El empleado especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!employee.getRole().getTeach()){
                msg = "El empleado especificado no es un docente.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            if (Helpers.isPastSemester(Integer.parseInt(courseYear), semester)) {
                msg = "No puede hacerse una clase de un ciclo pasado.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            msg = "El año debe ser un número.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (Integer.parseInt(classCount) < 15) {
                msg = "La cantidad de sesiones debe ser mayor o igual a 15.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            msg = "La cantidad de sesiones debe ser un número.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(semester.equals("1") || semester.equals("2") || semester.equals("Interciclo"))) {
            msg = "El valor especificado para el ciclo no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (semester.equals("1")) {
            if (!(course.getSemester().equals("1") || course.getSemester().equals("12"))) {
                msg = "La materia especificada no está disponible para el primer ciclo.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        }
        if (semester.equals("2")) {
            if (!(course.getSemester().equals("2") || course.getSemester().equals("12"))) {
                msg = "La materia especificada no está disponible para el segundo ciclo.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        }
        if (semester.equals("Interciclo")) {
            if (!course.getInter()) {
                msg = "La materia especificada no está disponible para interciclo.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        }
        
        //El estado se calculará en función de si el courseTeacher está en el ciclo actual o no
        Boolean state = false;
        
        if (Integer.parseInt(courseYear) == Helpers.getCurrentYear() && semester.equals(Helpers.getCurrentSemester())) {
            state = true;
        }
        
        
        try {
            CourseTeacher courseTeacher = new CourseTeacher();
            courseTeacher.setCourse(course);
            courseTeacher.setEmployee(employee);
            courseTeacher.setCourseYear(Integer.parseInt(courseYear));
            courseTeacher.setClassCount(Integer.parseInt(classCount));
            courseTeacher.setSemester(semester);
            courseTeacher.setState(state);
            
            int status = crsTchDao.add(courseTeacher);
            
            if (status == DaoStatus.OK) {
                msg = "Clase agregada.";
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
        
        msg = "No se pudo guardar la clase.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("courseYear") String courseYear, @FormParam("semester") String semester, 
            @FormParam("classCount") String classCount, @FormParam("id") String id) {
        
        CourseTeacherDAO crsTchDao = new CourseTeacherDAO(false);
        
        String msg = "";
        if (courseYear == null || courseYear.equals("")) {
            msg += " Año\n";
        }
        if (semester == null || semester.equals("")) {
            msg += " Ciclo\n";
        }
        if (classCount == null || classCount.equals("")) {
            msg += " Cantidad de sesiones\n";
        }
        if (id == null || id.equals("")) {
            msg += " ID";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        CourseTeacher courseTeacher = null;
        
        try {
            courseTeacher = new CourseTeacherDAO().getCourseTeacherNiceWay(Integer.parseInt(id));
            if (courseTeacher == null) {
                msg = "La clase especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            if (Integer.parseInt(classCount) < 15) {
                msg = "La cantidad de sesiones debe ser mayor o igual a 15.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            msg = "La cantidad de sesiones debe ser un número.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (Helpers.isPastSemester(Integer.parseInt(courseYear), semester)) {
                msg = "No puede hacerse una clase de un ciclo pasado.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            msg = "El año debe ser un número.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        List<RegisteredCourse> registeredCourses = null;
        
        try {
            registeredCourses = new RegisteredCourseDAO().getRegisteredCourseByCourseTeacher(Integer.parseInt(id), false);
        
            //La modificación es válida solo para registros sin hijos
            if (registeredCourses.isEmpty()) {
            
                if (!(semester.equals("1") || semester.equals("2") || semester.equals("Interciclo"))) {
                    msg = "El valor especificado para el ciclo no es válido.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }

                if (semester.equals("1")) {
                    if (!(courseTeacher.getCourse().getSemester().equals("1") || courseTeacher.getCourse().getSemester().equals("12"))) {
                        msg = "La materia especificada no está disponible para el primer ciclo.";
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                    }
                }
                if (semester.equals("2")) {
                    if (!(courseTeacher.getCourse().getSemester().equals("2") || courseTeacher.getCourse().getSemester().equals("12"))) {
                        msg = "La materia especificada no está disponible para el segundo ciclo.";
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                    }
                }
                if (semester.equals("Interciclo")) {
                    if (!courseTeacher.getCourse().getInter()) {
                        msg = "La materia especificada no está disponible para interciclo.";
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                    }
                }
                
                //El estado se calculará en función de si el courseTeacher está en el ciclo actual o no
                Boolean state = false;

                if (Integer.parseInt(courseYear) == Helpers.getCurrentYear() && semester.equals(Helpers.getCurrentSemester())) {
                    state = true;
                }
                courseTeacher.setState(Boolean.valueOf(state));
            }
            else {
                msg = "Esta clase ya está en uso, asi que no puede ser modificada.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        try {
            courseTeacher.setCourseYear(Integer.parseInt(courseYear));
            //El setter de state se mudó unas lineas más arribita
            
            if (registeredCourses.isEmpty()) {
                courseTeacher.setClassCount(Integer.parseInt(classCount));
                courseTeacher.setSemester(semester);
            }
            
            int status = crsTchDao.update(courseTeacher);
            
            if (status == DaoStatus.OK) {
                if (registeredCourses.isEmpty()) {
                    msg = "Clase modificada.";
                    return Response.ok(msg, "text/plain").build();
                }
                else {
                    msg = "Clase modificada. Los cambios en año y ciclo no fueron tomados en cuenta ya que este registro está en uso.";
                    return Response.ok(msg, "text/plain").build();
                }
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
        
        msg = "No se pudo modificar la clase.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id) {
        
        String msg = "";
        CourseTeacherDAO courseTeacherDao = new CourseTeacherDAO();
        
        CourseTeacher courseTeacher = null;
        
        try {
            courseTeacher = courseTeacherDao.get(Integer.parseInt(id));
            
            if (courseTeacher == null) {
                msg = "La clase a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = courseTeacherDao.delete(courseTeacher);
            
            if (status == DaoStatus.OK) {
                msg = "Clase eliminada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("La clase no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar la clase.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
