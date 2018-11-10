/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import dao.PersonDao;
import dao.UserDAO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import helpers.Helpers;
import hibernate.Person;
import hibernate.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.security.DenyAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
    public List<User> getUsers (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
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
    public List<User> getActiveUsers (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
        try {
            return new UserDAO().getUserList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/people/noStudent")
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> getUsersWithNoStudent (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER, FilterRequest.STUDENT);
        try {
            List <User> users = new UserDAO().getUsersNiceWay("", true);
            List <User> usersWithNoStudent = new ArrayList<User>();
            for (User u : users) {
                if (u.getStudents().isEmpty()) {
                    u.setEmployees(null);
                    u.setStudents(null);
                    u.setPass(null);

                    u.getPerson().setUsers(null);
                    
                    usersWithNoStudent.add(u);
                }
            }
            return usersWithNoStudent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/people/noEmployee")
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> getUsersWithNoEmployee (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER, FilterRequest.EMPLOYEE);
        try {
            List <User> users = new UserDAO().getUsersNiceWay("", true);
            List <User> usersWithNoEmployee = new ArrayList<User>();
            for (User u : users) {
                if (u.getEmployees().isEmpty()) {
                    u.setEmployees(null);
                    u.setStudents(null);
                    u.setPass(null);

                    u.getPerson().setUsers(null);
                    
                    usersWithNoEmployee.add(u);
                }
            }
            return usersWithNoEmployee;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/people/detached")
    @Produces({MediaType.APPLICATION_JSON})
    public List<User> getDetacchedUsers (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
        try {
            return new UserDAO().getDetachedUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{userId: \\d+}/people")
    @Produces({MediaType.APPLICATION_JSON})
    public User getUser (@PathParam("userId") String userId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
        try {
            User user = new UserDAO().getUser(Integer.parseInt(userId), false);
            user.setImagePath(Helpers.downloadFileToString(user.getImagePath()));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response create (@FormDataParam("name") String name, @FormDataParam("surname") String surname, 
            @FormDataParam("phone") String phone, @FormDataParam("email") String email,
            @FormDataParam("dui") String dui, @FormDataParam("address") String address,
            @FormDataParam("pass") String pass, @FormDataParam("passConfirm") String passConfirm,
            @FormDataParam("imagePath") InputStream fileInputStream,
            @FormDataParam("imagePath") FormDataContentDisposition contentDispositionHeader,
            @FormDataParam("imagePath") FormDataBodyPart fileBody, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
        
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
            msg += " Confirmación de contraseña\n";
        }
        if (contentDispositionHeader == null || contentDispositionHeader.getFileName().equals("")) {
            msg += " Imagen de perfil";
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
        
        if (!fileBody.getMediaType().getType().equals("image")) {
            msg = "Solo se aceptan archivos del tipo imagen.";
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
        
        //Dirección de imagen
        String imagePath = Helpers.SERVER_IMAGE_LOCATION + username + ".png";
        
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
                user.setImagePath(imagePath);
                user.setPerson(person);
                user.setState(true);

                status = userDao.add(user);

                if (status == DaoStatus.OK) {
                    try {
                        //Subiendo imagen a servidor
                        Helpers.saveFile(fileInputStream, imagePath);
                        
                        msg = "Usuario agregado.";
                        return Response.ok(msg, "text/plain").build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = "Usuario agregado, pero su imagen no pudo ser guardada. Intente subirla de nuevo modificando el nuevo registro.";
                        return Response.ok(msg, "text/plain").build();
                    }
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
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response update (@FormDataParam("name") String name, @FormDataParam("surname") String surname, 
            @FormDataParam("phone") String phone, @FormDataParam("email") String email,
            @FormDataParam("dui") String dui, @FormDataParam("address") String address,
            @FormDataParam("pass") String pass, @FormDataParam("passConfirm") String passConfirm,
            @FormDataParam("state") String state, @FormDataParam("id") String id,
            @FormDataParam("imagePath") InputStream fileInputStream,
            @FormDataParam("imagePath") FormDataContentDisposition contentDispositionHeader,
            @FormDataParam("imagePath") FormDataBodyPart fileBody, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
        
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
        
        //La modificación de imagen es opcional
        if(contentDispositionHeader != null && !contentDispositionHeader.getFileName().equals("")) {
            if (!fileBody.getMediaType().getType().equals("image")) {
                msg = "Solo se aceptan archivos del tipo imagen.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        }
        
        User user = null;
        
        try {
            user = userDao.getUser(Integer.parseInt(id), true);
            if (user == null) {
                msg = "El usuario a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
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
                String imagePath = "";
                
                if (!(pass == null || pass.equals(""))) {
                    user.setPass(pass);
                }
                if(contentDispositionHeader != null && !contentDispositionHeader.getFileName().equals("")) {
                    imagePath = Helpers.SERVER_IMAGE_LOCATION + user.getUsername() + ".png";
                    user.setImagePath(imagePath);
                }
                user.setPerson(person);
                user.setState(Boolean.valueOf(state));

                status = userDao.update(user);

                if (status == DaoStatus.OK) {
                    try {
                        //Subiendo imagen a servidor, si aplica
                        if(contentDispositionHeader != null && !contentDispositionHeader.getFileName().equals("")) {
                            Helpers.saveFile(fileInputStream, imagePath);
                        }
                        
                        msg = "Usuario modificado.";
                        return Response.ok(msg, "text/plain").build();
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg = "Usuario modificado, pero su imagen no pudo ser guardada. Intente subirla de nuevo.";
                        return Response.ok(msg, "text/plain").build();
                    }
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
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete(@PathParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.USER);
        
        String msg = "";
        UserDAO userDao = new UserDAO();
        
        User user = null;
        
        try {
            user = userDao.getUserNiceWay(Integer.parseInt(id));
            
            if (user == null) {
                msg = "El usuario a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Person person = user.getPerson();
            
            //Guardando ruta de imagen para eliminarla posteriormente
            String imagePath = user.getImagePath();
            
            int status = userDao.delete(user);
            
            if (status == DaoStatus.OK) {
                
                status = new PersonDao().delete(person);
            
                if (status == DaoStatus.OK) {
                    //Eliminando foto de perfil del usuario
                    File file = new File(imagePath);
                    if (!file.getName().equals("")) {
                        file.delete();
                    }
                    
                    msg = "Usuario eliminado.";
                    return Response.ok(msg, "text/plain").build();
                }
                if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                    return Response.status(Response.Status.CONFLICT).entity("Error de contraint desconocido al eliminar a la persona.").type(MediaType.TEXT_PLAIN).build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error al eliminar a la persona.").type(MediaType.TEXT_PLAIN).build();
                }
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El usuario no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error al eliminar al usuario.").type(MediaType.TEXT_PLAIN).build();
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar el usuario.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
    
    @GET
    @Path("/image/{userId}")
    @Produces("image/png")
    public Response downloadImage(@PathParam("userId") String userId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR);
        try {
            User user = new UserDAO().getUser(Integer.parseInt(userId), false);
        
            StreamingOutput fileStream = Helpers.downloadFile(user.getImagePath());
            
            String[] fileNameParts = user.getImagePath().split("\\\\"); //Regex equivalente a "\"
            String fileName = fileNameParts[fileNameParts.length - 1];

            return Response
                    .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition","attachment; filename = " + fileName)
                    .build();
        
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
