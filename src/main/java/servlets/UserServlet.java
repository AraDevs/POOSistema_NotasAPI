/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.PersonDao;
import dao.UserDAO;
import helpers.DaoStatus;
import hibernate.Person;
import hibernate.User;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;
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
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("name") String name, @FormParam("surname") String surname, 
            @FormParam("phone") String phone, @FormParam("email") String email,
            @FormParam("dui") String dui, @FormParam("address") String address,
            @FormParam("pass") String pass, @FormParam("passConfirm") String passConfirm) {
        
        userDao = new UserDAO(false);
        PersonDao persDao = new PersonDao(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre\n";
        }
        if (surname == null || surname.equals("")) {
            msg += " Apellido\n";
        }
        if (phone == null || phone.equals("")) {
            msg += " Teléfono\n";
        }
        if (email == null || email.equals("")) {
            msg += " Correo electrónico\n";
        }
        if (dui == null || dui.equals("")) {
            msg += " DUI\n";
        }
        if (address == null || address.equals("")) {
            msg += " Dirección\n";
        }
        if (pass == null || pass.equals("")) {
            msg += " Contraseña\n";
        }
        if (passConfirm == null || passConfirm.equals("")) {
            msg += " Confirmación de contraseña";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(Pattern.matches("^\\d{8}-\\d", dui))) {
            msg = "El formato de DUI no es válido (00000000-0).";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(Pattern.matches("^[267]\\d{3}\\d{4}", phone))) {
            msg = "El formato de teléfono no es válido (00000000).";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(Pattern.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", email))) {
            msg = "El correo electrónico no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (persDao.getPersonByDui(dui, 0) != null) {
                msg = "El DUI especificado ya está en uso.";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            if (persDao.getPersonByEmail(email, 0) != null) {
                msg = "El correo electrónico especificado ya está en uso.";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
        }
        
        if (pass.length() < 6) {
            msg = "La contraseña debe contener al menos 6 caracteres.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(pass.equals(passConfirm))) {
            msg = "Las contraseñas ingresadas no coinciden.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        //Generando username
        String firstLetter = name.substring(0, 1).toUpperCase();
        String secondLetter = surname.substring(0, 1).toUpperCase();
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        int index = 0;
        try {
            index = userDao.getYearIndex();
        } catch (Exception e) {
            msg = e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        String username = firstLetter + secondLetter + year + String.format("%04d", index + 1);
        
        try {
            Person person = new Person();
            person.setName(name);
            person.setSurname(surname);
            person.setPhone(phone);
            person.setEmail(email);
            person.setDui(dui);
            person.setAddress(address);
            person.setState(true);
            
            int status = persDao.add(person);
            
            if (status == DaoStatus.OK) {
                person = persDao.getPersonByEmail(email, 0);
                
                User user = new User();
                user.setUsername(username);
                user.setPass(pass);
                user.setImagePath("");
                user.setPerson(person);
                user.setState(true);

                status = userDao.add(user);

                if (status == DaoStatus.OK) {
                    msg = "Usuario agregado.";
                    return Response.ok(msg, "text/plain").build();
                }
                if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                    return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido al agregar al usuario.").type(MediaType.TEXT_PLAIN).build();
                }
                else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error al agregar al usuario.").type(MediaType.TEXT_PLAIN).build();
                }
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido al agregar a la persona.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error al agregar a la persona.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo agregar al usuario.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("name") String name, @FormParam("surname") String surname, 
            @FormParam("phone") String phone, @FormParam("email") String email,
            @FormParam("dui") String dui, @FormParam("address") String address,
            @FormParam("pass") String pass, @FormParam("passConfirm") String passConfirm,
            @FormParam("state") String state, @FormParam("id") String id) {
        
        userDao = new UserDAO(false);
        PersonDao persDao = new PersonDao(false);
        
        String msg = "";
        if (name == null || name.equals("")) {
            msg += " Nombre\n";
        }
        if (surname == null || surname.equals("")) {
            msg += " Apellido\n";
        }
        if (phone == null || phone.equals("")) {
            msg += " Teléfono\n";
        }
        if (email == null || email.equals("")) {
            msg += " Correo electrónico\n";
        }
        if (dui == null || dui.equals("")) {
            msg += " DUI\n";
        }
        if (address == null || address.equals("")) {
            msg += " Dirección\n";
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
        
        if (!(Pattern.matches("^\\d{8}-\\d", dui))) {
            msg = "El formato de DUI no es válido (00000000-0).";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(Pattern.matches("^[267]\\d{3}\\d{4}", phone))) {
            msg = "El formato de teléfono no es válido (00000000).";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(Pattern.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", email))) {
            msg = "El correo electrónico no es válido.";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (persDao.getPersonByDui(dui, Integer.parseInt(id)) != null) {
                msg = "El DUI especificado ya está en uso.";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            if (persDao.getPersonByEmail(email, Integer.parseInt(id)) != null) {
                msg = "El correo electrónico especificado ya está en uso.";
                return Response.status(Response.Status.CONFLICT).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
        }
        
        //La modificación de contraseña es opcional
        if (!(pass == null || pass.equals("")) && !(passConfirm == null || passConfirm.equals(""))) {
            if (pass.length() < 6) {
                msg = "La contraseña debe contener al menos 6 caracteres.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }

            if (!(pass.equals(passConfirm))) {
                msg = "Las contraseñas ingresadas no coinciden.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        }
        
        User user = null;
        
        try {
            user = userDao.getUser(Integer.parseInt(id));
            if (user == null) {
                msg = "El usuario a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {}
        
        try {
            Person person = user.getPerson();
            person.setName(name);
            person.setSurname(surname);
            person.setPhone(phone);
            person.setEmail(email);
            person.setDui(dui);
            person.setAddress(address);
            person.setState(Boolean.valueOf(state));
            
            int status = persDao.update(person);
            
            if (status == DaoStatus.OK) {
                if (!(pass == null || pass.equals(""))) {
                    user.setPass(pass);
                }
                user.setImagePath("");
                user.setPerson(person);
                user.setState(Boolean.valueOf(state));

                status = userDao.update(user);

                if (status == DaoStatus.OK) {
                    msg = "Usuario modificado.";
                    return Response.ok(msg, "text/plain").build();
                }
                if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                    return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido al modificar al usuario.").type(MediaType.TEXT_PLAIN).build();
                }
                else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error al modificar al usuario.").type(MediaType.TEXT_PLAIN).build();
                }
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido al modificar a la persona.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error al modificar a la persona.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar al usuario.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
}
