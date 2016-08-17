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
import org.restlet.resource.ResourceException;
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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import com.badlogic.gdx.audio.Sound;

import Domain.StateManager;
import Entities.Monster;
import Entities.User;

public class MainMenu extends GameState {
	
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton ok;
    private ImageButton play, exit, team, item, equip;
    private Label pl, ex, tm, it, eq;
    private Label head, matching, other;
    private Texture texture, imgtex;
    private TextureRegion region;
    private Image loadcirc;
    private String resp;
	private Boolean foundGame=false;
	private Dialog dial;
	    
    private int degrees=360;
    
    private Timer t;
    private Sound soundButton, soundError;
    private ServerAccess sa = new ServerAccess();
    
    
    public class Tempored implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {		//cerco match
			try {
				if (sa.searchMatch(User.getInstance().getId())) {	//match trovato	
					foundGame = true;
					User.getInstance().setFoe(sa.findFoe(User.getInstance().getId()));
				}
				
			} catch (IOException e) {
				System.out.println("Matching connection failed");				
				e.printStackTrace();
			}
		}
	}
    
	public MainMenu(StateManager gsm)  {
		super(gsm);
		
		try {
			User.getInstance().addToTeam(sa.mt(User.getInstance().getId()));
			User.getInstance().addToOwned(sa.mnt(User.getInstance().getId()));
			
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
		
		soundButton = Gdx.audio.newSound(Gdx.files.internal("music/buttonEnabled.mp3"));
		soundError = Gdx.audio.newSound(Gdx.files.internal("music/error.wav"));
		
		// inizializzo atlas dandogli file atlas.pack che si riferisce all'immagine atlas.png		
		atlas = new TextureAtlas("ui/uiskin.atlas");
		
		// inizializzo skin dandogli un file json contenente infomazioni
		// riguardanti font, labelStyle, ScrollPaneStyle, colori
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);
			
		// inizializzo lo spriteBatch la texture e la region
		batch = new SpriteBatch();       																				// serve a disegnare il background
		texture = new Texture(Gdx.files.internal("img/background.png"));				// contiene l'immagine
        region = new TextureRegion(texture, 0, 0, 600, 600);											// ritaglia un pezzo della texture
        
     // creazione pulsante exit ed aggancio di un listener
		exit = new ImageButton( skin, "button");
		exit.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					soundButton.play();
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Gdx.app.exit();	
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
		play = new ImageButton( skin, "button");
		play.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//gsm.setState(1); 		// porta al playState
				soundButton.play();
				
				if (User.getInstance().teamGetSize()==0){
					Label l = new Label("No monsters in team", skin);
					dial = new Dialog("Play", skin, "dialog");
					dial.text("No monsters in team");	
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
				else{
					play.setVisible(false);
					equip.setVisible(false);
					team.setVisible(false);
					item.setVisible(false);
					exit.setVisible(false);
					ex.setVisible(false);
					tm.setVisible(false);
					it.setVisible(false);
					eq.setVisible(false);
					pl.setVisible(false);
					loadcirc.setVisible(true);
					matching.setVisible(true);
					other.setVisible(true);

					try {		//aggiungo il giocatore in coda per il matching
						resp = sa.joinMatchMaking(User.getInstance().getId());
						//inMatching = true;
						
						t.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		// creazione pulsante team ed aggancio di un listener
		team = new ImageButton(skin, "button");
		team.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				soundButton.play();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gsm.setState(3); 		// porta alla selezione team
			}
		});
		
		// pulsante inventory ed aggancio di un listener
		item = new ImageButton( skin, "button");
		item.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				soundButton.play();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gsm.setState(4);
			}
		});
				
		
		equip = new ImageButton( skin, "button");
		equip.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				soundButton.play();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		head.setPosition(Gdx.graphics.getWidth()*3/6 - head.getWidth()/2, Gdx.graphics.getHeight()* 4/5 );
		matching.setPosition(Gdx.graphics.getWidth()*1/10, Gdx.graphics.getHeight()*12/20 - matching.getHeight()/2);
		other.setPosition(Gdx.graphics.getWidth()*1/2 - other.getWidth()/2 , Gdx.graphics.getHeight()*3/10 - other.getHeight()/2);
		
		play.setSize(150, 70);
		play.setPosition(Gdx.graphics.getWidth()*1/20 , Gdx.graphics.getHeight()*8/20 - play.getHeight()/2 );
		team.setSize(150, 70);
		team.setPosition(Gdx.graphics.getWidth()*2/20 , Gdx.graphics.getHeight()*6/20 - team.getHeight()/2 );
		item.setSize(150, 70);
		item.setPosition(Gdx.graphics.getWidth()*3/20 , Gdx.graphics.getHeight()*4/20 - item.getHeight()/2 );
		equip.setSize(150, 70);
		equip.setPosition(Gdx.graphics.getWidth()*4/20 , Gdx.graphics.getHeight()*2/20 - item.getHeight()/2);
		exit.setSize(150, 70);
		exit.setPosition(Gdx.graphics.getWidth() - exit.getWidth() -10 , Gdx.graphics.getHeight()*1/10 - exit.getHeight()/2 );
		
		pl = new Label(" PLAY ", skin, "lucida.small");
		ex = new Label(" EXIT ", skin, "lucida.small");
		tm = new Label(" TEAM ", skin, "lucida.small");
		it = new Label(" ITEM ", skin, "lucida.small");
		eq = new Label(" EQUIP ", skin, "lucida.small");
		pl.setPosition(play.getX() + 25, play.getY() + 25);
		ex.setPosition(exit.getX() + 25, exit.getY() + 25);
		tm.setPosition(team.getX() + 25, team.getY() + 25);
		it.setPosition(item.getX() + 25, item.getY()+ 25);
		eq.setPosition(equip.getX() + 25, equip.getY() + 25);
		pl.setTouchable(Touchable.disabled);
		ex.setTouchable(Touchable.disabled);
		tm.setTouchable(Touchable.disabled);
		it.setTouchable(Touchable.disabled);
		eq.setTouchable(Touchable.disabled);
		
		stage.addActor(head);
		stage.addActor(matching);
		stage.addActor(other);
		stage.addActor(play);
		stage.addActor(team);
		stage.addActor(item);
		stage.addActor(equip);
		stage.addActor(exit);
		stage.addActor(eq);
		stage.addActor(pl);
		stage.addActor(tm);
		stage.addActor(it);
		stage.addActor(ex);
		
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
		{
			t.stop();
						
			gsm.setState(4);
		}
			
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
		soundButton.dispose();
		soundError.dispose();
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
}