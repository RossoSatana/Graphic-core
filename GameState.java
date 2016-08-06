package State;

import Domain.StateManager;
import Entities.User;

public abstract class GameState  {
	protected StateManager gsm;
	
	protected GameState(StateManager gsm){
		this.gsm = gsm;
		init();
	}
	
	public abstract void init();
	public abstract void update(float dt);
	public abstract void resize(int width, int height);
	public abstract void draw();
	public abstract void handleInput();
	public abstract void dispose();

}