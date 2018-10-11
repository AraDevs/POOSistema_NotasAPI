/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.PersonDao;
import helpers.DaoStatus;
import helpers.Helpers;
import hibernate.Person;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author kevin
 */
@Path("/people")
public class PersonServlet {
    private static PersonDao personDao;

    public PersonServlet() {
    }
    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Person> getPeople() {
        try {
            return new PersonDao().getPeopleList("", false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/active")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Person> getActivePeople() {
        try {
            return new PersonDao().getPeopleList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{personId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON})
    public Person getPerson(@PathParam("personId") String personId) {
        try {
            return new PersonDao().getPerson(Integer.parseInt(personId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("surname") String surname,
                            @FormParam("phone") String phone, @FormParam("email") String email,
                            @FormParam("dui") String dui, @FormParam("address") String address) {
        
        personDao = new PersonDao(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg = "Must specify person name";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            Person person = new Person(name, surname, phone, email, dui, address);
            int status = personDao.add(person);
            
            if (status == DaoStatus.OK) {
                msg = "Person created with id ";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Duplicated value").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error ocurred").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "Could not insert the person";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
}
