package co.kartoo.app.cards.suggestioncard.applycard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import co.kartoo.app.R;

@EActivity(R.layout.activity_apply_landing)
public class ApplyLandingActivity extends AppCompatActivity {
    @ViewById
    Toolbar mToolbar;
    @ViewById
    TextView mTVtitle;

    @ViewById
    Button mBtnNext;

    @AfterViews
    void init() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        setUpNavDrawer();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Click(R.id.mBtnNext)
    public void mBtnNextClick () {
        Intent intent = new Intent(this, ApplyProfileActivity_.class);
        startActivity(intent);
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
