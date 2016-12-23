package co.kartoo.app.category;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.kartoo.app.R;
import co.kartoo.app.events.TrendingFinishedEventCategory;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.promo.AvailableOutlet.TrendingPromoAdapterCategory;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.Category;
import co.kartoo.app.rest.model.CategoryOutlet;
import co.kartoo.app.views.MeasuredStaggeredGridLayoutManagerViewAll;
import co.kartoo.app.views.SpaceItemDecoration;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

@EFragment(R.layout.fragment_categories)
public class FragmentCategory extends Fragment{

    @ViewById
    RecyclerView mRVcategories;
    @ViewById
    ProgressBar progressBarPopular;
    @ViewById
    RelativeLayout timeOut;
    @ViewById
    ImageView reload;

    String authorization;
    ArrayList<Category> listTrendingPromo;
    CategoryAdapter adapterTrendingPromo;

    @Pref
    LoginPref_ loginPref;

    @AfterViews
    public void init() {
        mRVcategories.setVisibility(View.GONE);
        progressBarPopular.setVisibility(View.VISIBLE);
        listTrendingPromo = new ArrayList<>();

        adapterTrendingPromo = new CategoryAdapter(getContext(),listTrendingPromo);

        mRVcategories.addItemDecoration(new SpaceItemDecoration(3));
        mRVcategories.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        mRVcategories.setAdapter(adapterTrendingPromo);

        authorization = loginPref.token().get();
        loadData();
    }

    public void loadData() {
        mRVcategories.setVisibility(View.GONE);
        progressBarPopular.setVisibility(View.VISIBLE);
        timeOut.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        PromoService promoService = retrofit.create(PromoService.class);

        Call<ArrayList<Category>> promoByCategoryCall = promoService.getCategory(authorization);
        promoByCategoryCall.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Response<ArrayList<Category>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.e("TAG", "Category: myCard"+ response.code());
                    if (response.code() == 200) {
                        mRVcategories.setVisibility(View.VISIBLE);
                        progressBarPopular.setVisibility(View.GONE);
                        timeOut.setVisibility(View.GONE);

                        EventBus.getDefault().postSticky(new CategoryEvent(response.body()));
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                mRVcategories.setVisibility(View.GONE);
                progressBarPopular.setVisibility(View.GONE);
                timeOut.setVisibility(View.VISIBLE);
            }
        });
    }

    @Click(R.id.reload)
    public void  reloadClick(){
        Log.e("TAG", "reloadClick: "+"Click" );
        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();
        listTrendingPromo.clear();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(CategoryEvent event) {
        Log.e("eventTrending", "caught");
        listTrendingPromo.clear();
        listTrendingPromo.addAll(event.getListPromo());
        adapterTrendingPromo.notifyDataSetChanged();
    }
}
