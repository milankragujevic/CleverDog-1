package com.soowin.cleverdog.info.service;

/**
 * Created by hxt on 2017/8/30.
 */

public class CoordinateBean {
    private String lat;
    private String lon;
    private int id;
    private String denoter_type;
    private String direction;
    private String direction_name;
    private String denoter_name;
    private int denoter_id;
    private String star_time;
    private String end_time;

    public String getDirection_name() {
        return direction_name;
    }

    public void setDirection_name(String direction_name) {
        this.direction_name = direction_name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }


    public String getDenoter_type() {
        return denoter_type;
    }

    public void setDenoter_type(String denoter_type) {
        this.denoter_type = denoter_type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDenoter_name() {
        return denoter_name;
    }

    public void setDenoter_name(String denoter_name) {
        this.denoter_name = denoter_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDenoter_id() {
        return denoter_id;
    }

    public void setDenoter_id(int denoter_id) {
        this.denoter_id = denoter_id;
    }

    public String getStar_time() {
        return star_time;
    }

    public void setStar_time(String star_time) {
        this.star_time = star_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
