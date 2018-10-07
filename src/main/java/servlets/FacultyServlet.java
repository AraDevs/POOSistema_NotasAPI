/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import aaaaa.Faculty;
import controllers.FacultyController;
import dao.FacultyDAO;
import helpers.Helpers;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author kevin
 */
@Path("/faculties")
public class FacultyServlet {
    private static FacultyDAO fctDao;

    public FacultyServlet() {
    }
    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public FacultyDAO getJson() {
        fctDao = new FacultyDAO();
        return fctDao;
    }
    /*
    @GET
    @Path("/read/{param}")
    @Produces({MediaType.APPLICATION_JSON})
    public FacultyController getJson (@PathParam("param") String param) {
        fctDao = new FacultyController(param);
        return fctDao;
    }
    */
    @GET
    @Path("/readById/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Faculty getFacultyById (@PathParam("id") String id) {
        try {
            return new FacultyController().getFacultyById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    @POST
    @Path("/create")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name) {
        
        fctDao = new FacultyController(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg = "Must specify faculty name";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            String outputId = fctDao.add(name);
            if (!Helpers.isInt(outputId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(outputId)).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(outputId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            msg = "Faculty created with id " + outputId;
            return Response.ok(msg, "text/plain").build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not insert the faculty";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/update")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("state") String state, @FormParam("id") String id) {
        
        fctDao = new FacultyController(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " name";
        }
        if (state == null || state.equals("")) {
            msg += " state";
        }
        if (id == null || id.equals("")) {
            msg += " id";
        }
        
        if (!msg.equals("")) {
            msg = "Must specify following parameters:" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (fctDao.getFacultyById(id) == null) {
                msg = "Given id does not exist.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        
        try {
            String updFactId = fctDao.update(name, state, id);
            if (!Helpers.isInt(updFactId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(updFactId)).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(updFactId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            msg = "Faculty updated with id " + updFactId;
            return Response.ok(msg, "text/plain").build();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not update the faculty";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/delete/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete (@PathParam("id") String id) {
        
        String msg = "";
        fctDao = new FacultyController();
        
        try {
            if (fctDao.getFacultyById(id) == null) {
                msg = "Given id does not exist.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        try {
            String outputId = fctDao.delete(id);
            if (!Helpers.isInt(outputId)) {
                return Response.status(Response.Status.CONFLICT).entity(Helpers.parseSqlError(outputId)).type(MediaType.TEXT_PLAIN).build();
            }
            if (Integer.parseInt(outputId) <= 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
            msg = "Faculty with id " + outputId + " deleted";
            return Response.ok(msg, "text/plain").build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not delete the faculty";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }*/
}
