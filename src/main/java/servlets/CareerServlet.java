/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerDAO;
import hibernate.Career;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/careers")
public class CareerServlet {
    private static CareerDAO carDao;
    
    @GET
    @Path("/full")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Career> getCareers () {
        try {
            return new CareerDAO().getCareerList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/full/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Career> getActiveCareers () {
        try {
            return new CareerDAO().getCareerList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerId}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public Career getCareer (@PathParam("careerId") String careerId) {
        try {
            return new CareerDAO().getCareer(Integer.parseInt(careerId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
