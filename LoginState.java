package State;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import Domain.StateManager;
import Entities.User;
import ServerConnection.ServerAccess;

public class LoginState extends GameState {
	
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton login, register, back, exit;
    private Label userID, password;
    private Texture texture;
    private TextureRegion region;
    private TextField userTf, passTf;
    private String user, pass;
    private Dialog dial;    
    private String resp;
    
    private ServerAccess sa = new ServerAccess();
    
	public LoginState(StateManager gsm) {
		super(gsm);
	}

	@Override
	public void init() {
		// inizializzo Stage e faccio in modo che possa catturare IMPUT
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		// inizializzo atlas dandogli file atlas.pack che si riferisce all'immagine atlas.png
		atlas = new TextureAtlas("ui/uiskin.atlas");
	
		// inizializzo skin dandogli un file json contenente infomazioni
		// riguardanti font, labelStyle, ScrollPaneStyle, colori
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);
			
		// inizializzo lo spriteBatch la texture e la region
		batch = new SpriteBatch();       																				// serve a disegnare il background
        texture = new Texture(Gdx.files.internal("img/neon.png"));				// contiene l'immagine
        region = new TextureRegion(texture, 0, 0, 600, 600);											// ritaglia un pezzo della texture
        
        userTf = new TextField(null, skin);
        passTf = new TextField(null, skin);
        passTf.setPasswordMode(true);
        passTf.setPasswordCharacter('*');
        userTf.setColor(0, 0, 0, 0.4f);
        passTf.setColor(0, 0, 0, 0.4f);
        
     // creazione pulsante login ed aggancio di un listener
		login = new TextButton("  LOGIN ", skin);
		
		login.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				user = userTf.getText();
				pass = passTf.getText();
				
				dial = new Dialog("Login", skin, "dialog");
				
				resp = "Connection faild";
				
				try {
					resp = new String( sa.login(user, pass) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Label l = new Label(resp, skin);
				
				dial.text(resp);	
				dial.setResizable(true);
				dial.setSize(l.getWidth() + 20, l.getHeight() + 80);
				dial.setPosition(Gdx.graphics.getWidth()*1/2 - dial.getWidth()/2, Gdx.graphics.getHeight()*1/2 - dial.getHeight()/2 );
				
				TextButton ok = new TextButton(" OK ", skin);
				ok.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {	
						if (resp.equalsIgnoreCase("You are now logged in as: " + user)){
							   
								User.getInstance().setId(user);
								User.getInstance().setPw(pass);
							    
							    gsm.setState(1);		//MAIN
						}	
						dial.hide();
					}
				});

				dial.button(ok);
				stage.addActor(dial);	
			}
		});
		
		// creazione pulsante register ed aggancio di un listener
		register = new TextButton(" REGISTER ", skin);
		register.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				user = userTf.getText();
				pass = passTf.getText();
				
			    dial = new Dialog("Registration", skin, "dialog");
				resp = "Connection faild";
				
				try {
					resp = new String( sa.insertUser(user, pass) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Label l = new Label(resp, skin);
				
				dial.text(resp);	
				dial.setResizable(true);
				dial.setSize(l.getWidth() + 20, l.getHeight() + 80);
				dial.setPosition(Gdx.graphics.getWidth()*1/2 - dial.getWidth()/2, Gdx.graphics.getHeight()*1/2 - dial.getHeight()/2 );
				
				TextButton ok = new TextButton(" OK ", skin);
				ok.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {		
						dial.hide();
					}
				});

				dial.button(ok);
				stage.addActor(dial);	
			}
		});
		
		 // creazione pulsante exit ed aggancio di un listener
		exit = new TextButton("  EXIT ", skin);
		exit.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();		// esce dall'app
			}
		});
		
		
		
		// distanza tra scritta interna al pulsante e bordi del pulsante
		//play.pad(15);
		//exit.pad(15);
		
		register.setSize(register.getWidth(), 50);
		register.setPosition(Gdx.graphics.getWidth()/2 + 10, Gdx.graphics.getHeight()/2 - 150);
		exit.setSize(exit.getWidth(), 50);
		exit.setPosition(Gdx.graphics.getWidth() - exit.getWidth() -10 , 10);
		/*back.setSize(back.getWidth(), 50);
		back.setPosition(Gdx.graphics.getWidth() - exit.getWidth() -20 - back.getWidth() , 10);*/
		login.setSize(login.getWidth(), 50);
		login.setPosition(Gdx.graphics.getWidth()/2 - 10 - login.getWidth(), Gdx.graphics.getHeight()/2 - 150);
		userTf.setSize(200, 50);
		userTf.setPosition(Gdx.graphics.getWidth()/2 , Gdx.graphics.getHeight()/2 + 50 );
		passTf.setSize(200, 50);
		passTf.setPosition(Gdx.graphics.getWidth()/2 , Gdx.graphics.getHeight()/2 - 50 );
		
		// inizializzo la Label ( si può anche fare come mostrato nella classe PlayState)
		userID = new Label ("Username: ", skin);
		password = new Label ("Password: ", skin);
	
		userID.setSize(100, 50);
		password.setSize(100, 50);
		userID.setPosition(Gdx.graphics.getWidth()/2 - userID.getWidth() *2, Gdx.graphics.getHeight()/2 + 50 );
		password.setPosition(Gdx.graphics.getWidth()/2 - password.getWidth()*2, Gdx.graphics.getHeight()/2 - 50);
		
		stage.addActor(userID);
		stage.addActor(password);
		stage.addActor(login);
		stage.addActor(register);
		stage.addActor(exit);
		stage.addActor(userTf);
		stage.addActor(passTf);
	//	stage.addActor(back);

	}
      

	@Override
	public void update(float dt) {
	
	}

	@Override
	public void draw() {
		// inizia la sessione dello SpriteBatch dove disegna il background
		batch.begin();	
		batch.draw(region, 0, 0);
	    batch.end();        
	    
	  
		// avvia lo stage e tutti i suoi attori

	    stage.act();
	    
		stage.draw();
	}

	@Override
	public void handleInput() {
		
	}

	@Override
	public void dispose() {
		// cancellazzione dalla memoria ram
		stage.dispose();
		atlas.dispose();
		skin.dispose();
		texture.dispose();
	}

	@Override
	public void resize(int width, int height) {
		
	
		
	}
}