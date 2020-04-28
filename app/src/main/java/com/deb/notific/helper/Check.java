package com.deb.notific.helper;

import com.google.android.gms.maps.model.LatLng;
import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;

import java.util.List;

public class Check {
    private List <LatLng>mLatLngs;
    private Integer count;
    private LatLng mLatLng;
    private Boolean state = false;

    public Boolean getState() {
         count =  mLatLngs.size();
         for(int i =0;i<count;i++)
         {
             Polygon m = Polygon.Builder().addVertex(new Point(mLatLngs.get(i).latitude,mLatLngs.get(i).latitude))
                     .addVertex(new Point(mLatLngs.get(i+1).latitude,mLatLngs.get(i+1).latitude))
                     .addVertex(new Point(mLatLngs.get(i+2).latitude,mLatLngs.get(i+2).latitude))
                     .addVertex(new Point(mLatLngs.get(i+3).latitude,mLatLngs.get(i+3).latitude))
                     .addVertex(new Point(mLatLngs.get(i+4).latitude,mLatLngs.get(i+4).latitude)).build();
             Point mp = new Point(mLatLng.latitude,mLatLng.longitude);
               state =  m.contains(mp);
               if(state)
               {
                   state = false;
                   break;
               }
         }
        return state;
    }

    public Check() {
    }

    public List<LatLng> getLatLngs() {
        return mLatLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        mLatLngs = latLngs;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }
}
