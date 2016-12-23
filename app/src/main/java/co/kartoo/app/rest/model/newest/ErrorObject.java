package co.kartoo.app.rest.model.newest;

import java.util.ArrayList;

/**
 * Created by Luthfi Apriyanto on 12/19/2016.
 */

public class ErrorObject {
    String id;
    String suggestCheckList;
    String suggestContent;

    public ErrorObject() {

    }

    public ErrorObject(String id, String suggestCheckList, String suggestContent) {
        this.id = id;
        this.suggestCheckList = suggestCheckList;
        this.suggestContent = suggestContent;

    }
}