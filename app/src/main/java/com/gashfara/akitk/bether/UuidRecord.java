package com.gashfara.akitk.bether;

/**
 * Created by akitk on 2016/05/08.
 */
public class UuidRecord {
    private String Proximity;
    private String Major;
    private String Minor;
    private String Url;

    public UuidRecord(String Proximity, String Major, String Minor, String Url) {
        this.Proximity = Proximity;
        this.Major = Major;
        this.Minor = Minor;
        this.Url = Url;
    }

    //Accessor
    public String getProximity() {return Proximity;}
    public String getMajor() {return Major;}
    public String getMinor() {return Minor;}
    public String getUrl() {return Url;}
}
