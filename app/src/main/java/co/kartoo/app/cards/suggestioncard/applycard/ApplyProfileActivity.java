package co.kartoo.app.cards.suggestioncard.applycard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.kartoo.app.EditProfile;
import co.kartoo.app.R;
import co.kartoo.app.models.LoginPref_;
import co.kartoo.app.models.ProfilePref_;

import static co.kartoo.app.R.id.datePicker;

@EActivity(R.layout.activity_apply_profile)
public class ApplyProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    @ViewById
    Toolbar mToolbar;
    @ViewById
    TextView mTVtitle;

    @ViewById
    EditText mETbirth, mETage, mETname, mETphoneNumber, mETemail, mEToccupation, mETcity;

    @ViewById
    Button mBtnNext;

    @Pref
    ProfilePref_ profilePref;

    @Pref
    LoginPref_ loginPref;

    String age;

    @AfterViews
    void init() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        setUpNavDrawer();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mETbirth.setFocusable(false);
        mETbirth.setClickable(true);

        Log.e("TAG", "ApplyProfile: "+profilePref.name().get());
        if (loginPref.name().get() != null || !loginPref.name().get().equals("")) {
            mETname.setText(loginPref.name().get());
        }
        if (profilePref.phone().get() != null || !profilePref.phone().get().equals("")) {
            mETphoneNumber.setText(profilePref.phone().get());
        }
        if (loginPref.email().get() != null || !loginPref.email().get().equals("")) {
            mETemail.setText(loginPref.email().get());
        }
        if (profilePref.dob().get() != null || !profilePref.dob().get().equals("")) {
            mETbirth.setText(profilePref.dob().get());

            mETage.setText(age);
        }
        if (profilePref.city().get() != null || !profilePref.city().get().equals("")) {
            mETcity.setText(profilePref.city().get());
        }

    }
    public int getAge (int _year, int _month, int _day) {
        Calendar cal = Calendar.getInstance();
        int y, m, d, a;
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);

        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
    }

    @Click(R.id.mETbirth)
    public void mETbirthClick () {
        Calendar now = Calendar.getInstance();
        now.set(1990,1,1);
        DatePickerDialog dpd = DatePickerDialog.newInstance(ApplyProfileActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Click(R.id.mBtnNext)
    public void mBtnNextClick () {
        Intent intent = new Intent(this, ApplyConfirmCardActivity_.class);
        startActivity(intent);
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
        //String date = ""+dayOfMonth+"/"+(monthOfYear)+"/"+year;

        age = String.valueOf(getAge(year,monthOfYear,dayOfMonth));

        mETbirth.setText(formatDate(year,monthOfYear,dayOfMonth));
        mETage.setText(String.valueOf(getAge(year,monthOfYear,dayOfMonth)));
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
