package com.pigdodger.android;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pigdodger.PigDodgerGame;
import com.pigdodger.modes.platformspecific.ActionResolver;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // require google play services (for ads)
        int resultCode = isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != 0) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            return;
        }

        InitGoogleAnalytics();
        InitViews();
    }

    private void InitViews() {
        // Create the layout
        RelativeLayout layout = new RelativeLayout(this);

        // Create the libgdx View
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(new PigDodgerGame(this), config);

        // Add the libgdx view
        layout.addView(gameView);

        // Create and setup the AdMob view
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("");
        adView.loadAd(new AdRequest.Builder().build());

        // Add the AdMob view
        LayoutParams adParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        layout.addView(adView, adParams);

        // Hook it all up
        setContentView(layout);
    }

    private void InitGoogleAnalytics() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void setTrackerScreenName(String path) {
        tracker.setScreenName(path);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
