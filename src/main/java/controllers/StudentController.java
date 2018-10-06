/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import aaaaa.Student;
import aaaaa.UserDTO;
import helpers.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
@XmlSeeAlso( { Student.class, UserDTO.class })
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
            String whereQuery = "";
            if (param != null) {
                whereQuery = " INNER JOIN users u ON s.user_id = u.id WHERE u.name LIKE '%" + param + "%' OR u.surname LIKE '%" + param + "%' " +
                             "OR u.username LIKE '%" + param + "%' OR u.email LIKE '%" + param + "%' OR u.phone LIKE '%" + param + "%'";
            }
            
            PreparedStatement cmd = conn.prepareStatement("SELECT s.id, s.user_id, s.state FROM students s" + whereQuery);
            
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
    
    public String add(String userId) throws Exception {   
        String id = "-1";  
        Connection conn = new DbConnection().conn();  
        try {
            Statement st = conn.createStatement();     
            String sql = "INSERT INTO students VALUES(null, " + userId + ", 1)";      
            st.executeUpdate(sql);  
            ResultSet res = st.executeQuery("SELECT max(id) as id FROM students");   
            res.next();   
            id = res.getString(1);
        } catch (SQLException ex) {
            id = "sqlError: " + ex.getErrorCode();
        } catch (Exception ex) {
            System.err.println("Error creating Student: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "addStudent");
        }
        return id;
    }
    
    public String delete(String userId) throws Exception {   
        
        Connection conn = new DbConnection().conn();  
        String outputId = "-1"; 
        try {           
            String sql= "DELETE FROM students WHERE user_id=" + userId;       
            Statement st = conn.createStatement();    
            st.executeUpdate(sql);
            outputId = userId; //Operacion exitosa  
        } catch (SQLException ex) {
            outputId = "sqlError: " + ex.getErrorCode();
        } catch (Exception ex) {
            System.err.println("Error deleting Student: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "deleteStudent");
        }
        return outputId;  
    } 
}
