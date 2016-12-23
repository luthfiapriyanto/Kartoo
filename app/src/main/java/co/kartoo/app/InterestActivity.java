package co.kartoo.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import co.kartoo.app.category.CategoryEvent;
import co.kartoo.app.interest.InterestAdapter;
import co.kartoo.app.interest.InterestInterface;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.rest.MainService;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.Category;
import co.kartoo.app.rest.model.ResponseDefault;
import co.kartoo.app.rest.model.newest.Interest;
import co.kartoo.app.views.SpaceItemDecoration;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.support.v7.recyclerview.R.attr.layoutManager;
import static co.kartoo.app.R.id.mIVoverlay;

@EActivity(R.layout.activity_interest)
public class InterestActivity extends AppCompatActivity implements InterestInterface{

    @ViewById
    RecyclerView mRVinterest;
    @ViewById
    Button mBtnDone;
    @ViewById
    ProgressBar progressBar;

    @Pref
    LoginPref_ loginPref;

    ArrayList<Category> listInterest;
    ArrayList<String> interestList;
    ArrayList<String> storeInterest;
    InterestAdapter adapterInterest;

    ProgressDialog loadingDialog;

    String from, authorization;

    Boolean found;

    @AfterViews
    public void init() {

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        Log.e("TAG", "init: " + from);

        authorization = loginPref.token().get();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        PromoService promoService = retrofit.create(PromoService.class);

        loadingDialog = new ProgressDialog(InterestActivity.this);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);

        listInterest = new ArrayList<>();
        adapterInterest = new InterestAdapter(this, listInterest, promoService, authorization);
        interestList = new ArrayList<>();
        storeInterest = new ArrayList<>();

        mRVinterest.setAdapter(adapterInterest);
        mRVinterest.setLayoutManager(new GridLayoutManager(this, 2));
        mRVinterest.addItemDecoration(new SpaceItemDecoration(3));

        progressBar.setVisibility(View.VISIBLE);
        mRVinterest.setVisibility(View.GONE);

        Call<ArrayList<Category>> promoByCategoryCall = promoService.getCategory(loginPref.token().get());
        promoByCategoryCall.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Response<ArrayList<Category>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.e("TAG", "Interest" + response.code());
                    if (response.code() == 200) {
                        storeInterest.clear();

                        int a = response.body().size();
                        for (int i = 0; i < a; i++) {
                            storeInterest.add(response.body().get(i).getId());
                        }

                        EventBus.getDefault().postSticky(new CategoryEvent(response.body()));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        Call<ArrayList<Interest>> getInterest = promoService.getInterest(loginPref.token().get());
        getInterest.enqueue(new Callback<ArrayList<Interest>>() {
            @Override
            public void onResponse(Response<ArrayList<Interest>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Log.e("TAG", "Interest" + response.code());
                    if (response.code() == 200) {

                        progressBar.setVisibility(View.GONE);
                        mRVinterest.setVisibility(View.VISIBLE);

                        int a = response.body().size();
                        for (int i = 0; i < a; i++) {
                            ArrayList<Interest> interestChoosen = response.body();
                            adapterInterest.setInterest(interestChoosen);
                            adapterInterest.notifyDataSetChanged();
                            interestList.add(response.body().get(i).getCategory().getId());
                            Log.e("TAG", "onResponse: " + response.body().get(i).getId());
                            Log.e("TAG", "interestList: " + interestList);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    @Override
    public void userItemClick(int pos) {
        found = false;
        if (interestList.size() !=0){
            for (int i = 0; i < interestList.size(); i++) {
                Log.e("TAG", "Value: "+"LOOP"+i);
                if (interestList.get(i).equals(listInterest.get(pos).getId())){
                    interestList.remove(listInterest.get(pos).getId());
                    found = true;
                    break;
                }
            }
            if(!found){
                interestList.add(listInterest.get(pos).getId());
            }
        }
        else if (interestList.size()==0){
            interestList.add(listInterest.get(pos).getId());
        }
    }

    public void doPost(){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build();
        final MainService service = retrofit.create(MainService.class);

        Call<ResponseDefault> postInterest = service.doInterest(loginPref.token().get(), interestList);
        postInterest.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Response<ResponseDefault> response, Retrofit retrofit) {

                Log.e("TAG", "onResponse: " + response.code());
                if (response.code() == 200) {
                    interestList.clear();
                    if (from.equals("MainActivity")){
                        Intent intent = new Intent(InterestActivity.this, MainActivity_.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "Change this later in Edit Profile", Toast.LENGTH_LONG).show();
                    }
                    else if(from.equals("EditProfile")){
                        finish();
                    }

                    loginPref.interest().put(true);
                    if (loadingDialog.isShowing())
                        loadingDialog.dismiss();

                }
                if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Failed to save your interest", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Failed to save your interest", Toast.LENGTH_LONG).show();

            }
        });

    }


    @Click(R.id.mBtnDone)
    public void mBtnDoneClick() {
        Log.e("TAG", "mBtnDoneClick: "+interestList );
        loadingDialog.show();
        doPost();
    }

    @Override
    public void onStart() {
        super.onStart();
        listInterest.clear();
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
        listInterest.clear();
        listInterest.addAll(event.getListPromo());
        adapterInterest.notifyDataSetChanged();
    }

}
