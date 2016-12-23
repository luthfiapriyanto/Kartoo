package co.kartoo.app.forgotpassword;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.kartoo.app.R;
import co.kartoo.app.landing.LoginActivity;

public class CheckYourMail extends AppCompatActivity {
    Button mBtnReset;
    TextView mTVCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_your_mail);

        mBtnReset = (Button) findViewById(R.id.mBtnReset);
        mTVCancel = (TextView) findViewById(R.id.mTVCancel);

        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBtnReset.setBackground(getResources().getDrawable(R.drawable.shape_btn_coral_dead_button));
                mBtnReset.setText(millisUntilFinished / 1000+"");
            }
            public void onFinish() {
                mBtnReset.setBackground(getResources().getDrawable(R.drawable.shape_btn_coral_rc));
                mBtnReset.setText("Continue");
                mTVCancel.setVisibility(View.VISIBLE);
                clickButton();
            }
        }.start();
    }

    public void clickButton() {
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckYourMail.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        mTVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckYourMail.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
