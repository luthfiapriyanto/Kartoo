package co.kartoo.app.rest.model;

import java.io.Serializable;

/**
 * Created by MartinOenang on 10/19/2015.
 */
public class Category implements Serializable {
    String id;
    String caption;
    String name;
    String url_img;
    String currentPage;
    String maxPage;
    String promotions;
    Boolean hasPromoForMyCard;

    public Boolean getHasPromoForMyCard() {
        return hasPromoForMyCard;
    }

    public void setHasPromoForMyCard(Boolean hasPromoForMyCard) {
        this.hasPromoForMyCard = hasPromoForMyCard;
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(String maxPage) {
        this.maxPage = maxPage;
    }

    public String getPromotions() {
        return promotions;
    }

    public void setPromotions(String promotions) {
        this.promotions = promotions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
