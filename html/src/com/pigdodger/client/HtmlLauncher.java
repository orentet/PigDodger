package com.pigdodger.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.pigdodger.PigDodgerGame;
import com.pigdodger.modes.platformspecific.ActionResolver;

public class HtmlLauncher extends GwtApplication implements ActionResolver {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new PigDodgerGame(this);
        }

        @Override
        public void setTrackerScreenName(String path) {

        }
}