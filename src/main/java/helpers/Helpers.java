/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.util.Calendar;

/**
 *
 * @author kevin
 */
public class Helpers {
    
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
}
