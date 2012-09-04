/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nimbus_Rating_DB;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebResult;
/**
 *
 * @author nimil.christopher
 */
@WebService()
public class Nimbus_Rating_DB {
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getCount")
    public String[] getCount(@WebParam(name = "Type")
    String Type) {
        String count[] = null;
        CdrHelper rch = new CdrHelper();
        count = rch.getCategoryCountByType(Type);
        return count;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getCategoryInfoByType")
    @WebResult
    public RejectedCdr[] getCategoryInfoByType(@WebParam(name = "suspenseType")
    String suspenseType, @WebParam(name = "Index")
    int Index) {
        //Implementation code here:
        List<RejectedCdr> result = null;
        RejectedCdr[] results = null;
        CdrHelper rch =  new CdrHelper();
        result = rch.getCategoryInfo(suspenseType,Index);
        results = new RejectedCdr[result.size()];
        results = (RejectedCdr[]) result.toArray(new RejectedCdr[result.size()]);
        return results;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "updateBillId")
    public Boolean updateBillId(@WebParam(name = "mappingString") String mappingString) {
        //Implementation code here:
        CdrHelper rch = new CdrHelper();
        return rch.updateBillId(mappingString);    
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "submitRejection")
    public Boolean submitRejection(@WebParam(name = "Id") String[] Id, @WebParam (name = "selectedType") String selectedType) {
        CdrHelper rch = new CdrHelper();
        return rch.submitRejections(Id,selectedType);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "submitAllRejections")
    public Boolean submitAllRejections(@WebParam(name = "selectedType") String selectedType) {
        CdrHelper rch = new CdrHelper();
        return rch.submitAllRejections(selectedType);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getEvents")
    @WebResult
    public RatedCdr[] getEvents(@WebParam(name = "msn") String msn, @WebParam(name = "Index")
    int Index, @WebParam(name = "isHp") boolean isHp) {
        //Implementation code here:
        List<RatedCdr> eventList = null;
        RatedCdr[] events = null;
        CdrHelper rch = new CdrHelper();
        eventList = rch.getEvents(msn,Index,isHp);
        events = new RatedCdr[eventList.size()];
        events = (RatedCdr[]) eventList.toArray(new RatedCdr[eventList.size()]);
        return events;
    }
    
    /**
     * Web service operation
     
    @WebMethod(operationName = "getHPEvents")
    @WebResult
    public RatedCdr[] getHPEvents(@WebParam(name = "msn") String msn) {
        //Implementation code here:
        List<RatedCdr> eventList = null;
        RatedCdr[] events = null;
        CdrHelper rch = new CdrHelper();
        eventList = rch.getEvents(msn,Index);
        events = new RatedCdr[eventList.size()];
        events = (RatedCdr[]) eventList.toArray(new RatedCdr[eventList.size()]);
        return events;
    }
    */
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getEventCountByMsn")
    public Integer getEventCountByMsn(@WebParam(name = "MSN") String MSN) {
        //Implementation code here:
        CdrHelper rch = new CdrHelper();
        return rch.getEventCountByMsn(MSN);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getGeo")
    public Geo[] getGeo() {
        Geo[] geo = null;
        List<Geo> geoList = null;
        CdrHelper rch = new CdrHelper();
        geoList = rch.getGeo();
        geo = new Geo[geoList.size()];
        geo = (Geo[]) geoList.toArray(new Geo[geoList.size()]);
        return geo;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getNonGeo")
    public NonGeo[] getNonGeo() {
        NonGeo[] nGeo = null;
        List<NonGeo> nGeoList = null;
        CdrHelper rch = new CdrHelper();
        nGeoList = rch.getNonGeo();
        nGeo = new NonGeo[nGeoList.size()];
        nGeo = (NonGeo[]) nGeoList.toArray(new NonGeo[nGeoList.size()]);
        return nGeo;
    }
}
