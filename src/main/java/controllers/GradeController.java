/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Course;
import beans.Evaluation;
import beans.Grade;
import beans.RegisteredCourse;
import beans.User;
import helpers.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author kevin
 */
public class GradeController {
    private List<Grade> grades;
    String param;
    
    public GradeController() {
        grades = new CopyOnWriteArrayList<Grade>(); 
        param = null;
    }
    
    public GradeController(String name) {   
        grades = new CopyOnWriteArrayList<Grade>();   
        param = name;  
    }
    
    public GradeController(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            grades = new CopyOnWriteArrayList<Grade>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getGrade() {   
        try {    
            grades = getGradeList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.grades;  
    }
    
    public void setGrade(List<Grade> grades) {   
        this.grades = grades;     
    } 
    
    public List<Grade> getGradeList(String param) throws Exception {      
        Connection conn = DbConnection.conn();
        try {
            
            PreparedStatement cmd = conn.prepareStatement("SELECT g.id, g.grade, g.observations, g.state, e.id, e.name, e.description, e.percentage, e.period, " +
                                                          "e.laboratory, e.start_date, e.end_date, e.state\n" +
                                                          "FROM grades g INNER JOIN evaluations e ON g.evaluation_id = e.id WHERE g.registered_course_id = " + param);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                Grade tmpGrade = new Grade(
                        rs.getInt(1),
                        rs.getDouble(2),
                        rs.getString(3),
                        null,
                        new Evaluation(
                                rs.getInt(5),
                                rs.getString(6),
                                rs.getString(7),
                                rs.getInt(8),
                                rs.getString(9),
                                rs.getBoolean(10),
                                rs.getDate(11),
                                rs.getDate(12),
                                null,
                                rs.getBoolean(13)
                        ),
                        rs.getBoolean(4)
                );
                grades.add(tmpGrade);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Grades: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getGradeList");
        }
        return grades; 
    }
}
