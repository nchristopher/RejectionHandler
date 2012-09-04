/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author nimil.christopher
 */
public class Utilities {
    protected static Connection con = getConnection();
    protected static ArrayList<String> sqlResults = new ArrayList<String>();
    protected static String fields = "";
    protected static String aggregatefieldName = "";
    private static Statement stmt = null;
    private static ResultSet rs = null;

    protected static void setFields(String field) {
        fields = field;
    }

    protected static String getFields() {
        return fields;
    }

    //Specify database connection parameters here
    private static Connection getConnection() {
        try {
            Class.forName(Static_Variables.OR_DB_DRIVER);
            
            con = DriverManager.getConnection(Static_Variables.getOR_DB_CONNECTION_STRING(), Static_Variables.OR_DB_UN, Static_Variables.OR_DB_PW);
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (ClassNotFoundException cE) {
            System.out.println("Class Not Found Exception: " + cE.toString());
        }
        return con;
    }
    
    protected static ArrayList getQueryResults(String theSQLQuery, Connection theConnection) throws SQLException {
        ArrayList resultsList = new ArrayList();
        System.out.println("theSQLQuery=" + theSQLQuery);
        if (theConnection == null) {
            System.out.println("[FATAL] The connection is null");
        } else {
            stmt = theConnection.createStatement();
            rs = stmt.executeQuery(theSQLQuery);

            while (rs.next()) {
                String thisRecord = "";

                /*for (String thisField : theFields) {
                    String thisColumn =  rs.getString(thisField);

                    if (thisRecord.length() == 0) {
                        thisRecord = thisColumn;
                    } else {
                        thisRecord += "|" + thisColumn;
                    }
                }*/

                resultsList.add(thisRecord);
            }
        }

        return resultsList;
    }

}
