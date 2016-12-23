package co.kartoo.app.forgotpassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import co.kartoo.app.MainActivity_;
import co.kartoo.app.R;
import co.kartoo.app.rest.MainService;
import co.kartoo.app.rest.model.ChangePassword;
import co.kartoo.app.rest.model.ResponseDefault;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ChangeYourPassword extends AppCompatActivity {
    Button mBtnReset;
    TextView mTVCancel;
    EditText mETEmailChangePassword, mETnewPassword;
    ProgressDialog loadingDialog;
    String token, email, oldpassword, newpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_your_password);
        loadingDialog = new ProgressDialog(ChangeYourPassword.this);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(false);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        token = intent.getStringExtra("token");

        mETEmailChangePassword = (EditText) findViewById(R.id.mETEmailChangePassword);
        mETnewPassword = (EditText) findViewById(R.id.mETnewPassword);
        mBtnReset = (Button) findViewById(R.id.mBtnReset);
        mTVCancel = (TextView) findViewById(R.id.mTVCancel);

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
                if (!loadingDialog.isShowing())
                    loadingDialog.show();
            }
        });

        mTVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeYourPassword.this, MainActivity_.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    public void changePassword(){
        oldpassword = mETEmailChangePassword.getText().toString();
        newpassword = mETnewPassword.getText().toString();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://kartoo-dev.azure-mobile.net/").addConverterFactory(GsonConverterFactory.create()).build();
        final MainService service = retrofit.create(MainService.class);

        ChangePassword credentials = new ChangePassword(email, newpassword, oldpassword);
        Call<ResponseDefault> getCities = service.doChangePassword(token, credentials);
        getCities.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Response<ResponseDefault> response, Retrofit retrofit) {
                Log.e("TAG", "onResponse: " + response.code());
                if (response.code() == 200) {
                    if (loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    Log.e("TAG", "editProfile: "+response.body());
                    Intent intent = new Intent(ChangeYourPassword.this, MainActivity_.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if( response.code() == 403){
                    if (loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Your old password is incorrect", Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
                Intent intent = new Intent(ChangeYourPassword.this, MainActivity_.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }
}
