package co.kartoo.app.events;

import co.kartoo.app.rest.model.CategoryOutlet;

/**
 * Created by MartinOenang on 10/21/2015.
 */
public class TrendingFinishedEventCategory {
    CategoryOutlet listPromo;

    public TrendingFinishedEventCategory(CategoryOutlet listPromo) {
        this.listPromo = listPromo;
    }

    public CategoryOutlet getListPromo() {
        return listPromo;
    }

    public void setListPromo(CategoryOutlet listPromo) {
        this.listPromo = listPromo;
    }
}
