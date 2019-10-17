package controllers.Astar;

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
    ArrayList<StateObservation> frontier = new ArrayList<StateObservation>();
    ArrayList<ArrayList<Types.ACTIONS> > possible_actions = new ArrayList<ArrayList<Types.ACTIONS> >();

    private int dis(Vector2d a,Vector2d b){  //计算两点之间的曼哈顿距离
        return Math.abs((int) a.x - (int) b.x) +  Math.abs((int) a.y - (int) b.y);
    }

    private int h(StateObservation ob){  //状态的总代价 = 已经走过步数*1000 + 预测剩余距离
        return ob.weight + ob.ahead*50;
    }

    private int predict(StateObservation ob){ //预测剩余距离
        if(ob.getGameWinner() == Types.WINNER.PLAYER_WINS) return 0;
        else if(ob.getGameWinner() == Types.WINNER.PLAYER_LOSES) return 0xFFFFFFF;
        ArrayList<Observation>[] fixedPositions = ob.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = ob.getMovablePositions();
        Vector2d now = ob.getAvatarPosition();
        Vector2d  gatepos = fixedPositions[1].get(0).position;;

        if(movingPositions[0].size() != 1) return  dis(now, gatepos);
        else {
            Vector2d keypos = movingPositions[0].get(0).position;
//            //箱子压住钥匙
//            if(movingPositions.length > 1) {
//                for (int i = 0; i < movingPositions[1].size(); i++) {
//                    Vector2d box = movingPositions[1].get(i).position;
//                    if (keypos.x == box.x && keypos.y == box.y) return 0xFFFFFFF;
//                }
//            }
            return dis(now,keypos) + dis(keypos,gatepos) ;
        }
    }
    private int choose_ob(){
        int min_index = 0;
        int min_num = h(frontier.get(0));
        for(int i = 1; i < frontier.size(); i++){
            if(h(frontier.get(i)) < min_num) {
                min_index = i;
                min_num = h(frontier.get(i));
            }
        }
        return min_index;
    }
    private void extend_ob(int ob_index){
        StateObservation ob = frontier.get(ob_index);
        ArrayList<Types.ACTIONS> now_actions = possible_actions.get(ob_index);
        if(ob.isGameOver()) return;
        frontier.remove(ob_index);
        possible_actions.remove(ob_index);
        ArrayList<Types.ACTIONS> all_actions = ob.getAvailableActions();
        for(int i = 0; i < all_actions.size(); i++){
            StateObservation stCopy = ob.copy();
            stCopy.advance(all_actions.get(i));
            ArrayList<Types.ACTIONS> new_actions = (ArrayList)now_actions.clone();
            new_actions.add(all_actions.get(i));
            stCopy.weight = predict(stCopy);
            stCopy.ahead = ob.ahead + 1;        //copy无法复制获得ahead
            if(!ob.equalPosition(stCopy)) {
                frontier.add(stCopy);
                possible_actions.add(new_actions);
            }
        }

        return;
    }
    private Types.ACTIONS astar(StateObservation stateObs){

        frontier.clear();
        possible_actions.clear();
        //将当前状态和行动加入可访问状态中
        stateObs.weight = predict(stateObs);
        stateObs.ahead = 0;
        frontier.add(stateObs);
        ArrayList<Types.ACTIONS> tem_actions = new ArrayList<Types.ACTIONS>();
        tem_actions.add(Types.ACTIONS.ACTION_NIL);
        possible_actions.add(tem_actions);
        //进行 n 次 A* 展开
        for(int i = 0; i < 20; i++){   //数目
            int extOb = choose_ob();  //选择当前最优的状态
            extend_ob(extOb);         //展开
        }
        return possible_actions.get(choose_ob()).get(1); //获得当前最优状态的第一步

    }
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        Types.ACTIONS action = astar(stateObs);
        System.out.println(action);
        return action;
    }
}
