package com.example.designapptest.Controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.designapptest.Adapters.AdapterRecyclerMainRoom;
import com.example.designapptest.Controller.Interfaces.IMapRoomModel;
import com.example.designapptest.Controller.Interfaces.IMapViewModel;
import com.example.designapptest.Controller.Interfaces.IStringCallBack;
import com.example.designapptest.Model.LocationModel;
import com.example.designapptest.Model.MapRoomModel;
import com.example.designapptest.Model.RoomModel;
import com.example.designapptest.R;
import com.example.designapptest.Views.searchMapView;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class searchMapViewController {

    LocationModel locationModel;
    MapRoomModel mapRoomModel;
    Context context;
    RoomModel roomModel;

    public searchMapViewController(Context context){
        mapRoomModel = new MapRoomModel();
        roomModel = new RoomModel();
        this.context=context;
        locationModel = new LocationModel();
    }

    public void listLocationRoom(Map map){
        IMapRoomModel iMapRoomModel = new IMapRoomModel() {
            @Override
            public void getListLocationRoom(RoomModel valueRoom) {

                //Custom marker with image
                com.here.android.mpa.common.Image myImage = new com.here.android.mpa.common.Image();

                try {
                    String typeRoom = valueRoom.getTypeID();
                    switch (typeRoom){
                        case "RTID0":
                            myImage.setImageResource(R.drawable.ic_marker_ktx);
                            break;
                        case "RTID1":
                            myImage.setImageResource(R.drawable.ic_marker_nha_nguyen_can);
                            break;
                        case "RTID2":
                            myImage.setImageResource(R.drawable.ic_marker_chung_cu);
                            break;
                        case "RTID3":
                            myImage.setImageResource(R.drawable.ic_marker_phong_tro);
                            break;
                    }

                }catch (IOException ex){

                }

                //Th??m v??o marker t????ng ???ng v???i m???i t???a ????? tr??n map
                MapMarker defaultMarker = new MapMarker();
                defaultMarker.setIcon(myImage);
                defaultMarker.setTitle(valueRoom.getRoomID());
                defaultMarker.setCoordinate(new GeoCoordinate(valueRoom.getLatitude(),valueRoom.getLongtitude(), 0.0));
                defaultMarker.unsetVisibleMask(1,11);
                map.addMapObject(defaultMarker);

            }
        };

        mapRoomModel.listLocationRoom(iMapRoomModel);
    }

    public void listInfoRoom(RecyclerView recyclerView, List<String> listRoomID, ProgressBar progressBar){
        final List<RoomModel> roomModelList = new ArrayList<>();

        //T???o layout cho danh s??ch tr???
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        //T???o adapter cho recycle view
        final AdapterRecyclerMainRoom adapterRecyclerMainRoom = new AdapterRecyclerMainRoom(context, roomModelList, R.layout.room_element_list_view,"");
        //C??i adapter cho recycle
        recyclerView.setAdapter(adapterRecyclerMainRoom);

        IMapViewModel iMapViewModel = new IMapViewModel() {
            @Override
            public void getListRoom(RoomModel valueRoom) {
                i++;
                valueRoom.setCompressionImageFit(Picasso.get().load(valueRoom.getCompressionImage()).fit());
                roomModelList.add(valueRoom);
                adapterRecyclerMainRoom.notifyDataSetChanged();
                if(i==listRoomID.size()){
                    progressBar.setVisibility(View.GONE);
                }
            }

            int i = 0;

        };

        roomModel.SendData_NoLoadMore(listRoomID,iMapViewModel);
    }

    public void TopLocation(Fragment currentFragment){
        Log.d("check3", "contro");
        IStringCallBack iStringCallBack = new IStringCallBack() {
            @Override
            public void sendString(String value) {
                //Zoom map ?????n v??? tr?? c?? nhi???u
                ((searchMapView)currentFragment).Search(value);
            }
        };

        locationModel.Top_1_Location(iStringCallBack);
    }

}
