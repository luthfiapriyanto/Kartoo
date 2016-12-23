package co.kartoo.app.rest;

import java.util.ArrayList;

import co.kartoo.app.rest.model.Category;
import co.kartoo.app.rest.model.CategoryOutlet;
import co.kartoo.app.rest.model.CategoryPromo;
import co.kartoo.app.rest.model.LatestPromo;
import co.kartoo.app.rest.model.Outlet;
import co.kartoo.app.rest.model.ResponseDefault;
import co.kartoo.app.rest.model.newest.BankPage;
import co.kartoo.app.rest.model.newest.Discover;
import co.kartoo.app.rest.model.newest.DiscoverPromotionCategory;
import co.kartoo.app.rest.model.newest.Home;
import co.kartoo.app.rest.model.newest.Interest;
import co.kartoo.app.rest.model.newest.MallGeofence;
import co.kartoo.app.rest.model.newest.Malls;
import co.kartoo.app.rest.model.newest.Nearby;
import co.kartoo.app.rest.model.newest.PopularDetail;
import co.kartoo.app.rest.model.newest.ViewAllOutlet;
import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by MartinOenang on 10/10/2015.
 */
public interface PromoService {

    @GET("api/promotions/home")
    Call<Home> getHomeHeader(@Header("x-zumo-auth") String apikey);

    @GET("api/promotions/discover/{pageNumber}")
    Call<Discover> getDiscover(@Header("x-zumo-auth") String apikey, @Path("pageNumber") int page);

    @GET("api/promotions/{id}")
    Call<PopularDetail> getPromoActivityPopular(@Header("x-zumo-auth") String apikey, @Path("id") String id);

    @GET("api/promotions/{id}/outlets/{page}")
    Call<ViewAllOutlet> getPromoActivityViewAll(@Header("x-zumo-auth") String apikey, @Path("id") String id, @Path("page") int page);

    @GET("api/category/{id}/{pagenumber}")
    Call<CategoryOutlet> getPromoByCategory(@Header("x-zumo-auth") String authorization, @Path("id") String idCategory, @Path("pagenumber") int pagenumber);

    @GET("api/category/{id}/{pagenumber}/mycard")
    Call<CategoryOutlet> getPromoByCategoryMycard(@Header("x-zumo-auth") String authorization, @Path("id") String idCategory, @Path("pagenumber") int pagenumber);

    @GET("api/category")
    Call<ArrayList<Category>> getAllCategory(@Header("Authorization") String authorization);

    @GET("api/category")
    Call<ArrayList<Category>> getCategory(@Header("x-zumo-auth") String authorization);

    @GET("api/promotions/category")
    Call<ArrayList<CategoryPromo>> getAllPromoCategory(@Header("Authorization") String authorization);

    @GET("api/promotions/sorted/{longitude}/{latitude}")
    Call<LatestPromo> getLatestPromoBasedOnLocation(@Header("Authorization") String authorization, @Path("longitude") double longitude, @Path("latitude") double latitude);

    @GET("api/promotions/recommended")
    Call<Outlet> getCustomizedPromotion(@Header("Authorization") String authorization);

    @GET("api/users/favorite/promotions?")
    Call<ArrayList<DiscoverPromotionCategory>> getAllFavoritePromotions(@Header("x-zumo-auth") String authorization, @Query("sortValue") String sortValue, @Query("latitude") String latitude, @Query("longitude") String longitude);

    @POST("api/promotions/{id}/favorite")
    Call<ResponseDefault> doFavoriteOutlet(@Header("x-zumo-auth") String authorization, @Path("id") String idPromotion);

    @DELETE("api/promotions/{id}/favorite")
    Call<ResponseDefault> doUnFavoriteOutlet(@Header("x-zumo-auth") String authorization, @Path("id") String idPromotion);

    @GET("api/users/{idUser}/favorite/outlets")
    Call<ArrayList<Outlet>> getAllFavoriteOutlets(@Header("Authorization") String authorization, @Path("idUser") String idUser);

    @GET("api/promotions/nearby/{latitude}/{longitude}")
    Call<ArrayList<Nearby>> searchFromCoordinate(@Header("x-zumo-auth") String apikey, @Path("latitude") String latitude, @Path("longitude") String longitude);

    @GET(" api/promotions/nearby/{latitude}/{longitude}/mycard")
    Call<ArrayList<Nearby>> searchFromCoordinateMyCard(@Header("x-zumo-auth") String apikey, @Path("latitude") String latitude, @Path("longitude") String longitude);

    @GET("api/promotions/category/{idCategory}/outlet/{idOutlet}/bank/{idBank}")
    Call<ArrayList<Outlet>> filterPromo(@Header("Authorization") String authorization, @Path("idCategory") String idPromoCategory, @Path("idOutlet") String idOutletCategory, @Path("idBank") String idBank);

    @GET("api/outlet/mall/location")
    Call<ArrayList<MallGeofence>> mall(@Header("x-zumo-auth") String authorization);

    @GET("api/banks/pages")
    Call<ArrayList<BankPage>> getBankList(@Header("x-zumo-auth") String authorization);

    @GET("api/category/{id}/{pagenumber}?")
    Call<CategoryOutlet> getPromo(@Header("x-zumo-auth") String authorization, @Path("id") String idCategory, @Path("pagenumber") int pagenumber, @Query("sortValue") String sortValue, @Query("longitude") String longitude, @Query("latitude") String latitude);

    @GET("api/category/{id}/{pagenumber}/mycard?")
    Call<CategoryOutlet> getPromoMyCard(@Header("x-zumo-auth") String authorization, @Path("id") String idCategory, @Path("pagenumber") int pagenumber, @Query("sortValue") String sortValue, @Query("longitude") String longitude, @Query("latitude") String latitude);

    @GET("api/promotions/nearby/{latitude}/{longitude}?")
    Call<ArrayList<Nearby>> searchCoordinate(@Header("x-zumo-auth") String apikey, @Path("latitude") String latitude, @Path("longitude") String longitude, @Query("sortValue") String sortValue);

    @GET("api/promotions/nearby/{latitude}/{longitude}/mycard?")
    Call<ArrayList<Nearby>> searchCoordinateMyCard(@Header("x-zumo-auth") String apikey, @Path("latitude") String latitude, @Path("longitude") String longitude, @Query("sortValue") String sortValue);

    @GET("api/search?")
    Call<ArrayList<DiscoverPromotionCategory>> searchAll(@Header("x-zumo-auth") String authorization, @Query("query") String query, @Query("sortValue") String sortValue, @Query("latitude") String latitude, @Query("longitude") String longitude);

    @GET("api/mall/{pagenumber}?")
    Call<Malls> getMalls(@Header("x-zumo-auth") String apikey, @Path("pagenumber") int pagenumber, @Query("sortValue") String sortValue, @Query("latitude") String latitude, @Query("longitude") String longitude);

    @GET("api/users/interest")
    Call<ArrayList<Interest>> getInterest(@Header("x-zumo-auth") String authorization);
}
