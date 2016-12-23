package co.kartoo.app.nearby;

import java.util.ArrayList;

import co.kartoo.app.rest.model.newest.Nearby;

/**
 * Created by Luthfi Apriyanto on 5/2/2016.
 */
public class NearbyEvent {
    ArrayList<Nearby> listPromo;

    public NearbyEvent(ArrayList<Nearby> listPromo) {
        this.listPromo = listPromo;
    }

    public ArrayList<Nearby> getListPromo() {
        return listPromo;
    }

    public void setListPromo(ArrayList<Nearby> listPromo) {
        this.listPromo = listPromo;
    }
}
