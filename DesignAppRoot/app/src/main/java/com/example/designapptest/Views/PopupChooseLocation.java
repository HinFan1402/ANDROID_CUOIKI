package com.example.designapptest.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.designapptest.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.GeocodeResult;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class PopupChooseLocation extends AppCompatActivity {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE};
    Map map = null;
    private SupportMapFragment mapFragment = null;
    MapMarker marker = null;

    Button btnExit;
    ProgressBar progessBarLoadMap;

    FloatingSearchView searchView;

    //Bi???n l??u t???a ????? ????? truy???n v??? l???i m??n h??nh ????ng ph??ng
    double latitude = 10.776927;
    double longtitude = 106.637588;

    //Bi???n l??u ?????a ch??? ????? truy???n v??? l???i m??n h??nh ????ng ph??ng
    //Qu???n
    String District="";

    //???????ng
    String Street ="";

    //Th??nh ph???
    String City="";

    //S??? nh??
    String No="";

    TextView txtStreet,txtWard,txtDistrict,txtNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thay ?????i k??ch th?????c c???a m??n h??nh

        //L???y ra Intent
        Intent intent = getIntent();
        if(intent!=null){
            //L???y ra t???a ?????
            latitude = intent.getDoubleExtra(postRoomStep1.SHARE_LATITUDE,0.0);
            longtitude = intent.getDoubleExtra(postRoomStep1.SHARE_LONGTITUDE,0.0);
        }
        checkPermissions();

    }

    private void Search(String Query) {
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
                        dropMarker(location,true);
                    }
                }
            }
        });
    }

    private void updateView(){
        txtDistrict.setText(District.equals("")?"Kh??ng r??":District);
        txtWard.setText("Kh??ng r??");
        txtNo.setText(No.equals("")?"Kh??ng r??":No);
        txtStreet.setText(Street.equals("")?"Kh??ng r??":Street);
    }

    private void initControl(){

        progessBarLoadMap= findViewById(R.id.progess_bar_load_map);
        //?????i m??u progessBar
        progessBarLoadMap.getIndeterminateDrawable().setColorFilter(Color.parseColor("#00DDFF"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        progessBarLoadMap.setVisibility(View.VISIBLE);

        txtDistrict = findViewById(R.id.txt_district);
        txtNo =findViewById(R.id.txt_no);
        txtStreet = findViewById(R.id.txt_street);
        txtWard = findViewById(R.id.txt_ward);

        btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ki???m tra n???u ng?????i d??ng kh??ng ch???n v??o Th??nh ph??? H??? ch?? minh
                if(City.contains("H??? Ch?? Minh")){
                    Intent intent=new Intent();
                    latitude = marker.getCoordinate().getLatitude();
                    longtitude = marker.getCoordinate().getLongitude();

                    //Truy???n v??? th??ng tin hi???n t???i tr??n map
                    intent.putExtra(postRoomStep1.SHARE_LATITUDE,latitude);
                    intent.putExtra(postRoomStep1.SHARE_LONGTITUDE,longtitude);

                    //Th??ng tin v??? v??? tr?? v???t l??
                    intent.putExtra(postRoomStep1.SHARE_DISTRICT,District);
                    intent.putExtra(postRoomStep1.SHARE_STREET,Street);
                    intent.putExtra(postRoomStep1.SHARE_NO,No);

                    //End truy???n v??? th??ng tin hi???n t???i tr??n map

                    setResult(AppCompatActivity.RESULT_OK,intent);
                    finish();
                }
                else {
                    Toast.makeText(PopupChooseLocation.this,"Vui l??ng ch???n c??c ?????a ch??? ??? HCM",Toast.LENGTH_LONG).show();
                }

            }
        });

        searchView = findViewById(R.id.floating_search_view);

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                //Zoom map ?????n ?????a ch??? c???n t??m
                if(currentQuery.isEmpty()){
                    Toast.makeText(PopupChooseLocation.this,"Vui l??ng kh??ng ????? tr???ng ?????a ch???",Toast.LENGTH_LONG).show();
                }else {
                    Search(currentQuery);
                }
            }
        });
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    //H??m thay ?????i k??ch th?????c c???a m??n h??nh
    private void changeDisplay(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*.9),(int)(height*.9));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y=-20;

        getWindow().setAttributes(params);
    }

    //h??m ki???m tra quy???n c???a ???ng d???ng
    protected void checkPermissions() {

        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    //H??m thay ?????i v??? tr?? c???a marker
    public void dropMarker(GeoCoordinate position,boolean isSearch) {
        if(marker != null) {
            map.removeMapObject(marker);
        }

        marker = new MapMarker();
        marker.setCoordinate(position);
        map.addMapObject(marker);

        //N???u search th?? s??? chuy???n m??n h??nh sang v??? tr?? t??m th???y
        if(isSearch){
            //Zome ?????n v??? tr?? m???i
            map.setCenter(new GeoCoordinate(position.getLatitude(), position.getLongitude(), 0.0), Map.Animation.NONE);
            map.setZoomLevel(15.0);
        }

        //Thay ?????i v??? tr?? c???a v??? tr?? v???t l??
        triggerRevGeocodeRequest(position);
    }


    //H??m kh???i t???o map Engine
    private void initMapEngine() {

        setContentView(R.layout.activity_popup_choose_location);
        initControl();
        // Search for the map fragment to finish setup by calling init().
        mapFragment = getMapFragment();
        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        //?????a ch??? ????? l??u tr??? map tr??n b??? nh??? ?????m
        String intentName = "com.example.designapptest";
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);

        if (!success) {
        } else {
            //Hi???n th??? map
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {

                        map = mapFragment.getMap();
                        map.setCenter(new GeoCoordinate(latitude, longtitude, 0.0), Map.Animation.NONE);
                        map.setZoomLevel(15.0);

                        //Th??m v??o marker hi???n t???i l?? t???a ????? hi???n t???i
                        marker = new MapMarker();
                        marker.setCoordinate(new GeoCoordinate(latitude, longtitude, 0.0));

                        //Th??m v??o mapMarker
                        map.addMapObject(marker);

                        //Th??m v??o v??? tr?? trong l???n ?????u ti??n hi???n th??? l??n
                        triggerRevGeocodeRequest(new GeoCoordinate(latitude,longtitude));

                        //Th??m even cho map
                        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
                            @Override
                            public boolean onTapEvent(PointF p) {
                                //Even khi Tap v??o m??n h??nh
                                return false;
                            }
                            @Override
                            public boolean onLongPressEvent(PointF p) {
                                //Even khi ch???m l??u v??o m??n h??nh
                                GeoCoordinate position = map.pixelToGeo(p);
                                //X??a marker c?? th??m v??o marker m???i v?? thay ?????i v??? tr?? t????ng ???ng
                                dropMarker(position,false);
                                return false;
                            }
                        },0,false);

                        //???n progess load ??i
                        progessBarLoadMap.setVisibility(View.GONE);
                    } else {
                        progessBarLoadMap.setVisibility(View.GONE);
                        Toast.makeText(PopupChooseLocation.this,"L???i khi t???i map",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void triggerRevGeocodeRequest(GeoCoordinate coordinate) {
        ReverseGeocodeRequest revGecodeRequest = new ReverseGeocodeRequest(coordinate);
        revGecodeRequest.execute(new ResultListener<Location>() {
            @Override
            public void onCompleted(Location location, ErrorCode errorCode) {
                if (errorCode == ErrorCode.NONE) {
                    //L??u l???i ?????a ch??? ????? tr??? v???
                    District = location.getAddress().getDistrict();
                    City = location.getAddress().getCity();
                    Street = location.getAddress().getStreet();
                    String test = District+City+Street+No;
                    Log.d("check", test);
                    //Lo???i b??? ???????ng v?? H???m trong chu???i tr??? v???
                    Street = Street.replace("???????NG","");
                    Street =Street .replace("H???M","");
                    //remove space
                    Street = Street.trim();

                    No = location.getAddress().getHouseNumber();

                    //Update hi???n th???
                    updateView();

                } else {

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                //Kh???i t???i UI n???u th???a h???t permission
                //initialize();
                //Kh???i t???o map eng
                initMapEngine();
                //G???i h??m thay ?????i k??ch th?????c c???a m??n h??nh
                changeDisplay();

                break;
        }
    }

    //Kh???i t???o map
    private void initialize() {
        setContentView(R.layout.activity_popup_choose_location);
        initControl();
        // Search for the map fragment to finish setup by calling init().
        mapFragment = getMapFragment();
        // Set up disk cache path for the map service for this application
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "com.example.designapptest");
        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {

            //Hi???n th??? map
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {


                        map = mapFragment.getMap();
                        map.setCenter(new GeoCoordinate(latitude, longtitude, 0.0), Map.Animation.NONE);
                        map.setZoomLevel(15.0);

                        //Th??m v??o marker hi???n t???i l?? t???a ????? hi???n t???i
                        marker = new MapMarker();
                        marker.setCoordinate(new GeoCoordinate(latitude, longtitude, 0.0));

                        //Th??m v??o mapMarker
                        map.addMapObject(marker);

                        //Th??m v??o v??? tr?? trong l???n ?????u ti??n hi???n th??? l??n
                        triggerRevGeocodeRequest(new GeoCoordinate(latitude,longtitude));



                        //Th??m even cho map
                        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
                            @Override
                            public boolean onTapEvent(PointF p) {
                                //Even khi Tap v??o m??n h??nh
                                return false;
                            }
                            @Override
                            public boolean onLongPressEvent(PointF p) {
                                //Even khi ch???m l??u v??o m??n h??nh
                                GeoCoordinate position = map.pixelToGeo(p);
                                //X??a marker c?? th??m v??o marker m???i v?? thay ?????i v??? tr?? t????ng ???ng
                                dropMarker(position,false);
                                return false;
                            }
                        },0,false);

                        //???n progess load ??i
                        progessBarLoadMap.setVisibility(View.GONE);
                    } else {
                        progessBarLoadMap.setVisibility(View.GONE);
                        Toast.makeText(PopupChooseLocation.this,"L???i khi t???i map",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
