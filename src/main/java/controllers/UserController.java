/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import beans.User;
import helpers.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "userController") 
@XmlSeeAlso( { User.class })
public class UserController {
    private List<User> users;
    String param;
    
    public UserController() {
        users = new CopyOnWriteArrayList<User>(); 
        param = null;
    }
    
    public UserController(String name) {   
        users = new CopyOnWriteArrayList<User>();   
        param = name;  
    }
    
    public UserController(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            users = new CopyOnWriteArrayList<User>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getUser() {   
        try {    
            users = getUserList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.users;  
    }
    
    public void setUser(List<User> users) {   
        this.users = users;     
    } 
    
    public List<User> getUserList(String param) throws Exception {      
        Connection conn = DbConnection.conn();
        try {
            String whereQuery="";   
            /*if(param != null)   
                whereQuery = " WHERE u.id LIKE '%" + param + "%'"; */
            
            PreparedStatement cmd = conn.prepareStatement("SELECT u.id, u.name, u.surname, u.username, u.pass, u.phone, u.email, u.state FROM users u" + whereQuery);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                User tmpUser = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getBoolean(8)
                );
                users.add(tmpUser);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Users: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getUserList");
        }
        return users; 
    }
    
    public User getUserById(int id) throws Exception {      
        Connection conn = DbConnection.conn();
        User user = null;
        try {  
            PreparedStatement cmd = conn.prepareStatement("SELECT u.id, u.name, u.surname, u.username, u.pass, u.phone, u.email, u.state FROM users u WHERE u.id = " + id);
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                user = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getBoolean(8)
                );
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for User: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "getUserById");
        }
        return user; 
    }
    
    public int getYearIndex() throws Exception {   
        int id = -1;  
        Connection conn = new DbConnection().conn();  
        try {
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT MAX(id) FROM users WHERE username LIKE '%" + Calendar.getInstance().get(Calendar.YEAR) + "%'");   
            res.next();   
            int registerId = res.getInt(1);
            res = st.executeQuery("SELECT username FROM users WHERE id = " + registerId);   
            res.next();  
            id = Integer.parseInt((res.getString(1)).substring(6, 10));
        } catch (Exception ex) {
            System.err.println("Error creating User: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "addUser");
        }
        return id;
    }
    
    public String add(String name, String surname, String username, String pass, String phone, String email) throws Exception {   
        String id = "-1";  
        Connection conn = new DbConnection().conn();  
        try {
            Statement st = conn.createStatement();     
            String sql = "INSERT INTO users VALUES(null, '" + name + "', '" + surname + "', '" + username + "', '" + pass + "', '" + phone + "', '" + email + "', 1)";      
            st.executeUpdate(sql);  
            ResultSet res = st.executeQuery("SELECT max(id) as id FROM users");   
            res.next();   
            id = res.getString(1);
        } catch (SQLException ex) {
            id = ex.getMessage();
        } catch (Exception ex) {
            System.err.println("Error creating User: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "addUser");
        }
        return id;
    }
    
    public String update(String name, String surname, String pass, String phone, String email, String state, String id) throws Exception {  
        Connection conn = new DbConnection().conn();  
        String outputId = "-1";
        try {
            String passQuery = "";
            if (pass != null) {
                passQuery = "pass='" + pass + "', ";
            }
            
            Statement st = conn.createStatement();      
            String sql = "UPDATE users SET name='" + name + "', surname='" + surname + "', "
                    + passQuery + "phone='" + phone + "', email='" + email + "', state = '" + state + "' WHERE id=" + id;      
            st.executeUpdate(sql);  
            outputId = id; //Operacion exitosa, devolver el id modificado
        } catch (SQLException ex) {
            outputId = ex.getMessage();
        } catch (Exception ex) {
            System.err.println("Error updating User: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "updateUser");
        }
        return outputId;
    }
    
    public String delete(String id) throws Exception {   
        
        Connection conn = new DbConnection().conn();  
        String outputId = "-1"; 
        try {           
            String sql= "DELETE FROM users WHERE id=" + id;       
            Statement st = conn.createStatement();    
            st.executeUpdate(sql);
            outputId = id; //Operacion exitosa  
        } catch (SQLException ex) {
            outputId = ex.getMessage();
        } catch (Exception ex) {
            System.err.println("Error deleting User: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "deleteUser");
        }
        return outputId;  
    } 
    
    /**
     * 
     * @param username
     * @param pass
     * @return El id del usuario logeado si la autenticacion es exitosa, 0 si la autenticacion falla
     * @throws Exception 
     */
    public int login(String username, String pass) throws Exception {      
        Connection conn = DbConnection.conn();
        int id = 0;
        try {  
            
            PreparedStatement cmd = conn.prepareStatement("SELECT id FROM users WHERE " +
                                                          "username = '" + username + "' AND pass = '" + pass + "'");
            
            ResultSet rs = cmd.executeQuery();
            
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving data for Login: " + ex.getMessage());
        } finally {
            DbConnection.close(conn, "login");
        }
        return id; 
    }
}
