package State;

import ServerConnection.ServerAccess;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.events.MouseEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import Domain.StateManager;
import Entities.Monster;
import Entities.User;

public class MainMenu extends GameState {
	
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton play, exit, team, inventory, ok;
    private Label head, matching, other;
    private Texture texture, imgtex;
    private TextureRegion region;
    private Image loadcirc;
    private Image teamimg, inventoryimg;
    private Dialog dial;
    private String resp;
    private JSONArray jArr;
    private JSONObject jObj;
	private Boolean foundGame=false;
	    
    private int degrees=360;
    
    private ServerAccess sa = new ServerAccess();
    private Timer t;
    
    public class Tempored implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {		//cerco match
			try {
				
				if (sa.searchMatch(User.getInstance().getId()))	//match trovato	
					foundGame = true;
				
			} catch (IOException e) {
				System.out.println("Matching connection failed");				
				e.printStackTrace();
			}
		}
	}
    
	public MainMenu(StateManager gsm)  {
		super(gsm);
		
		try {
			resp = new String(sa.showInTeam(User.getInstance().getId()));
			User.getInstance().addToTeam(resp);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Tempored listener = new Tempored();
		t = new Timer (5000, listener);		//intervallo di tempo che temporizza il matching
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
        
     // creazione pulsante exit ed aggancio di un listener
		exit = new TextButton(" EXIT ", skin);
		exit.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();		// esce dall'app
			}
		});
		
		// inizializzo la Label ( si pu� anche fare come mostrato nella classe PlayState)
		head = new Label ("LetiFillo's Game", skin);
		
		// inizializzo la Label ( si pu� anche fare come mostrato nella classe PlayState)
		matching = new Label ("Waiting for a match", skin);
		matching.setVisible(false);
		
		// inizializzo la Label ( si pu� anche fare come mostrato nella classe PlayState)
		other = new Label ("TIPS: beware the number 27 \n Lorem ipsum dolor sit amet, \n Aenean commodo ligula eget dolor.", skin);
		other.setVisible(false);
		
		// creazione pulsante play ed aggancio di un listener
		play = new TextButton(" PLAY ", skin);
		play.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//gsm.setState(1); 		// porta al playState
				play.setVisible(false);
				team.setVisible(false);
				inventory.setVisible(false);
				exit.setVisible(false);
				loadcirc.setVisible(true);
				matching.setVisible(true);
				other.setVisible(true);
				teamimg.setVisible(false);
				inventoryimg.setVisible(false);

				try {		//aggiungo il giocatore in coda per il matching
					resp = sa.joinMatchMaking(User.getInstance().getId());
					//inMatching = true;
					
					t.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// creazione pulsante team ed aggancio di un listener
		team = new TextButton(" TEAM ", skin);
		team.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gsm.setState(3); 		// porta alla selezione team
			}
		});
		
		
		// pulsante inventory ed aggancio di un listener
		inventory = new TextButton(" INVENTORY ", skin);
		inventory.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//gsm.setState(2); 		// porta alla selezione inventario
				Skin sd = new Skin(Gdx.files.internal("ui/uiskin.json"));
				dial = new Dialog("OK", sd, "dialog");
				dial.setPosition(Gdx.graphics.getWidth()*1/2, Gdx.graphics.getWidth()*1/2);
				dial.setSize(200, 100);
				
				ok = new TextButton(" OK ", sd);
				ok.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						dial.hide();
			            dial.cancel();
			            dial.remove(); 
					}
				});

				dial.button(ok);
				stage.addActor(dial);
			}
		});
		
		/* Bottone con immagine
		
		Skin buttonSkin = new Skin();
		TextButtonStyle style = new TextButtonStyle(); // Button properties
        style.up = buttonSkin.getDrawable("buttonOff");
        style.down = buttonSkin.getDrawable("buttonOn");*/

		head.setPosition(Gdx.graphics.getWidth()*3/6 - head.getWidth()/2, Gdx.graphics.getHeight()* 4/5 );
		matching.setPosition(Gdx.graphics.getWidth()*1/10, Gdx.graphics.getHeight()*12/20 - matching.getHeight()/2);
		other.setPosition(Gdx.graphics.getWidth()*1/2 - other.getWidth()/2 , Gdx.graphics.getHeight()*3/10 - other.getHeight()/2);
		
		play.setSize(200, 70);
		play.setPosition(Gdx.graphics.getWidth()*1/10 , Gdx.graphics.getHeight()*12/20 - play.getHeight()/2 );
		team.setSize(200, 70);
		team.setPosition(Gdx.graphics.getWidth()*1/10 , Gdx.graphics.getHeight()*9/20 - team.getHeight()/2 );
		inventory.setSize(200, 70);
		inventory.setPosition(Gdx.graphics.getWidth()*1/10 , Gdx.graphics.getHeight()*6/20 - inventory.getHeight()/2 );
		exit.setSize(100, 50);
		exit.setPosition(Gdx.graphics.getWidth()*8/10 - exit.getWidth()/2 , Gdx.graphics.getHeight()*1/10 - exit.getHeight()/2 );
		
		stage.addActor(head);
		stage.addActor(matching);
		stage.addActor(other);
		stage.addActor(play);
		stage.addActor(team);
		stage.addActor(inventory);
		stage.addActor(exit);
		
		// Loading circle
		imgtex = new Texture(Gdx.files.internal("ui/Loading_indicator_circle.svg.png"));
		TextureRegion loadcircreg = new TextureRegion(imgtex);          
		loadcirc = new Image(loadcircreg);
		       
		loadcirc.setSize(50, 50);
		loadcirc.setPosition(Gdx.graphics.getWidth()*1/10 + 300, Gdx.graphics.getHeight()*12/20 - loadcirc.getHeight()/2);
		loadcirc.setRotation(0);
		loadcirc.setOrigin(50/2.0f, 50/2.0f);
		loadcirc.setVisible(false);
		stage.addActor(loadcirc);
				
		// Immegine TEAM	
		imgtex = new Texture(Gdx.files.internal("img/sunflower.png"));
		TextureRegion teamreg = new TextureRegion(imgtex);          
		teamimg = new Image(teamreg);       
		teamimg.setSize(50, 50);
		teamimg.setPosition(Gdx.graphics.getWidth()*1/10 + 225 , Gdx.graphics.getHeight()*9/20 - teamimg.getHeight()/2 );
		teamimg.setRotation(0);
		teamimg.setOrigin(50/2.0f, 50/2.0f);
		//teamimg.setVisible(false);
		stage.addActor(teamimg);
		
		// Immegine INVENTORY
		imgtex = new Texture(Gdx.files.internal("img/backpack_icon.png"));
		TextureRegion inventoryreg = new TextureRegion(imgtex);          
		inventoryimg = new Image(inventoryreg);       
		inventoryimg.setSize(50, 50);
		inventoryimg.setPosition(Gdx.graphics.getWidth()*1/10 +225, Gdx.graphics.getHeight()*6/20 - inventoryimg.getHeight()/2 );
		inventoryimg.setRotation(0);
		inventoryimg.setOrigin(50/2.0f, 50/2.0f);
		//inventoryimg.setVisible(false);
		stage.addActor(inventoryimg);
		
	}       
      

	@Override
	public void update(float dt) {
	
	}

	@Override
	public void draw() {
		// inizia la sessione dello SpriteBatch dove disegna il background
		
		batch.begin();	
		batch.draw(texture, 0, 0);
		
		loadcirc.setRotation(degrees--);
		
		batch.draw(region, 0, 0);
		
		if (foundGame)
			gsm.setState(2);
		
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
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
}