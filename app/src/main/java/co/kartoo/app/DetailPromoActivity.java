package co.kartoo.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.kartoo.app.views.MeasuredStaggeredGridLayoutManagerViewAll;
import co.kartoo.app.views.NumberClusterRendererDetailPromo;
import de.greenrobot.event.EventBus;
import co.kartoo.app.events.TrendingFinishedEvent;
import co.kartoo.app.promo.ApplicableCardsAdapter;
import co.kartoo.app.promo.AvailableOutlet.ApplicableOutletAdapter;
import co.kartoo.app.promo.AvailableOutlet.ViewAll;
import co.kartoo.app.promo.TrendingPromoAdapter;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.ResponseDefault;
import co.kartoo.app.rest.model.newest.Availablecards;
import co.kartoo.app.rest.model.newest.Availableoutlets;
import co.kartoo.app.rest.model.newest.DiscoverPromotion;
import co.kartoo.app.rest.model.newest.PopularDetail;
import co.kartoo.app.views.SpaceItemDecoration;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class DetailPromoActivity extends AppCompatActivity implements OnMapReadyCallback {

    Retrofit retrofit;
    String authorization;
    ArrayList<Availablecards> listCards;
    ArrayList<Availableoutlets> listOutltet;
    ApplicableCardsAdapter adapterCards;

    LinearLayout mLLmaps, All, placeHolderview, mLLerror;
    Button  BtnPromoUrl;
    ImageButton IVShare, IVFavorite;
    TextView mTVdate, mTVpromoName, mTVpromoSubtitle, mTVtermsConditions, mTVapplicableCards, mTVOutletName, mTVbank, mTVviewAll, CallBank;
    ImageView mIVshare, mIVfavorite, mIVpromodetail, mIVmap, IVmail, IVweb, IVfb, IVtwitter;
    RecyclerView mRVoutlets, mRVapplicableCards, mLVseeAll, mRVsimilarPromo;
    //FrameLayout header;
    //NestedScrollView nestedScroll;

    Toolbar mToolbar;
    DiscoverPromotion promo;
    PromoService promoService;

    ArrayList<DiscoverPromotion> listTrendingPromo;
    TrendingPromoAdapter adapterTrendingPromo;

    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    ArrayList<Availableoutlets> allNearbyOutlet;
    ArrayList<Availableoutlets> showedNearbyOutlet;
    LatLng myLocation;
    LatLngBounds.Builder builder;

    String outletName, textHeader, imageHeader, promoUrl, image, call, email, website, facebook, tweet, promoName, merchantName, bankName, token, idPromotion, mail, from, id, promoname, merchantname;

    ClusterManager<Availableoutlets> mClusterManager;
    boolean isFavorite;
    ProgressDialog dialog;

    net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout collapsingTolbarLayout;
    MeasuredStaggeredGridLayoutManagerViewAll layoutManagerLatestPromo;

    double[] Latitude;
    double[] Longitude;

    double maxLat;
    double maxLong;
    double minLat;
    double minLong;

    private LatLngBounds.Builder bounds;

    Boolean isOnline;

    AVLoadingIndicatorView avi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detil_promo);


        IVShare = (ImageButton)findViewById(R.id.Share);
        IVFavorite = (ImageButton) findViewById(R.id.Favorite);

        IVmail = (ImageView)findViewById(R.id.mail);
        IVweb = (ImageView) findViewById(R.id.web);
        IVfb = (ImageView) findViewById(R.id.fb);
        IVtwitter = (ImageView) findViewById(R.id.twitter);

        mTVviewAll = (TextView) findViewById(R.id.mTVviewAll);
        mTVdate = (TextView) findViewById(R.id.mTVdate);
        mTVpromoName = (TextView) findViewById(R.id.mTVpromoName);
        mTVpromoSubtitle = (TextView) findViewById(R.id.mTVpromoSubtitle);
        mTVtermsConditions = (TextView) findViewById(R.id.mTVtermsConditions);
        mTVapplicableCards = (TextView) findViewById(R.id.mTVapplicableCards);
        mTVbank = (TextView) findViewById(R.id.mTVbank);
        mIVpromodetail = (ImageView) findViewById(R.id.mIVpromodetail);
        mIVmap = (ImageView) findViewById(R.id.mIVmap);
        mRVapplicableCards = (RecyclerView) findViewById(R.id.mRVapplicableCards);
        mLLmaps = (LinearLayout) findViewById(R.id.mLLmaps);
        mLVseeAll = (RecyclerView) findViewById(R.id.mLVseeAll);
        mRVsimilarPromo = (RecyclerView) findViewById(R.id.mRVsimilarPromo);
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        CallBank = (TextView) findViewById(R.id.CallBank);
        All = (LinearLayout) findViewById(R.id.All);
        placeHolderview = (LinearLayout) findViewById(R.id.placeHolderview);
        BtnPromoUrl = (Button) findViewById(R.id.BtnPromoUrl);
        mLLerror =(LinearLayout) findViewById(R.id.mLLerror);
        //header = (FrameLayout) findViewById(R.id.header);
        //nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);

        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.show();

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "50");
        sequence.setConfig(config);
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(IVFavorite)
                .setDismissOnTouch(true)
                .setShapePadding(30)
                .setMaskColour(getResources().getColor(R.color.transparent))

                .setTitleText("Like")
                .setContentText("Like a promotion?\nTouch here to save it on your list")
                .build()
        );
        sequence.start();

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Processing");

        Intent intent = getIntent();
        idPromotion = intent.getStringExtra("Id");
        from = intent.getStringExtra("from");


        SharedPreferences bb = getSharedPreferences("my_prefs", 0);
        token = bb.getString("MID", null);
        mail = bb.getString("mail", null);

        collapsingTolbarLayout = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);

        collapsingTolbarLayout.setExpandedTitleTextAppearance(R.style.DetilPromotion);
        collapsingTolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.logo_colour));
        collapsingTolbarLayout.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));


        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        imageHeader = intent.getStringExtra("imageHeader");
        textHeader = intent.getStringExtra("textHeader");
        outletName = intent.getStringExtra("outletName");

        Log.e("TAG", "outletName: "+outletName);
/*
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mToolbar.setTitle(outletName);
                    //collapsingTolbarLayout.setTitle(name);
                    //collapsingTolbarLayout.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
                    //collapsingTolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.ColorPrimaryYellow));
                    header.setVisibility(View.INVISIBLE);
                    isShow = true;
                } else if(isShow) {
                    header.setVisibility(View.VISIBLE);
                    //collapsingTolbarLayout.setTitle("");
                    //collapsingTolbarLayout.setBackgroundColor(getResources().getColor(R.color.transparent));

                    isShow = false;
                }
            }
        });
*/
        if(imageHeader!=null){
            if (!imageHeader.isEmpty()){
                Picasso.with(getApplicationContext()).load(imageHeader)
                        .fit()
                        .centerCrop()
                        .placeholder(R.color.placeholder)
                        .into(mIVpromodetail);
            }
        }if (textHeader!=null){
            if (!textHeader.isEmpty()){
                mTVpromoName.setText(textHeader);
            }
        }if (outletName!=null){
            if (!outletName.isEmpty()) {
                mToolbar.setTitle(outletName);
                collapsingTolbarLayout.setTitle(outletName);
            }
        }

        setUpNavDrawer();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mLVseeAll.setNestedScrollingEnabled(false);
        //layoutManagerLatestPromo = new MeasuredStaggeredGridLayoutManagerViewAll(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        //mLVseeAll.setLayoutManager(layoutManagerLatestPromo);
        mLVseeAll.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //gridLayoutManager.setSpanCount(1);
        //mLVseeAll.setLayoutManager(gridLayoutManager);


        retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        final PromoService promoService = retrofit.create(PromoService.class);
        Log.e("TAG", "oncall: " + idPromotion);
        Log.e("TAG", "oncall: " + token);

        Call<PopularDetail> discover = promoService.getPromoActivityPopular(token, idPromotion);
        Log.e("TAG", "onCreate: " + discover);

        allNearbyOutlet = new ArrayList<>();
        showedNearbyOutlet = new ArrayList<>();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        All.setVisibility(View.GONE);
        IVShare.setVisibility(View.GONE);
        IVFavorite.setVisibility(View.GONE);
        placeHolderview.setVisibility(View.VISIBLE);


        discover.enqueue(new Callback<PopularDetail>() {
            @Override
            public void onResponse(Response<PopularDetail> response, Retrofit retrofit) {
                Log.e("TAG", "onResponsePop: " + response.code());
                    if (response.code() == 200) {
                        avi.hide();
                        All.setVisibility(View.VISIBLE);
                        IVShare.setVisibility(View.VISIBLE);
                        IVFavorite.setVisibility(View.VISIBLE);
                        placeHolderview.setVisibility(View.GONE);
                        Log.e("TAG", "onResponse: " + response.body().getName());
                        Log.e("TAG", "onResponseLiked: " + response.body().getFavorite());


                        if (response.body().getFavorite()) {
                            isFavorite = true;
                            IVFavorite.setImageResource(R.drawable.icon_favorite_active);
                        } else {
                            isFavorite = false;
                        }
                        id = response.body().getId();

                        promoName = response.body().getName();
                        merchantName = response.body().getMerchant().getName();
                        bankName = response.body().getBank().getName();

                        email = response.body().getMerchant().getEmail();
                        website = response.body().getMerchant().getWebsite();
                        facebook = response.body().getMerchant().getFacebook();
                        tweet = response.body().getMerchant().getTwitter();

                        if (website == null) {
                            IVweb.setImageDrawable(getResources().getDrawable(R.drawable.icon_web_mct));
                        } else {
                            IVweb.setImageDrawable(getResources().getDrawable(R.drawable.icon_web_mct_a));
                        }
                        if (facebook == null) {
                            IVfb.setImageDrawable(getResources().getDrawable(R.drawable.icon_fb_mct));
                        } else {
                            IVfb.setImageDrawable(getResources().getDrawable(R.drawable.icon_fb_mct_a));
                        }
                        if (tweet == null) {
                            IVtwitter.setImageDrawable(getResources().getDrawable(R.drawable.icon_tweet_mct));
                        } else {
                            IVtwitter.setImageDrawable(getResources().getDrawable(R.drawable.icon_tweet_mct_a));
                        }
                        if (email == null) {
                            IVmail.setImageDrawable(getResources().getDrawable(R.drawable.icon_mail_mct));
                        } else {
                            IVmail.setImageDrawable(getResources().getDrawable(R.drawable.icon_mail_mct_a));
                        }

                        call = response.body().getBank().getCall_center();
                        mTVbank.setText(response.body().getBank().getName());

                        CallBank.setText("Call " + response.body().getBank().getName());
                        if (response.body().getSubtitle() == null || response.body().getSubtitle().equals("")) {
                            mTVpromoSubtitle.setVisibility(View.GONE);
                        } else {
                            mTVpromoSubtitle.setText(response.body().getSubtitle());
                        }
                        mTVdate.setText("Valid until " + response.body().getEnd_date());
                        Spanned tnc = Html.fromHtml("</br></br>\r\n" + "<b>Terms & Conditions</b> :</br></br>\r\n");
                        mTVtermsConditions.setText(TextUtils.concat(TextUtils.concat(tnc), "\r\n\n" + response.body().getTerms_and_condition()));


                        if (textHeader==null || textHeader.isEmpty() ){
                            mTVpromoName.setText(response.body().getName());
                        }

                        if (outletName==null || outletName.equals("")){
                            mToolbar.setTitle(response.body().getMerchant().getName());
                            collapsingTolbarLayout.setTitle(response.body().getMerchant().getName());
                        }

                        listCards = response.body().getAvailablecards();
                        showList();
                        Log.e("TAG", "MAPS: " + response.body().getAvailableoutlets());
                        Log.e("TAG", "MAPS: " + response.body().getAvailableoutlets().size());

                        int size = response.body().getAvailableoutlets().size();

                        if (size!=0){
                            if (response.body().getOnline()!=null){
                                isOnline = response.body().getOnline();
                                Log.e("TAG", "isOnline: "+isOnline);

                                if (response.body().getOnline()) {
                                    mLLmaps.setVisibility(View.GONE);
                                    BtnPromoUrl.setVisibility(View.VISIBLE);
                                    mapFragment.getView().setVisibility(View.GONE);
                                } else if (!response.body().getOnline()) {
                                    mLLmaps.setVisibility(View.VISIBLE);
                                    BtnPromoUrl.setVisibility(View.GONE);

                                    if (response.body().getAvailableoutlets().size() > 4) {
                                        mTVviewAll.setVisibility(View.VISIBLE);
                                        listOutltet = response.body().getAvailableoutlets();
                                        showListOutlet();

                                        mTVviewAll.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(v.getContext(), ViewAll.class);
                                                intent.putExtra("Id", idPromotion);
                                                v.getContext().startActivity(intent);
                                            }
                                        });
                                    } else {
                                        mTVviewAll.setVisibility(View.GONE);
                                        listOutltet = response.body().getAvailableoutlets();
                                        showListOutlet();
                                    }
                                }
                            }
                        }



                        if (response.body().getPromo_url()!=null){
                            promoUrl = response.body().getPromo_url();
                            //BtnPromoUrl.setVisibility(View.VISIBLE);
                            BtnPromoUrl.setText("Go to Detail Promotion");
                        }
                        else {
                            promoUrl = response.body().getMerchant().getWebsite();
                            //BtnPromoUrl.setVisibility(View.VISIBLE);
                            BtnPromoUrl.setText("Go to Detail Promotion");
                        }

                        image = response.body().getUrl_img();
                        EventBus.getDefault().postSticky(new TrendingFinishedEvent(response.body().getSimilarpromotions()));
                        Log.e("TAG", "similar: " + response.body().getSimilarpromotions().size());

                        if(imageHeader==null || imageHeader.isEmpty()){
                            Picasso.with(getApplicationContext()).load(response.body().getUrl_img())
                                    .fit()
                                    .centerCrop()
                                    .into(mIVpromodetail);
                        }



                        mixpanel();
/*
                        int a = response.body().getAvailableoutlets().size();
                        Latitude = new double[a];
                        Longitude = new double[a];
                        for (int i = 0; i < a; i++) {
                            Log.e("TAG", "Bound: " + response.body().getAvailableoutlets().get(i).getLatitude());
                            Latitude[i] = Double.valueOf(response.body().getAvailableoutlets().get(i).getLatitude());
                            Longitude[i] = Double.valueOf(response.body().getAvailableoutlets().get(i).getLongitude());
                        }
                        getMax();
                        */
                    }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });



        IVFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                if (isFavorite) {
                    Call<ResponseDefault> favorite = promoService.doUnFavoriteOutlet(token, idPromotion);
                    favorite.enqueue(new Callback<ResponseDefault>() {
                        @Override
                        public void onResponse(Response<ResponseDefault> response, Retrofit retrofit) {
                            dialog.dismiss();
                            if (response.code() == 200) {
                                dialog.dismiss();
                                isFavorite = false;
                                IVFavorite.setImageResource(R.drawable.ic_favorite_blue);
                            }
                            else if(response.code() == 403){
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "You need to Login first", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Throwable t) {
                            dialog.dismiss();
                            isFavorite = false;
                            IVFavorite.setImageResource(R.drawable.ic_favorite_blue);
                        }
                    });

                } else {
                    Call<ResponseDefault> favorite = promoService.doFavoriteOutlet(token, idPromotion);
                    favorite.enqueue(new Callback<ResponseDefault>() {
                        @Override
                        public void onResponse(Response<ResponseDefault> response, Retrofit retrofit) {
                            if (response.code() == 200) {
                                dialog.dismiss();
                                isFavorite = true;
                                IVFavorite.setImageResource(R.drawable.icon_favorite_active);

                                }
                                else if(response.code() == 403){
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "You need to Login first", Toast.LENGTH_LONG).show();
                                }
                        }
                        @Override
                        public void onFailure(Throwable t) {
                            dialog.dismiss();
                            isFavorite = true;
                            IVFavorite.setImageResource(R.drawable.icon_favorite_active);

//                            IVFavorite.setBackground(getResources().getDrawable(R.drawable.icon_favorite_active));
                        }
                    });

                }
            }
        });

        IVShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Kartoo");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, promoName+" di "+merchantName +" untuk kartu "+bankName+" - "+"download Kartoo untuk promo lainnya!\nhttp://app.kartoo.co/");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        CallBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call==null){
                    Toast.makeText(getApplicationContext(), "There is no Phone Number", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+call));
                    startActivity(callIntent);
                }

            }
        });

        BtnPromoUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promoUrl!=null){
                    String url = promoUrl;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        mLLerror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailPromoActivity.this, ReportError_.class);
                intent.putExtra("idPromotion", idPromotion);
                startActivity(intent);
            }
        });

        IVweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (website!=null){
                    String url = website;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });
        IVfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facebook!=null){{
                    String url = facebook;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }}

            }
        });
        IVtwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet!=null){
                    String url = tweet;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

            }
        });
        IVmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email!=null){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, email);
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            }
        });

        listTrendingPromo = new ArrayList<>();
        int spacingInPixels = 3;

        mRVapplicableCards.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mRVapplicableCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterTrendingPromo = new TrendingPromoAdapter(getApplicationContext(),listTrendingPromo);

        mRVsimilarPromo.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        mRVsimilarPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRVsimilarPromo.setAdapter(adapterTrendingPromo);

        //mLVseeAll.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

    }

    public void mixpanel(){
        String projectToken = getString(R.string.mixpanel_token);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStamp = dateFormat.format(new Date());
        try {
            JSONObject props = new JSONObject();
            props.put("TimeStamp", currentTimeStamp);
            props.put("from", from);
            props.put("email", mail);
            props.put("Promotion Name", promoName);
            props.put("Merchant Name", merchantName);
            props.put("Promotion ID", id);
            mixpanel.track("Promo Detail Page", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        myLocation = getCurrentLocation();

        CameraUpdate updateToMyLocation = CameraUpdateFactory.newLatLngZoom(myLocation,10);

        if (myLocation != null) {
            googleMap.moveCamera(updateToMyLocation);

            mClusterManager = new ClusterManager<Availableoutlets>(this, googleMap);
            mClusterManager.setRenderer(new NumberClusterRendererDetailPromo(this, googleMap, mClusterManager));
            googleMap.setOnCameraChangeListener(mClusterManager);
            googleMap.setOnMarkerClickListener(mClusterManager);


            Intent intent = getIntent();
            idPromotion = intent.getStringExtra("Id");

            SharedPreferences bb = getSharedPreferences("my_prefs", 0);
            token = bb.getString("MID", null);

            bounds = new LatLngBounds.Builder();

            retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
            PromoService promoService = retrofit.create(PromoService.class);
            Call<PopularDetail> discover = promoService.getPromoActivityPopular(token, idPromotion);
            Log.e("TAG", "onMapReady: "+myLocation.latitude+myLocation.longitude+token );
            discover.enqueue(new Callback<PopularDetail>() {
                @Override
                public void onResponse(Response<PopularDetail> response, Retrofit retrofit) {
                    Log.e("TAG", "onResponseCluster: " + response.code());
                    if (response.isSuccess()) {
                        if (response.code() == 200) {
                            Log.e("TAG", "onResponseCluster: " + response.body().getName());
                            int a = response.body().getAvailableoutlets().size();
                            for (int i = 0; i < a; i++) {
                                Log.e("TAG", "onResponse: " + response.body().getAvailableoutlets().get(i).getLatitude());
                                allNearbyOutlet.clear();
                                allNearbyOutlet.addAll(response.body().getAvailableoutlets());
                                mClusterManager.addItems(allNearbyOutlet);
                                mClusterManager.cluster();

                                double lati = Double.parseDouble(response.body().getAvailableoutlets().get(i).getLatitude());
                                double longi = Double.parseDouble(response.body().getAvailableoutlets().get(i).getLongitude());

                                bounds.include(new LatLng(lati, longi));
                            }
                            if (response.body().getAvailableoutlets().size()!=0) {
                                if (response.body().getAvailableoutlets().size() == 1) {
                                    LatLng latlong = new LatLng(Double.parseDouble(response.body().getAvailableoutlets().get(0).getLatitude()), Double.parseDouble(response.body().getAvailableoutlets().get(0).getLongitude()));
                                    CameraUpdate updateToMyLocation = CameraUpdateFactory.newLatLngZoom(latlong, 10);
                                    googleMap.moveCamera(updateToMyLocation);
                                } else if (response.body().getAvailableoutlets().size()> 1){
                                    if(isOnline!=null){
                                        Log.e("TAG", "isOnline: "+isOnline);
                                        if (!isOnline){
                                            if (bounds!=null){
                                                Log.e("TAG", "bound: "+bounds.toString());
                                                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                                    @Override
                                                    public void onMapLoaded() {
                                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                                                    }
                                                });
                                            }
                                            else {
                                                LatLng latlong = new LatLng(Double.parseDouble(response.body().getAvailableoutlets().get(0).getLatitude()), Double.parseDouble(response.body().getAvailableoutlets().get(0).getLongitude()));
                                                CameraUpdate updateToMyLocation = CameraUpdateFactory.newLatLngZoom(latlong, 10);
                                                googleMap.moveCamera(updateToMyLocation);

                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //lastPage = 1;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(TrendingFinishedEvent event) {
        Log.e("eventTrending", "caught");
        listTrendingPromo.clear();
        listTrendingPromo.addAll(event.getListPromo());
        adapterTrendingPromo.notifyDataSetChanged();
    }

    public LatLng getCurrentLocation() {
        return new LatLng(-6.227908, 106.828797);
    }

    private void showList(){
        mRVapplicableCards.setAdapter(new ApplicableCardsAdapter(listCards, this));

    }

    private void showListOutlet(){
        mLVseeAll.setAdapter(new ApplicableOutletAdapter(listOutltet, this));
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_back_orange);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
