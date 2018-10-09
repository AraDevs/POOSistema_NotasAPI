/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.CareerType;
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
@XmlRootElement ( name = "careerTypeDao") 
@XmlSeeAlso( {CareerType.class} )
public class CareerTypeDAO extends DAO {
    private List<CareerType> careerTypes;
    String param;
    
    public CareerTypeDAO() {
        careerTypes = new CopyOnWriteArrayList<CareerType>();        
        param = null;
    }
    
    public CareerTypeDAO(String name) {        
        careerTypes = new CopyOnWriteArrayList<CareerType>();        
        param = name;        
    }
    
    public CareerTypeDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            careerTypes = new CopyOnWriteArrayList<CareerType>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getCareerTypes() {        
        try {            
            careerTypes = getCareerTypeList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.careerTypes;        
    }
    
    public void setCareerTypes(List<CareerType> careerTypes) {        
        this.careerTypes = careerTypes;        
    }
    
    public List<CareerType> getCareerTypeList(String param, boolean active) throws Exception {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM CareerType" + activeQuery;
            Query query = ses.createQuery(queryString, CareerType.class);
            careerTypes = query.list();
            
            for (CareerType ct : careerTypes) {
                ct.setCareers(null);
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
        
        return careerTypes;
    }
    
    public CareerType getCareerType (int id) throws Exception {
        CareerType careerType = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM CareerType where id = :id";
            Query query = ses.createQuery(queryString, CareerType.class);
            query.setParameter("id", id);
            careerType = (CareerType) query.uniqueResult();
            
            careerType.setCareers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return careerType;
    }
    
    public CareerType get(int id) throws Exception {
        CareerType careerType = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            careerType = (CareerType) ses.get(CareerType.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return careerType;
    }
}
