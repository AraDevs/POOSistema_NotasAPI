/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerTypeDAO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import hibernate.CareerType;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public List<CareerType> getCareerTypes (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
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
    public List<CareerType> getActiveCareerTypes (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            return new CareerTypeDAO().getCareerTypeList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerTypeId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON})
    public CareerType getCareerType (@PathParam("careerTypeId") String careerTypeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            return new CareerTypeDAO().getCareerType(Integer.parseInt(careerTypeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.CAREER);
        
        carTypeDao = new CareerTypeDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg = "Ingrese el nombre del tipo de carrera.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            CareerType careerType = new CareerType(name);
            careerType.setState(true);
            int status = carTypeDao.add(careerType);
            
            if (status == DaoStatus.OK) {
                msg = "Tipo de carrera ingresado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre del tipo de carrera ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo ingresar el tipo de carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("state") String state, 
            @FormParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.CAREER);
        
        carTypeDao = new CareerTypeDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de tipo de carrera\n";
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
        
        CareerType careerType = null;
        
        try {
            careerType = carTypeDao.get(Integer.parseInt(id));
            if (careerType == null) {
                msg = "El tipo de carrera a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            careerType.setName(name);
            careerType.setState(Boolean.valueOf(state));
            
            int status = carTypeDao.update(careerType);
            
            if (status == DaoStatus.OK) {
                msg = "Tipo de carrera modificado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre del tipo de carrera ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar el tipo de carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.CAREER);
        
        String msg = "";
        CareerTypeDAO careerTypeDao = new CareerTypeDAO();
        
        CareerType careerType = null;
        
        try {
            careerType = careerTypeDao.get(Integer.parseInt(id));
            
            if (careerType == null) {
                msg = "El tipo de carrera a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = careerTypeDao.delete(careerType);
            
            if (status == DaoStatus.OK) {
                msg = "Tipo de carrera eliminado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El tipo de carrera no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar el tipo de carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
