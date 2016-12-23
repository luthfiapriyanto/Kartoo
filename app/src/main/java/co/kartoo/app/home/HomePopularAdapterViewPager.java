package co.kartoo.app.home;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import co.kartoo.app.DetailPromoActivity;
import co.kartoo.app.R;
import co.kartoo.app.rest.model.newest.DiscoverPromotion;

/**
 * Created by Luthfi Apriyanto on 2/25/2016.
 */

public class HomePopularAdapterViewPager extends PagerAdapter {

    Context context;
    String[] Id;
    String[] Name;
    String[] Bank;
    String[] Promo;
    String[] UrlImage;
    String[] Band;
    LayoutInflater inflater;
    Context mContext;
    DiscoverPromotion promo;

    public HomePopularAdapterViewPager(Context context, String[] Id, String[] Name, String[] Bank, String[] Promo, String[] UrlImage, String[] Band) {
        this.context = context;
        this.Id = Id;
        this.Name = Name;
        this.Promo = Promo;
        this.Bank = Bank;
        this.UrlImage = UrlImage;
        this.Band = Band;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Name.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        // Declare Variables
        TextView mTVbank;
        final TextView mTVoutletName;
        TextView mTVoutletPromo, mTVband;
        RelativeLayout band;
        final ImageView mLLcontainer;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.home_popular_item_viewpager, container, false);

        // Locate the TextViews in viewpager_item.xml
        mTVbank = (TextView) itemView.findViewById(R.id.mTVbank);
        mTVoutletName = (TextView) itemView.findViewById(R.id.mTVoutletName);
        mTVoutletPromo = (TextView) itemView.findViewById(R.id.mTVoutletPromo);
        mLLcontainer = (ImageView) itemView.findViewById(R.id.mLLcontainer);
        mTVband = (TextView) itemView.findViewById(R.id.mTVband);
        band = (RelativeLayout) itemView.findViewById(R.id.band);

        // Capture position and set to the TextViews
        mTVbank.setText(Bank[position]);
        mTVoutletName.setText(Name[position]);
        mTVoutletPromo.setText(Promo[position]);

        if (Band[position].equals("")){
            band.setVisibility(View.GONE);
        }
        else {
            mTVband.setText(Band[position]);
        }

        final String id = Id[position];
        this.promo = promo;

        Picasso.with(mContext)
                .load(UrlImage[position])
                .placeholder(R.color.placeholder)
                .fit()
                .centerCrop()
                .into(mLLcontainer);

        // Add viewpager_item.xml to ViewPager
        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("selectedPromo", promo);
                //Intent intent = new Intent(v.getContext(), ActivityPromo_.class);
                //intent.putExtras(bundle);
                //v.getContext().startActivity(intent);

                Log.e("TAG", "onClickViewPager: "+position );
                Intent intent = new Intent(v.getContext(), DetailPromoActivity.class);
                intent.putExtra("Id", id);
                intent.putExtra("imageHeader", UrlImage[position]);
                intent.putExtra("textHeader", Promo[position]);
                intent.putExtra("outletName", Name[position]);

                intent.putExtra("from", "Open Popular Promotion");

                Pair<View, String> p1 = Pair.create((View)mTVoutletName, "mainText");
                Pair<View, String> p2 = Pair.create((View)mLLcontainer, "mainImage");

                //ActivityOptionsCompat options = ActivityOptionsCompat.
                  //      makeSceneTransitionAnimation((Activity) v.getContext(), p1, p2);

                //intent.putExtra("token", token);
               // v.getContext().startActivity(intent, options.toBundle());
                v.getContext().startActivity(intent);

            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((RelativeLayout) object);

    }
}