/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import dao.CorrectionDAO;
import dao.GradeDAO;
import dto.CorrectionDTO;
import helpers.DaoStatus;
import helpers.FilterRequest;
import helpers.Helpers;
import hibernate.Correction;
import hibernate.Grade;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.StreamingOutput;
import org.codehaus.jettison.json.JSONArray;

/**
 *
 * @author kevin
 */
@Path("/corrections")
public class CorrectionServlet {
    private static CorrectionDAO corrDao;

    public CorrectionServlet() {
    }
    
    @GET
    @Path("/byStudent/{studentId: \\d+}/course/owner")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CorrectionDTO> getCorrectionsByStudent (@PathParam("studentId") String studentId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.IS_STUDENT);
        try {
            return new CorrectionDAO().getCorrectionsDTOByStudent(Integer.parseInt(studentId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        /*try {
            return Response.ok().entity(new CorrectionDAO().getCorrectionsDTOByStudent(Integer.parseInt(studentId))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error en el servidor").type(MediaType.TEXT_PLAIN).build();
        }*/
        /*try {
            return new JSONArray(new CorrectionDAO().getCorrectionsDTOByStudent(Integer.parseInt(studentId)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
    }
    
    @GET
    @Path("/byEmployee/{employeeId: \\d+}/course/owner")
    @Produces({MediaType.APPLICATION_JSON})
    public List<CorrectionDTO> getCorrectionsByTeacher (@PathParam("employeeId") String employeeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.TEACH);
        try {
            return new CorrectionDAO().getCorrectionsDTOByTeacher(Integer.parseInt(employeeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byGrade/{gradeId: \\d+}/course/studentName")
    @Produces({MediaType.APPLICATION_JSON})
    public CorrectionDTO getCorrectionByGradeWithStudentName (@PathParam("gradeId") String gradeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.TEACH);
        try {
            return new CorrectionDAO().getCorrectionDTOByGradeWithStudentName(Integer.parseInt(gradeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @GET
    @Path("/byGrade/{gradeId: \\d+}/course/teacherName")
    @Produces({MediaType.APPLICATION_JSON})
    public CorrectionDTO getCorrectionByGradeWithEmployeeName (@PathParam("gradeId") String gradeId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.IS_STUDENT);
        try {
            return new CorrectionDAO().getCorrectionDTOByGradeWithTeacherName(Integer.parseInt(gradeId));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response create (@FormDataParam("description") String description, @FormDataParam("gradeId") String gradeId, 
                            @FormDataParam("filePath") InputStream fileInputStream,
                            @FormDataParam("filePath") FormDataContentDisposition contentDispositionHeader,
                            @FormDataParam("filePath") FormDataBodyPart fileBody, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.IS_STUDENT);
        
        corrDao = new CorrectionDAO(false);
        
        String msg = "";
        if (description == null || description.equals("")) {
            msg += " Descripción\n";
        }
        if (gradeId == null || gradeId.equals("")) {
            msg += " Registro de nota\n";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Grade grade = null;
        
        try {
            grade = new GradeDAO().getNiceWay(Integer.parseInt(gradeId));
            if (grade == null) {
                msg = "El registro de nota especificado no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            else if (!grade.getRegisteredCourse().getCourseState().equals("En curso")) {
                msg = "El registro de nota especificado no está disponible.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        //La subida de archivos es opcional
        if(contentDispositionHeader != null && !contentDispositionHeader.getFileName().equals("")) {
            if (!fileBody.getMediaType().getSubtype().equals("zip")) {
                msg = "Solo se aceptan archivos zip.";
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        }
        
        try {
            Correction correction = corrDao.getCorrectionByGrade(grade.getId());
            
            Boolean isNew = false; //Determina si se guardará o se modificará
            
            if (correction == null) {
                //Si no había un registro de corrección anterior
                correction = new Correction();
                isNew = true;
            }
            correction.setGrade(grade);
            correction.setCorrectionState("Pendiente");
            correction.setDescription(description);
            correction.setState(true);
            
            String filePath = Helpers.SERVER_CORRECTION_LOCATION + "CRGN" + grade.getId() + ".zip";;
                
            //Si se adjuntó un archivo
            if(contentDispositionHeader != null && !contentDispositionHeader.getFileName().equals("")) {
                correction.setFilePath(filePath);
            }
            else {
                correction.setFilePath(null);
            }
            
            int status = (isNew ? corrDao.add(correction) : corrDao.update(correction));
            
            if (status == DaoStatus.OK) {
                try {
                    //Subiendo archivo a servidor, si fue especificada
                    if(contentDispositionHeader != null && !contentDispositionHeader.getFileName().equals("")) {
                        Helpers.saveFile(fileInputStream, filePath);
                    }
                    //Si no se especificó un archivo, se eliminará el anterior en caso de que exista
                    else {
                        File file = new File(filePath);
                        if (!file.getName().equals("")) {
                            file.delete();
                        }
                    }

                    msg = "Solicitud de corrección enviada.";
                    return Response.ok(msg, "text/plain").build();
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = "La solicitud de corrección fue enviada, pero el archivo adjunto no pudo ser guardado. Intente crear la solicitud de nuevo.";
                    return Response.ok(msg, "text/plain").build();
                }
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Ocurrió un error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo guardar la solicitud de corrección.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @PUT
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN})
    public Response update (@FormParam("correctionState") String correctionState, @FormParam("id") String id, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.TEACH);
        
        corrDao = new CorrectionDAO(false);
        
        String msg = "";
        if (correctionState == null || correctionState.equals("")) {
            msg += " Estado de la solicitud\n";
        }
        if (id == null || id.equals("")) {
            msg += " ID";
        }
        
        if (!msg.equals("")) {
            msg = "Por favor ingrese todos los valores:\n" + msg + ".";
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        if(!(correctionState.equals("Aprobada") || correctionState.equals("Denegada"))) {
            msg = "El valor proporcionado para el estado de la solicitud no es válido. (Valores válidos: Aprobada, Denegada)";
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }
        
        Correction correction = null;
        
        try {
            correction = corrDao.getCorrectionNiceWay(Integer.parseInt(id));
            if (correction == null) {
                msg = "La solicitud de corrección a modificar no existe.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            if (!correction.getGrade().getRegisteredCourse().getCourseState().equals("En curso")) {
                msg = "La solicitud de corrección a modificar no está disponible.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        try {
            correction.setCorrectionState(correctionState);
            
            int status = corrDao.update(correction);
            
            if (status == DaoStatus.OK) {
                msg = "Solicitud procesada.";
                return Response.ok(msg, "text/plain").build();
            }
            if (status == DaoStatus.CONSTRAINT_VIOLATION) {
                return Response.status(Response.Status.CONFLICT).entity("Error de constraint desconocido.").type(MediaType.TEXT_PLAIN).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocurrió un error.").type(MediaType.TEXT_PLAIN).build();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        msg = "No se pudo modificar la solicitud de corrección.";
        
        return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
    }
    
    @GET
    @Path("/file/{correctionId}")
    @Produces("application/zip")
    public Response downloadFile(@PathParam("correctionId") String correctionId, @Context HttpHeaders header) {
        new FilterRequest(header, FilterRequest.OR, FilterRequest.IS_STUDENT, FilterRequest.TEACH);
        try {
            Correction correction = new CorrectionDAO().get(Integer.parseInt(correctionId));
        
            if (correction.getFilePath() == null) {
                String msg = "No se encontró el recurso solicitado.";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
            }
            
            StreamingOutput fileStream = Helpers.downloadFile(correction.getFilePath());
            
            String[] fileNameParts = correction.getFilePath().split("\\\\"); //Regex equivalente a "\"
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
