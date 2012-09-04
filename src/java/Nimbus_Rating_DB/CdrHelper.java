/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Nimbus_Rating_DB;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

/**
 *
 * @author nimil.christopher
 */
public class CdrHelper {

    Session session = null;
    String[] processed = null;
    
    
    private static Logger myLogger = Logger.getLogger(CdrHelper.class.getName()); //Logger

    List<String> Types = new ArrayList<String>(); // Rejection Types
    public CdrHelper() {
        this.session = HibernateUtil.getSessionFactory().getCurrentSession();
    }

    public String[] getCategoryCountByType(String Type) {
        Long rejectedCdr = null;
        org.hibernate.Transaction tx = session.beginTransaction();
        Query q = session.createQuery("Select Distinct(rCdr.suspenseCategory) from RejectedCdr as rCdr WHERE rCdr.state = 99");
        Types = q.list();
        Integer ROW = Types.size();
        Integer COLUMN = 2;
        String elementReturned[][] = new String[ROW][COLUMN];
        List<String> count = new ArrayList<String>();
        try {
            for (int i = 0; i < ROW; i++) {
                for (String element : Types) {
                    elementReturned[i][0] = element;
                    q = session.createQuery("select count(category.id) from RejectedCdr as category where category.suspenseCategory = '" + element + "'");
                    rejectedCdr = (Long) q.uniqueResult();
                    elementReturned[i][1] = rejectedCdr.toString();
                    i++;
                }
            }
            String returnString[] = new String[elementReturned.length];
            for (int i = 0; i < elementReturned.length; i++) {
                returnString[i] = elementReturned[i][0] + "|" + elementReturned[i][1];
            }
            return returnString;
        } catch (Exception e) {
            e.printStackTrace();
            count.add(e.getMessage());
            return count.toArray(new String[0]);
        }
    }

    public List<RejectedCdr> getCategoryInfo(String suspenseType, int Index) {
        int Limit = 10;
        //String returnString[] = null;
        List<RejectedCdr> categoryInfo = new ArrayList<RejectedCdr>();
        try {
            Transaction tx = session.beginTransaction();
            Query q = session.createSQLQuery(" SELECT t.id, t.ORIGINAL_DATA as originalData, t.SUSPENSE_CATEGORY as suspenseCategory, t.BillingEntity as billingEntity, t.Destination as destination, t.DIAGNOSTIC_INFO as diagnosticInfo, t.REJECTION_REASON as rejectionReason FROM (  SELECT TOP 10 id FROM ( SELECT TOP " + Index * 10 + " id FROM rejected_cdr WHERE SUSPENSE_CATEGORY = '" + suspenseType + "' AND STATE = 99 ORDER BY id ASC) AS inside ORDER BY id DESC) AS outside INNER JOIN rejected_cdr AS t ON outside.id = t.id ORDER BY outside.id ASC  ").setResultTransformer(Transformers.aliasToBean(RejectedCdr.class));
            q.setMaxResults(Limit);
            categoryInfo = (List<RejectedCdr>) q.list();
            return categoryInfo;
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error : " + e.getMessage());
        }

    }

    public boolean updateBillId(String updateString) {
        try {
            String stringToUse = updateString;
            String AggregationId = "";
            String BillId = "";
            int updateCountAggregation;
            int updateCountRated;
            String splitString[] = stringToUse.split(",");

            Transaction tx = session.beginTransaction();
            for (int i = 0; i < splitString.length; i++) {
                String split[] = splitString[i].split("\\|");
                for (int j = 0; j < split.length; j++) {
                    if (j == 0) {
                        AggregationId = split[j];
                    } else if (j == 1) {
                        BillId = split[j];
                    }
                }
                updateCountAggregation = performUpdate("AggregatedCdr", AggregationId, BillId);
                updateCountRated = performUpdate("RatedCdr", AggregationId, BillId);
                if (updateCountAggregation > 0) {
                    myLogger.log(Level.INFO, "Updated {0} record/s on AggregatedCDR", updateCountAggregation);
                } else {
                    myLogger.log(Level.INFO, "No AggregationId's on AggregatedCdr to update!");
                }
                if (updateCountRated > 0) {
                    myLogger.log(Level.INFO, "Updated {0} record/s on RatedCDR", updateCountRated);
                } else {
                    myLogger.log(Level.INFO, "No AggregationId's on RatedCDR to update!");
                }

            }
            tx.commit();

            return true;
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            return false;
        }
    }

    public Boolean submitRejections(String[] Id, String SelectedType) {
        int executedNumber = 0;
        Transaction tx = session.beginTransaction();
        for (int i = 0; i < Id.length; i++) {
            String hqlUpdate = "update RejectedCdr set state = 0 where state = 99 and id = " + Id[i] + "and suspenseCategory = '" + SelectedType +"'";
            Query q = session.createQuery(hqlUpdate);
            executedNumber += q.executeUpdate();
        }
        tx.commit();
        if (executedNumber > 0) {
            myLogger.log(Level.INFO, "Submitted {0} Records", executedNumber);
            return true;
        } else {
            myLogger.log(Level.INFO, "No Rejections Submitted");
            return false;
        }
    }

    public Boolean submitAllRejections(String SelectedType) {
        int executedNumber = 0;
        String hqlUpdate = "update RejectedCdr set state = 0 WHERE state = 99 and suspenseCategory = '" + SelectedType + "'";
        Transaction tx = session.beginTransaction();
        Query q = session.createQuery(hqlUpdate);
        executedNumber = q.executeUpdate();
        tx.commit();
        if (executedNumber > 0) {
            myLogger.log(Level.INFO, "Submitted {0} Records", executedNumber);
            return true;
        } else {
            myLogger.log(Level.INFO, "No Rejections Submitted");
            return false;
        }
    }

    public List<RatedCdr> getEvents(String msn, int Index, boolean isHp){
        if(!isHp){
            int Limit = 3000;
            List<RatedCdr> events = new ArrayList<RatedCdr>();
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createSQLQuery("SELECT t.id, t.START_TIMESTAMP as startTimestamp, t.[USER] as [user], "
                    + "t.DESTINATION as destination, t.CALLER as caller, t.DURATION as duration, t.WHOLESALE_PRICE as wholesalePrice, "
                    + "t.TIME_BAND as timeBand, t.ZONE_DESTINATION as zoneDestination FROM (  SELECT TOP 3000 id FROM "
                    + "( SELECT TOP " + Index * 3000 + " id FROM rated_cdr WHERE MSN = '" + msn + "' ORDER BY id ASC)"
                    + " AS inside ORDER BY id DESC) AS outside INNER JOIN rated_cdr AS t ON outside.id = t.id "
                    + "ORDER BY outside.id ASC").setResultTransformer(Transformers.aliasToBean(RatedCdr.class));
            q.setMaxResults(Limit);
            events = (List<RatedCdr>) q.list();
            return events;
        }else{
            List<RatedCdr> events = new ArrayList<RatedCdr>();
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("select rCdr.zoneDestination as zoneDestination, sum(rCdr.retailPrice) as retailPrice, "
                    + "sum(rCdr.wholesalePrice) as wholesalePrice, count(rCdr.cdrGuid) as cdrGuid "
                    + "from RatedCdr rCdr where rCdr.msn='" + msn + "' group by rCdr.zoneDestination");
            for(Iterator it=q.iterate();it.hasNext();){ 
                RatedCdr rCdr = new RatedCdr();
                Object[] row = (Object[]) it.next();  
                rCdr.setZoneDestination(row[0].toString());
                rCdr.setRetailPrice(Double.valueOf(row[1].toString()));
                rCdr.setWholesalePrice(Double.valueOf(row[2].toString()));
                rCdr.setCdrGuid(row[3].toString());
                events.add(rCdr);
                row = null;
            }  
            return events;
        }
    }

    public Integer getEventCountByMsn(String msn){
        Long count = null;
        Transaction tx = session.beginTransaction();
        Query q = session.createQuery("Select count(rc.id) from RatedCdr as rc where rc.msn ='" + msn + "'");
        count = (Long)q.uniqueResult();
        return count.intValue();
    }
    
    
    public List<Geo> getGeo() {
        /*Connection con = Utilities.con;
        String sqlQuery = "Exec [Ormakeplus].[dbo].[sp_GetGeo]";
        ArrayList theResults = Utilities.getQueryResults(sqlQuery, con);*/
        List<Geo> geo = new ArrayList<Geo>();
        org.hibernate.Transaction tx = session.beginTransaction();
        Query q = session.createQuery("from Geo");
        geo = (List<Geo>)q.list();
        return geo;
    }
    
    List<NonGeo> getNonGeo() {
        List<NonGeo> nonGeo = new ArrayList<NonGeo>();
        org.hibernate.Transaction tx = session.beginTransaction();
        Query q = session.createQuery("from NonGeo");
        nonGeo = (List<NonGeo>)q.list();
        return nonGeo;
    }
    
    private int performUpdate(String dbTable, String AggId, String BillId) {
        String hqlUpdate = "";
        if (dbTable.equalsIgnoreCase("aggregatedCdr")) {
            hqlUpdate = "update " + dbTable + " set billId = :BillId , status = 'B' where aggregationId = :AggregationId";
        } else if (dbTable.equalsIgnoreCase("ratedCdr")) {
            hqlUpdate = "update " + dbTable + " set billId = :BillId , billStatus = 'B' where aggregationId = :AggregationId";
        }
        Query q = session.createQuery(hqlUpdate);
        q.setString("AggregationId", AggId);
        q.setString("BillId", BillId);
        int executeUpdate = q.executeUpdate();
        return executeUpdate;
    }

    

}


    /** Private Utility Methods here!
    //Not Currently Used!!
    private String[] process(String[] returnString) {
        String[] swapString = null;
        for (int i = 0; i < returnString.length; i++) {
            swapString = returnString[i].split(INPUT_DELIMITER);
            if (swapString.length < EXPECTED_FIELDS) {
                return null;
            } else {
                returnString[i] = null;
                returnString[i] = "CallReference|" + swapString[IDX_CALL_REF] + "," + "Caller|" + swapString[IDX_CALLER] + "," + "CallType|" + swapString[IDX_CALL_TYPE] + "," + "BillingEntity|" + swapString[IDX_GUIDING_KEY];
            }
        }
        return returnString;
    }*/

            /*returnString = new String[categoryInfo.size()];
            System.out.println("Size -- >" + categoryInfo.size());
            for (int i = 0; i < categoryInfo.size(); i++) {
            returnString[i] = categoryInfo.get(i).getOriginalData();
            System.out.println("-->" + returnString[i]);
            }
            returnString = process(returnString);
            return returnString;*/

/*public static String INPUT_DELIMITER = ",";
    public static final int EXPECTED_FIELDS = 19;
    public static final int cdrGUID = 0;
    public static final int ENTRY_DATE = 1;
    public static final int IDX_CALL_REF = 2;
    public static final int IDX_NETWORK = 3;
    public static final int IDX_CALL_TYPE = 4;
    public static final int IDX_REMOTE_NETWORK = 5;
    public static final int IDX_REMOTE_SWITCH = 6;
    public static final int IDX_DIRECTION = 7;
    public static final int IDX_PORTING_PREFIX = 8;
    public static final int IDX_CLI = 9;
    public static final int IDX_DEST = 10;
    public static final int IDX_USAGE_TYPE = 11;
    public static final int IDX_NUMBER_TYPE = 12;
    public static final int IDX_START_DATE = 13;
    public static final int IDX_DURATION = 14;
    public static final int IDX_GUIDING_KEY = 15;
    public static final int IDX_DISPLAY_NUMBER = 16;
    public static final int IDX_PLATFORM = 17;
    public static final int IDX_MACHINE = 18;
    public static final int IDX_LINK_NUMBER = 19;
    public static final int IDX_CALLER = 20;
    public static final int IDX_SERVICE = 4;*/