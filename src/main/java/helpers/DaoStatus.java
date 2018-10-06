/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

/**
 * Esta clase contendrpa constantes que los DAO usaran como pseudo-status code,
 * para comunicarse con la clase que los invoque
 * @author kevin
 */
public class DaoStatus {
    public static final int ERROR = 0;
    public static final int OK = 1;
    public static final int CONSTRAINT_VIOLATION = 2;
}
