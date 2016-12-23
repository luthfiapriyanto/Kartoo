package co.kartoo.app.category;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.kartoo.app.R;
import co.kartoo.app.rest.PromoService;
import co.kartoo.app.rest.model.newest.DiscoverPromotion;
import co.kartoo.app.rest.model.newest.DiscoverPromotionCategory;

public class PromoFromCategoryAdapter extends RecyclerView.Adapter<PromoFromCategoryViewHolder> {
    ArrayList<DiscoverPromotionCategory> bookmarkedOutlet;
    ArrayList<DiscoverPromotionCategory> listOutlet;
    Context mContext;
    String authorization;
    PromoService promoService;
    RecyclerView recycleView;
    int diff;


    public PromoFromCategoryAdapter(Context mContext, ArrayList<DiscoverPromotionCategory> listOutlet, PromoService promoService, String authorization, RecyclerView recycleView) {
        this.mContext = mContext;
        this.listOutlet = listOutlet;
        this.bookmarkedOutlet = new ArrayList<>();
        this.authorization = authorization;
        this.promoService = promoService;
        this.recycleView = recycleView;
    }

    @Override
    public PromoFromCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycleView.getLayoutManager();
        Log.e("TAG", "linearLayoutManager.getChildCount(): "+linearLayoutManager.getChildCount());
        Log.e("TAG", "parent.getBottom(): "+parent.getBottom());
        Log.e("TAG", "recycleView.getChildCount: "+recycleView.getChildCount());


        View view = recycleView.getChildAt(recycleView.getChildCount() - 1);

        return new PromoFromCategoryViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_promo_category, parent, false), promoService, authorization);

        /*
        if (linearLayoutManager.getChildCount()!=0){
            diff = (view.getBottom() - (recycleView.getHeight() + recycleView.getScrollY()));
            Log.e("linearLayoutManager", "diff: "+diff );
        }

        if (diff <= 0){
            return new PromoFromCategoryViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false), promoService, authorization);
        }

        else{
            return new PromoFromCategoryViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_promo_category, parent, false), promoService, authorization);
        }
*/
    }



    @Override
    public void onBindViewHolder(PromoFromCategoryViewHolder holder, int position) {
        Log.e("BIND", "position"+position);
        Log.e("BIND", "holder"+holder.toString());


        holder.bind(listOutlet.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;

    }

    @Override
    public int getItemCount() {
        return listOutlet.size();
    }
}