/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

/**
 *
 * @author nimil.christopher
 */
public class Static_Variables {
    /**********************************************************************/
    /*                          SANDBOX SETTINGS                          */
    public static final String OR_HOST = "10.1.251.31";
    public static final String RATING_ENGINE_HOST = "10.1.251.34";
    public static final String OR_DB_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    public static final String OR_DB_CONNECTION_PREFIX = "jdbc:jtds:sqlserver://";
    public static final String OR_DB_HOST = "172.16.1.76";
    public static final String OR_DB_PORT = "1433";
    public static final String OR_DB_SCHEMA = "ORMakePlus";
    public static final String OR_DB_UN = "sa";
    public static final String OR_DB_PW = "5c0tl4nd";
    /*                          SANDBOX SETTINGS                          */
    /**********************************************************************/

  

    public static String getOR_DB_CONNECTION_STRING() {
        if (OR_DB_DRIVER.equals("com.mysql.jdbc.Driver")) {
            return Static_Variables.OR_DB_CONNECTION_PREFIX + Static_Variables.OR_DB_HOST + ":" + Static_Variables.OR_DB_PORT + "/" + Static_Variables.OR_DB_SCHEMA;
        } else if (OR_DB_DRIVER.equals("net.sourceforge.jtds.jdbc.Driver")) {
            return Static_Variables.OR_DB_CONNECTION_PREFIX + Static_Variables.OR_DB_HOST + ":" + Static_Variables.OR_DB_PORT + ";databaseName=" + Static_Variables.OR_DB_SCHEMA;
        } else {
            return "";
        }
    }
}
