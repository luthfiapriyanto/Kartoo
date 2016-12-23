package co.kartoo.app.cards.suggestioncard;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.kartoo.app.R;
import co.kartoo.app.rest.model.newest.Availablecards;
import co.kartoo.app.rest.model.newest.CardDetail;
import co.kartoo.app.rest.model.newest.DetailItem;
import co.kartoo.app.rest.model.newest.Details;

public class ViewHolderMoreCards extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView mIVcard;
    TextView mTVname, mTVincome, mTVperks;
    Context mContext;
    Availablecards details;



    public ViewHolderMoreCards(Context mContext, View itemView) {
        super(itemView);
        mTVname = (TextView) itemView.findViewById(R.id.mTVname) ;
        mTVincome = (TextView) itemView.findViewById(R.id.mTVincome) ;
        mTVperks = (TextView) itemView.findViewById(R.id.mTVperks) ;

        mIVcard = (ImageView) itemView.findViewById(R.id.mIVcard);

        itemView.setOnClickListener(this);

        this.mContext = mContext;
    }

    public void bind(Availablecards details) {

        mTVname.setText(details.getCard_Edition());
        mTVincome.setText(details.getMinimumIncome());
        mTVperks.setText(details.getShortPerks());

        Picasso.with(mContext).load(details.getUrl_img())
                .fit()
                .placeholder(R.color.placeholder)
                .into(mIVcard);

        this.details = details;
    }

    @Override
    public void onClick(View v) {

    }
}