/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

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
}
