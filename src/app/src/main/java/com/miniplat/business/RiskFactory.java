package com.miniplat.business;

import com.loopj.android.http.RequestParams;
import com.miniplat.core.IListHandler;
import com.miniplat.http.Http;
import com.miniplat.http.ListHandler;

import java.util.List;

public class RiskFactory {
    public static void getRiskEvents(double lng, double lat, final IListHandler<RiskEvent> handler) {
        RequestParams params = new RequestParams();
        params.put("lng", lng);
        params.put("lat", lat);
        Http.postResList("api/exec/execquery?app=home&ds=risk.gets_event_nearby", params, new ListHandler<RiskEvent>() {
            @Override
            public Class<RiskEvent> getClazz() {
                return RiskEvent.class;
            }
            @Override
            public void success(List<RiskEvent> res) {
                handler.callback(res);
            }
        }, true);
    }
}
