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
    public static Boolean isInt (String param) {
        try {
            double d = Double.parseDouble(param);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
