package co.kartoo.app.cards.suggestioncard;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.kartoo.app.R;
import co.kartoo.app.cards.suggestioncard.applycard.ApplyLandingActivity;
import co.kartoo.app.cards.suggestioncard.applycard.ApplyLandingActivity_;
import co.kartoo.app.cards.suggestioncard.applycard.ApplyProfileActivity_;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.rest.CardService;
import co.kartoo.app.rest.model.newest.Availablecards;
import co.kartoo.app.rest.model.newest.CardDetail;
import co.kartoo.app.rest.model.newest.Details;
import co.kartoo.app.rest.model.newest.Perks;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

@EFragment(R.layout.fragment_suggestion_cards)
public class FragmentSuggestionCards extends Fragment {

    @ViewById(R.id.mIVindicator1)
    View mVindicator1;
    @ViewById(R.id.mIVindicator2)
    View mVindicator2;
    @ViewById(R.id.mIVindicator3)
    View mVindicator3;
    @ViewById(R.id.mIVindicator4)
    View mVindicator4;
    @ViewById(R.id.mIVindicator5)
    View mVindicator5;

    @ViewById
    Button buttonMoreCards;
    @ViewById
    Button mBtnApply;
    @ViewById
    Button mBtnMoreDetail;

    @ViewById
    NestedScrollView scrollView;

    @ViewById
    RelativeLayout RLHeadlineCards, timeOut;

    @ViewById
    ViewPager mVPcardTop;
    @ViewById
    TextView txtLeft;
    @ViewById
    TextView txtRight;
    @ViewById
    TextView txtCenter;

    @ViewById
    RecyclerView mRVperks;
    @ViewById
    RecyclerView mRVdetail;

    @ViewById
    ProgressBar progressBar;

    @Pref
    LoginPref_ loginPref;

    Retrofit retrofit;
    CardService service;

    PagerAdapter adapter;

    AdapterPerks perksAdapter;
    AdapterDetail detailAdapter;

    ArrayList<Perks> listPerks;
    ArrayList<Details> listDetails;

    String[] Id;
    String[] Name;
    String[] UrlImage;


    String authorization;


    @AfterViews
    public void init() {

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        timeOut.setVisibility(View.GONE);

        mRVdetail.setVisibility(View.GONE);

        mVPcardTop.setClipToPadding(false);
        mVPcardTop.setPadding(60, 0, 60, 0);
        mVPcardTop.setPageMargin(10);

        retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(CardService.class);

        authorization = loginPref.token().get();

        listPerks = new ArrayList<>();
        perksAdapter = new AdapterPerks(getContext(), listPerks);

        listDetails = new ArrayList<>();
        detailAdapter = new AdapterDetail(getContext(), listDetails);

        mRVperks.setNestedScrollingEnabled(false);
        mRVperks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRVperks.setAdapter(perksAdapter);

        mRVdetail.setNestedScrollingEnabled(false);
        mRVdetail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRVdetail.setAdapter(detailAdapter);

        Log.e("TAG", "suggestedCardTop: "+authorization );

        cardsTopRequest();
        mixpanel();
    }

    public void cardsTopRequest(){
        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        timeOut.setVisibility(View.GONE);

        Call<ArrayList<Availablecards>> suggestedCardTop = service.getSuggestedTop(loginPref.token().get());
        Log.e("TAG", "init: "+suggestedCardTop);
        suggestedCardTop.enqueue(new Callback<ArrayList<Availablecards>>() {
            @Override
            public void onResponse(final Response<ArrayList<Availablecards>> response, Retrofit retrofit) {
                Log.e("TAG", "suggestedCardTop: " + response.code());

                if (response.code() == 200) {

                    scrollView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    timeOut.setVisibility(View.GONE);

                    int a = response.body().size();

                    Id = new String[a];
                    Name = new String[a];
                    UrlImage = new String[a];

                    for (int i = 0; i < a; i++) {
                        Log.e("TAG", "suggestedCardTop: " + response.body().get(i).getUrl_img());

                        Id[i] = response.body().get(i).getId();
                        Name[i] = response.body().get(i).getCard_Edition();
                        UrlImage[i] = response.body().get(i).getUrl_img();
                    }
                    cardsTopViewPager();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.e("TAG", "suggestedCardTop: "+"Fail" );
                scrollView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                timeOut.setVisibility(View.VISIBLE);
                //do Refresh
            }
        });
    }

    public void cardsDetailRequest(String id){
        Call<CardDetail> cardDetail = service.getCardDetail(loginPref.token().get(), id);
        Log.e("TAG", "init: "+cardDetail);
        cardDetail.enqueue(new Callback<CardDetail>() {
            @Override
            public void onResponse(final Response<CardDetail> response, Retrofit retrofit) {
                Log.e("TAG", "cardDetail: " + response.code());
                if (response.code() == 200) {

                    txtLeft.setText(response.body().getMinimumIncome());
                    txtCenter.setText(response.body().getCardFee());
                    txtRight.setText(response.body().getMonthlyRateFee());

                    EventBus.getDefault().postSticky(new EventPerks(response.body().getPerks()));

                    EventBus.getDefault().postSticky(new EventDetail(response.body().getDetails()));

                    Log.e("TAG", "getCardDetail: "+response.body().getDetails().size() );
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.e("TAG", "cardDetail: "+t);
                //do Refresh
            }
        });
    }

    public void cardsTopViewPager(){
        adapter = new CardsAdapterViewPager(getActivity(), Id, Name, UrlImage);
        updateIndicators(0);
        cardsDetailRequest(Id[0]);
        if (Name.length != 0){
            mVPcardTop.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    updateIndicators(position);
                    cardsDetailRequest(Id[position]);
                    perksAdapter.notifyDataSetChanged();
                    detailAdapter.notifyDataSetChanged();

                    Log.e("TAG", "onPageSelected: "+Id[position] );
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            mVPcardTop.setAdapter(adapter);
        }
    }

    public void mixpanel(){
        String projectToken = getString(R.string.mixpanel_token);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), projectToken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStamp = dateFormat.format(new Date());
        try {
            JSONObject props = new JSONObject();
            props.put("TimeStamp", currentTimeStamp);
            props.put("email", loginPref.email().get());
            mixpanel.track("My Cards Page", props);
        } catch (JSONException e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }
    }

    @Click(R.id.timeOut)
    public void timeOutClick(){
        cardsTopRequest();
    }

    @Click(R.id.mBtnApply)
    public void mBtnApplyClick() {
        Intent intent = new Intent(getActivity(), ApplyLandingActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.buttonMoreCards)
    public void buttonMoreCardsClick() {
        Intent intent = new Intent(getActivity(), MoreCardActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.mBtnMoreDetail)
    public void mBtnMoreDetailClick(){
        mRVdetail.setVisibility(View.VISIBLE);
        mBtnMoreDetail.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(EventPerks event) {
        listPerks.clear();
        listPerks.addAll(event.getListPerks());
        perksAdapter.notifyDataSetChanged();
    }

    public void onEvent(EventDetail event) {
        listDetails.clear();
        listDetails.addAll(event.getlistDetail());
        detailAdapter.notifyDataSetChanged();
    }

    public void updateIndicators(int position) {
        switch (position) {
            case 0:
                mVindicator1.setBackgroundResource(R.drawable.indicator_blue);
                mVindicator1.requestLayout();
                mVindicator2.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator2.requestLayout();
                mVindicator3.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator3.requestLayout();
                mVindicator4.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator4.requestLayout();
                break;

            case 1:
                mVindicator1.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator1.requestLayout();
                mVindicator2.setBackgroundResource(R.drawable.indicator_blue);
                mVindicator2.requestLayout();
                mVindicator3.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator3.requestLayout();
                mVindicator4.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator4.requestLayout();

                break;

            case 2:
                mVindicator1.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator1.requestLayout();
                mVindicator2.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator2.requestLayout();
                mVindicator3.setBackgroundResource(R.drawable.indicator_blue);
                mVindicator3.requestLayout();
                mVindicator4.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator4.requestLayout();

                break;

            case 3:
                mVindicator1.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator1.requestLayout();
                mVindicator2.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator2.requestLayout();
                mVindicator3.setBackgroundResource(R.drawable.indicator_blue_dead);
                mVindicator3.requestLayout();
                mVindicator4.setBackgroundResource(R.drawable.indicator_blue);
                mVindicator4.requestLayout();
                break;

        }
    }
}
