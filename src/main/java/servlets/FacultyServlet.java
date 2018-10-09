/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import hibernate.Faculty;
import controllers.FacultyController;
import dao.FacultyDAO;
import helpers.DaoStatus;
import helpers.Helpers;
import java.util.List;
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
    public List<Faculty> getFaculties () {
        try {
            return new FacultyDAO().getFacultyList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Faculty> getActiveFaculties () {
        try {
            return new FacultyDAO().getFacultyList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    @Path("/{facultyId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Faculty getFacultyById (@PathParam("facultyId") String facultyId) {
        try {
            return new FacultyDAO().getFaculty(Integer.parseInt(facultyId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name) {
        
        fctDao = new FacultyDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg = "Ingrese el nombre de la facultad.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            Faculty faculty = new Faculty(name);
            faculty.setState(true);
            int status = fctDao.add(faculty);
            
            if (status == DaoStatus.OK) {
                msg = "Facultad ingresada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre de la facultad ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo ingresar la facultad.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("state") String state, @FormParam("id") String id) {
        
        fctDao = new FacultyDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de facultad\n";
        }
        if (state == null || state.equals("")) {
            msg += " Estado\n";
        }
        if (id == null || id.equals("")) {
            msg += " ID";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Faculty faculty = null;
        
        try {
            faculty = fctDao.get(Integer.parseInt(id));
            if (faculty == null) {
                msg = "La facultad a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            faculty.setName(name);
            faculty.setState(Boolean.valueOf(state));
            
            int status = fctDao.update(faculty);
            
            if (status == DaoStatus.OK) {
                msg = "Facultad modificada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre de la facultad ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar la facultad.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    /*
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
