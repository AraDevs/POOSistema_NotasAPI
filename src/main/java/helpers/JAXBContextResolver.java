/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import dto.CorrectionDTO;
import dto.EmployeeTokenDTO;
import dto.Plan;
import dto.StudentTokenDTO;
import hibernate.Career;
import hibernate.CareerCourse;
import hibernate.CareerStudent;
import hibernate.CareerType;
import hibernate.Correction;
import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Employee;
import hibernate.Evaluation;
import hibernate.Faculty;
import hibernate.Grade;
import hibernate.Person;
import hibernate.RegisteredCourse;
import hibernate.Role;
import hibernate.Student;
import hibernate.Unattendance;
import hibernate.User;
import java.util.ArrayList;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

/**
 *
 * @author kevin
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

	private JAXBContext context;
	private Class[] types = {
            Career.class,
            CareerCourse.class,
            CareerStudent.class,
            CareerType.class,
            Correction.class,
            Course.class,
            CourseTeacher.class,
            Employee.class, 
            Evaluation.class,
            Faculty.class,
            Grade.class,
            Person.class,
            RegisteredCourse.class,
            Role.class,
            Student.class,
            Unattendance.class,
            User.class,
            
            Plan.class,
            CorrectionDTO.class,
            StudentTokenDTO.class,
            EmployeeTokenDTO.class
        };

	public JAXBContextResolver() throws Exception {
		this.context = new JSONJAXBContext(JSONConfiguration.natural().build(), types);
	}

	public JAXBContext getContext(Class<?> objectType) {
		for (Class type : types) {
			if (type == objectType) {
				return context;
			}
		}
		return null;
	}
}
