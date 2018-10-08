/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Employee;
import hibernate.HibernateUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "employeeDao") 
@XmlSeeAlso( {Employee.class} )
public class EmployeeDAO {
    private List<Employee> employees;
    String param;
    
    public EmployeeDAO() {
        employees = new CopyOnWriteArrayList<Employee>();        
        param = null;
    }
    
    public EmployeeDAO(String name) {        
        employees = new CopyOnWriteArrayList<Employee>();        
        param = name;        
    }
    
    public EmployeeDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            employees = new CopyOnWriteArrayList<Employee>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getEmployees() {        
        try {            
            //employees = getEmployeeList(param);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.employees;        
    }
    
    public void setEmployees(List<Employee> employees) {        
        this.employees = employees;        
    }
    
    public List<Employee> getEmployeeList(String param, boolean active) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where e.state = true and u.state = true and p.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Employee e join fetch e.user u join fetch u.person p "
                    + "join fetch e.role r" + activeQuery;
            Query query = ses.createQuery(queryString, Employee.class);
            employees = query.list();
            
            for (Employee e : employees) {
                e.setCourseTeachers(null);
                e.getRole().setEmployees(null);
                e.getUser().setEmployees(null);
                e.getUser().setStudents(null);
                e.getUser().getPerson().setUsers(null);
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
        
        return employees;
    }
    
    public List<Employee> getEmployeeByStudent(int studentId) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT e FROM Employee e join fetch e.user u join fetch u.person p "
                    + "join fetch e.courseTeachers ct join fetch ct.registeredCourses rc "
                    + "join fetch rc.student s where s.id = :studentId and rc.courseState = 'En curso'";
            Query query = ses.createQuery(queryString, Employee.class);
            query.setParameter("studentId", studentId);
            employees = query.list();
            
            for (Employee e : employees) {
                e.setRole(null);
                e.setCourseTeachers(null);
                e.getUser().setStudents(null);
                e.getUser().setPass(null);
                e.getUser().setEmployees(null);
                e.getUser().getPerson().setUsers(null);
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
        
        return employees;
    }
    
    public Employee getEmployeeByRegisteredCourse(int regCourseId) {
        
        Employee employee = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Employee e join fetch e.user u join fetch u.person p "
                    + "join fetch e.courseTeachers ct join fetch ct.registeredCourses rc "
                    + "where rc.id = :regCourseId";
            Query query = ses.createQuery(queryString, Employee.class);
            query.setParameter("regCourseId", regCourseId);
            employee = (Employee) query.uniqueResult();
            
            employee.setRole(null);
            employee.setCourseTeachers(null);
            employee.getUser().setStudents(null);
            employee.getUser().setPass(null);
            employee.getUser().setEmployees(null);
            employee.getUser().getPerson().setUsers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return employee;
    }
    
    public Employee getEmployee(int id) {
        
        Employee employee = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Employee e join fetch e.user u join fetch u.person p "
                    + "join fetch e.role r where e.id = :id";
            Query query = ses.createQuery(queryString, Employee.class);
            query.setParameter("id", id);
            employee = (Employee) query.uniqueResult();
            
            employee.setCourseTeachers(null);
            employee.getRole().setEmployees(null);
            employee.getUser().setEmployees(null);
            employee.getUser().setStudents(null);
            employee.getUser().getPerson().setUsers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return employee;
    }
    
    public Employee getTeacher(int employeeId) {
        Employee employee = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Employee e join fetch e.user u join fetch u.person p "
                    + "join fetch e.courseTeachers ct join fetch ct.course c where e.id = :employeeId";
            Query query = ses.createQuery(queryString, Employee.class);
            query.setParameter("employeeId", employeeId);
            employee = (Employee) query.uniqueResult();
            
            employee.setRole(null);
            employee.getUser().setStudents(null);
            employee.getUser().setPass(null);
            employee.getUser().setEmployees(null);
            employee.getUser().getPerson().setUsers(null);

            for (Object ct : employee.getCourseTeachers()) {
                ((CourseTeacher) ct).setEmployee(null);
                ((CourseTeacher) ct).setRegisteredCourses(null);
                ((CourseTeacher) ct).getCourse().setCareerCourses(null);
                ((CourseTeacher) ct).getCourse().setCourse(null);
                ((CourseTeacher) ct).getCourse().setCourses(null);
                ((CourseTeacher) ct).getCourse().setEvaluations(null);
                ((CourseTeacher) ct).getCourse().setFaculty(null);
                ((CourseTeacher) ct).getCourse().setCourseTeachers(null);
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
        return employee;
    }
}
