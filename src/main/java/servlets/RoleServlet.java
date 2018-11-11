/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dao.EmployeeDAO;
import dao.RoleDAO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import helpers.JWTHelper;
import hibernate.Employee;
import hibernate.Role;
import io.jsonwebtoken.Claims;
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
@Path("/roles")
public class RoleServlet {
    private static RoleDAO roleDao;

    public RoleServlet() {
    }
    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Role> getRoleList (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.ROLE);
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
    public List<Role> getActiveRoleList (@Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.ROLE, FilterRequest.EMPLOYEE);
        try {
            return new RoleDAO().getRoleList("", true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/{roleId: \\d+}")
    @Produces({MediaType.APPLICATION_JSON})
    public Role getRole (@PathParam("roleId") String roleId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.ROLE);
        try {
            return new RoleDAO().getRole(Integer.parseInt(roleId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response create (@FormParam("role") String role, @FormParam("teach") String teach, 
            @FormParam("manageUsers") String manageUsers, @FormParam("manageStudents") String manageStudents, 
            @FormParam("manageEmployees") String manageEmployees, @FormParam("manageFaculties") String manageFaculties, 
            @FormParam("manageCareers") String manageCareers, @FormParam("manageCourses") String manageCourses, 
            @FormParam("managePensums") String managePensums, @FormParam("manageEvaluations") String manageEvaluations, 
            @FormParam("manageRoles") String manageRoles, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.ROLE);
        
        roleDao = new RoleDAO(false);
        
        String msg = "";
        if (role == null || role.equals("")) {
            msg += " Nombre del rol\n";
        }
        if (teach == null || teach.equals("")) {
            msg += " Permiso de enseñar\n";
        }
        if (manageUsers == null || manageUsers.equals("")) {
            msg += " Permiso de usuarios\n";
        }
        if (manageStudents == null || manageStudents.equals("")) {
            msg += " Permiso de estudiantes\n";
        }
        if (manageEmployees == null || manageEmployees.equals("")) {
            msg += " Permiso de empleados\n";
        }
        if (manageFaculties == null || manageFaculties.equals("")) {
            msg += " Permiso de facultades\n";
        }
        if (manageCareers == null || manageCareers.equals("")) {
            msg += " Permiso de carreras\n";
        }
        if (manageCourses == null || manageCourses.equals("")) {
            msg += " Permiso de materias\n";
        }
        if (managePensums == null || managePensums.equals("")) {
            msg += " Permiso de pensums\n";
        }
        if (manageEvaluations == null || manageEvaluations.equals("")) {
            msg += " Permiso de evaluaciones\n";
        }
        if (manageRoles == null || manageRoles.equals("")) {
            msg += " Permiso de roles";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if (!(Boolean.valueOf(teach) || Boolean.valueOf(manageUsers) || Boolean.valueOf(manageStudents) || Boolean.valueOf(manageEmployees)
                 || Boolean.valueOf(manageFaculties) || Boolean.valueOf(manageCareers) || Boolean.valueOf(manageCourses) || Boolean.valueOf(managePensums)
                 || Boolean.valueOf(manageEvaluations) || Boolean.valueOf(manageRoles))) {
            msg = "Dele al rol al menos un permiso.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (roleDao.getRoleByPermissions(Boolean.valueOf(teach), Boolean.valueOf(manageUsers), Boolean.valueOf(manageStudents), Boolean.valueOf(manageEmployees),
                 Boolean.valueOf(manageFaculties), Boolean.valueOf(manageCareers), Boolean.valueOf(manageCourses), Boolean.valueOf(managePensums),
                 Boolean.valueOf(manageEvaluations), Boolean.valueOf(manageRoles)) != null) {
                msg = "Ya existe un rol con los permisos especificados.";
                return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Role newRole = new Role();
            newRole.setRole(role);
            newRole.setTeach(Boolean.valueOf(teach));
            newRole.setManageUsers(Boolean.valueOf(manageUsers));
            newRole.setManageStudents(Boolean.valueOf(manageStudents));
            newRole.setManageEmployees(Boolean.valueOf(manageEmployees));
            newRole.setManageFaculties(Boolean.valueOf(manageFaculties));
            newRole.setManageCareers(Boolean.valueOf(manageCareers));
            newRole.setManageCourses(Boolean.valueOf(manageCourses));
            newRole.setManagePensums(Boolean.valueOf(managePensums));
            newRole.setManageEvaluations(Boolean.valueOf(manageEvaluations));
            newRole.setManageRoles(Boolean.valueOf(manageRoles));
            newRole.setState(true);
            
            int status = roleDao.add(newRole);
            
            if (status == DaoStatus.OK) {
                msg = "Rol ingresado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre del rol ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo ingresar el rol.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("role") String role, @FormParam("teach") String teach, 
            @FormParam("manageUsers") String manageUsers, @FormParam("manageStudents") String manageStudents, 
            @FormParam("manageEmployees") String manageEmployees, @FormParam("manageFaculties") String manageFaculties, 
            @FormParam("manageCareers") String manageCareers, @FormParam("manageCourses") String manageCourses, 
            @FormParam("managePensums") String managePensums, @FormParam("manageEvaluations") String manageEvaluations, 
            @FormParam("manageRoles") String manageRoles, @FormParam("state") String state, 
            @FormParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.ROLE);
        
        roleDao = new RoleDAO(false);
        
        String msg = "";
        if (role == null || role.equals("")) {
            msg += " Nombre del rol\n";
        }
        if (teach == null || teach.equals("")) {
            msg += " Permiso de enseñar\n";
        }
        if (manageUsers == null || manageUsers.equals("")) {
            msg += " Permiso de usuarios\n";
        }
        if (manageStudents == null || manageStudents.equals("")) {
            msg += " Permiso de estudiantes\n";
        }
        if (manageEmployees == null || manageEmployees.equals("")) {
            msg += " Permiso de empleados\n";
        }
        if (manageFaculties == null || manageFaculties.equals("")) {
            msg += " Permiso de facultades\n";
        }
        if (manageCareers == null || manageCareers.equals("")) {
            msg += " Permiso de carreras\n";
        }
        if (manageCourses == null || manageCourses.equals("")) {
            msg += " Permiso de materias\n";
        }
        if (managePensums == null || managePensums.equals("")) {
            msg += " Permiso de pensums\n";
        }
        if (manageEvaluations == null || manageEvaluations.equals("")) {
            msg += " Permiso de evaluaciones\n";
        }
        if (manageRoles == null || manageRoles.equals("")) {
            msg += " Permiso de roles\n";
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
        
        Role newRole = null;
        
        try {
            newRole = roleDao.get(Integer.parseInt(id));
            if (newRole == null) {
                msg = "El rol a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        if (!(Boolean.valueOf(teach) || Boolean.valueOf(manageUsers) || Boolean.valueOf(manageStudents) || Boolean.valueOf(manageEmployees)
                 || Boolean.valueOf(manageFaculties) || Boolean.valueOf(manageCareers) || Boolean.valueOf(manageCourses) || Boolean.valueOf(managePensums)
                 || Boolean.valueOf(manageEvaluations) || Boolean.valueOf(manageRoles))) {
            msg = "Dele al rol al menos un permiso.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            if (roleDao.getRoleByPermissionsExcludeId(Boolean.valueOf(teach), Boolean.valueOf(manageUsers), Boolean.valueOf(manageStudents), Boolean.valueOf(manageEmployees),
                 Boolean.valueOf(manageFaculties), Boolean.valueOf(manageCareers), Boolean.valueOf(manageCourses), Boolean.valueOf(managePensums),
                 Boolean.valueOf(manageEvaluations), Boolean.valueOf(manageRoles), Integer.parseInt(id)) != null) {
                msg = "Ya existe un rol con los permisos especificados.";
                return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (id.equals("1") || id.equals("2")) {
            msg = "Los roles por defecto del sistema no pueden ser modificados.";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        try {
            Claims tokenInfo = new JWTHelper().parseJWT(header.getRequestHeader("token").get(0));
            Employee requester = new EmployeeDAO().getEmployee(Integer.parseInt(tokenInfo.getId()));
            
            if (requester.getRole().getId() == Integer.parseInt(id)) {
                msg = "No puede modificar su propio rol.";
                return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            newRole.setRole(role);
            newRole.setTeach(Boolean.valueOf(teach));
            newRole.setManageUsers(Boolean.valueOf(manageUsers));
            newRole.setManageStudents(Boolean.valueOf(manageStudents));
            newRole.setManageEmployees(Boolean.valueOf(manageEmployees));
            newRole.setManageFaculties(Boolean.valueOf(manageFaculties));
            newRole.setManageCareers(Boolean.valueOf(manageCareers));
            newRole.setManageCourses(Boolean.valueOf(manageCourses));
            newRole.setManagePensums(Boolean.valueOf(managePensums));
            newRole.setManageEvaluations(Boolean.valueOf(manageEvaluations));
            newRole.setManageRoles(Boolean.valueOf(manageRoles));
            newRole.setState(Boolean.valueOf(state));
            
            int status = roleDao.update(newRole);
            
            if (status == DaoStatus.OK) {
                msg = "Rol modificado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El nombre del rol ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar el rol.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @DELETE
    @Path("/{id: \\d+}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response delete (@PathParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.ROLE);
        
        String msg = "";
        RoleDAO roleDao = new RoleDAO();
        
        Role role = null;
        
        try {
            role = roleDao.get(Integer.parseInt(id));
            
            if (role == null) {
                msg = "El rol a eliminar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            int status = roleDao.delete(role);
            
            if (status == DaoStatus.OK) {
                msg = "Rol eliminado.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("El rol no se puede eliminar, porque ya está en uso.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo eliminar el rol.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        
    }
}
