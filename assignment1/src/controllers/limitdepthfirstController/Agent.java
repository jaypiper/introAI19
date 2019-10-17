package controllers.limitdepthfirstController;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import java.util.ArrayList;


public class Agent extends AbstractPlayer {
    public Agent(StateObservation so, ElapsedCpuTimer timer){

    }

    private boolean reach_goal(Vector2d goal, StateObservation ob){
        Vector2d nowpos = ob.getAvatarPosition();
        return(ob.isGameOver() || (goal.x == nowpos.x && goal.y == nowpos.y));
    }
    private Vector2d set_goal(StateObservation ob){
        ArrayList<Observation>[] fixedPositions = ob.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = ob.getMovablePositions();
        Vector2d goal;
        if (movingPositions[0].size() == 0) goal = fixedPositions[1].get(0).position; //目标的坐标
        else goal = movingPositions[0].get(0).position; //钥匙的坐标
        return goal;
    }
    private Types.ACTIONS find_action(StateObservation stateObs){
        ArrayList<Types.ACTIONS> all_actions = stateObs.getAvailableActions();
        Vector2d goal = set_goal(stateObs);
        int d = 0xFFFFFFF, d_index = -1;
        for(int i = 0; i < all_actions.size(); i++){
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(all_actions.get(i));
            if(reach_goal(goal,stCopy)) return all_actions.get(i);
            int tem = ldfs(stCopy,1);
            if(tem < d){ d = tem;  d_index = i; }
        }
       return all_actions.get(d_index);
    }

    private int ldfs(StateObservation stateObs, int dep) {
        Vector2d goal = set_goal(stateObs);
        Vector2d now = stateObs.getAvatarPosition(); //人物位置
        int d = (Math.abs((int) now.x - (int) goal.x) +  Math.abs((int) now.y - (int) goal.y))*dep;
        if (dep == 4) return d;
        else {
            ArrayList<Types.ACTIONS> all_actions = stateObs.getAvailableActions();
            for(int i = 0; i < all_actions.size(); i++){
                StateObservation stCopy = stateObs.copy();
                stCopy.advance(all_actions.get(i));
                if(reach_goal(goal,stCopy)) d = Math.min(dep + 1,d); //达到设立目标时权为步数
                if(stateObs.equalPosition(stCopy)) continue;
                d = Math.min(d,ldfs(stCopy,dep + 1));
            }
            return d;
        }
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        Types.ACTIONS action = find_action(stateObs);
        System.out.println(action);
        return action;
    }
}
