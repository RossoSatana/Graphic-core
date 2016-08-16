package State;

import javax.sound.sampled.AudioSystem;

import sun.audio.AudioStream;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

import org.restlet.resource.ResourceException;
import org.w3c.dom.events.MouseEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
/*import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;*/
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;

import Domain.StateManager;
import Entities.Monster;
import Entities.User;
import ServerConnection.ServerAccess;
import State.ChangeTeamState.MonsterButton;
import State.MainMenu.Tempored;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Match extends GameState {
	
	public static final int MSEL = 0;	//fase di selezione del mostro del proprio team che svolgerà l'azione
	public static final int EMSEL = 1;
	public static final int ER = 2;		//enemy's round
	
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton attack, attconf, attcanc, exit, send, iconchat;
    private Label stat0, stat1, stat2, stat3, stat4, stat5, emsel;
    private Pixmap pixmap;
    private Dialog dial;
    private Texture texture, tabletex, labeltex;
    private TextureRegion region; 
    private ArrayList <ProgressBar> barl, barm, barel, barem;
    private ArrayList <ImageButton> m, em;
    private Integer mindex = -1, emindex = -1;	//valore errato per controlli di selezione
    private int hp = 0, mp = 0, ad = 0, ap = 0 ,def = 0, mDef = 0, i, range;
    private String name, denomination, clas, type, chatStr="";
    private int state;
    private TextField chatTf;
    private Label chatL;
    private Table chatTable;
    private ScrollPane chatScroll;
    private ProgressBar roundBar;
    private Sound attacksound;
  //  private Sound attck, mattck, base;
    
    private ServerAccess sa = new ServerAccess();
    private Timer t, td;
    
    private final static int ROUND = 30;
    
    public class Tempored implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {		// aggiornamento ogni 5 secondi con il server
			try {
				String chatRead = sa.chatRead(User.getInstance().getId());
				if(chatRead!=null)
				{
					chatStr = chatStr + "\n" + User.getInstance().getFoe() + ": " + chatRead;
					chatL.setText(chatStr);
					sa.chatWrite(User.getInstance().getFoe(), "");
				}
				
				if (sa.round(User.getInstance().getId()).equals(User.getInstance().getId())) {	//turno del giocatore
					if(state == ER)
						state = MSEL;
				}
				else
				{
					if(state != ER)
					{
						state = ER;
						clearActionQueue();		//a fine turno svuoto la coda di azioni del giocatore
					}
					updateInfo();
					updateFoeInfo();
				}
			} catch (IOException ioe) {
				System.out.println("Update connection failed: IOException");				
				ioe.printStackTrace();
			} catch (ResourceException re) {
				System.out.println("Update connection failed: ResourceException");				
				re.printStackTrace();
			} catch (JSONException je) {
				System.out.println("Update connection failed: JSONException");				
				je.printStackTrace();
			}
			
		}
	}
    
    public class DialTimer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0)  {		// aggiornamento ogni 5 secondi con il server
			dial.hide();
			td.stop();
		}
	}
    
    public class MonsterButton extends ClickListener{
    private int index;
    private Monster monster;
    private String source;
    
    public MonsterButton(int index, Monster monster, String source){
    	this.index = index;
    	this.monster = monster;
    	this.source = source;
    }
    	
    @Override
    public void clicked(InputEvent event, float x, float y) {
    	name = monster.getName();
    	denomination = monster.getDenomination();
		clas = monster.getClas();
		type = monster.getType();
		
    	if (source == "m"){
        	hp = monster.getcHp();
    		ad = monster.getAd();
    		ap = monster.getAp();
    		def = monster.getDef();
    		mDef = monster.getmDef();
    		range = monster.getRange();
    	}
		
			if (source == "m")
				mindex = Integer.valueOf(index);
			if (source == "em")
				emindex = Integer.valueOf(index);
			
			dispose();
			init();
    	}
    }
    
	public Match(StateManager gsm){
		super(gsm);

		attacksound = Gdx.audio.newSound(Gdx.files.internal("data/sword_attack.wav"));	//suono per attacchi normali
	/*	mattck = Gdx.audio.newSound(Gdx.files.internal("data/mattck.wav"));	//suono per attacchi con abilità
		base = Gdx.audio.newSound(Gdx.files.internal("data/bgm_action_1.wav"));	//suono per attacchi con abilità
		base.loop();*/
		
			try {
				if (sa.round(User.getInstance().getId()).equals(User.getInstance().getId())) {
					state = MSEL;
				}
				else
				{
					state = ER;
				}
			} catch (ResourceException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		Tempored listener = new Tempored();
		t = new Timer (5000, listener);		//intervallo di tempo che temporizza gli aggiornamenti con il server
		t.start();
		
		DialTimer dhlistener = new DialTimer();
		td = new Timer (3000, dhlistener);
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
		batch = new SpriteBatch();       										// serve a disegnare il background
		texture = new Texture(Gdx.files.internal("img/neon.png"));				// contiene l'immagine
		region = new TextureRegion(texture, 0, 0, 600, 600);					// ritaglia un pezzo della texture
		
		//CHAT
		chatL = new Label(null, skin);
		chatL.setWrap(true);
		
		chatTable = new Table();
		chatTable.add(chatL).width(150);
		chatTable.setSize(150, chatTable.getHeight());
		chatTable.getCell(chatL).spaceBottom(1).expand().bottom().left().uniform();
		
		chatScroll = new ScrollPane(chatTable);
		chatTable.setColor(0, 0, 0, 0.4f);
		chatScroll.setScrollingDisabled(true, false);
		chatScroll.setSize(150, 200);
		chatScroll.setPosition(Gdx.graphics.getWidth()*4/120 , Gdx.graphics.getHeight()*75/120);
		
		chatTf = new TextField(null, skin);
		chatTf.setColor(0, 0, 0, 0.6f);
		chatTf.setPosition(Gdx.graphics.getWidth()*4/120 , Gdx.graphics.getHeight()*75/120-chatTf.getHeight());
		stage.addActor(chatTf);
		stage.addActor(chatScroll);
		
		iconchat = new TextButton("^", skin);
		iconchat.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
				if(chatTf.isVisible())
				{
					chatTf.setVisible(false);
					chatScroll.setVisible(false);
					send.setVisible(false);
				}
				else
				{
					chatTf.setVisible(true);
					chatScroll.setVisible(true);
					send.setVisible(true);
				}
			}
		});				
		iconchat.setSize(10, 10);
		iconchat.setPosition(Gdx.graphics.getWidth()*4/120 + chatScroll.getWidth() -10, Gdx.graphics.getHeight()*75/120);
		stage.addActor(iconchat);
		
		send = new TextButton("<-'", skin);
		send.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
				chatStr = chatStr + "\n" + User.getInstance().getId() + ": " + chatTf.getText();
				chatL.setText(chatStr);
				try {
					sa.chatWrite(User.getInstance().getId(), chatTf.getText());
				} catch (ResourceException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chatTf.setText("");
			}
		});				
		send.setSize(20, 20);
		send.setPosition(Gdx.graphics.getWidth()*4/120 + chatTf.getWidth(), Gdx.graphics.getHeight()*75/120-chatTf.getHeight());
		stage.addActor(send);
		
		// Inizializzazione array di ProgressBar
		barl = new ArrayList<ProgressBar>();
		barm = new ArrayList<ProgressBar>();
		barel = new ArrayList<ProgressBar>();
		barem = new ArrayList<ProgressBar>();
		
		for(i=0; i<User.getInstance().teamGetSize(); i++){
			User.getInstance().showTeamMonster(i).setPosition(i);
		}
		
		m = new ArrayList<ImageButton>();
		for(i=0; i<User.getInstance().fightingGetSize(); i++){	
			m.add(new ImageButton(skin, User.getInstance().showFightingMonster(i).getDenomination()));	
			m.get(i).addListener(new MonsterButton(i, User.getInstance().showFightingMonster(i), "m"));
		}
		
		em = new ArrayList<ImageButton>();
		for(i=0; i<User.getInstance().foeTeamGetSize(); i++){	
			em.add(new ImageButton(skin, User.getInstance().showFoeTeamMonster(i).getDenomination()));	
			em.get(i).addListener(new MonsterButton(i, User.getInstance().showFoeTeamMonster(i), "em"));
		}
		
		
		for(i=0; i<User.getInstance().fightingGetSize(); i++){		//posizionamento sullo stage degli image button dei mostri del giocatore
			m.get(i).setSize(106, 75);
			switch(User.getInstance().showFightingMonster(i).getPosition() +1){
				case 7:	m.get(i).setPosition(Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*5/120);
						break;
				case 8:	m.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*5/120);
						break;
				case 9: m.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120+120, Gdx.graphics.getHeight()*5/120);
						break;
				case 4: m.get(i).setPosition(Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*6/120 + 75);
						break;
				case 5:	m.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*6/120 + 75);
						break;
				case 6: m.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*6/120 + 75);
						break;
				case 1: m.get(i).setPosition(Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*7/120 +75 + 75);
						break;
				case 2:	m.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*7/120 +75 +75);
						break;
				case 3:	m.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*7/120 +75 + 75);
						break;
			}
			stage.addActor(m.get(i));
		}
		
		for(i=0; i<User.getInstance().foeTeamGetSize(); i++){
			em.get(i).setSize(106, 75);
			switch(User.getInstance().showFoeTeamMonster(i).getPosition() +1){
				case 1:	em.get(i).setPosition(Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*70/120);
						break;
				case 2:	em.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*70/120);
						break;
				case 3: em.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*70/120);
						break;
				case 4: em.get(i).setPosition(Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*71/120 + 75);
						break;
				case 5:	em.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*71/120 + 75);
						break;
				case 6: em.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*71/120 + 75);
						break;
				case 7: em.get(i).setPosition(Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*72/120 +75 +75);
						break;
				case 8:	em.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*72/120 +75 +75);
						break;
				case 9:	em.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*72/120 +75 +75);
						break;
			}
			stage.addActor(em.get(i));
		}
		
		// Tavolo da gioco
		pixmap = new Pixmap(380, 246, Format.RGBA8888 );
		pixmap.setColor(128,128,128, 0.7f);
		pixmap.fillRectangle(0, 1, 380, 246);
		tabletex = new Texture(pixmap);
		
		// Sfondo per statistiche
		pixmap = new Pixmap(380, 60, Format.RGBA8888 );
		pixmap.setColor(128,0,128, 0.5f);
		pixmap.fillRectangle(0, 1, 380, 60);
		labeltex = new Texture(pixmap);
		
		// HP, MP barStyle
		Skin skinb = new Skin();
		pixmap = new Pixmap(8, 75, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skinb.add("white", new Texture(pixmap));
		
		TextureRegionDrawable textureBarHP = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/life.png"))));
		ProgressBarStyle barStyleHP = new ProgressBarStyle(skinb.newDrawable("white", Color.DARK_GRAY), textureBarHP);
		barStyleHP.knobBefore = barStyleHP.knob;
		
		TextureRegionDrawable textureBarMP = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/mana.png"))));
		ProgressBarStyle barStyleMP = new ProgressBarStyle(skinb.newDrawable("white", Color.DARK_GRAY), textureBarMP);
		barStyleMP.knobBefore = barStyleMP.knob;
		
		// Inizializzazione delle barre della vita dei mostri del giocatore
		for(i=0; i<User.getInstance().fightingGetSize(); i++)
		{						
			barl.add(new ProgressBar(8, 75, 0.1f, true, barStyleHP));
			barl.get(i).setSize(8, 75);
			
			barm.add(new ProgressBar(6, 75, 0.1f, true, barStyleMP));
			barm.get(i).setSize(6, 75);
			
			switch(User.getInstance().showFightingMonster(i).getPosition()+1){
				case 7: barl.get(i).setPosition(Gdx.graphics.getWidth()*41/120 + 106, Gdx.graphics.getHeight()*5/120);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*41/120 + 114, Gdx.graphics.getHeight()*5/120);
						break;
				case 8:	barl.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*5/120);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*5/120);
					  	break;
				case 9: barl.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*5/120);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*43/120 +120+ 120+114, Gdx.graphics.getHeight()*5/120);
						break;
				case 4: barl.get(i).setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*6/120 + 75);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*41/120+114, Gdx.graphics.getHeight()*6/120 +75);
					    break;
				case 5: barl.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*6/120 + 75);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*6/120 +75);
						break;
				case 6: barl.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*6/120 +75);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +114, Gdx.graphics.getHeight()*6/120 +75);
						break;
				case 1: barl.get(i).setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*7/120 +75 +75);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*41/120+114, Gdx.graphics.getHeight()*7/120 +75 + 75);
					    break;
				case 2: barl.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*7/120 +75 +75);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*7/120 +75 + 75);
						break;
				case 3: barl.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*7/120 +75 +75);
						barm.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +114, Gdx.graphics.getHeight()*7/120 +75 +75);
						break;
			}
			
			stage.addActor(barl.get(i));
			stage.addActor(barm.get(i));
		}
		
		// Inizializzazione barre della vita dei mostri avversari
		for(i=0; i<User.getInstance().foeTeamGetSize(); i++)
		{
			barel.add(new ProgressBar(8, 75, 0.1f, true, barStyleHP));
			barel.get(i).setSize(8, 75);
			
			barem.add(new ProgressBar(6, 75, 0.1f, true, barStyleMP));
			barem.get(i).setSize(6, 75);
			
			switch(User.getInstance().showFoeTeamMonster(i).getPosition() +1){
				case 1: barel.get(i).setPosition(Gdx.graphics.getWidth()*41/120 + 106, Gdx.graphics.getHeight()*70/120);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*41/120 + 114, Gdx.graphics.getHeight()*70/120);
					    break;
				case 2:	barel.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*70/120);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*70/120);
					    break;
				case 3: barel.get(i).setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*70/120);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*43/120 +120+ 120+114, Gdx.graphics.getHeight()*70/120);
						break;
				case 4: barel.get(i).setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*71/120 +75);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*41/120 +114, Gdx.graphics.getHeight()*71/120 +75);
					    break;
				case 5: barel.get(i).setPosition(Gdx.graphics.getWidth()*42/120 +120 +106, Gdx.graphics.getHeight()*71/120 +75);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*42/120 +120 +114, Gdx.graphics.getHeight()*71/120 +75);
						break;
				case 6: barel.get(i).setPosition(Gdx.graphics.getWidth()*43/120 +120 +120 +106, Gdx.graphics.getHeight()*71/120 +75);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*43/120 +120 +120 +114, Gdx.graphics.getHeight()*71/120 +75);
						break;
				case 7: barel.get(i).setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*72/120 +75 +75);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*41/120 +114, Gdx.graphics.getHeight()*72/120 +75 +75);
					    break;
				case 8: barel.get(i).setPosition(Gdx.graphics.getWidth()*42/120 +120 +106, Gdx.graphics.getHeight()*72/120 +75 +75);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*42/120 +120 +114, Gdx.graphics.getHeight()*72/120 +75 +75);
						break;
				case 9: barel.get(i).setPosition(Gdx.graphics.getWidth()*43/120 +120 +120 +106, Gdx.graphics.getHeight()*72/120 +75 +75);
						barem.get(i).setPosition(Gdx.graphics.getWidth()*43/120 +120 +120 +114, Gdx.graphics.getHeight()*72/120 +75 +75);
						break;
			}
			
			stage.addActor(barel.get(i));
			stage.addActor(barem.get(i));
		}
		
	// creazione pulsante attacco ed aggancio di un listener
	attack = new TextButton("ATTACK ", skin);
	attack.addListener(new ClickListener(){
	@Override
	public void clicked(InputEvent event, float x, float y) {
			if (mindex == -1)
			{
				Label l = new Label("Select a monster form your team to proceed", skin);
				dial = new Dialog("Attack", skin, "dialog");
				dial.text("Select a monster form your team to proceed");	
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
			else
			{
				state = EMSEL;	
			}
		}
	});		
	attack.setSize(150, 50);
	attack.setPosition(Gdx.graphics.getWidth()*6/120, Gdx.graphics.getHeight()*40/120);
	stage.addActor(attack);
	
	attconf = new TextButton("OK", skin);
	attconf.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (emindex == -1)
			{
				Label l = new Label("Select a monster form your enemy's team to proceed", skin);
				dial = new Dialog("Attack", skin, "dialog");
				dial.text("Select a monster form your team to proceed");	
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
			else
			{
				//System.out.println("COD_A: " + User.getInstance().showFightingMonster(mindex).getCodM() + " COD_T: " + User.getInstance().showFoeTeamMonster(emindex).getCodM());
				try {
					JSONObject jobj = new JSONObject(sa.aAttack(User.getInstance().showFightingMonster(mindex).getCodM(), User.getInstance().showFoeTeamMonster(emindex).getCodM()));
					
					String dd;
					
					if(jobj.has("Error")){
						dd = jobj.getString("Error");						
					}
					else
						dd = "Your " + User.getInstance().showFightingMonster(mindex).getName() + " has inflicted " + jobj.getInt("Damage") + " damage to enemy's" + User.getInstance().showFoeTeamMonster(emindex).getName();
					
					Label l = new Label(dd, skin);
					dial = new Dialog("Attack", skin, "dialog");
					dial.text(dd);	
					dial.setResizable(true);
					dial.setSize(l.getWidth() + 20, l.getHeight() + 80);
					dial.setPosition(Gdx.graphics.getWidth()*1/2 - dial.getWidth()/2, Gdx.graphics.getHeight()*1/2 - dial.getHeight()/2 );
					
					stage.addActor(dial);	
					
					attacksound.play();
					
					updateInfo();
					updateFoeInfo();
					
					td.start();
				} catch (ResourceException | IOException | JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				state=MSEL;
			}
		}
	});		
	attconf.setSize(40, 30);
	attconf.setPosition(Gdx.graphics.getWidth()*6/120 + 110, Gdx.graphics.getHeight()*32/120);
	stage.addActor(attconf);
	
	attcanc = new TextButton("<-", skin);
	attcanc.addListener(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y) {
			state=MSEL;
		}
	});		
	attcanc.setSize(40, 30);
	attcanc.setPosition(Gdx.graphics.getWidth()*6/120, Gdx.graphics.getHeight()*32/120);
	stage.addActor(attcanc);
		
	// creazione pulsante exit ed aggancio di un listener
	exit = new TextButton(" EXIT ", skin);
	exit.addListener(new ClickListener(){
	@Override
	public void clicked(InputEvent event, float x, float y) {
			Gdx.app.exit();		// esce dall'app
		}
	});				
	exit.setSize(100, 50);
	exit.setPosition(Gdx.graphics.getWidth()*6/120, Gdx.graphics.getHeight()*6/120);
	stage.addActor(exit);
	
	// inizializzo STAT per MSEL
	stat0 = new Label ("NAME: " + name, skin, "lucida.12");
	stat1 = new Label ("HP: " + hp +   "\nAD: " + ad , skin, "lucida.12");
	stat2 = new Label ("MP: " + mp +   "\nAP: " + ap , skin, "lucida.12");
	stat3 = new Label ("DEF: " + def + "\nmDEF: " + mDef  , skin, "lucida.12");
	stat4 = new Label ("DENOMINATION: " + denomination + "\nCLASS: " + clas + "\nTYPE: " + type, skin,"lucida.12");
	stat5 = new Label ("RANGE: " + range , skin,"lucida.12");

	stat0.setPosition(Gdx.graphics.getWidth()*80/120, Gdx.graphics.getHeight()*62/120);
	stat1.setPosition(Gdx.graphics.getWidth()*80/120, Gdx.graphics.getHeight()*56/120);
	stat2.setPosition(Gdx.graphics.getWidth()*90/120, Gdx.graphics.getHeight()*56/120);
	stat3.setPosition(Gdx.graphics.getWidth()*100/120, Gdx.graphics.getHeight()*56/120);
	stat4.setPosition(Gdx.graphics.getWidth()*45/120, Gdx.graphics.getHeight()*56/120);
	stat5.setPosition(Gdx.graphics.getWidth()*65/120, Gdx.graphics.getHeight()*58/120);

	stage.addActor(stat0);
	stage.addActor(stat1);
	stage.addActor(stat2);
	stage.addActor(stat3);
	stage.addActor(stat4);
	stage.addActor(stat5);
	
	roundBar = new ProgressBar(0, ROUND*100, 1, false, skin);
	roundBar.setSize(300, roundBar.getHeight());
	roundBar.setPosition(Gdx.graphics.getWidth()*50/120, Gdx.graphics.getHeight()*57/120);
	roundBar.setAnimateDuration(ROUND);
	//roundBar.setValue(ROUND*100);
	stage.addActor(roundBar);
	roundBar.setVisible(false);
	
	//inizalizzo label per EMSEL
	emsel = new Label("Select a target", skin);
	emsel.setPosition(Gdx.graphics.getWidth()*45/120, Gdx.graphics.getHeight()*60/120);
	stage.addActor(emsel);
}
	
@Override
public void update(float dt) {

}

@Override
public void draw() {
	// inizia la sessione dello SpriteBatch dove disegna il background
	
	batch.begin();	
	batch.draw(texture, 0, 0);
	
	chatL.setText(chatStr);
	
	if(state == MSEL){
		attconf.setVisible(false);
		attcanc.setVisible(false);
		stat0.setVisible(true);
		stat1.setVisible(true);
		stat2.setVisible(true);
		stat3.setVisible(true);
		stat4.setVisible(true);
		stat5.setVisible(true);
		emsel.setVisible(false);
		roundBar.setVisible(false);
		roundBar.setAnimateDuration(0);
		roundBar.setValue(0);
		roundBar.setAnimateDuration(ROUND);
		for(ImageButton ib : m)
			ib.setTouchable(Touchable.enabled);
		for(ImageButton ib : em)
			ib.setTouchable(Touchable.disabled);
	}
	if(state == EMSEL)
	{
		attconf.setVisible(true);
		attcanc.setVisible(true);
		stat0.setVisible(false);
		stat1.setVisible(false);
		stat2.setVisible(false);
		stat3.setVisible(false);
		stat4.setVisible(false);
		stat5.setVisible(false);
		emsel.setVisible(true);
		emsel.setText("Select a target for your " + User.getInstance().showFightingMonster(mindex).getName() + "'s attack");
		for(ImageButton ib : m)
			ib.setTouchable(Touchable.disabled);
		for(ImageButton ib : em)
			ib.setTouchable(Touchable.enabled);
	}
	if(state == ER){
		attack.setTouchable(Touchable.disabled);
		attconf.setVisible(false);
		attcanc.setVisible(false);
		stat0.setVisible(false);
		stat1.setVisible(false);
		stat2.setVisible(false);
		stat3.setVisible(false);
		stat4.setVisible(false);
		stat5.setVisible(false);
		emsel.setVisible(true);
		emsel.setText("Wait for your round, " + User.getInstance().getFoe() + " is playing");
		roundBar.setVisible(true);
		roundBar.setValue(ROUND*100);
		exit.setTouchable(Touchable.disabled);
		for(ImageButton ib : m)
			ib.setTouchable(Touchable.disabled);
		for(ImageButton ib : em)
			ib.setTouchable(Touchable.disabled);	
	}
	
	for(i=0; i<User.getInstance().fightingGetSize(); i++)
	{
		barl.get(i).setValue((barl.get(i).getMaxValue()/User.getInstance().showFightingMonster(i).getHp()) * User.getInstance().showFightingMonster(i).getcHp());
//		barm.get(i).setValue(barm.get(i).getMaxValue()/User.getInstance().showFightingMonster(i).getMp() * User.getInstance().showFightingMonster(i).getcMp());
	}
	
	for(i=0; i<User.getInstance().foeTeamGetSize(); i++)
	{
		barel.get(i).setValue((barel.get(i).getMaxValue()/User.getInstance().showFoeTeamMonster(i).getHp()) * User.getInstance().showFoeTeamMonster(i).getcHp());
//		barem.get(i).setValue(barem.get(i).getMaxValue()/User.getInstance().showFoeTeamMonster(i).getMp() * User.getInstance().showFoeTeamMonster(i).getcMp());
	}
	
	batch.draw(region, 0, 0);
	
	batch.draw(tabletex, Gdx.graphics.getWidth()*40/120 , Gdx.graphics.getHeight()*4/120);	// scacchiera del giocatore
	batch.draw(tabletex, Gdx.graphics.getWidth()*40/120 , Gdx.graphics.getHeight()*69/120);	//scacchiera dell'avversario
	
	batch.draw(labeltex, Gdx.graphics.getWidth()*40/120 , Gdx.graphics.getHeight()*55/120);
    
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

public void updateInfo() throws ResourceException, JSONException, IOException{
	JSONArray jarr = new JSONArray(sa.tfInfo(User.getInstance().getId()));
	for (int i=0; i<User.getInstance().fightingGetSize(); i++)
	{
		JSONObject jObj = jarr.getJSONObject(i);
		User.getInstance().showFightingMonster(i).setAd(jObj.getInt("AD"));
		User.getInstance().showFightingMonster(i).setAp(jObj.getInt("AP"));
		User.getInstance().showFightingMonster(i).setDef(jObj.getInt("DEF"));
		User.getInstance().showFightingMonster(i).setmDef(jObj.getInt("MDEF"));
		User.getInstance().showFightingMonster(i).setcHp(jObj.getInt("HP"));
	//	User.getInstance().showFightingMonster(i).setcMp(jObj.getInt("MP"));		//
		User.getInstance().showFightingMonster(i).setPosition(jObj.getInt("POS"));
	}
}

public void updateFoeInfo() throws ResourceException, JSONException, IOException{
	JSONArray jarr = new JSONArray(sa.tfInfo(User.getInstance().getFoe()));
	for (int i=0; i<User.getInstance().foeTeamGetSize(); i++)
	{
		JSONObject jObj = jarr.getJSONObject(i);
		User.getInstance().showFoeTeamMonster(i).setAd(jObj.getInt("AD"));
		User.getInstance().showFoeTeamMonster(i).setAp(jObj.getInt("AP"));
		User.getInstance().showFoeTeamMonster(i).setDef(jObj.getInt("DEF"));
		User.getInstance().showFoeTeamMonster(i).setmDef(jObj.getInt("MDEF"));
		User.getInstance().showFoeTeamMonster(i).setcHp(jObj.getInt("HP"));
	//	User.getInstance().showFoeTeamMonster(i).setcMp(jObj.getInt("MP"));		//
		User.getInstance().showFoeTeamMonster(i).setPosition(jObj.getInt("POS"));
	}
}

public void clearActionQueue(){
	try {
		sa.clearActionQueue(User.getInstance().getId());
	} catch (ResourceException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

public void clearMatch(){
	
	
}

}