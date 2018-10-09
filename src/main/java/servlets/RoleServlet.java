/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.RoleDAO;
import hibernate.Role;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author kevin
 */
@Path("/roles")
public class RoleServlet {
    private static RoleDAO roleDao;

    public RoleServlet() {
    }
    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Role> getRoleList () {
        try {
            return new RoleDAO().getRoleList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Role> getActiveRoleList () {
        try {
            return new RoleDAO().getRoleList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
