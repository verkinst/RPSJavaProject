package com.template.checkers;

public enum GameState implements Comparable<GameState> {
    END(-1), PLAY(1), MENU(0);


    final int state;

    GameState(int state) {
        this.state = state;
    }

}
