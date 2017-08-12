package com.softDev.tamilentertainment.model;

/**
 * Created by SMARTTECHX on 7/31/2017.
 */
public class FavouriteItem {

    String stationListName;
    Integer stationListImage;
    int id;

    public FavouriteItem(String stationName, Integer stationImage, int id)
    {
        this.stationListImage=stationImage;
        this.stationListName=stationName;
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getstationName()
    {
        return stationListName;
    }
    public Integer getstationImage()
    {
        return stationListImage;
    }
    public void setstationName(String name) {
        this.stationListName = name;
    }

    public void setstationImage(Integer Image) {
        this.stationListImage = Image;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FavouriteItem other = (FavouriteItem) obj;
        if (id != other.id)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Station [id=" + id + ", StationName=" + stationListName + ", StationImage="
                + stationListImage+"]";
    }

}
