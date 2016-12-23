package co.kartoo.app.rest.model.newest;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

import co.kartoo.app.rest.model.Merchant;

/**
 * Created by Luthfi Apriyanto on 3/13/2016.
 */
public class Nearby implements ClusterItem, Serializable {
    String id;
    String telephone;
    String address;
    String name;
    String latitude;
    String longitude;
    Merchant merchant;
    DiscoverPromotionCategory promotion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public DiscoverPromotionCategory getPromotion() {
        return promotion;
    }

    public void setPromotion(DiscoverPromotionCategory promotion) {
        this.promotion = promotion;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }
}
