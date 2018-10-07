/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import helpers.JAXBContextResolver;
import helpers.ObjectMapperContextResolver;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import servlets.CareerServlet;
import servlets.CareerTypeServlet;
import servlets.CourseServlet;
import servlets.CourseTeacherServlet;
import servlets.EmployeeServlet;
import servlets.EvaluationServlet;
import servlets.FacultyServlet;
import servlets.GradeServlet;
import servlets.PersonServlet;
import servlets.RegisteredCourseServlet;
import servlets.StudentServlet;

/**
 *
 * @author kevin
 */
@ApplicationPath("/")
public class PersonService extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(JAXBContextResolver.class);
        set.add(PersonServlet.class);
        set.add(StudentServlet.class);
        set.add(RegisteredCourseServlet.class);
        set.add(GradeServlet.class);
        set.add(CourseServlet.class);
        set.add(EvaluationServlet.class);
        set.add(EmployeeServlet.class);
        set.add(CourseTeacherServlet.class);
        set.add(FacultyServlet.class);
        set.add(CareerTypeServlet.class);
        set.add(CareerServlet.class);
        return set;
    }
}
