/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import controllers.UserController;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/")
public class UserServlet {
    
    private static UserController userCtrl;

    public UserServlet() {
    }
    
    @GET
    @Path("/read")
    @Produces({MediaType.APPLICATION_JSON})
    public UserController getJson() {
        userCtrl = new UserController();
        return userCtrl;
    }
}
