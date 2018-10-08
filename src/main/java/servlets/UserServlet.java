/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.UserDAO;
import hibernate.User;
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
@Path("/users")
public class UserServlet {
    
    private static UserDAO userDao;

    public UserServlet() {
    }
    
    @GET
    @Path("/people")
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> getUsers () {
        try {
            return new UserDAO().getUserList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/people/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> getActiveUsers () {
        try {
            return new UserDAO().getUserList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/people/detached")
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> getDetacchedUsers () {
        try {
            return new UserDAO().getDetachedUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{userId}/people")
    @Produces({MediaType.APPLICATION_JSON})
    public User getUser (@PathParam("userId") String userId) {
        try {
            return new UserDAO().getUser(Integer.parseInt(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
