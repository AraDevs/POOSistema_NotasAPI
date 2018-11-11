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
public class RoleDAO extends DAO {
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
    
    public Role getRole(int id) {
        
        Role role = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Role where id = :id";
            Query query = ses.createQuery(queryString, Role.class);
            query.setParameter("id", id);
            role = (Role)query.uniqueResult();
            
            role.setEmployees(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return role;
    }
    
    public Role getRoleByPermissions(Boolean teach, Boolean manageUsers, Boolean manageStudents, Boolean manageEmployees,
                                    Boolean manageFaculties, Boolean manageCareers, Boolean manageCourses, Boolean managePensums,
                                    Boolean manageEvaluations, Boolean manageRoles) {
        
        Role role = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Role where teach = :teach and manageUsers = :manageUsers and manageStudents = :manageStudents "
                    + "and manageEmployees = :manageEmployees and manageFaculties = :manageFaculties and manageCareers = :manageCareers "
                    + "and manageCourses = :manageCourses and managePensums = :managePensums and manageEvaluations = :manageEvaluations "
                    + "and manageRoles = :manageRoles";
            Query query = ses.createQuery(queryString, Role.class);
            query.setParameter("teach", teach);
            query.setParameter("manageUsers", manageUsers);
            query.setParameter("manageStudents", manageStudents);
            query.setParameter("manageEmployees", manageEmployees);
            query.setParameter("manageFaculties", manageFaculties);
            query.setParameter("manageCareers", manageCareers);
            query.setParameter("manageCourses", manageCourses);
            query.setParameter("managePensums", managePensums);
            query.setParameter("manageEvaluations", manageEvaluations);
            query.setParameter("manageRoles", manageRoles);
            role = (Role)query.uniqueResult();
            
            if (role != null) {
                role.setEmployees(null);
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
        
        return role;
    }
    
    public Role getRoleByPermissionsExcludeId(Boolean teach, Boolean manageUsers, Boolean manageStudents, Boolean manageEmployees,
                                    Boolean manageFaculties, Boolean manageCareers, Boolean manageCourses, Boolean managePensums,
                                    Boolean manageEvaluations, Boolean manageRoles, int roleId) {
        
        Role role = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Role where teach = :teach and manageUsers = :manageUsers and manageStudents = :manageStudents "
                    + "and manageEmployees = :manageEmployees and manageFaculties = :manageFaculties and manageCareers = :manageCareers "
                    + "and manageCourses = :manageCourses and managePensums = :managePensums and manageEvaluations = :manageEvaluations "
                    + "and manageRoles = :manageRoles and id != :roleId";
            Query query = ses.createQuery(queryString, Role.class);
            query.setParameter("teach", teach);
            query.setParameter("manageUsers", manageUsers);
            query.setParameter("manageStudents", manageStudents);
            query.setParameter("manageEmployees", manageEmployees);
            query.setParameter("manageFaculties", manageFaculties);
            query.setParameter("manageCareers", manageCareers);
            query.setParameter("manageCourses", manageCourses);
            query.setParameter("managePensums", managePensums);
            query.setParameter("manageEvaluations", manageEvaluations);
            query.setParameter("manageRoles", manageRoles);
            query.setParameter("roleId", roleId);
            role = (Role)query.uniqueResult();
            
            if (role != null) {
                role.setEmployees(null);
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
        
        return role;
    }
    
    public Role get(int id) throws Exception {
        Role role = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            role = (Role) ses.get(Role.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return role;
    }
}
