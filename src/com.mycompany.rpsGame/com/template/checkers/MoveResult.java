package com.template.checkers;

public class MoveResult
{
    private MoveType type;

    public MoveType getType() {
        return type;
    }

    private Piece piece;
    private Piece defenderPiece;

    public Piece getPiece(){
        return piece;
    }

    public Piece getDefenderPiece(){
        return defenderPiece;
    }

    public MoveResult(MoveType type){
        this(type, null);
    }

    public MoveResult(MoveType type, Piece piece) {
        this.type = type;
        this.piece = piece;
        this.defenderPiece = null;
    }

    public MoveResult(MoveType type, Piece attackerPiece, Piece defenderPiece) {
        this.type = type;
        this.piece = attackerPiece;
        this.defenderPiece = defenderPiece;
    }
}
