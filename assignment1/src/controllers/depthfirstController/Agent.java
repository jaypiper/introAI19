package controllers.depthfirstController;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

public class Agent extends AbstractPlayer {

    public Agent(StateObservation so, ElapsedCpuTimer timer){

    }
    boolean AtBegin = true;
    boolean game_over = false;

    ArrayList<Types.ACTIONS> optimal_action = new ArrayList<Types.ACTIONS>();
    ArrayList<StateObservation> reached_state = new ArrayList<StateObservation>();

    private boolean reached(StateObservation st){
        if(reached_state.size() == 0) return false;
        for(int i = 0; i < reached_state.size(); i++){
            if(st.equalPosition(reached_state.get(i))) return true;
        }
        return false;
    }

    private void depth_first(StateObservation stateObs){
        if(stateObs.isGameOver()) game_over = true;
        if(game_over) return;

        ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
        /*不探索所有解，只要找到解就进行输出，因此不一定是最优解*/
        for(int i = 0; !game_over && i < actions.size(); i++){
            StateObservation stCopy = stateObs.copy();
            Types.ACTIONS one_action = actions.get(i);
            stCopy.advance(one_action);
            if(!reached(stCopy)) {
                optimal_action.add(one_action);
                reached_state.add(stCopy);
                depth_first(stCopy);
            }
        }

        if(game_over == false){
            if(optimal_action.size() != 0)
                optimal_action.remove(optimal_action.size()-1);
            if(reached_state.size() != 0)
                reached_state.remove(reached_state.size()-1);
        }
        return;
    }

    int cou = 0;  //用于计数，从而按顺序输出解

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        StateObservation newstateob = stateObs.copy();
        if(AtBegin) {
            cou = 0;
            reached_state.add(stateObs);
            depth_first(newstateob);
            AtBegin = false;
            System.out.println("size"+optimal_action.size());
            for(int i = 0; i < optimal_action.size();i++)
                System.out.println("action:"+optimal_action.get(i));
        }
        Types.ACTIONS action = null ;
        if(cou < optimal_action.size())
            action = optimal_action.get(cou);
        cou ++;

        return action;
    }

    /*printDebug 用于调试输出当前状态,来源:sampleRandom, 测试时使用*/ /*
    private void printDebug(ArrayList<Observation>[] positions, String str)
    {
        if(positions != null){
            System.out.print(str + ":" + positions.length + "(");
            for (int i = 0; i < positions.length; i++) {
                System.out.print(positions[i].size() + ",");
            }
            System.out.print("); ");
        }else System.out.print(str + ": 0; ");
    }
    */
}
