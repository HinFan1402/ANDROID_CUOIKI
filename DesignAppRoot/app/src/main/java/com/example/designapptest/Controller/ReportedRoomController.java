package com.example.designapptest.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.designapptest.Adapters.AdapterRecyclerHost;
import com.example.designapptest.Adapters.AdapterRecyclerReportRoom;
import com.example.designapptest.Controller.Interfaces.IReportedRoomModel;
import com.example.designapptest.Controller.Interfaces.IRoomViewsModel;
import com.example.designapptest.Controller.Interfaces.IUserModel;
import com.example.designapptest.Model.ReportedRoomModel;
import com.example.designapptest.Model.UserModel;
import com.example.designapptest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReportedRoomController {
    ReportedRoomModel reportedRoomModel;
    Context context;

    int quantityReportRoomLoaded = 0;
    int quantityReportRoomEachTime = 5;

    public ReportedRoomController(Context context) {
        this.context = context;
        this.reportedRoomModel = new ReportedRoomModel();
    }

    public void addReport(ReportedRoomModel reportedRoomModel, String roomId) {
        IReportedRoomModel iReportedRoomModel = new IReportedRoomModel() {
            @Override
            public void makeToast(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void getListReportRooms(ReportedRoomModel reportedRoomModel) {

            }

            @Override
            public void setProgressBarLoadMore() {

            }

            @Override
            public void setQuantityTop(int quantity) {

            }

            @Override
            public void setQuantityLoadMore(int quantityLoaded) {
                quantityReportRoomLoaded = quantityLoaded;
            }
        };

        reportedRoomModel.addReport(reportedRoomModel, roomId, iReportedRoomModel);
    }

    public void ListReports(RecyclerView recyclerAdminReportRoomView, TextView txtQuantity, ProgressBar progressBarAdminReportRoom,
                            LinearLayout lnLtQuantityTopAdminReportRoom, NestedScrollView nestedScrollAdminReportRoomView,
                            ProgressBar progressBarLoadMoreAdminReportRoom) {
        final List<ReportedRoomModel> reportedRoomModelList = new ArrayList<>();

        //T???o layout cho danh s??ch tr??? t??m ki???m nhi???u nh???t
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerAdminReportRoomView.setLayoutManager(layoutManager);

        //T???o adapter cho recycle view
        final AdapterRecyclerReportRoom adapterRecyclerReportRoom = new AdapterRecyclerReportRoom(context, reportedRoomModelList, R.layout.element_report_list_view);
        //C??i adapter cho recycle
        recyclerAdminReportRoomView.setAdapter(adapterRecyclerReportRoom);
        ViewCompat.setNestedScrollingEnabled(recyclerAdminReportRoomView, false);
        //End t???o layout cho danh s??ch tr??? t??m ki???m nhi???u nh???t

        IReportedRoomModel iReportedRoomModel = new IReportedRoomModel() {
            @Override
            public void makeToast(String message) {

            }

            @Override
            public void getListReportRooms(ReportedRoomModel reportedRoomModel) {
                // Load ???nh n??n
                reportedRoomModel.getReportedRoom()
                        .setCompressionImageFit(Picasso.get().load(reportedRoomModel.getReportedRoom().getCompressionImage())
                                .fit());

                //Th??m v??o trong danh s??ch ch??? tr???
                reportedRoomModelList.add(reportedRoomModel);

                //Th??ng b??o l?? ???? c?? th??m d??? li???u
                adapterRecyclerReportRoom.notifyDataSetChanged();
            }

            @Override
            public void setProgressBarLoadMore() {
                progressBarAdminReportRoom.setVisibility(View.GONE);
                progressBarLoadMoreAdminReportRoom.setVisibility(View.GONE);
            }

            @Override
            public void setQuantityTop(int quantity) {
                lnLtQuantityTopAdminReportRoom.setVisibility(View.VISIBLE);
                // Hi???n th??? k???t qu??? tr??? v???
                txtQuantity.setText(quantity + "");
            }

            @Override
            public void setQuantityLoadMore(int quantityLoaded) {
            }
        };

        //
        ColorDrawable swipeBackground = new ColorDrawable(Color.parseColor("#C03A2B"));
        Drawable deleteIcon = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_garbage, null);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                adapterRecyclerReportRoom.removeItem(viewHolder, recyclerAdminReportRoomView, adapterRecyclerReportRoom,
                        txtQuantity, iReportedRoomModel, reportedRoomModel, quantityReportRoomLoaded,
                        quantityReportRoomEachTime);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int iconMarginTopBottom = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;

                if (dX > 0) {
                    swipeBackground.setBounds(itemView.getLeft(), itemView.getTop(), ((int) dX), itemView.getBottom());
                    deleteIcon.setBounds(
                            itemView.getLeft() + deleteIcon.getIntrinsicWidth(),
                            itemView.getTop() + iconMarginTopBottom,
                            itemView.getLeft() + 2 * deleteIcon.getIntrinsicWidth(),
                            itemView.getBottom() - iconMarginTopBottom);
                } else {
                    swipeBackground.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(),
                            itemView.getBottom());
                    deleteIcon.setBounds(
                            itemView.getRight() - 2 * deleteIcon.getIntrinsicWidth(),
                            itemView.getTop() + iconMarginTopBottom,
                            itemView.getRight() - deleteIcon.getIntrinsicWidth(),
                            itemView.getBottom() - iconMarginTopBottom);
                }

                swipeBackground.draw(c);

                c.save();

                if (dX > 0) {
                    c.clipRect(itemView.getLeft(), itemView.getTop(), ((int) dX), itemView.getBottom());
                } else {
                    c.clipRect(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                }
                deleteIcon.draw(c);

                c.restore();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerAdminReportRoomView);
        //

        // G???i h??m l???y d??? li???u khi scroll xu???ng ????y.
        nestedScrollAdminReportRoomView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                // check xem c?? scroll ??c ko
                View child = nestedScrollView.getChildAt(0);
                if (child != null) {
                    int childHeight = child.getHeight();
                    // N???u scroll ??c
                    if (nestedScrollView.getHeight() < childHeight + nestedScrollView.getPaddingTop() + nestedScrollView.getPaddingBottom()) {
                        View lastItemView = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

                        if (lastItemView != null) {
                            if (i1 >= lastItemView.getMeasuredHeight() - nestedScrollView.getMeasuredHeight()) {
                                // Hi???n th??? progress bar
                                progressBarLoadMoreAdminReportRoom.setVisibility(View.VISIBLE);

                                quantityReportRoomLoaded += quantityReportRoomEachTime;
                                reportedRoomModel.ListReportedRooms(iReportedRoomModel,
                                        quantityReportRoomLoaded + quantityReportRoomEachTime,
                                        quantityReportRoomLoaded);
                            }
                        }
                    }
                }
            }
        });

        //G???i h??m l???y d??? li???u trong model.
        reportedRoomModel.ListReportedRooms(iReportedRoomModel, quantityReportRoomLoaded + quantityReportRoomEachTime,
                quantityReportRoomLoaded);
    }
}
