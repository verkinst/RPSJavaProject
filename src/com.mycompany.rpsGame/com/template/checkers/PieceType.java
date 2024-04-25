    package com.template.checkers;

    public enum PieceType implements Comparable<PieceType> {
        RED(1), WHITE(-1),

        STONE(10), SCISSORS(11), PAPER(12), KAMIKAZE(13), FLAG(14);


        final int moveDir;

        PieceType(int moveDir) {
            this.moveDir = moveDir;
        }

        @Override
        public String toString() {
            return switch (this.moveDir) {
                case 10 -> "stone";
                case 11 -> "scissors";
                case 12 -> "paper";
                case 13 -> "kamikaze";
                default -> "error";
            };
        }


    }
