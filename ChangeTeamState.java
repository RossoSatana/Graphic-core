package State;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import Entities.Monster;
import Entities.User;
import Domain.StateManager;

public class ChangeTeamState extends GameState{
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton in, out, back;
    private Label monsterOwned, monsterTeam, stat1, stat2;
    private Texture texture, texture2, monsterTexture, statTexture;
    private TextureRegion region;
    private ScrollPane ownedScroll, teamScroll;
    private Pixmap pm;
    private Table ownedTable, teamTable;
    private ArrayList<TextButton> ownedList;//, teamList;
    private int hp = 0, mp = 0, ad = 0, ap = 0 ,def = 0, mDef = 0, i, j;
    private Integer ownedIndex = null, teamIndex = null;
    private ArrayList<ImageButton> /*ownedList,*/ teamList;
    
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
    		hp = monster.getHp();
			ad = monster.getAd();
			ap = monster.getAp();
			def = monster.getDef();
			mDef = monster.getmDef();
			
			if (source == "owned")
			ownedIndex = Integer.valueOf(index);
			if (source == "team")
			teamIndex = Integer.valueOf(index);
			dispose();
			init();
    	}
    	
    }
    
	public ChangeTeamState(StateManager gsm) {
		super(gsm);
	}

	@Override
	public void init() {
		// inizializzo Stage e faccio in modo che possa catturare IMPUT
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		// inizializzo atlas dandogli file atlas.pack che si riferisce all'immagine atlas.png
		atlas = new TextureAtlas("ui/atlas.pack");
		
		// inizializzo skin dandogli un file json contenente infomazioni
		// riguardanti font, labelStyle, ScrollPaneStyle, colori
		skin = new Skin(Gdx.files.internal("ui/MenuSkin.json"), atlas);
			
		// inizializzo lo spriteBatch la texture e la region
		batch = new SpriteBatch();       																				// serve a disegnare il background
        texture = new Texture(Gdx.files.internal("img/neon.png"));				// contiene l'immagine
        region = new TextureRegion(texture, 0, 0, 600, 600);											// ritaglia un pezzo della texture
        
        pm = new Pixmap(300, 250, Format.RGBA8888);
        pm.setColor(1, 1, 1, 0.5f);
        pm.fillRectangle(0, 1, 300, 300);
        monsterTexture = new Texture(pm);
        statTexture = new Texture(pm);
       
   
        
     // creazione pulsante IN ed aggancio di un listener
		in = new TextButton("  IN ", skin);
		in.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(ownedIndex != null){
				User.getInstance().addToTeam(User.getInstance().showOwnedMonster(ownedIndex));
				User.getInstance().removeFromOwned(ownedIndex);
				ownedIndex = null;
				hp = 0; mp = 0; ad = 0; ap = 0; def = 0; mDef = 0;
				dispose();
				init();
				}
			}
		});
		
		// creazione pulsante OUT ed aggancio di un listener
		out = new TextButton(" OUT ", skin);
		out.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(teamIndex != null){
				User.getInstance().addToOwned(User.getInstance().showTeamMonster(teamIndex));
				User.getInstance().removeFromTeam(teamIndex);
				teamIndex = null;
				hp = 0; mp = 0; ad = 0; ap = 0; def = 0; mDef = 0;
				dispose();
				init();
				}
			}
		});
		
		
		 // creazione pulsante back ed aggancio di un listener
		back = new TextButton("  BACK ", skin);
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gsm.setState(1);		// esce dall'app
			}
		});
		
		// creazione arrayList di bottoni
		//teamList = new ArrayList<TextButton>();
		ownedList = new ArrayList<TextButton>();
		String button = "button";
		
		teamList = new ArrayList<ImageButton>();
		//ownedList = new ArrayList<ImageButton>();
		
		
		// inserimento bottoni nell'arrayList
		/*for(i=0; i<User.getInstance().teamGetSize(); i++){
			button = button + i;
			teamList.add(new TextButton(button, skin));	
			button = "button";
		}*/
		
		for(i=0; i<User.getInstance().teamGetSize(); i++){	
			teamList.add(new ImageButton(skin));	
		}
		
		for(i=0; i<User.getInstance().teamGetSize(); i++){
			teamList.get(i).addListener(new MonsterButton(i, User.getInstance().showTeamMonster(i), "team"));
		}
		
		for(j=0; j<User.getInstance().OwnedGetSize(); j++){
			button = button + j;
			ownedList.add(new TextButton(button, skin));	
			button = "button";
		}
		
		for(j=0; j<User.getInstance().OwnedGetSize(); j++){
			ownedList.get(j).addListener(new MonsterButton(j, User.getInstance().showOwnedMonster(j), "owned"));	
		}
	
		// creazione tavoli
		teamTable = new Table(skin);
		ownedTable = new Table(skin);
		
		// inserimento bottoni da arrayList al tavolo
		
		for(i =0; i <User.getInstance().teamGetSize(); i++){
			teamTable.add(teamList.get(i));
			teamTable.getCell(teamList.get(i)).spaceBottom(1).uniform().row();
			
		}
		
		for(j =0; j <User.getInstance().OwnedGetSize(); j++){
			ownedTable.add(ownedList.get(j));
			ownedTable.getCell(ownedList.get(j)).spaceBottom(1).row();
			
		}
		
		// creazione scrollPane con tavoli
		teamScroll = new ScrollPane(teamTable);
		teamScroll.setScrollingDisabled(true, false);

		ownedScroll = new ScrollPane(ownedTable);
		ownedScroll.setScrollingDisabled(true, false);

		// settaggio dimensioni e posizione scrollPane
		ownedScroll.setSize(100, 200);
		teamScroll.setSize(100, 200);
		ownedScroll.setPosition(10, 200);
		teamScroll.setPosition(Gdx.graphics.getWidth() -10 - teamScroll.getWidth() , 200);
		
		// setaggio dimensioni e posizione pulsanti
		in.setSize(in.getWidth(), 50);
		in.setPosition(10, ownedScroll.getHeight() - 70);
		out.setSize(out.getWidth(), 50);
		out.setPosition(Gdx.graphics.getWidth() - out.getWidth() -10 , ownedScroll.getHeight() - 70);
		back.setSize(back.getWidth(), 50);
		back.setPosition(Gdx.graphics.getWidth() - back.getWidth() -10, 10);
		

		// inizializzo la Label 
		monsterOwned = new Label (" DEPOSITO ", skin);
		monsterTeam = new Label (" TEAM ", skin);
		stat1 = new Label ("HP: " + hp +   "\n\nAD: " + ad + "\n\nDEF: " + def  , skin);
		stat2 = new Label ("MP: " + mp +   "\n\nAP: " + ap +   "\n\nmDEF: " + mDef  , skin);
	
		// settaggio posizione e dimensione label	
		monsterOwned.setFontScale( 0.8f);
		monsterTeam.setFontScale( 0.8f);
		stat1.setPosition(ownedScroll.getWidth() + 50 ,Gdx.graphics.getHeight()/2 - 200);
		stat2.setPosition(ownedScroll.getWidth() + 200 ,Gdx.graphics.getHeight()/2 - 200);
		monsterOwned.setPosition(10, ownedScroll.getHeight() + 250 );
		monsterTeam.setPosition(Gdx.graphics.getWidth() - monsterTeam.getWidth() - 10 , teamScroll.getHeight() + 250);
		
		// inserimento degli attori nello stage
		stage.addActor(in);
		stage.addActor(out);
		stage.addActor(back);
		stage.addActor(monsterOwned);
		stage.addActor(monsterTeam);
		stage.addActor(ownedScroll);
		stage.addActor(teamScroll);
		stage.addActor(stat1);
		stage.addActor(stat2);
		
	}
        
      

	@Override
	public void update(float dt) {
	
	}

	@Override
	public void draw() {
		// inizia la sessione dello SpriteBatch dove disegna il background
		batch.begin();	
		batch.draw(region, 0, 0);
		batch.draw(statTexture, ownedScroll.getWidth() + 50 ,Gdx.graphics.getHeight()/2 - 270);
		batch.draw(monsterTexture,  ownedScroll.getWidth() + 50 ,Gdx.graphics.getHeight()/2 + 20);
		//batch.draw(texture2,  ownedScroll.getWidth() + 50 ,Gdx.graphics.getHeight()/2 + 20);
		//batch.draw(texture2,  ownedScroll.getWidth() + 50 ,Gdx.graphics.getHeight()/2 + 20,  106, 75);
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
		pm.dispose();
		texture.dispose();
		monsterTexture.dispose();
		statTexture.dispose();
	}

	@Override
	public void resize(int width, int height) {
		
	
		
	}
}
