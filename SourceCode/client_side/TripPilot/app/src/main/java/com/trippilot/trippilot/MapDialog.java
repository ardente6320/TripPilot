package com.trippilot.trippilot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

/**
 * Created by arden on 2018-11-07.
 */
public class MapDialog extends DialogFragment implements MapView.MapViewEventListener{
    List<Location> location;

    public void setLocation(List<Location> location){this.location = location;}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_view,container,false);
        ((TextView)v.findViewById(R.id.map_title)).setText("장소 위치");
        MapView mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map);
        mapView.setMapViewEventListener(this);
        mapViewContainer.addView(mapView);
        return v;
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        int size = location.size();
        MapPoint mpoint = null;
        MapPOIItem[] marker =new MapPOIItem[size];
        for(int i =0; i<size; i++) {
            Location loc = location.get(i);
            marker[i] = new MapPOIItem();
            mpoint = MapPoint.mapPointWithGeoCoord(loc.getMapy(), loc.getMapx());
            mapView.setMapCenterPoint(mpoint, true);
            marker[i].setItemName(loc.getName());
            marker[i].setShowCalloutBalloonOnTouch(true);
            marker[i].setTag(i);
            marker[i].setMapPoint(mpoint);
            marker[i].setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker[i].setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        }
        mapView.addPOIItems(marker);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }
}
