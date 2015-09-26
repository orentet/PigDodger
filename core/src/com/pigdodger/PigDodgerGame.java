package com.pigdodger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Array;
import com.pigdodger.modes.Mode;
import com.pigdodger.modes.ModesManager;
import com.pigdodger.modes.countdown.CountDownMode;
import com.pigdodger.modes.game.GameMode;
import com.pigdodger.modes.mainmenu.MainMenuMode;
import com.pigdodger.modes.platformspecific.ActionResolver;

import java.util.Observable;
import java.util.Observer;

public class PigDodgerGame extends ApplicationAdapter {

    private final ActionResolver actionResolver;
    ModesManager modesManager;
    private Observer mainMenuModeOnStartGameClick = new Observer() {
        @Override
        public void update(Observable arg0, Object arg1) {
            Array<Mode> newModes = new Array<Mode>();
            newModes.add(createGameMode());
            modesManager.replaceModes(newModes);
            actionResolver.setTrackerScreenName("com.pigdodger.ingame");
        }
    };
    private Observer countDownModeOnCountDownFinished = new Observer() {
        @Override
        public void update(Observable arg0, Object arg1) {
            Array<Mode> newModes = new Array<Mode>();
            newModes.add(createMainMenuMode());
            modesManager.replaceModes(newModes);
            actionResolver.setTrackerScreenName("com.pigdodger.mainMenu");
        }
    };
    private Observer gameModeOnGameOver = new Observer() {
        @Override
        public void update(Observable arg0, Object arg1) {
            modesManager.addMode(createCountDownMode());
            actionResolver.setTrackerScreenName("com.pigdodger.countdown");
        }
    };

    public PigDodgerGame(ActionResolver actionResolver) {
        this.actionResolver = actionResolver;
    }

    @Override
    public void create() {
        Array<Mode> initialModeList = new Array<Mode>();
        initialModeList.add(this.createMainMenuMode());
        this.modesManager = new ModesManager(initialModeList);
        actionResolver.setTrackerScreenName("com.pigdodger.mainMenu");
    }

    private CountDownMode createCountDownMode() {
        CountDownMode countDownMode = new CountDownMode();
        countDownMode.getCountDownFinishedObservable().addObserver(countDownModeOnCountDownFinished);
        return countDownMode;
    }

    private GameMode createGameMode() {
        GameMode gameMode = new GameMode();
        gameMode.getGameOverObservable().addObserver(gameModeOnGameOver);
        return gameMode;
    }

    private MainMenuMode createMainMenuMode() {
        MainMenuMode mainMenuMode = new MainMenuMode();
        mainMenuMode.getStartGameClickObservable().addObserver(mainMenuModeOnStartGameClick);
        return mainMenuMode;
    }

    @Override
    public void render() {
        this.modesManager.render();
    }
}
