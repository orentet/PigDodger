package com.pigdodger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.pigdodger.PigDodgerGame;
import com.pigdodger.modes.platformspecific.ActionResolver;

public class DesktopLauncher implements ActionResolver {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Pig Dodger";
        config.width = 800;
        config.height = 480;
        new LwjglApplication(new PigDodgerGame(new DesktopLauncher()), config);
    }

    @Override
    public void setTrackerScreenName(String path) {

    }
}
