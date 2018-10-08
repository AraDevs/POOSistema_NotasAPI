/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerCourseDAO;
import dao.CareerStudentDAO;
import dao.CourseDAO;
import dao.RegisteredCourseDAO;
import hibernate.CareerCourse;
import hibernate.CareerStudent;
import hibernate.Course;
import hibernate.RegisteredCourse;
import java.util.ArrayList;
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
}
