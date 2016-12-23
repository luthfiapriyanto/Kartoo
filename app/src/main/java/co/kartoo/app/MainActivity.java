package co.kartoo.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.kartoo.app.drawer.DrawerViewActivity_;
import co.kartoo.app.forgotpassword.ChangeYourPassword;
import co.kartoo.app.forgotpassword.ForgotYourPassword;
import co.kartoo.app.landing.LandingActivity_;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.nearby.ActivityNearbyResult_;
import co.kartoo.app.nearby.GPSTracker;
import co.kartoo.app.notifications.GeofenceTransitionsIntentService;
import co.kartoo.app.rest.CardService;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.ResponseDefault;
import co.kartoo.app.rest.model.newest.DiscoverPromotionCategory;
import co.kartoo.app.rest.model.newest.MallGeofence;
import co.kartoo.app.rest.model.newest.Notifications;
import co.kartoo.app.rest.model.newest.Tags;
import co.kartoo.app.search.SearchActivity_;
import co.kartoo.app.tabfragment.TabAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.microsoft.windowsazure.messaging.*;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import android.widget.Button;
import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    @ViewById(R.id.mFLcontainer)
    FrameLayout mFLcontainer;
    @ViewById
    Toolbar mToolbar;
    @ViewById
    DrawerLayout mDLdrawer;
    @ViewById
    NavigationView mNVdrawer;
    @ViewById(R.id.mVPmain)
    ViewPager mVPmain;
    @ViewById
    TabLayout mTLtab;
    @ViewById
    FrameLayout mFLsearch;
    @ViewById
    EditText mETsearch;
//    @ViewById
//    PagerTabStrip mPTStab;
    @ViewById
    LinearLayout mLLsearch;
    @ViewById
    ImageView toolbar_title;
    @ViewById
    TextView toolbar_title_text;
    @ViewById
    ImageView mIVsearchClear;
    @ViewById
            ImageView mIVsearch;
    @ViewById
    AppBarLayout tabanim_appbar;

    @ViewById
    LinearLayout All;

    @ViewById
    Button one;
    @ViewById
    Button two;
    @ViewById
    Button three;
    @ViewById
    Button four;

    TabAdapter tabAdapter;

    Retrofit retrofit, retrofit1;
    PromoService promoService;
    CardService cardService;
    PromoService mainService;

    String token;
    ArrayList<DiscoverPromotionCategory> listCategoryOutlet;
    View headerView;

    @Pref
    LoginPref_ loginPref;

    Bitmap bitmap;
    GoogleApiClient mGoogleApiClient;
    String authorization;
    protected ArrayList<Geofence> mGeofenceList;

    Tags tag;

    private String SENDER_ID = "136945608372";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private String HubName = "kartoo-devhub";
    private String HubListenConnectionString = "Endpoint=sb://kartoo-devhub-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=CZvooSmhFWdvyyEmSbVUk48j9HCHIMet/0StvPRbbTk=;EntityPath=kartoo-devhub";
    private static Boolean isVisible = false;

    private RegisterClient registerClient;
    private static final String BACKEND_ENDPOINT = "http://kartoo-dev.azure-mobile.net/";

    ArrayList<String> mallId;
    ArrayList<String> mallName;
    ArrayList<String> mallLat;
    ArrayList<String> mallLong;
    ArrayList<String> mallImg;

    ArrayList<String> list;
    double [] distance;
    String [] mallLatFinal;
    String [] mallLongFinal;
    String [] mallNameFinal;

    public static final double Rad = 6372.8; // In kilometers
    GPSTracker gps;
    LatLng myLocation;
    int limitLoop;


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    private PendingIntent mGeofencePendingIntent;

    @AfterViews
    public void init() {

        Log.e("TAG", "loginPref: "+loginPref.token().get());

        mVPmain.setOffscreenPageLimit(4);

        ArrayList<String> list = new ArrayList<String>();
        list.add("");

        View view = findViewById(R.id.mTLtab);
        //myLocation = getCurrentLocation();

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100); // half second between each showcase view
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "49");
        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(one)
                .setDismissOnTouch(true)
                .setTitleText("Home")
                .setContentText(Html.fromHtml(this.getString(R.string.home)))
                .setMaskColour(getResources().getColor(R.color.transparent))
                .setContentTextColor(getResources().getColor(R.color.white))
                .build()
        );
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(two)
                .setDismissOnTouch(true)
                .setTitleText("Categories")
                .setMaskColour(getResources().getColor(R.color.transparent))
                .setContentText("Browse through different promotion\ncategories")
                .build());
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(three)
                .setDismissOnTouch(true)
                .setTitleText("Locator")
                .setMaskColour(getResources().getColor(R.color.transparent))
                .setContentText("Find promotions based on location")
                .build());
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(four)
                .setDismissOnTouch(true)
                .setTitleText("Mall")
                .setMaskColour(getResources().getColor(R.color.transparent))
                .setContentText("Find promotions based on mall")
                .build());
        sequence.start();

        //GeofenceController.getInstance().init(this);

        MyHandler.mainActivity = this;
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

        gcm = GoogleCloudMessaging.getInstance(this);
        hub = new NotificationHub(HubName, HubListenConnectionString, this);
        registerWithNotificationHubs();
        registerClient = new RegisterClient(this, BACKEND_ENDPOINT);

        authorization = loginPref.token().get();
        retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(new ToStringConverterFactory()).build();
        cardService = retrofit.create(CardService.class);

        retrofit1 = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        mainService = retrofit1.create(PromoService.class);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();


        Intent intent = getIntent();
        String origin = intent.getStringExtra("FromEditProfile");

        if (origin==null || origin.equals("")){
            if (loginPref.email().get() == null || loginPref.email().get().equals("")) {
                final String email = intent.getStringExtra("email");
                loginPref.email().put(email);
            }
            if (loginPref.name().get() == null || loginPref.name().get().equals("")) {
                final String name = intent.getStringExtra("name");
                loginPref.name().put(name);
            }
            if (loginPref.token().get() == null || loginPref.token().get().equals("")) {
                final String token = intent.getStringExtra("auth");
                loginPref.token().put(token);
            }
            if (loginPref.type().get() == null || loginPref.type().get().equals("")) {
                final String type = intent.getStringExtra("type");
                loginPref.type().put(type);
            }
            if (loginPref.urlPhoto().get() == null || loginPref.urlPhoto().get().equals("")) {
                final String urlPhoto = intent.getStringExtra("urlPhoto");
                loginPref.urlPhoto().put(urlPhoto);
            }

            if (loginPref.uid().get() == null || loginPref.uid().get().equals("")) {
                if ((intent.getStringExtra("uidGplus") == null || intent.getStringExtra("uidGplus").equals(""))) {
                    final String uidGplus = intent.getStringExtra("uidGplus");
                    loginPref.uid().put(uidGplus);
                }
                if ((intent.getStringExtra("uidFacebook") == null || intent.getStringExtra("uidFacebook").equals(""))) {
                    final String uidFacebook = intent.getStringExtra("uidFacebook");
                    loginPref.uid().put(uidFacebook);
                }
            }
        }
        else {
            final String name = intent.getStringExtra("name");
            loginPref.name().put(name);
            bitmap = intent.getParcelableExtra("BitmapImage");
            if (bitmap!=null){
                loginPref.urlPhoto().put(BitMapToString(bitmap));
                loginPref.fromBitmap().put(1);
                Log.e("TAG", "urlImage: "+loginPref.urlPhoto().get() );
            }
        }

        if (!loginPref.type().get().equals("guest")){
            if(!loginPref.interest().get()){
                Intent intent5 = new Intent(MainActivity.this, InterestActivity_.class);
                intent5.putExtra("from", "MainActivity");
                startActivity(intent5);
            }
        }


        Call<String> notification = cardService.notifiication(authorization, loginPref.regid().get());
        notification.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                Log.e("TAG", "POST: "+response.code() );
                if (response.code() == 200) {
                    Log.e("TAG", "POST: "+response.body());
                    String notif = response.body();
                    notif = notif.substring(1, notif.length()-1);
                    loginPref.notifID().put(notif);
                    putgcm();
                }
                else if (response.code() == 401){
                    //doSignin();
                }
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });

        String projectToken = getString(R.string.mixpanel_token);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStamp = dateFormat.format(new Date());
        Log.e("TAG", "SKIPLOGIN "+"loginskip");
        try {
            JSONObject props = new JSONObject();
            props.put("TimeStamp", currentTimeStamp);
            props.put("email", loginPref.email().get());
            Log.e("TAG", "MixpanelAPI: "+loginPref.email().get() );
            mixpanel.track("Home Page", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }


        MixpanelAPI.People people = mixpanel.getPeople();
        people.identify(loginPref.email().get());
        mixpanel.getPeople().identify(loginPref.email().get());
        mixpanel.getPeople().set("$email",loginPref.email().get());
        people.initPushHandling("136945608372");

        boolean mustChangePassword = intent.getBooleanExtra("mustChangePassword", false);
        if (mustChangePassword){
            Intent intent2 = new Intent(MainActivity.this, ChangeYourPassword.class);
            intent2.putExtra("email", loginPref.email().get());
            intent2.putExtra("token", loginPref.token().get());
            startActivity(intent2);
        }

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        setUpNavDrawer();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        headerView = LayoutInflater.from(this).inflate(R.layout.drawer_header, null);

        ImageView mIVprofilePicture = (ImageView) headerView.findViewById(R.id.mIVprofilePicture);
        TextView mTVname = (TextView) headerView.findViewById(R.id.mTVname);
        ProgressBar mPBlevel = (ProgressBar) headerView.findViewById(R.id.mPBlevel);
        mNVdrawer.addHeaderView(headerView);
        try {
            mTVname.setText("Hi, " + loginPref.name().get().split(" ")[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            mTVname.setText("Hi, " + loginPref.name().get());
        }
        if (!loginPref.poin().get().equals("")) {
            mPBlevel.setProgress(Integer.parseInt(loginPref.poin().get()));
        } else {
            mPBlevel.setProgress(0);
        }
        if (loginPref.urlPhoto().get() == null || loginPref.urlPhoto().get().equals("")) {
            //mIVprofilePicture.setImageBitmap(bitmap);
        } else {
            if(loginPref.fromBitmap().get()==1){
                mIVprofilePicture.setImageBitmap(StringToBitMap(loginPref.urlPhoto().get()));
            }
            else {
                mIVprofilePicture.setBackground(null);
                Picasso.with(this).load(loginPref.urlPhoto().get())
                        .resize(300,300)
                        .centerInside()
                        .onlyScaleDown()
                        .into(mIVprofilePicture);
            }
        }



        mNVdrawer.setItemIconTintList(null);
        mNVdrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this, DrawerViewActivity_.class);
                switch (menuItem.getItemId()) {
//                    case R.id.drawer_my_feed:
//                        User user = new User();
//                        user.setId(loginPref.uid().get() + "");
//                        intent = new Intent(MainActivity.this, ActivityDetailFriends_.class);
//                        bundle.putSerializable("selectedUser", user);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;

                    case R.id.bank_page:
                        bundle.putString("selectedPage", "bankPage");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;

                    case R.id.drawer_bookmark:
                        if (loginPref.type().get().equals("guest")){
                            dialog();
                            break;
                        }
                        else {
                            bundle.putString("selectedPage", "editprofile");
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        }


//                    case R.id.drawer_banks:
//                        bundle.putString("selectedPage", "banks");
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;
                    case R.id.drawer_liked_promo:
                        if (loginPref.type().get().equals("guest")){
                            dialog();
                            break;
                        }
                        else {
                            bundle.putString("selectedPage", "likedPromo");
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        }

//                    case R.id.drawer_friends:
//                        bundle.putString("selectedPage", "friends");
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;

                    case R.id.drawer_rate:
                        String url = "http://app.kartoo.co/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    case R.id.contact_us_title:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "support@kartoo.co"));
                        startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
                        break;

                    case R.id.drawer_my_cards:
                        if (loginPref.type().get().equals("guest")){
                            dialog();
                            break;
                        }
                        else {
                            bundle.putString("selectedPage", "my_cards");
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        }

//                    case R.id.drawer_search:
//                        bundle.putString("selectedPage", "search");
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;
//                    case R.id.drawer_settings:
//                        bundle.putString("selectedPage", "settings");
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;
                    case R.id.drawer_about:
                        bundle.putString("selectedPage", "about");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
//                    case R.id.drawer_leaderboard:
//                        bundle.putString("selectedPage", "leaderboard");
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        break;
                    case R.id.drawer_logout:
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setMessage("Are you sure?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("sebelum","sebelum"+loginPref.name().get());
                                loginPref.name().remove();
                                loginPref.email().remove();
                                loginPref.token().remove();
                                loginPref.urlPhoto().remove();
                                loginPref.level().remove();
                                loginPref.uid().remove();
                                loginPref.type().remove();
                                loginPref.fromBitmap().remove();
                                loginPref.addcard().remove();
                                loginPref.regid().remove();
                                loginPref.notifID().remove();
                                loginPref.interest().remove();
                                // LoginManager.getInstance().logOut();
                                FacebookSdk.sdkInitialize(getApplicationContext());
                                LoginManager.getInstance().logOut();
                                if (mGoogleApiClient.isConnected()) {
                                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                                    mGoogleApiClient.disconnect();
                                    Log.e("asdf", "logged out");
                                }
                                Log.e("sesudah","sesudah"+loginPref.name().get());
                                Intent intent2 = new Intent(MainActivity.this, LandingActivity_.class);
                                startActivity(intent2);
                                finish();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                }
                mDLdrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mETsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.e("TAG", "onEditorAction: "+v.getText().toString());
                    search(v.getText().toString());
                    mETsearch.clearFocus();
                    return true;
                }
                return false;
            }
        });

        Log.e("TAG", "GeoFenceBefore: "+ mGeofenceList);

        final int[] icons = new int[] {
                R.drawable.first_tab,
                R.drawable.second_tab,
                R.drawable.third_tab,
                R.drawable.fourth_tab,
        };

        final String[] title = new String[] {"","Categories","Nearby","Malls"};

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.mTLtab);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //tabAdapter = new TabAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.tab_menu));

        //mVPmain.setAdapter(tabAdapter);
        //mVPmain.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
//        mTLtab.setTabGravity(TabLayout.GRAVITY_FILL);
        //mTLtab.setupWithViewPager(mVPmain);
/*
        for (int i = 0; i < 4; i++) {
            mTLtab.getTabAt(i).setIcon(icons[i]);
        }
*/

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.mTLtab);
        for (int i = 0; i < 4; i++) {
            mTLtab.addTab(mTLtab.newTab().setIcon(icons[i]));
        }
        /*
        mTLtab.addTab(mTLtab.newTab().setIcon(icons[2]));
        mTLtab.addTab(mTLtab.newTab().setText("Suggested Cards"));
        mTLtab.addTab(mTLtab.newTab().setText("Suggested Cards"));
        mTLtab.addTab(mTLtab.newTab().setText("Suggested Cards"));
        */
        mTLtab.setTabGravity(TabLayout.GRAVITY_FILL);
        /*
        mTLtab.addTab(mTLtab.newTab().setText("Personal Cards"));
        mTLtab.addTab(mTLtab.newTab().setText("Personal Cards"));
        mTLtab.addTab(mTLtab.newTab().setText("Suggested Cards"));
        mTLtab.addTab(mTLtab.newTab().setText("Personal Cards"));
*/
        String category = intent.getStringExtra("category");
        Log.e("TAG", "find our category: "+category );
        /*
        if (category!=null){
            mVPmain.setCurrentItem(1);
            tabAdapter.notifyDataSetChanged();
        }
*/
        final ViewPager viewPager = (ViewPager) findViewById(R.id.mVPmain);
        final PagerAdapter TabAdapter = new TabAdapter(getSupportFragmentManager(), mTLtab.getTabCount());
        viewPager.setAdapter(TabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLtab));

        mTLtab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                invalidateOptionsMenu();
                viewPager.setCurrentItem(tab.getPosition());
                TabAdapter.notifyDataSetChanged();
                toolbar_title.setVisibility(View.VISIBLE);
                toolbar_title_text.setVisibility(View.GONE);

                mLLsearch.setVisibility(View.GONE);
                mFLsearch.setVisibility(View.VISIBLE);
                mETsearch.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mETsearch.getWindowToken(), 0);

                if (tab.getPosition()!=0){
                    toolbar_title.setVisibility(View.GONE);
                    toolbar_title_text.setVisibility(View.VISIBLE);
                    toolbar_title_text.setText(title[tab.getPosition()]);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Intent intent1 = new Intent(this,DinningAlarmService_.class);
        this.startService(intent1);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void callMallNotification(){
        Log.e("TAG", "mGoogleApiClient: "+ mGoogleApiClient.isConnected());

            mGeofenceList = new ArrayList<Geofence>();
            mGeofencePendingIntent = null;
            retrofit1 = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
            mainService = retrofit1.create(PromoService.class);
            Call<ArrayList<MallGeofence>> getMall = mainService.mall(authorization);
            getMall.enqueue(new Callback<ArrayList<MallGeofence>>() {
                @Override
                public void onResponse(Response<ArrayList<MallGeofence>> response, Retrofit retrofit) {
                    Log.e("TAG", "namaMall: "+response.code());
                    if (response.code() == 200) {
                        int a = response.body().size();
                        mallId = new ArrayList<String>();
                        mallName = new ArrayList<String>();
                        mallLat = new ArrayList<String>();
                        mallLong = new ArrayList<String>();
                        mallImg = new ArrayList<String>();

                        Log.e("TAG", "Jumlah: "+a);

                        for (int i = 0; i < a; i++) {
                            mallId.add(response.body().get(i).getMallId());
                            mallName.add(response.body().get(i).getMallName());
                            mallLat.add(response.body().get(i).getLatitude());
                            mallLong.add(response.body().get(i).getLongitude());
                            mallImg.add(response.body().get(i).getUrl_img());
                        }

                        if (a!=0){
                            if (a>99) {
                                limitLoop = 100;
                            }
                            else {
                                limitLoop = a;
                            }

                            Log.e("TAG", "limitLoop: "+limitLoop);

                            Float radius = Float.parseFloat("0.2") * 1000.0f;

                            for (int i = 0; i < limitLoop; i++) {
                                mGeofenceList.add(new Geofence.Builder()
                                        .setRequestId(mallLat.get(i))
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                                        .setCircularRegion(Double.parseDouble(mallLat.get(i)), Double.parseDouble(mallLong.get(i)), radius)
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setLoiteringDelay(180000)
                                        .build());
                            }
                        }
                        Log.e("TAG", "mGoogleApiClient: "+ mGoogleApiClient.isConnected());
                        Log.e("TAG", "GeoFence: "+ mGeofenceList);

                        if (mGoogleApiClient.isConnected()){
                            if (mGeofenceList.size()!=0){
                                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                                        getGeofencingRequest(),
                                        getGeofencePendingIntent());
                                Log.e("TAG", "LocationServices: "+LOCATION_SERVICE);
                            }
                        }

                        Log.e("TAG", "namaMall: "+mallName);
                        Log.e("TAG", "mallLat: "+mallLat);
                        Log.e("TAG", "mallLong: "+mallLong);
                        sharedClass();
                    }

                    else if (response.code() == 401){
                        //doSignin();
                    }

                    }
                @Override
                public void onFailure(Throwable t) {

                }
            });

    }


    private void check() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<String>();
            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                permissionsNeeded.add("GPS");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
                permissionsNeeded.add("Location");
            if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
                permissionsNeeded.add("and Call");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                    });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
            callMallNotification();

        }else{
            callMallNotification();

        }

    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    callMallNotification();

                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void putgcm(){
        cardService = retrofit1.create(CardService.class);
        Notifications credential = new Notifications("gcm", loginPref.regid().get(), list);
        Call<ResponseDefault> putnotification = cardService.putnotification(authorization, loginPref.notifID().get(), credential);
        putnotification.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Response<ResponseDefault> response, Retrofit retrofit) {
                Log.e("TAG", "SUCESSGULL: "+response.code());
                if (response.code() == 200) {

                }
            }
            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public void sharedClass(){

        SharedPreferences prefs = getSharedPreferences("mall_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();

        Gson gson = new Gson();
        List<String> setmallName = new ArrayList<String>();
        List<String> setmallLat = new ArrayList<String>();
        List<String> setmallLong = new ArrayList<String>();
        List<String> setmallId = new ArrayList<String>();
        List<String> setmallImg = new ArrayList<String>();



        setmallName.addAll(mallName);
        setmallLat.addAll(mallLat);
        setmallLong.addAll(mallLong);
        setmallId.addAll(mallId);
        setmallImg.addAll(mallImg);

        String setName = gson.toJson(setmallName);
        String setLat = gson.toJson(setmallLat);
        String setLong = gson.toJson(setmallLong);
        String setId = gson.toJson(setmallId);
        String setImg = gson.toJson(setmallImg);


        edit.putString("mallName", setName);
        edit.putString("mallLat", setLat);
        edit.putString("mallLong", setLong);
        edit.putString("mallId", setId);
        edit.putString("mallImg", setImg);


        edit.apply();
    }

    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    //ToastNotify("Registered Successfully - RegId : " + regid);
                    Log.e("TAG", "REGID: "+regid);
                    loginPref.regid().put(regid);

                } catch (Exception e) {
                    //ToastNotify("Registration Exception Message - " + e.getMessage());
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    public void ToastNotify(final String notificationMessage)
    {
        if (isVisible == true)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


    @Click(R.id.mFLsearch)
    public void mIVsearchClick() {
        mLLsearch.setVisibility(View.VISIBLE);


        if (toolbar_title.getVisibility() == View.VISIBLE){
            toolbar_title.setVisibility(View.GONE);
        }
        if (toolbar_title_text.getVisibility() == View.VISIBLE){
            toolbar_title_text.setVisibility(View.GONE);
        }


        mFLsearch.setVisibility(View.GONE);
        mETsearch.requestFocus();
        mETsearch.setText("");
        mETsearch.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
    }

    @Click(R.id.mIVsearchClear)
    public void mIVsearchClearClick() {
        mLLsearch.setVisibility(View.GONE);

        int position = mTLtab.getSelectedTabPosition();

        if(position > 0){
            toolbar_title.setVisibility(View.GONE);
            toolbar_title_text.setVisibility(View.VISIBLE);
        }
        else {
            toolbar_title_text.setVisibility(View.GONE);
            toolbar_title.setVisibility(View.VISIBLE);
        }

        mFLsearch.setVisibility(View.VISIBLE);
        mETsearch.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mETsearch.getWindowToken(), 0);
    }

    public void dialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Oops!");
        alertDialogBuilder.setMessage("You need to login first");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("sebelum","sebelum"+loginPref.name().get());
                loginPref.name().remove();
                loginPref.email().remove();
                loginPref.token().remove();
                loginPref.urlPhoto().remove();
                loginPref.level().remove();
                loginPref.uid().remove();
                loginPref.type().remove();
                loginPref.fromBitmap().remove();
                loginPref.addcard().remove();
                loginPref.regid().remove();
                loginPref.notifID().remove();
                loginPref.interest().remove();

                // LoginManager.getInstance().logOut();
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    Log.e("asdf", "logged out");
                }
                Log.e("sesudah","sesudah"+loginPref.name().get());
                Intent intent2 = new Intent(MainActivity.this, LandingActivity_.class);
                startActivity(intent2);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();


        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        isVisible = false;
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_menu_orange);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDLdrawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }


    void search(final String query) {
        Log.e("TAG", "onEditorAction:query "+query);
        Intent intent = new Intent(MainActivity.this, SearchActivity_.class);
        intent.putExtra("query", query);
        startActivity(intent);
        mETsearch.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
    }

    public  void doSignin(){
        Toast.makeText(this, "Your session has expired, please login again", Toast.LENGTH_LONG).show();
        loginPref.name().remove();
        loginPref.email().remove();
        loginPref.token().remove();
        loginPref.urlPhoto().remove();
        loginPref.level().remove();
        loginPref.uid().remove();
        loginPref.type().remove();
        loginPref.fromBitmap().remove();
        loginPref.addcard().remove();
        loginPref.regid().remove();
        loginPref.notifID().remove();
        loginPref.interest().remove();

        // LoginManager.getInstance().logOut();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            Log.e("asdf", "logged out");
        }
        Log.e("sesudah","sesudah"+loginPref.name().get());
        Intent intent2 = new Intent(MainActivity.this, LandingActivity_.class);
        startActivity(intent2);
        finish();
    }


    @Override
    public void onBackPressed() {
        if(mDLdrawer.isDrawerOpen(GravityCompat.START))
            mDLdrawer.closeDrawer(GravityCompat.START);
        else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() == 0)
                super.onBackPressed();
            else
                fm.popBackStack();
        }


        /*
        mLLsearch.setVisibility(View.GONE);

        if (toolbar_title.getVisibility() == View.GONE){
            toolbar_title.setVisibility(View.VISIBLE);
        }

        if (toolbar_title_text.getVisibility() == View.GONE){
            toolbar_title_text.setVisibility(View.VISIBLE);
        }

        mFLsearch.setVisibility(View.VISIBLE);
        mETsearch.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mETsearch.getWindowToken(), 0);
        */
    }
/*
    public LatLng getCurrentLocation() {
        gps = new GPSTracker(this);
        double latitude;
        double longitude;
        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.e("TAG", "getCurrentLocation: "+latitude+longitude );
            return new LatLng(latitude, longitude);
        }
        else{
            return new LatLng(-6.183997, 106.834849);
        }
    }
*/
    @Override
    public void onConnected(Bundle bundle) {
        Log.e("TAG", "onConnected"+"apa");
        check();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
