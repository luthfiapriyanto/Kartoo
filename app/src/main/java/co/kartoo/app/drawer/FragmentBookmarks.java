package co.kartoo.app.drawer;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import co.kartoo.app.R;
import co.kartoo.app.category.PromoFromCategoryAdapter;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.Outlet;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

@EFragment(R.layout.fragment_list_bank)
public class FragmentBookmarks extends Fragment {
    PromoFromCategoryAdapter adapter;
    ArrayList<Outlet> listBookmarked;
    @ViewById
    RecyclerView mRVbanks;
    @Pref
    LoginPref_ loginPref;
    String token;
    Retrofit retrofit;
    PromoService promoService;

    @AfterViews
    void init() {
        listBookmarked = new ArrayList<>();
        mRVbanks.setLayoutManager(new LinearLayoutManager(getActivity()));
        retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        promoService = retrofit.create(PromoService.class);
        token = "Bearer " + loginPref.token().get();

        //adapter = new PromoFromCategoryAdapter(getActivity(), listBookmarked, promoService, token);
        mRVbanks.setAdapter(adapter);

        Call<ArrayList<Outlet>> favoritePromotionsCall = promoService.getAllFavoriteOutlets(token, loginPref.uid().get() + "");
        favoritePromotionsCall.enqueue(new Callback<ArrayList<Outlet>>() {
            @Override
            public void onResponse(Response<ArrayList<Outlet>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.code() == 200) {
                        listBookmarked.clear();
                        listBookmarked.addAll(response.body());
                        //adapter.setBookmarkedOutlet(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
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

}
