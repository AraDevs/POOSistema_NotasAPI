/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.HibernateUtil;
import hibernate.Role;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement( name = "roleDao" )
public class RoleDAO {
    private List<Role> roles;
    String param;
    
    public RoleDAO() {
        roles = new CopyOnWriteArrayList<Role>();        
        param = null;
    }
    
    public RoleDAO(String name) {        
        roles = new CopyOnWriteArrayList<Role>();        
        param = name;        
    }
    
    public RoleDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            roles = new CopyOnWriteArrayList<Role>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getRoles() {        
        try {            
            //roles = getRoleList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.roles;        
    }
    
    public void setRoles(List<Role> roles) {        
        this.roles = roles;        
    }
    
    public List<Role> getRoleList(String param, boolean active) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Role" + activeQuery;
            Query query = ses.createQuery(queryString, Role.class);
            roles = query.list();
            
            for (Role rc : roles) {
                rc.setEmployees(null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return roles;
    }
}
