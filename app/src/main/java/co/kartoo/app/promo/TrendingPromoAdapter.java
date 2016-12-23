package co.kartoo.app.promo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.kartoo.app.R;
import co.kartoo.app.rest.model.newest.DiscoverPromotion;

/**
 * Created by MartinOenang on 10/12/2015.
 */
public class TrendingPromoAdapter extends RecyclerView.Adapter<TrendingPromoViewHolder> {

    ArrayList<DiscoverPromotion> listPromo;
    Context mContext;

    public TrendingPromoAdapter(Context mContext, ArrayList<DiscoverPromotion> listPromo) {
        Log.e("adapter", "initiated " + listPromo.size() + " items");
        this.listPromo = listPromo;
        this.mContext = mContext;
    }

    @Override
    public TrendingPromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrendingPromoViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home_outlet, parent, false));
    }

    @Override
    public void onBindViewHolder(TrendingPromoViewHolder holder, int position) {
        Log.e("adapter", "bind "+position);
        holder.bind(listPromo.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listPromo.size();
    }
}