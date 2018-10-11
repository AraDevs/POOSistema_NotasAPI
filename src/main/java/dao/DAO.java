/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import helpers.DaoStatus;
import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author kevin
 */
public class DAO {
    
    public int add(Object entity) throws Exception {
        int response;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            ses.save(entity);
            ses.getTransaction().commit();
            response = DaoStatus.OK;
        } catch (ConstraintViolationException e) {
            response = DaoStatus.CONSTRAINT_VIOLATION;
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } catch (Exception e) {
            response = DaoStatus.ERROR;
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush(); //Ver porqué esto se chingaba
            ses.close();
        }
        return response;
    }
    
    public int update(Object entity) throws Exception {
        int response;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            ses.update(entity);
            ses.getTransaction().commit();
            response = DaoStatus.OK;
        } catch (ConstraintViolationException e) {
            response = DaoStatus.CONSTRAINT_VIOLATION;
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } catch (Exception e) {
            //Verificando si la excepción fue originada por una constraintviolation
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                response = DaoStatus.CONSTRAINT_VIOLATION;
            }
            else {
                response = DaoStatus.ERROR;
            }
            
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush(); //Ver porqué esto se chingaba
            ses.close();
        }
        return response;
    }
    
    public int delete(Object entity) {
        int response;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            ses.delete(entity);
            ses.getTransaction().commit();
            response = DaoStatus.OK;
        } catch (ConstraintViolationException e) {
            response = DaoStatus.CONSTRAINT_VIOLATION;
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } catch (Exception e) {
            //Verificando si la excepción fue originada por una constraintviolation
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                response = DaoStatus.CONSTRAINT_VIOLATION;
            }
            else {
                response = DaoStatus.ERROR;
            }
            
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush(); //Ver porqué esto se chingaba
            ses.close();
        }
        return response;
    }
}
