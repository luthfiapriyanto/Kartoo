package co.kartoo.app.category;

import java.util.ArrayList;

import co.kartoo.app.rest.model.Category;
import co.kartoo.app.rest.model.CategoryOutlet;

/**
 * Created by MartinOenang on 10/21/2015.
 */
public class CategoryEvent {
    ArrayList<Category> listPromo;

    public CategoryEvent(ArrayList<Category> listPromo) {
        this.listPromo = listPromo;
    }

    public ArrayList<Category> getListPromo() {
        return listPromo;
    }

    public void setListPromo(ArrayList<Category> listPromo) {
        this.listPromo = listPromo;
    }
}
