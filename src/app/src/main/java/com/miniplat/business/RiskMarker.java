package com.miniplat.business;

import com.amap.api.maps.model.Marker;

public class RiskMarker {
    private Marker marker;
    private RiskEvent event;

    public RiskEvent getEvent() {
        return event;
    }

    public Marker getMarker() {
        return marker;
    }

    public RiskMarker(RiskEvent event, Marker marker) {
        this.event = event;
        this.marker = marker;
    }
}
