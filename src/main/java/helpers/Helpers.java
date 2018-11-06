/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

/**
 *
 * @author kevin
 */
public class Helpers {
    
    public static final String SERVER_IMAGE_LOCATION = "X:\\Documentos\\NetBeansProjects\\gradecheckFiles\\profilePictures\\";
    public static final String SERVER_CORRECTION_LOCATION = "X:\\Documentos\\NetBeansProjects\\gradecheckFiles\\corrections\\";
    
    //
    public static Boolean isInt (String param) {
        try {
            double d = Double.parseDouble(param);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
    
    public static String parseSqlError (String param) {
        param = param.replace("sqlError: ", "");
        
        switch (Integer.parseInt(param)) {
            case 1062:
                return "Duplicated value. Could not complete operation";
            case 1451:
                return "Cannot delete record, parent row conflict";
            default:
                return "SQL Unhandled Error: " + param;
        }
    }
    
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
    public static String getCurrentSemester() {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String semester = "";
        
        switch(month) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                semester = "1";
                break;
            case 5:
            case 6:
                semester = "Interciclo";
                break;
            case 7:
            case 8:
            case 9:
            case 10:
                semester = "2";
                break;
            case 11:
                semester = "Ninguno";
                break;
        }
        
        return semester;
    }
    
    public static String getDateSemester(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        String semester = "";
        
        switch(month) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                semester = "1";
                break;
            case 5:
            case 6:
                semester = "Interciclo";
                break;
            case 7:
            case 8:
            case 9:
            case 10:
                semester = "2";
                break;
            case 11:
                semester = "Ninguno";
                break;
        }
        
        return semester;
    }
    
    public static final String SEMESTER1 = "1";
    public static final String SEMESTER2 = "2";
    public static final String SEMESTER_INTER = "Interciclo";
    
    public static Boolean isPastSemester (int courseYear, String courseSemester) {
        
        Boolean response = false;
        
        if (courseYear < Calendar.getInstance().get(Calendar.YEAR)) {
            response = true;
        }
        else if (courseYear == Calendar.getInstance().get(Calendar.YEAR)) {
            int month = Calendar.getInstance().get(Calendar.MONTH);
            
            if (month > 6 && (courseSemester.equals(SEMESTER1) || courseSemester.equals(SEMESTER_INTER))) {
                response = true;
            }
            if (month > 4 && courseSemester.equals(SEMESTER1)) {
                response = true;
            }
        }
        
        return response;
    }
    
    public static void saveFile(InputStream uploadedInputStream, String serverLocation) throws IOException {
        
        OutputStream outputStream = new FileOutputStream(new File(serverLocation));
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = uploadedInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        outputStream.flush();
        outputStream.close();
    }
    
    public static StreamingOutput downloadFile(final String pathString) {
        StreamingOutput fileStream =  new StreamingOutput()
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException
            {
                try
                {
                    java.nio.file.Path path = Paths.get(pathString);
                    //byte[] data = Files.readAllBytes(path);
                    byte[] data = Base64.getEncoder().encode(Files.readAllBytes(path));
                    output.write(data);
                    output.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        return fileStream;
    }
    
    public static String downloadFileToString(final String pathString) throws IOException {
        StreamingOutput fileStream =  new StreamingOutput()
        {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException
            {
                try
                {
                    java.nio.file.Path path = Paths.get(pathString);
                    //byte[] data = Files.readAllBytes(path);
                    byte[] data = Base64.getEncoder().encode(Files.readAllBytes(path));
                    output.write(data);
                    output.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        fileStream.write(output);
        String string = new String(output.toByteArray(), "UTF-8");
        
        return string;
    }
}
