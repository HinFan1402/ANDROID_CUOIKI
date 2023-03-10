package com.example.designapptest.Views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.designapptest.Adapters.AdapterRecyclerComment;
import com.example.designapptest.Adapters.AdapterRecyclerConvenient;
import com.example.designapptest.Adapters.AdapterRecyclerMainRoom;
import com.example.designapptest.Adapters.AdapterRecyclerRoomPrice;
import com.example.designapptest.Adapters.AdapterViewPagerImageShow;
import com.example.designapptest.ClassOther.GenderFilter;
import com.example.designapptest.ClassOther.TypeFilter;
import com.example.designapptest.ClassOther.classFunctionStatic;
import com.example.designapptest.ClassOther.myFilter;
import com.example.designapptest.Controller.CommentController;
import com.example.designapptest.Controller.DetailRoomController;
import com.example.designapptest.Controller.MainActivityController;
import com.example.designapptest.Controller.ReportedRoomController;
import com.example.designapptest.Model.CommentModel;
import com.example.designapptest.Model.ReportedRoomModel;
import com.example.designapptest.Model.RoomModel;
import com.example.designapptest.Model.RoomViewsModel;
import com.example.designapptest.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class detailRoom extends AppCompatActivity implements ReportRoomDialog.ReportRoomDialogListener {
    TextView txtRoomType, txtRoomMaxNumber, txtQuantityComment, txtRoomName,
            txtRoomPrice, txtRoomStatus, txtRoomArea, txtRoomAddress, txtRoomDescription,
            txtRoomGreatReview, txtRoomPrettyGoodReview, txtRoomMediumReview, txtRoomBadReview,
            txtQuantityComment_2, txtRoomPhoneNumber, txtExpandConvenients, txtExpandDescription,
            txtCapacityCramped, txtCapacityMedium, txtCapacitySpacious;

    Button btnCallPhone, btnDirectMap, btnPostComment, btnViewAll;

    ImageView imgRoomGender, imgRoom1, imgRoom2, imgRoom3, imgRoom4, imgFavorite;

    List<ImageView> listImageRoom;

    // C??c recycler.
    RecyclerView recyclerCommentRoomDetail;
    AdapterRecyclerComment adapterRecyclerComment;

    RecyclerView recyclerConvenientsRoomDetail;
    AdapterRecyclerConvenient adapterRecyclerConvenient;

    // same room
    RecyclerView recyclerSameDetailRoom;
    ProgressBar progressBarSameDetailRoom;
    LinearLayout lnLtQuantityTopSameDetailRoom;

    // S??? l?????ng tr??? v???.
    TextView txtQuantitySameDetailRoom;

    NestedScrollView nestedScrollSameDetailRoomView;
    ProgressBar progressBarLoadMoreSameDetailRoom;
    // end same room

    RecyclerView recyclerPriceRoomDetail;
    AdapterRecyclerRoomPrice adapterRecyclerRoomPrice;

    RoomModel roomModel;

    // Hi???n th??? h??nh ???nh ph??ng tr??? ch??? ????? xem.
    Dialog dialogShowImage;
    Button btnCloseShowImage;
    ViewPager viewPagerShowImage;
    TextView txtPositionImage, txtMoreImg;

    int maxImageInRoom;
    int indexImage;

    SharedPreferences sharedPreferences;
    String UID;
    Boolean isAdmin;
    CommentController commentController;
    MainActivityController mainActivityController;
    ReportedRoomController reportedRoomController;

    FrameLayout frLoutContain;
    LinearLayout lnLtExpandConvenients;
    LinearLayout lnLtExpandDescription;

    Toolbar toolbar;
    MenuItem menuItemFavorite;
    MenuItem menuItemReport;

    String District;
    String CurrentRoomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_detail_view);

        roomModel = getIntent().getParcelableExtra("phongtro");
        District = roomModel.getCounty();
        CurrentRoomID = roomModel.getRoomID();

        sharedPreferences = getSharedPreferences(LoginView.PREFS_DATA_NAME, MODE_PRIVATE);
        UID = sharedPreferences.getString(LoginView.SHARE_UID, "n1oc76JrhkMB9bxKxwXrxJld3qH2");
        isAdmin = sharedPreferences.getBoolean(LoginView.IS_ADMIN, false);

        initControl();

        loadProgress();

        setMoreImageForLastCard();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();

        clickCallPhone();

        clickDirectMap();

        clickPostComment();

        clickShowImage();

        setViewSameRoom();

        loadTheSameRoom();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_detail_menu, menu);

        menuItemFavorite = menu.findItem(R.id.menu_item_favorite);
        menuItemReport = menu.findItem(R.id.menu_item_report);

        // Set tr??? y??u th??ch ?
        menuItemFavorite.setIcon(R.drawable.ic_favorite_border_white);
        for (String roomId : RoomModel.ListFavoriteRoomsID) {
            if (roomId.equals(roomModel.getRoomID())) {
                menuItemFavorite.setIcon(R.drawable.ic_favorite_full_white);
                break;
            }
        }

        menuItemReport.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openReportDialog();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_report:
                return true;
            case R.id.menu_item_favorite:
                clickAddToFavorite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openReportDialog() {
        ReportRoomDialog reportRoomDialog = new ReportRoomDialog();
        reportRoomDialog.show(getSupportFragmentManager(), "report room dialog");
    }

    @Override
    public void applyText(String reasonReportRoom, String detailedReasonReportRoom) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        ReportedRoomModel reportedRoomModel = new ReportedRoomModel();
        reportedRoomModel.setReason(reasonReportRoom);
        reportedRoomModel.setDetail(detailedReasonReportRoom);
        reportedRoomModel.setTime(date);
        reportedRoomModel.setUserID(UID);

        reportedRoomController.addReport(reportedRoomModel, roomModel.getRoomID());
    }

    private void loadProgress() {
        for (ImageView imageView : listImageRoom)
            classFunctionStatic.showProgress(this, imageView);
    }

    // Kh???i t???o c??c control trong room detail.
    private void initControl() {
        commentController = new CommentController(this, UID);
        reportedRoomController = new ReportedRoomController(this);
        mainActivityController = new MainActivityController(this, UID);

        toolbar = findViewById(R.id.toolbar);

        txtRoomType = (TextView) findViewById(R.id.txt_roomType);
        txtRoomMaxNumber = (TextView) findViewById(R.id.txt_roomMaxNumber);
        txtQuantityComment = (TextView) findViewById(R.id.txt_quantityComment);
        txtRoomName = (TextView) findViewById(R.id.txt_roomName);
        txtRoomPrice = (TextView) findViewById(R.id.txt_roomPrice);
        txtRoomStatus = (TextView) findViewById(R.id.txt_roomStatus);
        txtRoomArea = (TextView) findViewById(R.id.txt_roomArea);
        txtRoomAddress = (TextView) findViewById(R.id.txt_roomAddress);
        txtRoomDescription = (TextView) findViewById(R.id.txt_roomDescription);
        txtRoomGreatReview = (TextView) findViewById(R.id.txt_roomGreatReview);
        txtRoomPrettyGoodReview = (TextView) findViewById(R.id.txt_roomPrettyGoodReview);
        txtRoomMediumReview = (TextView) findViewById(R.id.txt_roomMediumReview);
        txtRoomBadReview = (TextView) findViewById(R.id.txt_roomBadReview);
        txtQuantityComment_2 = (TextView) findViewById(R.id.txt_quantityComment_2);
        txtRoomPhoneNumber = (TextView) findViewById(R.id.txt_room_phonenumber);
        txtExpandConvenients = (TextView) findViewById(R.id.txt_expand_convenients);
        txtExpandDescription = (TextView) findViewById(R.id.txt_expand_description);
        txtCapacityCramped = (TextView) findViewById(R.id.txt_capacity_cramped);
        txtCapacityMedium = (TextView) findViewById(R.id.txt_capacity_medium);
        txtCapacitySpacious = (TextView) findViewById(R.id.txt_capacity_spacious);

        btnCallPhone = (Button) findViewById(R.id.btn_callPhone);
        btnDirectMap = (Button) findViewById(R.id.btn_directMap);
        btnPostComment = (Button) findViewById(R.id.btn_postComment);
        btnViewAll = (Button) findViewById(R.id.btn_viewAll);

        imgRoomGender = (ImageView) findViewById(R.id.img_roomGender);
        imgRoom1 = (ImageView) findViewById(R.id.img_room1);
        imgRoom2 = (ImageView) findViewById(R.id.img_room2);
        imgRoom3 = (ImageView) findViewById(R.id.img_room3);
        imgRoom4 = (ImageView) findViewById(R.id.img_room4);

        //imgFavorite = (ImageView) findViewById(R.id.img_favorite);

        txtMoreImg = findViewById(R.id.txt_more_img);
        frLoutContain = findViewById(R.id.fr_lout_contain);

        lnLtExpandConvenients = findViewById(R.id.lnLt_expand_convenients);
        lnLtExpandDescription = findViewById(R.id.lnLt_expand_description);

        listImageRoom = new ArrayList<ImageView>();

        listImageRoom.add(imgRoom1);
        listImageRoom.add(imgRoom2);
        listImageRoom.add(imgRoom3);
        listImageRoom.add(imgRoom4);

        recyclerCommentRoomDetail = (RecyclerView) findViewById(R.id.recycler_comment_room_detail);
        recyclerConvenientsRoomDetail = (RecyclerView) findViewById(R.id.recycler_convenients_room_detail);
        recyclerPriceRoomDetail = (RecyclerView) findViewById(R.id.recycler_price_room_detail);
        recyclerSameDetailRoom = (RecyclerView) findViewById(R.id.recycler_same_detail_room);

        progressBarSameDetailRoom = (ProgressBar) findViewById(R.id.progress_bar_same_detail_room);
        progressBarSameDetailRoom.getIndeterminateDrawable().setColorFilter(Color.parseColor("#00DDFF"),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        lnLtQuantityTopSameDetailRoom = (LinearLayout) findViewById(R.id.lnLt_quantity_top_same_detail_room);
        txtQuantitySameDetailRoom = (TextView) findViewById(R.id.txt_quantity_same_detail_room);

        nestedScrollSameDetailRoomView = (NestedScrollView) findViewById(R.id.nested_scroll_same_detail_room_view);
        progressBarLoadMoreSameDetailRoom = (ProgressBar) findViewById(R.id.progress_bar_load_more_same_detail_rooms);
        progressBarLoadMoreSameDetailRoom.getIndeterminateDrawable().setColorFilter(Color.parseColor("#00DDFF"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    // Kh???i t???o c??c gi?? tr??? cho c??c control.
    private void initData() {
        // Thi???t l???p toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Chi ti???t ph??ng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //G??n c??c gi?? tr??? v??o giao di???n
        txtRoomType.setText(roomModel.getRoomType());
        txtRoomMaxNumber.setText(String.valueOf((int) roomModel.getMaxNumber()));
        txtQuantityComment.setText("0");
        txtQuantityComment_2.setText("0");
        txtRoomName.setText(roomModel.getName());
        txtRoomPrice.setText(String.valueOf(roomModel.getRentalCosts()) + " tri???u");
        txtRoomPhoneNumber.setText(roomModel.getRoomOwner().getPhoneNumber());

        if (((int) roomModel.getCurrentNumber()) < ((int) roomModel.getMaxNumber())) {
            txtRoomStatus.setText("C??n");
        } else {
            txtRoomStatus.setText("H???t");
        }

        txtRoomArea.setText(roomModel.getLength() + "m" + " x " + roomModel.getWidth() + "m");
        txtRoomDescription.setText(roomModel.getDescribe());
        txtRoomGreatReview.setText(roomModel.getGreat() + "");
        txtRoomPrettyGoodReview.setText(roomModel.getPrettyGood() + "");
        txtRoomMediumReview.setText(roomModel.getMedium() + "");
        txtRoomBadReview.setText(roomModel.getBad() + "");


        // expand/ collapse txtRoomDescription
        expandRoomDescription();

        // load s??? l?????ng ng?????i v??o 3 h??nh tr??n
        setRoomCapacity();

        //Set address for room
        String longAddress = roomModel.getApartmentNumber() + " " + roomModel.getStreet() + ", "
                + roomModel.getWard() + ", " + roomModel.getCounty() + ", " + roomModel.getCity();
        txtRoomAddress.setText(longAddress);
        //End set address for room


        //G??n h??nh cho gi???i t??nh
        if (roomModel.isGender()) {
            imgRoomGender.setImageResource(R.drawable.ic_svg_male_100);
        } else {
            imgRoomGender.setImageResource(R.drawable.ic_svg_female_100);
        }
        //End G??n gi?? tr??? cho gi???i t??nh


        //G??n gi?? tr??? cho s??? l?????t b??nh lu???n
        if (roomModel.getListCommentRoom().size() > 0) {
            txtQuantityComment.setText(roomModel.getListCommentRoom().size() + "");
            txtQuantityComment_2.setText(roomModel.getListCommentRoom().size() + "");
        }
        //End g??n gi?? tr??? cho s??? l?????ng b??nh lu???n


        //Download h??nh ???nh cho room
        for (int i = 0; i < 4; i++) {
            downloadImageForImageControl(listImageRoom.get(i), i);
        }
        // End download h??nh ???nh cho room


        // Load s??? l?????ng b??nh lu???n thu???c t???ng lo???i ??i???m
        long great, prettyGood, medium, bad;
        great = prettyGood = medium = bad = 0;
        for (CommentModel commentModel : roomModel.getListCommentRoom()) {
            long stars = commentModel.getStars();

            if (stars < 4) {
                bad += 1;
            } else if (stars < 7) {
                medium += 1;
            } else if (stars < 9) {
                prettyGood += 1;
            } else {
                great += 1;
            }
        }

        txtRoomBadReview.setText(bad + "");
        txtRoomMediumReview.setText(medium + "");
        txtRoomPrettyGoodReview.setText(prettyGood + "");
        txtRoomGreatReview.setText(great + "");
        // End load s??? l?????ng b??nh lu???n thu???c t???ng lo???i ??i???m


        // Load danh s??ch b??nh lu???n c???a ph??ng tr???
        commentController.sortListComments(roomModel.getListCommentRoom());
        downloadImageFitForComment();

        RecyclerView.LayoutManager layoutManagerComment = new LinearLayoutManager(this);
        recyclerCommentRoomDetail.setLayoutManager(layoutManagerComment);
        adapterRecyclerComment = new AdapterRecyclerComment(this, R.layout.comment_element_grid_room_detail_view,
                roomModel.getListCommentRoom(), roomModel.getRoomID(), UID, false);
        recyclerCommentRoomDetail.setAdapter(adapterRecyclerComment);
        adapterRecyclerComment.notifyDataSetChanged();


        // Load danh s??ch ti???n nghi c???a ph??ng tr???
        expandRoomConvenients();

        RecyclerView.LayoutManager layoutManagerConvenient = new GridLayoutManager(this, 3);
        recyclerConvenientsRoomDetail.setLayoutManager(layoutManagerConvenient);
        adapterRecyclerConvenient = new AdapterRecyclerConvenient(this, getApplicationContext(),
                R.layout.utility_element_grid_room_detail_view, roomModel.getListConvenientRoom());
        recyclerConvenientsRoomDetail.setAdapter(adapterRecyclerConvenient);
        recyclerConvenientsRoomDetail.setNestedScrollingEnabled(false);
        adapterRecyclerConvenient.notifyDataSetChanged();


        // Load danh s??ch gi?? c???a ph??ng tr???
        RecyclerView.LayoutManager layoutManagerRoomPrice = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPriceRoomDetail.setLayoutManager(layoutManagerRoomPrice);
        adapterRecyclerRoomPrice = new AdapterRecyclerRoomPrice(this, getApplicationContext(),
                R.layout.room_price_element_grid_room_detail_view, roomModel.getListRoomPrice());
        recyclerPriceRoomDetail.setAdapter(adapterRecyclerRoomPrice);
        adapterRecyclerRoomPrice.notifyDataSetChanged();
    }

    private void setRoomCapacity() {
        int areaPerPerson = 12;
        double roomArea = roomModel.getWidth() * roomModel.getLength();
        int spacious = (int) (roomArea / areaPerPerson);
        int medium = spacious + 1;
        int cramped = medium + 1;

        txtCapacitySpacious.setText(spacious + " ng?????i +");
        txtCapacityMedium.setText(medium + " ng?????i");
        txtCapacityCramped.setText(cramped + " ng?????i");
    }

    private void expandRoomDescription() {
        txtRoomDescription.post(new Runnable() {
            @Override
            public void run() {
                if (txtRoomDescription.getLineCount() <= 2) {
                    lnLtExpandDescription.setVisibility(View.GONE);
                } else {
                    txtExpandDescription.setText(R.string.stringExpand);
                    collapseTextView(txtRoomDescription, 2);
                }
            }
        });

        lnLtExpandDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtExpandDescription.getText().toString().equals(getString(R.string.stringCollapse))) {
                    collapseTextView(txtRoomDescription, 2);
                    txtExpandDescription.setText(R.string.stringExpand);
                } else {
                    expandTextView(txtRoomDescription);
                    txtExpandDescription.setText(R.string.stringCollapse);
                }
            }
        });
    }

    private void expandTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getLineCount());
        animation.setDuration(200).start();
    }

    private void collapseTextView(TextView tv, int numLines) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", numLines);
        animation.setDuration(200).start();
    }

    private void expandRoomConvenients() {
        int convenientRoomSize = roomModel.getListConvenientRoom().size();
        int rowConvenientHeight = 203;
        int fullRowConvenientHeight = (convenientRoomSize % 3 == 0) ?
                (convenientRoomSize / 3) * rowConvenientHeight : (convenientRoomSize / 3) * rowConvenientHeight + rowConvenientHeight;

        txtExpandConvenients.setText(R.string.stringExpand);

        if (convenientRoomSize > 3) {
            resizeRecyclerConvenientsRoomDetailAnimation(recyclerConvenientsRoomDetail.getHeight(), rowConvenientHeight);
        } else {
            lnLtExpandConvenients.setVisibility(View.GONE);
        }

        lnLtExpandConvenients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtExpandConvenients.getText().toString().equals(getString(R.string.stringCollapse))) {
                    resizeRecyclerConvenientsRoomDetailAnimation(recyclerConvenientsRoomDetail.getHeight(), rowConvenientHeight);
                    txtExpandConvenients.setText(R.string.stringExpand);
                } else {
                    resizeRecyclerConvenientsRoomDetailAnimation(recyclerConvenientsRoomDetail.getHeight(), fullRowConvenientHeight);
                    txtExpandConvenients.setText(R.string.stringCollapse);
                }
            }
        });
    }

    private void resizeRecyclerConvenientsRoomDetailAnimation(int fromHeight, int toHeight) {
        ValueAnimator anim = ValueAnimator.ofInt(fromHeight, toHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = recyclerConvenientsRoomDetail.getLayoutParams();
                layoutParams.height = val;

                recyclerConvenientsRoomDetail.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(200);
        anim.start();
    }

    // H??m t???i ???nh t??? firebase v??? theo image control v?? v??? tr?? ???nh c???n l???y tr??n firebase.
    private void downloadImageForImageControl(final ImageView imageDownload, final int positionDownload) {
        Picasso.get().load(roomModel.getListImageRoom().get(positionDownload)).fit().centerCrop().into(imageDownload);
    }

    private void downloadImageFitForComment() {
        for (CommentModel commentModel : roomModel.getListCommentRoom()) {
            // Load ???nh n??n
            commentModel.setCompressionImageFit(Picasso.get().load(commentModel.getUserComment().getAvatar()).fit());
        }
    }

    // H??m g???i ??i???n tho???i cho ch??? ph??ng tr???.
    private void clickCallPhone() {
        btnCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPhoneNumbet = roomModel.getRoomOwner().getPhoneNumber();
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", strPhoneNumbet, null));
                startActivity(intentCall);
            }
        });
    }

    // H??m hi???n th??? b???n ????? ch??? ???????ng.
    private void clickDirectMap() {
        btnDirectMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringRoomName = roomModel.getName();

                Intent intentDirectMap = new Intent(detailRoom.this, directMapRoomDetail.class);
                intentDirectMap.putExtra("phongtro", roomModel);
                startActivity(intentDirectMap);
            }
        });
    }

    // H??m vi???t hi???n th??? m??n h??nh vi???t b??nh lu???n.
    private void clickPostComment() {
        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCommentAndRate = new Intent(detailRoom.this, commentAndRateMain.class);
                intentCommentAndRate.putExtra("phongtro", roomModel);
                intentCommentAndRate.putExtra("currentPage", 0);
                startActivity(intentCommentAndRate);
            }
        });

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCommentAndRate = new Intent(detailRoom.this, commentAndRateMain.class);
                intentCommentAndRate.putExtra("phongtro", roomModel);
                intentCommentAndRate.putExtra("currentPage", 1);
                startActivity(intentCommentAndRate);
            }
        });
    }

    // H??m hi???n th??? ???nh ph??ng tr??? ch??? ????? xem.
    private void clickShowImage() {
        imgRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialogCustom();
                indexImage = 0;
            }
        });

        imgRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialogCustom();
                indexImage = 1;
            }
        });

        imgRoom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialogCustom();
                indexImage = 2;
            }
        });

        imgRoom4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialogCustom();
                indexImage = 3;
            }
        });
    }

    // H??m hi???n th??? v??? tr?? ???nh tr??n t???ng s???.
    private void showPostionImage(int position) {
        txtPositionImage.setText(String.valueOf(position) + "/" + String.valueOf(maxImageInRoom));
    }

    //H??m hi???n th??? c???ng th??m h??nh ??? th??? cu???i
    private void setMoreImageForLastCard() {
        int size = roomModel.getListImageRoom().size();
        if (size > 4) {
            txtMoreImg.setText("+" + (size - 4));
            frLoutContain.setBackgroundResource(R.color.colorTransParrent);
        } else {
            txtMoreImg.setText("");
            frLoutContain.setBackgroundResource(R.color.colorWhile100);
        }
    }

    // H??m t???o ra custom dialong v?? c??c t??c v??? li??n quan.
    private void showImageDialogCustom() {
        maxImageInRoom = roomModel.getListImageRoom().size();

        dialogShowImage = new Dialog(detailRoom.this);
        dialogShowImage.setContentView(R.layout.dialog_show_image_detail_room);

        // C??c control ch??? ????? xem ???nh ph??ng tr???.
        viewPagerShowImage = (ViewPager) dialogShowImage.findViewById(R.id.viewPager_showImage_detail_room);
        btnCloseShowImage = (Button) dialogShowImage.findViewById(R.id.btn_closeShowImage_detail_room);
        txtPositionImage = (TextView) dialogShowImage.findViewById(R.id.txt_positionImage_detail_room);

        // Truy???n d??? li???u qua view pager show image.
        AdapterViewPagerImageShow adapterViewPagerImageShow = new AdapterViewPagerImageShow(this,
                roomModel.getListImageRoom());
        viewPagerShowImage.setAdapter(adapterViewPagerImageShow);

        // Hi???n th??? l??c ban ?????u.
        showPostionImage(1);

        viewPagerShowImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                int index = viewPagerShowImage.getCurrentItem();
                showPostionImage(index + 1);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // K???t th??c ch??? ????? xem ???nh ph??ng tr???.
        btnCloseShowImage.setEnabled(true);
        btnCloseShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowImage.cancel();
            }
        });


        // T??y ch??nh l???i dialog g???m giao di???n, match parent width, height v?? chuy???n background trong su???t.
        dialogShowImage.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogShowImage.getWindow().setDimAmount(0.9f);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialogShowImage.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        dialogShowImage.show();
    }

    private void clickAddToFavorite() {
        String roomId = roomModel.getRoomID();

        if (menuItemFavorite.getIcon().getConstantState().equals(
                getResources().getDrawable(R.drawable.ic_favorite_border_white).getConstantState()
        )) {
            mainActivityController.addToFavoriteRooms(roomId, detailRoom.this, menuItemFavorite);
        } else {
            mainActivityController.removeFromFavoriteRooms(roomId, detailRoom.this, menuItemFavorite);
        }
    }

    // H??m load c??c ph??ng tr??? c??ng ti??u ch??
    private void loadTheSameRoom() {
        // Load ph??ng c??ng ti??u ch??
        List<myFilter> myFilters = new ArrayList<myFilter>();
        //Th??m v??o ti??u ch?? s??? ng?????i
        GenderFilter genderFilter = new GenderFilter((int)roomModel.getMaxNumber(),roomModel.isGender());
        myFilters.add(genderFilter);
        //Th??m v??o ti??u ch?? lo???i ph??ng tr???
        TypeFilter typeFilter = new TypeFilter("",roomModel.getTypeID());
        myFilters.add(typeFilter);
        //Th??m v??o ti??u ch?? ti???n ??ch c???a ph??ng
//        for(ConvenientModel dataConvenient: roomModel.getListConvenientRoom()){
//            ConvenientFilter convenientFilter = new ConvenientFilter(dataConvenient.getConvenientID());
//            myFilters.add(convenientFilter);
//        }

        DetailRoomController detailRoomController = new DetailRoomController(this, District, myFilters, UID);
        detailRoomController.loadSearchRoom(recyclerSameDetailRoom, CurrentRoomID, progressBarSameDetailRoom,
                txtQuantitySameDetailRoom, lnLtQuantityTopSameDetailRoom,
                nestedScrollSameDetailRoomView, progressBarLoadMoreSameDetailRoom);

        if (!isAdmin) {
            //G???i h??m th??m v??o l?????ng view t??? controller
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            RoomViewsModel data = new RoomViewsModel(date, UID, roomModel.getRoomID());
            detailRoomController.addViews(data);
        }
    }

    private void setViewSameRoom() {
        // Hi???n progress bar.
        progressBarSameDetailRoom.setVisibility(View.VISIBLE);
        // ???n progress bar load more.
        progressBarLoadMoreSameDetailRoom.setVisibility(View.GONE);
        // ???n layout k???t qu??? tr??? v???.
        lnLtQuantityTopSameDetailRoom.setVisibility(View.GONE);
    }
}
