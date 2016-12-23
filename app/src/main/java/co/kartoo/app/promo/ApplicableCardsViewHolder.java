package co.kartoo.app.promo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import co.kartoo.app.R;
import co.kartoo.app.rest.model.newest.Availablecards;

/**
 * Created by MartinOenang on 11/19/2015.
 */
public class ApplicableCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    Context mContext;
    RelativeLayout mRLcontainer;
    ImageView mIVphoto;
    Availablecards card;

    public ApplicableCardsViewHolder(Context mContext, View itemView) {
        super(itemView);
        mIVphoto = (ImageView)itemView.findViewById(R.id.mIVphoto);
        mRLcontainer = (RelativeLayout) itemView.findViewById(R.id.mRLcontainer);
        //mRLcontainer.setOnClickListener(this);
        //itemView.setOnClickListener(this);
        this.mContext = mContext;
        //mIVphoto.setOnClickListener(this);
    }

    public void bind(Availablecards card) {
        //mIVphoto = new ImageView(mContext);

        //mIVphoto.setMinimumHeight(300);
        //mIVphoto.setMinimumWidth(400);
        Picasso.with(mContext)
                .load(card.getUrl_img())
                .placeholder(R.color.placeholder)
                .fit()
                .centerCrop()
                .into(mIVphoto);
        this.card = card;

    }

    @Override
    public void onClick(View v) {
        if(v.getParent()!=null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
        Dialog settingsDialog = new Dialog(v.getContext());
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        settingsDialog.setContentView(mRLcontainer, new RelativeLayout.LayoutParams(300, 150));
        settingsDialog.show();

    }
}
