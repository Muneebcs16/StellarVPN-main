package com.stellar.vpn.New;

import com.google.gson.annotations.SerializedName;

public class GetIpLocation
{
    @SerializedName("zip")
    private String zip;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    @SerializedName("org")
    private String org;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("regionName")
    private String regionName;

    @SerializedName("isp")
    private String isp;

    @SerializedName("query")
    private String query;

    @SerializedName("lon")
    private String lon;

    @SerializedName("as")
    private String as;

    @SerializedName("countryCode")
    private String countryCode;

    @SerializedName("region")
    private String region;

    @SerializedName("lat")
    private String lat;

    @SerializedName("status")
    private String status;

    public String getZip ()
    {
        return zip;
    }

    public void setZip (String zip)
    {
        this.zip = zip;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }

    public String getCity ()
    {
        return city;
    }

    public void setCity (String city)
    {
        this.city = city;
    }

    public String getOrg ()
    {
        return org;
    }

    public void setOrg (String org)
    {
        this.org = org;
    }

    public String getTimezone ()
    {
        return timezone;
    }

    public void setTimezone (String timezone)
    {
        this.timezone = timezone;
    }

    public String getRegionName ()
    {
        return regionName;
    }

    public void setRegionName (String regionName)
    {
        this.regionName = regionName;
    }

    public String getIsp ()
    {
        return isp;
    }

    public void setIsp (String isp)
    {
        this.isp = isp;
    }

    public String getQuery ()
    {
        return query;
    }

    public void setQuery (String query)
    {
        this.query = query;
    }

    public String getLon ()
    {
        return lon;
    }

    public void setLon (String lon)
    {
        this.lon = lon;
    }

    public String getAs ()
    {
        return as;
    }

    public void setAs (String as)
    {
        this.as = as;
    }

    public String getCountryCode ()
    {
        return countryCode;
    }

    public void setCountryCode (String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getRegion ()
    {
        return region;
    }

    public void setRegion (String region)
    {
        this.region = region;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

}