package com.example.designapptest.Views;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.designapptest.Controller.searchMapViewController;
import com.example.designapptest.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.GeocodeResult;
import com.here.android.mpa.search.ResultListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;


public class searchMapView extends Fragment implements View.OnClickListener, PositioningManager.OnPositionChangedListener{

    View layout;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE};
    Map map = null;
    private SupportMapFragment mapFragment = null;
    MapMarker defaultMarker = null;

    //Bi???n l??u t???a ????? zoom ?????n v??? tr?? tr??n map
    double latitude = 10.776927;
    double longtitude = 106.637588;

    ProgressBar progessBarLoadMap;
    ProgressBar progessBarLoadRoom;

    searchMapViewController searchMapViewController;

    RecyclerView recyclerRoom;

    CardView cardViewListRoom;

    FloatingSearchView searchView;
    MapMarker searchMarker = null;

    FloatingActionButton btnTopLocation;
    FloatingActionButton btnNearLocation;

    FloatingActionsMenu btnMenuExpander;

    // positioning manager instance
    private PositioningManager mPositioningManager;

    //Bi???n l??u v??? tr?? hi???n t???i
    GeoCoordinate currentLocation;

    private boolean isFirstLoad=true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPositioningManager != null) {
            mPositioningManager.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPositioningManager != null) {
            mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_search_map_view, container, false);
        initControl();
        initMapEngine();
        searchMapViewController = new searchMapViewController(getContext());
        return layout;
    }

    //H??m ki???m tra quy???n
    protected void checkPermissions() {

        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(getContext(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    //H??m t??m fragment ch???a map
    private SupportMapFragment getMapFragment() {
        //Ch?? ?? ??? trong fragment mu???n t??m fragment th?? d??ng h??m n??y
        return (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initControl(){
        progessBarLoadMap= layout.findViewById(R.id.progess_bar_load_map);
        //?????i m??u progessBar
        progessBarLoadMap.getIndeterminateDrawable().setColorFilter(Color.parseColor("#00DDFF"),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        progessBarLoadRoom=layout.findViewById(R.id.progess_bar_load_room);
        //?????i m??u progessBar
        progessBarLoadRoom.getIndeterminateDrawable().setColorFilter(Color.parseColor("#00DDFF"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        //???n
        progessBarLoadRoom.setVisibility(View.GONE);

        progessBarLoadMap.setVisibility(View.VISIBLE);

        recyclerRoom = layout.findViewById(R.id.recycler_room);

        cardViewListRoom = layout.findViewById(R.id.cardViewListRoom);

        btnTopLocation = layout.findViewById(R.id.btn_top_location);
        btnTopLocation.setOnClickListener(this);

        btnNearLocation = layout.findViewById(R.id.btn_near_location);
        btnNearLocation.setOnClickListener(this);

        btnMenuExpander = layout.findViewById(R.id.btn_menu_expander);

        searchView = layout.findViewById(R.id.floating_search_view);

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                //Zoom map ?????n ?????a ch??? c???n t??m
                Search(currentQuery);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                break;
        }
    }

    //Th??m s??? ki???n khi thay ?????i v??? tr??
    private void addEvenTracking(){
        //node them vao
        mPositioningManager = PositioningManager.getInstance();

        mPositioningManager.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>
                (searchMapView.this));
        // start position updates, accepting GPS, network or indoor positions
        if (mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)) {
            mapFragment.getPositionIndicator().setVisible(true);
        } else {

        }
    }

    //Th??m even cho markar
    private void addEvenForMarker(){
        //Th??m even cho map
        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
            @Override
            public boolean onTapEvent(PointF p) {
                //Even khi Tap v??o m??n h??nh
                //L???y ra marker ch???m v??o
                List<ViewObject> selectedObjects = map.getSelectedObjects(p);
                int listSize = selectedObjects.size();
                if(listSize==0){
                    //???n card hi???n th??? danh s??ch ph??ng
                    AnimationVisibilityCardView(false);
                }
                List<String> listRoomID = new ArrayList<String>();
                int count = 0;
                for(ViewObject object:selectedObjects){
                    count++;
                    //Ki???m tra c?? ph???i l?? ?????i t?????ng c???a ng?????i d??ng th??m v??o
                    if(object.getBaseType() == ViewObject.Type.USER_OBJECT){
                        //L???y ra Mapobject
                        MapObject mapObject = (MapObject) object;
                        //Ki???m tra xem c?? ph???i l?? marker hay kh??ng
                        if(mapObject.getType()==MapObject.Type.MARKER){
                            //??p ki???u qua marker
                            MapMarker selectedMarker = (MapMarker)mapObject;
                            //Th??m v??o trong list String ID
                            if(searchMarker!=null && searchMarker!=selectedMarker ||searchMarker==null){
                                listRoomID.add(selectedMarker.getTitle());
                            }
                        }
                    }
                    if(count==listSize){
                        if(listRoomID.size()>0){
                            //Hi???n th??? card hi???n th??? danh s??ch ph??ng
                            AnimationVisibilityCardView(true);
                            //G???i h??m ????? d??? li???u t??? controller
                            callListInfoRoom(listRoomID);
                        }
                    }
                }
                return false;
            }
            @Override
            public boolean onLongPressEvent(PointF p) {
                return false;
            }
        },0,false);
    }

    //H??m kh???i t???o map Engine
    private void initMapEngine() {
        mapFragment = getMapFragment();
        mapFragment.setRetainInstance(false);//note them vao

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        //?????a ch??? ????? l??u tr??? map tr??n b??? nh??? ?????m
        String intentName = "com.example.designapptest";
        try {
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);

        if (!success) {
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    if (error == Error.NONE) {
                        map = mapFragment.getMap();
                        map.setCenter(new GeoCoordinate(latitude, longtitude, 0.0), Map.Animation.NONE);
                        map.setZoomLevel(15.0);
                        //???n progess load ??i
                        progessBarLoadMap.setVisibility(View.GONE);

                        //Th??m even cho marker
                        addEvenForMarker();
                        //Th??m even thay ?????i v??? tr??
                        addEvenTracking();

                        //G???i h??m ????? t???a ????? t??? controller
                        callListRoomLocation();

                    } else {
                        progessBarLoadMap.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    //Zoom ?????n v??? tr?? hi???n t???i
    private void zoomToCurrentLocation(GeoCoordinate geoCoordinate){
        map.setCenter(geoCoordinate, Map.Animation.BOW);
        map.setZoomLevel(15.0);
    }

    //H??m t??m ki???m t???a ????? c???a ?????a ch??? truy???n v??o
    public void Search(String Query) {
        //?????m b???o ng?????i d??ng ch??? search trong th??nh ph??? HCM
        Query = Query + ", Th??nh ph??? H??? Ch?? Minh";

        //T??m ki???m xung quanh ??i???m n??y
        GeoCoordinate area = new GeoCoordinate(latitude, longtitude);

        GeocodeRequest request = new GeocodeRequest(Query).setSearchArea(area,5000);
        request.execute(new ResultListener<List<GeocodeResult>>() {
            @Override
            public void onCompleted(List<GeocodeResult> geocodeResults, ErrorCode errorCode) {
                if(errorCode!=ErrorCode.NONE){
                    Log.e("HERE", errorCode.toString());
                }else {
                    for(GeocodeResult result:geocodeResults){
                        //L???y ra t???a ????? tr??? v???
                        GeoCoordinate location = result.getLocation().getCoordinate();

                        //Thay ?????i ?????a ch??? c???a marker tr??n map
                        dropMarker(location);
                    }
                }
            }
        });
    }

    public void dropMarker(GeoCoordinate position) {
        if (searchMarker != null) {
            map.removeMapObject(searchMarker);
        }
        com.here.android.mpa.common.Image myImage = new com.here.android.mpa.common.Image();
        try {
            myImage.setImageResource(R.drawable.ic_marker_flag_blue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchMarker = new MapMarker();
        searchMarker.setCoordinate(position);
        searchMarker.setIcon(myImage);
        map.addMapObject(searchMarker);
        //Zome ?????n v??? tr?? m???i
        map.setCenter(new GeoCoordinate(position.getLatitude(), position.getLongitude(), 0.0), Map.Animation.BOW);
        map.setZoomLevel(15.0);
    }

    //G???i h??m l???y t???a ????? t??? controlller
    private void callListRoomLocation(){
        searchMapViewController.listLocationRoom(map);
    }

    private void callListInfoRoom(List<String> listRoomID){
        progessBarLoadRoom.setVisibility(View.VISIBLE);
        searchMapViewController.listInfoRoom(recyclerRoom,listRoomID,progessBarLoadRoom);
    }

    //H??m ???n/ hi???n card hi???n th??ng tin chi ti???t ph??ng
    private void AnimationVisibilityCardView(boolean visibility){
        if (visibility){
            if(cardViewListRoom.getVisibility()!=View.VISIBLE){
                Animation slideUp = AnimationUtils.loadAnimation(getContext(),R.anim.translate_bottom_side);
                cardViewListRoom.setVisibility(View.VISIBLE);
                cardViewListRoom.startAnimation(slideUp);
            }
        }else {
            if(cardViewListRoom.getVisibility()==View.VISIBLE){
                Animation slideDown = AnimationUtils.loadAnimation(getContext(),R.anim.traslate_top_side);
                cardViewListRoom.setVisibility(View.GONE);
                cardViewListRoom.startAnimation(slideDown);
            }
        }
    }

    //G???i h??m Top location t??? controller
    private void callTopLocation(){
        searchMapViewController.TopLocation(this);
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id){
            case R.id.btn_top_location:
                //Tr??? v??? v??? tr?? C?? nhi???u ph??ng nh???t ?????ng th???i zoom map v??? ????
                callTopLocation();
                Log.d("check3", "clieck");
                break;
            case R.id.btn_near_location:
                //Zoome map v??? ?????a ch??? hi???n t???i
                zoomToCurrentLocation(currentLocation);
                break;
        }
    }

    @Override
    public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
        //Chuy???n map ?????n v??? tr?? hi???n t???i
        if(isFirstLoad){
            zoomToCurrentLocation(geoPosition.getCoordinate());
            isFirstLoad=false;
        }

        //C???p nh???t v??? tr?? hi???n t???i
        currentLocation = geoPosition.getCoordinate();
    }

    @Override
    public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

    }
}
