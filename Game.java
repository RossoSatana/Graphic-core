package Domain;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;

import Domain.StateManager;

public class Game implements ApplicationListener {
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static OrthographicCamera cam;
	private StateManager gsm;
	
	public void create(){
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT=Gdx.graphics.getHeight();
		
		/* Imposta la dimensione della camera */
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		/* Sposta la camera con l'angolo in basso a destra sull'origine */
		cam.translate(WIDTH   /  2, HEIGHT   /  2);
		cam.update();
		
		// Creo un gameStateManager
		gsm = new StateManager();
	
	}
	
	public void render(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
	
		gsm.draw();
	}
	
	public void resize(int width, int height){
		
	}
	
	public void pause(){
		
	}
	
	public void resume(){
		
	}
	
	public void dispose(){
		
	}
}
