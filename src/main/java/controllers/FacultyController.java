/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Faculty;
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
@XmlRootElement ( name = "facultyController") 
@XmlSeeAlso( { Faculty.class })
public class FacultyController {
    private List<Faculty> facts;
    String param;
    
    public FacultyController() {
        facts = new CopyOnWriteArrayList<Faculty>(); 
        param = null;
    }
    
    public FacultyController(String name) {   
        facts = new CopyOnWriteArrayList<Faculty>();   
        param = name;  
    }
    
    public FacultyController(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            facts = new CopyOnWriteArrayList<Faculty>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getFaculty() {   
        try {    
            facts = getFacultyList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.facts;  
    }
    
    public void setFaculty(List<Faculty> facts) {   
        this.facts = facts;     
    } 
    
    public List<Faculty> getFacultyList(String param) throws Exception {      
        Connection conn = DbConnection.conn();
        try {
            String whereQuery="";   
            if(param != null)   
                whereQuery = " WHERE name LIKE '%" + param + "%'"; 
            
            PreparedStatement cmd = conn.prepareStatement("SELECT id, name, state FROM faculties" + whereQuery);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                Faculty tmpFact = new Faculty(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getBoolean(3)
                );
                facts.add(tmpFact);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Faculties: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getFacultyList");
        }
        return facts; 
    }
    
    public Faculty getFacultyById(String id) throws Exception {      
        Connection conn = DbConnection.conn();
        Faculty faculty = null;
        try {
            PreparedStatement cmd = conn.prepareStatement("SELECT id, name, state FROM faculties WHERE id = " + id);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                faculty = new Faculty(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getBoolean(3)
                );
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Faculty: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getFacultyById");
        }
        return faculty; 
    }
    
    public String add(String name) throws Exception {   
        String id = "-1";  
        Connection conn = new DbConnection().conn();  
        try {
            Statement st = conn.createStatement();     
            String sql = "INSERT INTO faculties VALUES(null, '" + name + "', 1)";      
            st.executeUpdate(sql);  
            ResultSet res = st.executeQuery("SELECT max(id) as id FROM faculties");   
            res.next();   
            id = res.getString(1);
        } catch (SQLException ex) {
            id = "sqlError: " + ex.getErrorCode();
        } catch (Exception ex) {
            System.err.println("Error creating Faculty: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "addFaculty");
        }
        return id;
    }
    
    public String update(String name, String state, String id) throws Exception {  
        Connection conn = new DbConnection().conn();  
        String outputId = "-1";
        try {
            Statement st = conn.createStatement();      
            String sql = "UPDATE faculties SET name='" + name + "', state='" + state + "' WHERE id=" + id;      
            st.executeUpdate(sql);  
            outputId = id; //Operacion exitosa, devolver el id modificado
        } catch (SQLException ex) {
            outputId = "sqlError: " + ex.getErrorCode();
        } catch (Exception ex) {
            System.err.println("Error updating Faculty: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "updateFaculty");
        }
        return outputId;
    }
    
    public String delete(String id) throws Exception {   
        
        Connection conn = new DbConnection().conn();  
        String outputId = "-1"; 
        try {           
            String sql= "DELETE FROM faculties WHERE id=" + id;       
            Statement st = conn.createStatement();    
            st.executeUpdate(sql);
            outputId = id; //Operacion exitosa  
        } catch (SQLException ex) {
            outputId = "sqlError: " + ex.getErrorCode();
        } catch (Exception ex) {
            System.err.println("Error deleting Faculty: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "deleteFaculty");
        }
        return outputId;  
    } 
}
