package com.android.jh.mymusicplayer.util.Control;

import com.android.jh.mymusicplayer.util.Interfaces.ControlInterface;
import com.android.jh.mymusicplayer.util.Services.PlayerService;

import java.util.ArrayList;
import java.util.List;

import static com.android.jh.mymusicplayer.util.Services.PlayerService.controller;

public class Controller {
//    private static Controller controller = null;

    public static String ACTION = PlayerService.ACTION_STOP;

    public List<ControlInterface> observers;

    private Controller () {
        observers = new ArrayList<>();
    }

    public static Controller getInstance() {
        if(controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public void addObservers(ControlInterface controlInterface) {
        observers.add(controlInterface);
    }

    public void deleteObservers(ControlInterface controlInterface) {
        observers.remove(controlInterface);
    }

    public void play() {
        for(ControlInterface observer : observers) {
            observer.playPlayer();
        }
    }
    public void pause() {
        for(ControlInterface observer : observers) {
            observer.pausePlayer();
        }
    }
    public void pre() {
        for(ControlInterface observer : observers) {
            observer.prePlayer();
        }
    }
    public void next() {
        for(ControlInterface observer : observers) {
            observer.nextPlayer();
        }
    }
    public void startService() {
        for(ControlInterface observer : observers) {
            observer.startService();
        }
    }
}
