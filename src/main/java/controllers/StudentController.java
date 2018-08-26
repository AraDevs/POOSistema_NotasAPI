/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

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
@XmlRootElement ( name = "studentController") 
@XmlSeeAlso( { Student.class, User.class })
public class StudentController {
    private List<Student> students;
    String param;
    
    public StudentController() {
        students = new CopyOnWriteArrayList<Student>(); 
        param = null;
    }
    
    public StudentController(String name) {   
        students = new CopyOnWriteArrayList<Student>();   
        param = name;  
    }
    
    public StudentController(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            students = new CopyOnWriteArrayList<Student>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getStudent() {   
        try {    
            students = getStudentList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.students;  
    }
    
    public void setStudent(List<Student> students) {   
        this.students = students;     
    } 
    
    public List<Student> getStudentList(String param) throws Exception {      
        Connection conn = DbConnection.conn();
        try {  
            
            PreparedStatement cmd = conn.prepareStatement("SELECT id, user_id, state FROM students");
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                Student tmpStd = new Student(
                        rs.getInt(1),
                        new UserController().getUserById(rs.getInt(2)),
                        rs.getBoolean(3)
                );
                
                students.add(tmpStd);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Students: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getStudentList");
        }
        return students; 
    }
    
    public Student getStudentByUser (int userId) throws Exception {
        Connection conn = DbConnection.conn();
        Student student = null;
        try {  
            
            PreparedStatement cmd = conn.prepareStatement("SELECT id, state FROM students WHERE user_id = " + userId);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                student = new Student(
                        rs.getInt(1),
                        new UserController().getUserById(userId),
                        rs.getBoolean(2)
                );
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Student: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getStudentByUser");
        }
        return student; 
    }
}
