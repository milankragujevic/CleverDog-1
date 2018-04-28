package com.soowin.cleverdog.info.service;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * Created by hxt on 2017/9/2.
 * 禁止停车 坐标储存类
 */

public class NoStoppingBean {
    private int denoter_id;
    private List<LatLng> listLatlon;

    public List<LatLng> getListLatlon() {
        return listLatlon;
    }

    public void setListLatlon(List<LatLng> listLatlon) {
        this.listLatlon = listLatlon;
    }

    public int getDenoter_id() {
        return denoter_id;
    }

    public void setDenoter_id(int denoter_id) {
        this.denoter_id = denoter_id;
    }
}
