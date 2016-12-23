package co.kartoo.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.models.ProfilePref;
import co.kartoo.app.models.ProfilePref_;
import co.kartoo.app.rest.MainService;
import co.kartoo.app.rest.model.ResponseDefault;
import co.kartoo.app.rest.model.UpdateProfile;
import co.kartoo.app.rest.model.newest.Cities;
import co.kartoo.app.rest.model.newest.UserProfile;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@EActivity (R.layout.activity_edit_profile)
public class EditProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    @ViewById
    Toolbar mToolbar;

    @ViewById
    TextView datePicker, mTVtitle, mTVeditProfileEmail, mTVinterest;
    @ViewById
    FrameLayout mFLprofilePict;
    @ViewById
    ImageView mIVeditProfileProfilePict;
    @ViewById
    EditText mETeditProfileFullname, mETeditProfileAdress, mETeditProfilePhonenumber;
    //@ViewById
    //AutoCompleteTextView mETeditProfileCity;
    @ViewById
    ProgressBar progressBar;

    @ViewById
    Button mBtnSave;

    //@ViewById
    //ProgressDialog loadingDialog;

    @ViewById
    RadioButton male, female;
    @Pref
    ProfilePref_ profilePref;
    AutoCompleteTextView mETeditProfileCity;

    String Gender;
    //Button mBtnSave;
    ProgressDialog loadingDialog;

    //Bitmap bitMap;
    String token;
    String name;
    String image;
    String email;
    String isBitmap;

    String[] cities;

    boolean checked;
    //RadioButton male, female;

    @AfterViews
    void init() {

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        name = intent.getStringExtra("name");
        image = intent.getStringExtra("image");
        email = intent.getStringExtra("email");
        //isBitmap = intent.getStringExtra("isBitmap");

        loadingDialog = new ProgressDialog(EditProfile.this);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);
        mETeditProfileCity = (AutoCompleteTextView) findViewById(R.id.mETeditProfileCity);

        mTVeditProfileEmail.setText(email);


        mTVtitle.setText("Edit Profile");

        if (!name.equals("")){
            mETeditProfileFullname.setText(name);
        }
        if (!image.equals("")) {
                Picasso.with(this).load(image).into(mIVeditProfileProfilePict);
        }

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setCollapsible(true);
        }

        if (profilePref.dob().get() != null || !profilePref.dob().get().equals("")) {
            datePicker.setText(profilePref.dob().get());
        }
        if (profilePref.city().get() != null || !profilePref.city().get().equals("")) {
            mETeditProfileCity.setText(profilePref.city().get());
        }
        if (profilePref.address().get() != null || !profilePref.address().get().equals("")) {
            mETeditProfileAdress.setText(profilePref.address().get());
        }
        if (profilePref.phone().get() != null || !profilePref.phone().get().equals("")) {
            mETeditProfilePhonenumber.setText(profilePref.phone().get());
        }
        if (profilePref.gender().get() != null || !profilePref.gender().get().equals("")) {
                if (profilePref.gender().get().equals("Male")){
                    male.setChecked(true);
                }else if (profilePref.gender().get().equals("Female")){
                    female.setChecked(true);
                }
            }


        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://kartoo-dev.azure-mobile.net/").addConverterFactory(GsonConverterFactory.create()).build();
        final MainService service = retrofit.create(MainService.class);
        Call<Cities> getCities = service.getCities(token);
        getCities.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Response<Cities> response, Retrofit retrofit) {
                Log.e("TAG", "onResponse: " + response.code());
                if (response.code() == 200) {
                    Log.e("TAG", "editProfile: "+response.body());
                    int a = response.body().getCities().size();
                    cities = new String[a];

                    for (int i = 0; i < a; i++) {
                        cities[i] = response.body().getCities().get(i).getName();
                    }
                    AutoCompleteCity();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
            }
        });

        Call<UserProfile> getProfile = service.getUserProfile(token);
        getProfile.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Response<UserProfile> response, Retrofit retrofit) {
                if (response.code() == 200) {
                    progressBar.setVisibility(View.GONE);
                    mETeditProfileFullname.setText(response.body().getName());
                    mETeditProfileAdress.setText(response.body().getAddress());
                    mETeditProfilePhonenumber.setText(response.body().getPhonenumber());
                    datePicker.setText(response.body().getDateofbirth());
                    mTVeditProfileEmail.setText(response.body().getEmail());
                    mETeditProfileCity.setText(response.body().getCity());

                    Log.e("TAG", "gender: "+response.body().getGender() );

                    if (response.body().getGender()!=null){
                        if (response.body().getGender().equals("Male")){
                            male.setChecked(true);
                        }else if (response.body().getGender().equals("Female")){
                            female.setChecked(true);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
            }
        });

        setUpNavDrawer();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Click(R.id.mBtnSave)
    public void mBtnSaveClick () {
        updateProfile();
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }
    @Click(R.id.mTVinterest)
    public void mTVinterestClick () {
        Intent intent = new Intent(EditProfile.this, InterestActivity_.class);
        intent.putExtra("from", "EditProfile");
        startActivity(intent);
    }
    @Click(R.id.datePicker)
    public void datePickerClick () {
        Calendar now = Calendar.getInstance();
        now.set(1990,1,1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(EditProfile.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void AutoCompleteCity(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
        mETeditProfileCity.setAdapter(adapter);
    }

    public void updateProfile() {
        final String fullname = mETeditProfileFullname.getText().toString();
        String gender = Gender;

        String dateofbirth = datePicker.getText().toString();

        String city = mETeditProfileCity.getText().toString();
        String address = mETeditProfileAdress.getText().toString();
        String phonenumber = mETeditProfilePhonenumber.getText().toString();

        Log.e("TAG", "isina: "+fullname );
        Log.e("TAG", "isina: "+Gender );
        Log.e("TAG", "isina: "+dateofbirth );
        Log.e("TAG", "isina: "+city );
        Log.e("TAG", "isina: "+address );
        Log.e("TAG", "isina: "+phonenumber );
        Log.e("TAG", "token: "+token);

        profilePref.name().put(fullname);
        profilePref.gender().put(Gender);
        profilePref.dob().put(dateofbirth);
        profilePref.city().put(city);
        profilePref.address().put(address);
        profilePref.phone().put(phonenumber);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://kartoo-dev.azure-mobile.net/").addConverterFactory(GsonConverterFactory.create()).build();
        final MainService service = retrofit.create(MainService.class);
        UpdateProfile credentials = new UpdateProfile("","","","",fullname,"","", address,"","", phonenumber,"", dateofbirth, Gender, "", city);

        Log.e("TAG", "isina: "+credentials.toString());

        Call<ResponseDefault> updateProfile = service.doUpdateProfile(token, credentials);
        updateProfile.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Response<ResponseDefault> response, Retrofit retrofit) {
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
                Log.e("TAG", "onResponse: " + response.code());
                if (response.code() == 200) {
                    Intent intent = new Intent(EditProfile.this, MainActivity_.class);
                    intent.putExtra("FromEditProfile", "true");
                    intent.putExtra("name", fullname);
                    //intent.putExtra("BitmapImage", bitMap);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Failed to edit Your Profile", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
                Log.e("TAG", "onFailure: "+"fail" );

                Intent intent = new Intent(EditProfile.this, MainActivity_.class);

                intent.putExtra("FromEditProfile", "true");
                intent.putExtra("name", fullname);
                //intent.putExtra("BitmapImage", bitMap);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();


            }
        });
    }

    public void onRadioButtonClicked(View view) {
        checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.male:
                if (checked) {
                    Gender = "Male";
                }
                break;

            case R.id.female:
                if (checked) {
                    Gender = "Female";
                }
                break;
        }
    }

    private static String formatDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = ""+dayOfMonth+"/"+(monthOfYear)+"/"+year;
        datePicker.setText(formatDate(year,monthOfYear,dayOfMonth));
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