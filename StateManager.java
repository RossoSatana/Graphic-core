package Domain;

import Entities.User;
import State.ChangeTeamState;
import State.GameState;
import State.MainMenu;
import State.Match;
import State.LoginState;

public class StateManager {
	private GameState gamestate;
	
	public static final int LOGIN = 0;
	public static final int MAIN = 1;
	public static final int MATCH = 2;
	public static final int CHANGETEAM = 3;
	
	public StateManager(){
		setState(LOGIN);
	}

	public void setState(int state){
		if(gamestate != null) gamestate.dispose();
		
		if(state == LOGIN){
			gamestate = new LoginState(this);
		}
		if(state == MAIN){
			gamestate = new MainMenu(this);
		}
		if(state == MATCH){
			gamestate = new Match(this);
		}
		if(state == CHANGETEAM){
			gamestate = new ChangeTeamState(this);
		}
	}
	
	public void update (float dt) {
		gamestate.update(dt);
	}
	
	
	public void draw() {
		gamestate.draw();
	}
}