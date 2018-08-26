/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import servlets.StudentServlet;

/**
 *
 * @author kevin
 */
@ApplicationPath("/Students")

public class StudentService extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(StudentServlet.class);
        return set;
    }
}
