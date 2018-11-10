/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.CareerDAO;
import dao.CareerTypeDAO;
import dao.FacultyDAO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import hibernate.Career;
import hibernate.CareerType;
import hibernate.Faculty;
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
@Path("/careers")
public class CareerServlet {
    private static CareerDAO carDao;
    
    @GET
    @Path("/full")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Career> getCareers (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
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
    public List<Career> getActiveCareers (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            return new CareerDAO().getCareerList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{careerId: \\d+}/full")
    @Produces({MediaType.APPLICATION_JSON})
    public Career getCareer (@PathParam("careerId") String careerId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            return new CareerDAO().getCareer(Integer.parseInt(careerId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("facultyId") String facultyId, 
            @FormParam("careerTypeId") String careerTypeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.CAREER);
        
        carDao = new CareerDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de carrera\n";
        }
        if (facultyId == null || facultyId.equals("")) {
            msg += " Facultad\n";
        }
        if (careerTypeId == null || careerTypeId.equals("")) {
            msg += " Tipo de carrera";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Faculty faculty = null;
        CareerType careerType = null;
        
        try {
            faculty = new FacultyDAO().get(Integer.parseInt(facultyId));
            if (faculty == null) {
                msg = "La facultad especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!faculty.getState()) {
                msg = "La facultad especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            careerType = new CareerTypeDAO().get(Integer.parseInt(careerTypeId));
            if (careerType == null) {
                msg = "El tipo de carrera especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!careerType.getState()) {
                msg = "El tipo de carrera especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            Career career = new Career();
            career.setName(name);
            career.setFaculty(faculty);
            career.setCareerType(careerType);
            career.setState(true);
            
            int status = carDao.add(career);
            
            if (status == DaoStatus.OK) {
                msg = "Carrera agregada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error increíblemente improbable al generar el código de la materia. Intente de nuevo.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo guardar la carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("facultyId") String facultyId, 
                            @FormParam("careerTypeId") String careerTypeId, @FormParam("state") String state,
                            @FormParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.CAREER);
        
        carDao = new CareerDAO(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre de carrera\n";
        }
        if (facultyId == null || facultyId.equals("")) {
            msg += " Facultad\n";
        }
        if (careerTypeId == null || careerTypeId.equals("")) {
            msg += " Tipo de carrera\n";
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
        CareerType careerType = null;
        
        try {
            faculty = new FacultyDAO().get(Integer.parseInt(facultyId));
            if (faculty == null) {
                msg = "La facultad especificada no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!faculty.getState()) {
                msg = "La facultad especificada no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            careerType = new CareerTypeDAO().get(Integer.parseInt(careerTypeId));
            if (careerType == null) {
                msg = "El tipo de carrera especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!careerType.getState()) {
                msg = "El tipo de carrera especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        Career career = null;
        
        try {
            career = carDao.get(Integer.parseInt(id));
            if (career == null) {
                msg = "La carrera a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        try {
            career.setName(name);
            career.setFaculty(faculty);
            career.setCareerType(careerType);
            career.setState(Boolean.valueOf(state));
            
            int status = carDao.update(career);
            
            if (status == DaoStatus.OK) {
                msg = "Carrera modificada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre de la carrera ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar la carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.CAREER);
        
        String msg = "";
        CareerDAO careerDao = new CareerDAO();
        
        Career career = null;
        
        try {
            career = careerDao.get(Integer.parseInt(id));
            
            if (career == null) {
                msg = "La carrera a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = careerDao.delete(career);
            
            if (status == DaoStatus.OK) {
                msg = "Carrera eliminada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("La carrera no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar la carrera.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
