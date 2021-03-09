package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;


public class DuoStrategyBot implements IBot {


    private int roundCount = 1;
    private static final String BOTNAME = "Dual Strategy Bot";
    // Moves {row, col} in order of preferences. {0, 0} at top-left corner
    // Prefered moves when center field is available
    protected int[][] conquerCenterStrat1a = {
            {0, 0}, {2,0}, {1,0}, {2,2} , //Outer Middles
            {0,2}, {1,2}, {0,1}, {2,1},   //Corners ordered across
            {1, 1}}; //Center field
    protected int[][] conquerCenterStrat1b = {
            {2,0}, {1,0}, {2,2}, {0,0} , //Outer Middles
             {1,2}, {0,1}, {2,1}, {0,2}, //Corners ordered across
            {1, 1}}; //Center field
    protected int[][] conquerCenterStrat1c = {
             {1,0}, {2,2}, {0, 0}, {2,0}, //Outer Middles
             {1,2}, {0,1}, {2,1}, {0,2},  //Corners ordered across
            {1, 1}}; //Center field
    protected int[][] conquerCenterStrat1d = {
             {2,2}, {0, 0}, {2,0}, {1,0}, //Outer Middles
            {0,1}, {2,1}, {0,2}, {1,2},   //Corners ordered across
            {1, 1}}; //Center field


    // Prefered moves when center field is not available
    protected int[][] conquerCornerStrat1a = {
            {0, 0}, {2,0}, {0,2}, {2,2} , //Outer Middles
            {1,0}, {2,1}, {1,2}, {0,1},  //Corners ordered across
            {1, 1}}; //Center field
    protected int[][] conquerCornerStrat1b = {
            {2,0}, {1,0}, {2,2}, {0,0} , //Outer Middles
            {2,1}, {1,2}, {0,1}, {1,0},  //Corners ordered across
            {1, 1}}; //Center field
    protected int[][] conquerCornerStrat1c = {
            {1,0}, {2,2}, {0, 0}, {2,0} , //Outer Middles
             {1,2}, {0,1}, {1,0}, {2,1}, //Corners ordered across
            {1, 1}}; //Center field
    protected int[][] conquerCornerStrat1d = {
            {2,2}, {0, 0}, {2,0}, {1,0}, //Outer Middles
            {0,1}, {1,0}, {2,1}, {1,2},  //Corners ordered across
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

        if(++roundCount == 5){
            roundCount = 1;
        }


        if(state.getField().getMacroboard()[0][0].equals(IField.EMPTY_FIELD)){
            //Find macroboard to play in
            for (int[] move : conquerCenterStrat1a)
            {

                if(state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
                {
                    //find move to play
                    for (int[] selectedMove : conquerCenterStrat1a)
                    {
                        int x = move[0]*3 + selectedMove[0];
                        int y = move[1]*3 + selectedMove[1];
                        if(state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                        {
                            return new Move(x,y);
                        }
                    }
                }
            }
        } else {
            for (int[] move : conquerCornerStrat1a)
            {
                if(state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
                {
                    //find move to play
                    for (int[] selectedMove : conquerCornerStrat1a)
                    {
                        int x = move[0]*3 + selectedMove[0];
                        int y = move[1]*3 + selectedMove[1];
                        if(state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                        {
                            return new Move(x,y);
                        }
                    }
                }
            }
        }




        //NOTE: Something failed, just take the first available move I guess!
        return state.getField().getAvailableMoves().get(0);
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
