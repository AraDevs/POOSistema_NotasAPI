/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Course;
import beans.CourseTeacher;
import beans.Employee;
import beans.Evaluation;
import beans.Grade;
import beans.RegisteredCourse;
import beans.Student;
import beans.User;
import helpers.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "registeredCourseController") 
@XmlSeeAlso( { RegisteredCourse.class, Grade.class, Evaluation.class, CourseTeacher.class, Course.class })
public class RegisteredCourseController {
    private List<RegisteredCourse> regCourses;
    String param;
    
    public RegisteredCourseController() {
        regCourses = new CopyOnWriteArrayList<RegisteredCourse>(); 
        param = null;
    }
    
    public RegisteredCourseController(String studentId) {   
        regCourses = new CopyOnWriteArrayList<RegisteredCourse>();   
        param = studentId;  
    }
    
    public RegisteredCourseController(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            regCourses = new CopyOnWriteArrayList<RegisteredCourse>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getRegisteredCourse() {   
        try {    
            regCourses = getRegisteredCourseList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.regCourses;  
    }
    
    public void setUser(List<RegisteredCourse> regCourses) {   
        this.regCourses = regCourses;     
    } 
    
    public List<RegisteredCourse> getRegisteredCourseList(String param) throws Exception {      
        Connection conn = DbConnection.conn();
        try {
            
            PreparedStatement cmd = conn.prepareStatement("SELECT rc.id, rc.course_state, rc.course_year, rc.semester, rc.state, ct.id, ct.state, " +
                                                          "c.id, c.name, c.course_code, c.semester, c.inter, c.laboratory, c.uv, c.prerequisite_id, c.state " +
                                                          "FROM registered_courses rc INNER JOIN courses_teachers ct ON rc.course_teacher_id = ct.id " +
                                                          "INNER JOIN courses c ON ct.course_id = c.id WHERE rc.student_id = " + param);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                RegisteredCourse tmpRegCrs = new RegisteredCourse(
                        rs.getInt(1),
                        null,
                        new CourseTeacher(
                                rs.getInt(6),
                                new Course(
                                        rs.getInt(8),
                                        rs.getString(9),
                                        rs.getString(10),
                                        rs.getString(11),
                                        rs.getBoolean(12),
                                        rs.getBoolean(13),
                                        rs.getInt(14),
                                        null,
                                        rs.getBoolean(15)
                                ),
                                null,
                                rs.getBoolean(7)
                        ),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getBoolean(5),
                        new GradeController().getGradeList(String.valueOf(rs.getInt(1)))
                );
                regCourses.add(tmpRegCrs);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for RegisteredCourses: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getRegisteredCourseList");
        }
        return regCourses; 
    }
}
