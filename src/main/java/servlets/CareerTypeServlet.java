/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerTypeDAO;
import hibernate.CareerType;
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
@Path("/careerTypes")
public class CareerTypeServlet {
    private static CareerTypeDAO carTypeDao;
    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerType> getCareerTypes () {
        try {
            return new CareerTypeDAO().getCareerTypeList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CareerType> getActiveCareerTypes () {
        try {
            return new CareerTypeDAO().getCareerTypeList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerTypeId}")
    @Produces({MediaType.APPLICATION_JSON})
    public CareerType getCareerType (@PathParam("careerTypeId") String careerTypeId) {
        try {
            return new CareerTypeDAO().getCareerType(Integer.parseInt(careerTypeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
