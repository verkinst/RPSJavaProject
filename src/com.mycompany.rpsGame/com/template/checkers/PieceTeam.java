package com.template.checkers;

public enum PieceTeam implements Comparable<PieceTeam> {
    RED(1), WHITE(-1), UNDEFINED(0);


    final int team;

    PieceTeam(int team) {
        this.team = team;
    }

}
