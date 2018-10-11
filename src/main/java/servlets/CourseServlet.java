/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerCourseDAO;
import dao.CareerStudentDAO;
import dao.CourseDAO;
import dao.FacultyDAO;
import dao.RegisteredCourseDAO;
import helpers.DaoStatus;
import hibernate.CareerCourse;
import hibernate.CareerStudent;
import hibernate.Course;
import hibernate.Faculty;
import hibernate.RegisteredCourse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
@Path("/courses")
public class CourseServlet {
    private static CourseDAO courseDao;
    
    public CourseServlet() {
    }
    
    @GET
    @Path("/faculties/prerrequisite")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Course> getCourses() {
        try {
            return new CourseDAO().getCourseList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/faculties/prerrequisite/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Course> getActiveCourses() {
        try {
            return new CourseDAO().getCourseList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("{courseId}/faculties/prerrequisite")
    @Produces({MediaType.APPLICATION_JSON})
    public Course getCourse(@PathParam("courseId") String courseId) {
        try {
            return new CourseDAO().getCourse(Integer.parseInt(courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byRegisteredCourse/{regCourseId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Course getCoursesByRegisteredCourse(@PathParam("regCourseId") String regCourseId) {
        try {
            return new CourseDAO().getCourseByRegisteredCourse(Integer.parseInt(regCourseId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byStudent/{studentId}/available")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Course> getAvailableCourses(@PathParam("studentId") String studentId) {
        try {
            CareerCourseDAO carCrsDao = new CareerCourseDAO();
            
            //Obteniendo carrera del estudiante para saber cual es y en que año inició
            CareerStudent careerStudent = new CareerStudentDAO().getCurrentCareerStudentByStudent(Integer.parseInt(studentId));
            //Obteniendo plan de la carrera
            int plan = carCrsDao.getPlan(careerStudent);
            //Obteniendo pensum de la carrera y plan obtenidos
            List<CareerCourse> careerCourses = carCrsDao.getCareerCourseByCareerPlan(careerStudent.getCareer().getId(), plan);
            
            //Obteniendo lista de materias registradas por el estudiante
            List<RegisteredCourse> registeredCourses = new RegisteredCourseDAO().getRegisteredCourseList(Integer.parseInt(studentId), false);
            
            //Agrupando codigo y estado (aprobado, etc) de las materias registradas, para compararlas con el pensum
            ArrayList<String> courseCodes = new ArrayList<String>();
            ArrayList<String> courseStates = new ArrayList<String>();
            
            for (RegisteredCourse rc : registeredCourses) {
                courseCodes.add(rc.getCourseTeacher().getCourse().getCourseCode());
                courseStates.add(rc.getCourseState());
            }
            
            //Colección de materias disponibles para el estudiante
            List<Course> availableCourses = new ArrayList<Course>();
            
            //Comparando cada materia del pensum con las materias registradas por el estudiante
            for (CareerCourse cc : careerCourses) {
                boolean available = false;
                Course course = cc.getCourse();
                
                //Si el estudiante ya ha registrado la materia antes
                if (courseCodes.contains(course.getCourseCode())) {
                    //Se comprueba en un for por el hecho de que la misma materia podría
                    //haber sido registrada múltiples veces, no se puede saber si la primera
                    //que se comprobó es la que se busca
                    for (int i = 0; i < courseCodes.size(); i++) {
                        available = true; //Disponible hasta que se demuestre lo contrario
                        boolean notAvailable = false;
                        
                        //Si la materia está registrada y está en curso o ya fue aprobada, no está disponible
                        if (courseCodes.get(i).equals(course.getCourseCode()) && (courseStates.get(i).equals("Aprobada") || courseStates.get(i).equals("En curso"))) {
                            notAvailable = true;
                        }
                        if (notAvailable) {
                            available = false;
                            break;
                        }
                    }
                }
                //Si nunca ha registrado la materia
                else {
                    //Si la materia no tiene prerrequisito, está disponible
                    if (course.getCourse() == null) {
                        available = true;
                    }
                    //Si la materia tiene prerrequisito
                    else {
                        //Si el estudiante ha registrado el prerrequisito de la materia
                        if (courseCodes.contains(course.getCourse().getCourseCode())) {
                            for (int i = 0; i < courseCodes.size(); i++) {
                                //Si el prerrequisito fue aprobado
                                if (courseCodes.get(i).equals(course.getCourse().getCourseCode()) && courseStates.get(i).equals("Aprobada")) {
                                    available = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                //Si se determina que el estudiante puede inscribir la materia, la misma
                //será parte del resultado
                if (available) {
                    availableCourses.add(course);
                }
            }
            
            return availableCourses;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Course>();
        }
    }
       
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("semester") String semester, 
            @FormParam("inter") String inter, @FormParam("laboratory") String laboratory, @FormParam("uv") String uv,
            @FormParam("prerrequisiteId") String prerrequisiteId, @FormParam("facultyId") String facultyId) {
        
        courseDao = new CourseDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de materia\n";
        }
        if (semester == null || semester.equals("")) {
            msg += " Ciclos en los que está disponible\n";
        }
        if (inter == null || inter.equals("")) {
            msg += " Disponible en interciclo\n";
        }
        if (laboratory == null || laboratory.equals("")) {
            msg += " Posee laboratorio\n";
        }
        if (uv == null || uv.equals("")) {
            msg += " Unidades valorativas\n";
        }
        if (prerrequisiteId == null || prerrequisiteId.equals("")) {
            msg += " Prerrequisito\n";
        }
        if (facultyId == null || facultyId.equals("")) {
            msg += " Facultad";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        //Generando coursecode
        Random r = new Random();
        String nameWithoutSpaces = name.replace(" ", "").toUpperCase();
        String courseCode = nameWithoutSpaces.substring(0, 3) + 
                            nameWithoutSpaces.substring(nameWithoutSpaces.length() - 2) + 
                            String.valueOf(r.nextInt()).substring(1,6);
        
        if (!(semester.equals("1") || semester.equals("2") || semester.equals("12"))) {
            msg = "El valor pasado para el campo Ciclo no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (Integer.parseInt(uv) < 2 || Integer.parseInt(uv) > 6) {
            msg = "Las unidades valorativas deben estar en el rango 2 a 6.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Course prerrequisite = null;
        Faculty faculty = null;
        
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
            //Si es 0, no hay prerrequisito
            if (!prerrequisiteId.equals("0")) {
                prerrequisite = new CourseDAO().get(Integer.parseInt(prerrequisiteId));
                if (prerrequisite == null) {
                    msg = "El prerrequisito especificado no existe.";
                    return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
                else if (!prerrequisite.getState()) {
                    msg = "El prerrequisito especificado no está disponible.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        
        //VALIDACIONES PENDIENTES
        //Prerrequisito en la misma facultad que la materia (???)
        
        try {
            Course course = new Course();
            course.setName(name);
            course.setCourseCode(courseCode);
            course.setSemester(semester);
            course.setInter(Boolean.valueOf(inter));
            course.setLaboratory(Boolean.valueOf(laboratory));
            course.setUv(Integer.parseInt(uv));
            if (Integer.parseInt(prerrequisiteId) != 0) course.setCourse(prerrequisite);
            course.setFaculty(faculty);
            course.setState(true);
            
            int status = courseDao.add(course);
            
            if (status == DaoStatus.OK) {
                msg = "Materia agregada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre de la materia ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo guardar la materia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("semester") String semester, 
            @FormParam("inter") String inter, @FormParam("laboratory") String laboratory, @FormParam("uv") String uv,
            @FormParam("prerrequisiteId") String prerrequisiteId, @FormParam("facultyId") String facultyId,
            @FormParam("state") String state, @FormParam("id") String id) {
        
        courseDao = new CourseDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de materia\n";
        }
        if (semester == null || semester.equals("")) {
            msg += " Ciclos en los que está disponible\n";
        }
        if (inter == null || inter.equals("")) {
            msg += " Disponible en interciclo\n";
        }
        if (laboratory == null || laboratory.equals("")) {
            msg += " Posee laboratorio\n";
        }
        if (uv == null || uv.equals("")) {
            msg += " Unidades valorativas\n";
        }
        if (prerrequisiteId == null || prerrequisiteId.equals("")) {
            msg += " Prerrequisito\n";
        }
        if (facultyId == null || facultyId.equals("")) {
            msg += " Facultad\n";
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
        
        if (!(semester.equals("1") || semester.equals("2") || semester.equals("12"))) {
            msg = "El valor pasado para el campo Ciclo no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (Integer.parseInt(uv) < 2 || Integer.parseInt(uv) > 6) {
            msg = "Las unidades valorativas deben estar en el rango 2 a 6.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Course prerrequisite = null;
        Faculty faculty = null;
        
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
            //Si es 0, no hay prerrequisito
            if (!prerrequisiteId.equals("0")) {
                prerrequisite = new CourseDAO().get(Integer.parseInt(prerrequisiteId));
                if (prerrequisite == null) {
                    msg = "El prerrequisito especificado no existe.";
                    return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
                else if (!prerrequisite.getState()) {
                    msg = "El prerrequisito especificado no está disponible.";
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        
        Course course = null;
        
        try {
            course = new CourseDAO().get(Integer.parseInt(id));
            if (course == null) {
                msg = "La materia a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        //VALIDACIONES PENDIENTES
        //Prerrequisito en la misma facultad que la materia (???)
        
        try {
            course.setName(name);
            course.setSemester(semester);
            course.setInter(Boolean.valueOf(inter));
            course.setLaboratory(Boolean.valueOf(laboratory));
            course.setUv(Integer.parseInt(uv));
            if (Integer.parseInt(prerrequisiteId) != 0) course.setCourse(prerrequisite);
            course.setFaculty(faculty);
            course.setState(Boolean.valueOf(state));
            
            int status = courseDao.update(course);
            
            if (status == DaoStatus.OK) {
                msg = "Materia modificada.";
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
        
        msg = "No se pudo modificar la materia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id) {
        
        String msg = "";
        CourseDAO courseDao = new CourseDAO();
        
        Course course = null;
        
        try {
            course = courseDao.get(Integer.parseInt(id));
            
            if (course == null) {
                msg = "La materia a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = courseDao.delete(course);
            
            if (status == DaoStatus.OK) {
                msg = "Materia eliminada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("La materia no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar la materia.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
