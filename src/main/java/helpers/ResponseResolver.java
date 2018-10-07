/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import hibernate.CourseTeacher;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 *
 * @author kevin
 */

@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
public class ResponseResolver implements ContextResolver<JAXBContext> {
    private JAXBContext ctx;

    public ResponseResolver() {
        try {
            this.ctx = JAXBContext.newInstance(

                        hibernate.CourseTeacher.class

                    );
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JAXBContext getContext(Class<?> type) {
        return (type.equals(Response.class) ? ctx : null);
    }
}
