/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import helpers.ObjectMapperContextResolver;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import servlets.CourseServlet;
import servlets.EmployeeServlet;
import servlets.EvaluationServlet;
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
        set.add(ObjectMapperContextResolver.class);
        set.add(PersonServlet.class);
        set.add(StudentServlet.class);
        set.add(RegisteredCourseServlet.class);
        set.add(GradeServlet.class);
        set.add(CourseServlet.class);
        set.add(EvaluationServlet.class);
        set.add(EmployeeServlet.class);
        return set;
    }
}
