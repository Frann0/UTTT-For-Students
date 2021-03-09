package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.List;


public class JonaBot implements IBot {


    private int myId = -1;

    public enum MicroBoard {
        UPPER_LEFT, UPPER_MID, UPPER_RIGHT, MID_LEFT,
        MID, MID_RIGHT, LOWER_LEFT, LOWER_MID, LOWER_RIGHT
    }

    ;


    private static final String BOTNAME = "JonaBot";
    // Moves {row, col} in order of preferences. {0, 0} at top-left corner
    // Prefered moves when center field is available
    protected int[][] preferredFields = {
            {0, 1}, {1, 0}, {2, 1}, {1, 2}, //Outer Middles
            {0, 0}, {2, 0}, {2, 2}, {0, 2},  //Corners ordered across
            {1, 1}}; //Center field

    protected int[][] preferredFields2 = {
            {0, 0}, {2, 0}, {2, 2}, {0, 2}, //Outer Middles
            {0, 1}, {2, 1}, {0, 2}, {0, 0},  //Corners ordered across
            {1, 1}}; //Center field


    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * A bot that uses a local prioritised list algorithm, in order to win any local board,
     * and if all boards are available for play, it'll run a on the macroboard,
     * to select which board to play in.
     *
     * @return The selected move we want to make.
     */
    @Override
    public IMove doMove(IGameState state) {


        System.out.println("avail moves: " + (state.getField().getAvailableMoves().size() - state.getMoveNumber()));

        if (state.getRoundNumber() == 0) {
            myId = setMyId(state);
        }


        // TODO if center is not full  -> conquerCenterStrat
        // TODO else -> conquerCornerStrat
        // TODO + isMoveSafe


        if (state.getField().getMacroboard()[1][1].equals(IField.AVAILABLE_FIELD)) {

            //Find macroboard to play in
            for (int[] move : preferredFields) {

                if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD)) {
                    //find move to play
                    IMove winMove = getWinningMove(state);
                    if (winMove != null) {
                        return winMove;
                    }

                    for (int[] selectedMove : getMovePriority(state)) {

                            int x = selectedMove[0] * 3 + selectedMove[0];
                            int y = selectedMove[1] * 3 + selectedMove[1];
                            if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD)) {
                                return new Move(x, y);
                            }
                        }
                }
            }
        } else {
            for (int[] move : preferredFields2) {
                if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD)) {
                    //find move to play
                    IMove winMove = getWinningMove(state);
                    if (winMove != null) {
                        return winMove;
                    }

                    for (int[] selectedMove : getMovePriority(state)) {

                                int x = selectedMove[0] * 3 + selectedMove[0];
                                int y = selectedMove[1] * 3 + selectedMove[1];
                                if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD)) {
                                    return new Move(x, y);
                                }

                        }
                    }
                }
            }

        //NOTE: Something failed, just take the first available move I guess!
        return state.getField().getAvailableMoves().get(0);
    }

    private int setMyId(IGameState state) {
        if (state.getMoveNumber() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getMyId() {
        return myId;
    }

    private void checkForWinningMove(IGameState state) {
        String[][] board = state.getField().getBoard();

        int d = -1, e = -1, f = -1;
        MicroBoard currentMicroBoard = getCurrentMicroBoard(state);

        switch (currentMicroBoard) {
            case UPPER_LEFT -> {
                d = 0;
                e = 0;
                f = e + 3;
                break;
            }
            case UPPER_MID -> {
                d = 3;
                e = 0;
                f = e + 3;
                break;
            }
            case UPPER_RIGHT -> {
                d = 6;
                e = 0;
                f = e + 3;
                break;
            }
            case MID_LEFT -> {
                d = 0;
                e = 3;
                f = e + 3;
                break;
            }
            case MID -> {
                d = 3;
                e = 3;
                f = e + 3;
                break;
            }
            case MID_RIGHT -> {
                d = 6;
                e = 3;
                f = e + 3;
                break;
            }
            case LOWER_LEFT -> {
                d = 0;
                e = 6;
                f = e + 3;
                break;
            }
            case LOWER_MID -> {
                d = 3;
                e = 6;
                f = e + 3;
                break;
            }
            case LOWER_RIGHT -> {
                d = 6;
                e = 6;
                f = e + 3;
                break;
            }
        }

        for (int i = d; i < d + 3; i++) {
            for (int j = e; j < f; j++) {

                System.out.print(board[j][i]);
                if (j == 2) {
                    System.out.println("");
                }
                if (j == 2 && i == 2) {
                    System.out.println("---");
                }
            }

        }

    }


    public MicroBoard getCurrentMicroBoard(IGameState state) {
        MicroBoard currentMicroBoard = null;
        List<IMove> moves = state.getField().getAvailableMoves();
        List<IMove> centerMovs = new ArrayList<>();

        // Create list of Mid field moves
        for (int i = 3; i < 6; i++) {
            for (int j = 3; j < 6; j++) {
                centerMovs.add(new Move(i, j));
            }
        }

        for (IMove iMove : moves) {
            for (int j = 0; j < 9; j++) {

                if (iMove.getX() == centerMovs.get(j).getX() && iMove.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.MID;
                }
            }
        }

        // Create list of UPPER_LEFT corner fields
        centerMovs.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.UPPER_LEFT;
                }
            }
        }

        // Create list of UPPER_MID fields
        centerMovs.clear();
        for (int i = 3; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.UPPER_MID;
                }
            }
        }

        // Create list of UPPER_RIGHT fields
        centerMovs.clear();
        for (int i = 6; i < 9; i++) {
            for (int j = 0; j < 3; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.UPPER_RIGHT;
                }
            }
        }
        // Create list of MID_LEFT fields
        centerMovs.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 3; j < 6; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.MID_LEFT;
                }
            }
        }
        // Create list of MID_RIGHT fields
        centerMovs.clear();
        for (int i = 6; i < 9; i++) {
            for (int j = 3; j < 6; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.MID_RIGHT;
                }
            }
        }
        // Create list of LOWER_LEFT fields
        centerMovs.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 6; j < 9; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.LOWER_LEFT;
                }
            }
        }
        // Create list of LOWER_MID fields
        centerMovs.clear();
        for (int i = 3; i < 6; i++) {
            for (int j = 6; j < 9; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.LOWER_MID;
                }
            }
        }
        // Create list of LOWER_RIGHT fields
        centerMovs.clear();
        for (int i = 6; i < 9; i++) {
            for (int j = 6; j < 9; j++) {
                centerMovs.add(new Move(i, j));
            }
        }
        for (IMove move : moves) {
            for (int j = 0; j < 9; j++) {

                if (move.getX() == centerMovs.get(j).getX() && move.getY() == centerMovs.get(j).getY()) {
                    currentMicroBoard = MicroBoard.LOWER_RIGHT;
                }
            }
        }

        return currentMicroBoard;

    }


    public void getFieldStatus(IGameState state) {

        int d = -1, e = -1, f = -1;
        MicroBoard currentMicroBoard = getCurrentMicroBoard(state);

        switch (currentMicroBoard) {
            case UPPER_LEFT -> {
                d = 0;
                e = 0;
                f = e + 3;
                break;
            }
            case UPPER_MID -> {
                d = 3;
                e = 0;
                f = e + 3;
                break;
            }
            case UPPER_RIGHT -> {
                d = 6;
                e = 0;
                f = e + 3;
                break;
            }
            case MID_LEFT -> {
                d = 0;
                e = 3;
                f = e + 3;
                break;
            }
            case MID -> {
                d = 3;
                e = 3;
                f = e + 3;
                break;
            }
            case MID_RIGHT -> {
                d = 6;
                e = 3;
                f = e + 3;
                break;
            }
            case LOWER_LEFT -> {
                d = 0;
                e = 6;
                f = e + 3;
                break;
            }
            case LOWER_MID -> {
                d = 3;
                e = 6;
                f = e + 3;
                break;
            }
            case LOWER_RIGHT -> {
                d = 6;
                e = 6;
                f = e + 3;
                break;
            }
        }

        for (int i = d; d < d + 3; d++) {
            for (int j = e; j < f; j++) {
                System.out.print(state.getField().getBoard()[i][j]);
            }
            System.out.println();
        }

    }

    public IMove getWinningMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();
        for (IMove move : moves) {
            String[][] tempState = cloneBoard(state.getField().getBoard());
            tempState[move.getX()][move.getY()] = String.valueOf(getMyId());
            if (GameManager.isWin(tempState, move, String.valueOf(getMyId()))) {
                return move;
                //System.out.println("Winning move available!");
            }
        }
        return null;
    }

    public String[][] cloneBoard(String[][] board) {
        String[][] newBoard = new String[board.length][board[0].length];

        for (int x = 0; x < board.length; x++)
            for (int y = 0; y < board[x].length; y++)
                newBoard[x][y] = board[x][y];

        return newBoard;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    private int getRoundNumber(IGameState state) {
        return state.getRoundNumber() + 4 % 4;
    }


    private int[][] getMovePriority(IGameState state) {

        // Prefered moves when center field is available
        int[][] conquerCenterStrat1a = {
                {0, 0}, {2, 0}, {1, 0}, {2, 2}, //Outer Middles
                {0, 2}, {1, 2}, {0, 1}, {2, 1},   //Corners ordered across
                {1, 1}}; //Center field
        int[][] conquerCenterStrat1b = {
                {2, 0}, {1, 0}, {2, 2}, {0, 0}, //Outer Middles
                {1, 2}, {0, 1}, {2, 1}, {0, 2}, //Corners ordered across
                {1, 1}}; //Center field
        int[][] conquerCenterStrat1c = {
                {1, 0}, {2, 2}, {0, 0}, {2, 0}, //Outer Middles
                {1, 2}, {0, 1}, {2, 1}, {0, 2},  //Corners ordered across
                {1, 1}}; //Center field
        int[][] conquerCenterStrat1d = {
                {2, 2}, {0, 0}, {2, 0}, {1, 0}, //Outer Middles
                {0, 1}, {2, 1}, {0, 2}, {1, 2},   //Corners ordered across
                {1, 1}}; //Center field

        // Prefered moves when center field is not available
        int[][] conquerCornerStrat1a = {
                {0, 0}, {2, 0}, {0, 2}, {2, 2}, //Outer Middles
                {1, 0}, {2, 1}, {1, 2}, {0, 1},  //Corners ordered across
                {1, 1}}; //Center field
        int[][] conquerCornerStrat1b = {
                {2, 0}, {1, 0}, {2, 2}, {0, 0}, //Outer Middles
                {2, 1}, {1, 2}, {0, 1}, {1, 0},  //Corners ordered across
                {1, 1}}; //Center field
        int[][] conquerCornerStrat1c = {
                {1, 0}, {2, 2}, {0, 0}, {2, 0}, //Outer Middles
                {1, 2}, {0, 1}, {1, 0}, {2, 1}, //Corners ordered across
                {1, 1}}; //Center field
        int[][] conquerCornerStrat1d = {
                {2, 2}, {0, 0}, {2, 0}, {1, 0}, //Outer Middles
                {0, 1}, {1, 0}, {2, 1}, {1, 2},  //Corners ordered across
                {1, 1}}; //Center field

        if (getCurrentMicroBoard(state).equals(MicroBoard.MID)) {
            switch (getRoundNumber(state)) {
                case 0:
                    return conquerCenterStrat1a;

                case 1:
                    return conquerCenterStrat1b;

                case 2:
                    return conquerCenterStrat1c;

                case 3:
                    return conquerCenterStrat1d;

                default:
                    return conquerCenterStrat1a;

            }
        } else if (state.getField().getAvailableMoves().size() > 9) {
            // TODO lav strategi for åbent spil
            if (getWinningMove(state) == null) {
                return conquerCornerStrat1a;
            }

        } else {
            switch (getRoundNumber(state)) {
                case 0:
                    return conquerCornerStrat1a;

                case 1:
                    return conquerCornerStrat1b;

                case 2:
                    return conquerCornerStrat1c;

                case 3:
                    return conquerCornerStrat1d;

                default:
                    return conquerCornerStrat1a;
            }
        } return conquerCornerStrat1b;
    }


    private boolean isMoveSafe(IGameState state, IMove move){
        return false;
    }
}
