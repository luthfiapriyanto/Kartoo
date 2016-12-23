package co.kartoo.app;

import android.app.Application;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.util.Arrays;


public class KartooApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "tw3hIw8jZdLWUB1DWkkivgpqloZS1QAlTsfqBsnl", "PG4GLJuMlHSGBEbGmmMZroqxEj8KVGRYIRODecLH");
        ParseInstallation.getCurrentInstallation().addAllUnique("channels", Arrays.asList("BNI"));
        Log.e("asdf", ParseInstallation.getCurrentInstallation().getInstallationId());
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
