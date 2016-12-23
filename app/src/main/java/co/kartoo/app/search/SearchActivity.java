package co.kartoo.app.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Plus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.kartoo.app.MainActivity_;
import co.kartoo.app.R;
import co.kartoo.app.category.FragmentCategory_;
import co.kartoo.app.events.SearchEvent;
import co.kartoo.app.events.TrendingFinishedEvent;
import co.kartoo.app.landing.LandingActivity_;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.nearby.GPSTracker;
import co.kartoo.app.promo.AvailableOutlet.TrendingPromoAdapterCategory;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.newest.DiscoverPromotionCategory;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by MartinOenang on 11/2/2015.
 */
@EActivity(R.layout.activity_search)
public class SearchActivity extends AppCompatActivity{
    @ViewById
    RecyclerView mRVresult;
    @ViewById
    EditText mETsearch;
    @ViewById
    Toolbar mToolbar;
    @ViewById
    LinearLayout mLLsearch;
    @ViewById
    TextView mTVtitle;
    @ViewById
    FrameLayout mFLsearch;
    @ViewById
    ImageView mIVsearchClear;
    @ViewById
    ImageView mIVsearch;

    @ViewById
    ToggleButton mTBactiveNow;
    @ViewById
    ToggleButton mTBmyCards;
    @ViewById
    ImageView mIVfilter;
    @ViewById
    ProgressBar mPBloading;
    @ViewById
    LinearLayout SearchContainer;
    @ViewById
    RelativeLayout oops;
    @ViewById
    TextView toCategory;
    @ViewById
    RelativeLayout relativeLayout3;
    @ViewById
    RelativeLayout timeOut;
    @ViewById
    ImageView reload;

    @ViewById
    Spinner sortbySpinner;

    @Pref
    LoginPref_ loginPref;

    Retrofit retrofit;
    PromoService promoService;
    String token;
    String query;

    LatLng myLocation;
    String[] sortBy = {
            "Distance",
            "Popular",
            "Ending Soon",
            "A to Z",
    };
    String sortValue;
    GPSTracker gps;
    int a;
    int n;
    GoogleApiClient mGoogleApiClient;


    TrendingPromoAdapterCategory adapter;
    ArrayList<DiscoverPromotionCategory> listCategoryOutlet;

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
    @AfterViews
    void init() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        myLocation = getCurrentLocation();

        setUpNavDrawer();
        retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        promoService = retrofit.create(PromoService.class);
        token = loginPref.token().get();


        listCategoryOutlet = new ArrayList<>();


        Intent intent = getIntent();
        query = intent.getStringExtra("query");
        //firstsearch(query);

        mETsearch.setText(query);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = new TrendingPromoAdapterCategory(getApplicationContext(),listCategoryOutlet);
        mRVresult.setLayoutManager(new LinearLayoutManager(this));
        mRVresult.setAdapter(adapter);

        //spinner SortBy
        ArrayAdapter<String> adapterspinner= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, sortBy);
        sortbySpinner.setAdapter(adapterspinner);
        if (myLocation.longitude==0){
            a=2;
            sortbySpinner.setSelection(a);
            sortValue = "expiring";
        }
        else {
            sortbySpinner.setSelection(0);
            sortValue = "distance";
        }

        sortbySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos==0){
                    if (myLocation.longitude==0){
                        Toast.makeText(SearchActivity.this, "Are you lost? Please turn on your location", Toast.LENGTH_LONG).show();
                        sortbySpinner.setSelection(a);
                    }
                    else {
                        sortValue = "distance";
                        mRVresult.setVisibility(View.GONE);

                    }
                } else if (pos==1){
                    sortValue = "popular";
                    mRVresult.setVisibility(View.GONE);

                } else if (pos==2){
                    sortValue = "expiring";
                    mRVresult.setVisibility(View.GONE);

                } else if (pos==3){
                    sortValue = "alphabet";
                    mRVresult.setVisibility(View.GONE);

                }
                Log.e("TAG", "onItemSelected: "+pos);
                Log.e("TAG", "onItemSelected: "+sortbySpinner.getSelectedItem().toString().toLowerCase());
                a = sortbySpinner.getSelectedItemPosition();

                //if (n!=0){
                    search(query, sortValue);
                //}
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.e("TAG", "query: "+query+sortValue);

        mTVtitle.setVisibility(View.GONE);

/*
        Call<ArrayList<DiscoverPromotionCategory>> searchCall = promoService.searchAll(token,query,sortValue,myLocation.latitude+"", myLocation.longitude+"");
        Log.e("TAG", "masuk Call: "+searchCall );
        searchCall.enqueue(new Callback<ArrayList<DiscoverPromotionCategory>>() {
            @Override
            public void onResponse(Response<ArrayList<DiscoverPromotionCategory>> response, Retrofit retrofit) {
                mPBloading.setVisibility(View.VISIBLE);
                SearchContainer.setVisibility(View.GONE);
                oops.setVisibility(View.GONE);
                mTVtitle.setVisibility(View.GONE);
                Log.e("TAG", "responseSearch: "+response.code() );

                    if(response.code() == 200) {
                        Log.e("TAG", "responselenghtPertama: "+response.body().size() );
                        if(response.body().size()==0){
                            timeOut.setVisibility(View.GONE);
                            mPBloading.setVisibility(View.GONE);
                            oops.setVisibility(View.VISIBLE);
                            SearchContainer.setVisibility(View.GONE);
                            mRVresult.setVisibility(View.GONE);

                        }
                        else {
                            n=0;
                            timeOut.setVisibility(View.GONE);
                            oops.setVisibility(View.GONE);
                            SearchContainer.setVisibility(View.VISIBLE);
                            mPBloading.setVisibility(View.GONE);
                            mRVresult.setVisibility(View.VISIBLE);
                            listCategoryOutlet.clear();
                            listCategoryOutlet.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }
                else if (response.code() == 401){
                        Toast.makeText(SearchActivity.this, "Your session has expired, please login again", Toast.LENGTH_LONG).show();
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
                        //LoginManager.getInstance().logOut();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            Log.e("asdf", "logged out");
                        }

                        Log.e("sesudah","sesudah"+loginPref.name().get());
                        Intent intent2 = new Intent(SearchActivity.this, LandingActivity_.class);
                        startActivity(intent2);
                        finish();

                    }
                }

            @Override
            public void onFailure(Throwable t) {
                timeOut.setVisibility(View.VISIBLE);
                oops.setVisibility(View.GONE);
                SearchContainer.setVisibility(View.GONE);
                mPBloading.setVisibility(View.GONE);
                mRVresult.setVisibility(View.GONE);
            }
        });
*/
        mETsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(v.getText().toString(), sortValue);
                    mETsearch.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                    return true;
                }
                return false;
            }
        });
    }


    @Click(R.id.mFLsearch)
    public void mIVsearchClick() {
        mLLsearch.setVisibility(View.VISIBLE);
        mTVtitle.setVisibility(View.GONE);
        mFLsearch.setVisibility(View.GONE);
        mETsearch.requestFocus();
        mETsearch.setFocusable(true);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
    }

    @Click(R.id.mIVsearchClear)
    public void mIVsearchClearClick() {
        mLLsearch.setVisibility(View.GONE);
        mTVtitle.setVisibility(View.VISIBLE);
        mFLsearch.setVisibility(View.VISIBLE);
        mIVsearch.setVisibility(View.VISIBLE);

        mETsearch.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mETsearch.getWindowToken(), 0);
    }

    @Click(R.id.toCategory)
    public void toCategoryClick() {
        Intent intent = new Intent(SearchActivity.this, MainActivity_.class);
        intent.putExtra("category", "1");
        startActivity(intent);
        finish();
    }

    @Click(R.id.reload)
    public void reloadClick() {
        search(query, sortValue);
    }

    void search(String query, String sortValue) {

        mPBloading.setVisibility(View.VISIBLE);
        SearchContainer.setVisibility(View.GONE);
        oops.setVisibility(View.GONE);
        timeOut.setVisibility(View.GONE);

        Call<ArrayList<DiscoverPromotionCategory>> searchCall = promoService.searchAll(token, query, sortValue, myLocation.latitude+"", myLocation.longitude+"");
        searchCall.enqueue(new Callback<ArrayList<DiscoverPromotionCategory>>() {
            @Override
            public void onResponse(Response<ArrayList<DiscoverPromotionCategory>> response, Retrofit retrofit) {
                /*
                mPBloading.setVisibility(View.VISIBLE);
                SearchContainer.setVisibility(View.GONE);
                oops.setVisibility(View.GONE);
                mTVtitle.setVisibility(View.GONE);
                */
                Log.e("TAG", "responseSearch: "+response.code() );

                    if(response.code() == 200) {
                        Log.e("TAG", "responselenghtKedua: "+response.body().size() );
                        if(response.body().size()==0){
                            mPBloading.setVisibility(View.GONE);
                            oops.setVisibility(View.VISIBLE);
                            SearchContainer.setVisibility(View.GONE);
                            timeOut.setVisibility(View.GONE);
                            mRVresult.setVisibility(View.GONE);


                        }
                        else {
                            timeOut.setVisibility(View.GONE);
                            mRVresult.setVisibility(View.VISIBLE);
                            oops.setVisibility(View.GONE);
                            SearchContainer.setVisibility(View.VISIBLE);
                            mPBloading.setVisibility(View.GONE);
                            listCategoryOutlet.clear();
                            listCategoryOutlet.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else if (response.code() == 401){
                        Toast.makeText(SearchActivity.this, "Your session has expired, please login again", Toast.LENGTH_LONG).show();
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
                        //LoginManager.getInstance().logOut();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            Log.e("asdf", "logged out");
                        }

                        Log.e("sesudah","sesudah"+loginPref.name().get());
                        Intent intent2 = new Intent(SearchActivity.this, LandingActivity_.class);
                        startActivity(intent2);
                        finish();

                    }
                }


            @Override
            public void onFailure(Throwable t) {
                timeOut.setVisibility(View.VISIBLE);
                oops.setVisibility(View.GONE);
                mRVresult.setVisibility(View.GONE);

                SearchContainer.setVisibility(View.GONE);
                mPBloading.setVisibility(View.GONE);
            }
        });
    }

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
            return new LatLng(0, 0);
        }
    }

}
