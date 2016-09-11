package State;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import Entities.Monster;
import Entities.User;
import Domain.StateManager;

public class ChangeTeamState extends GameState{
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    
    private ImageButton in, out, back;
    private Label  monsterTeam, advice, lb1,lb2,lb3,lb4,lb5, lb6,lb7,lb8;
    private Texture texture;
    private ScrollPane ownedScroll, teamScroll;
    private Table ownedTable, teamTable;
    private int hp = 0, mp = 0, ad = 0, ap = 0 ,def = 0, mDef = 0, range, i, j;
    private Integer ownedIndex = null, teamIndex = null;
    private ArrayList<ImageButton> ownedList, teamList;
    private Sound soundButton, soundDrag, soundDrop;
    private String name, denomination, clas, type;
    
    
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
			name = monster.getName();
			denomination = monster.getDenomination();
			range = monster.getRange();
			clas = monster.getClas();
			type = monster.getType();
			
			if (source == "owned")
			ownedIndex = Integer.valueOf(index);
			if (source == "team")
			teamIndex = Integer.valueOf(index);
			
			
			lb1.setText(name);
			lb2.setText(denomination);
			lb3.setText("Type: " + type);
			lb4.setText("Class: " + clas);
			lb5.setText("Hp: " + hp + "\n\nMp: " + mp);
			lb6.setText("Ad: " + ad + "\n\nAp: " + ap);
			lb7.setText("Def: " + def + "\n\nmDef: " + mDef );
			lb8.setText("Range: " + range);
			
			advice.setVisible(false);
			lb1.setVisible(true);
			lb2.setVisible(true);
			lb3.setVisible(true);
			lb4.setVisible(true);
			lb5.setVisible(true);
			lb6.setVisible(true);
			lb7.setVisible(true);
			lb8.setVisible(true);
			
			
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
		
		soundDrag = Gdx.audio.newSound(Gdx.files.internal("music/drag.wav"));
		soundDrop = Gdx.audio.newSound(Gdx.files.internal("music/drop.wav"));
		soundButton = Gdx.audio.newSound(Gdx.files.internal("music/buttonEnabled.mp3"));
		
		// inizializzo atlas dandogli file atlas.pack che si riferisce all'immagine atlas.png
		atlas = new TextureAtlas("ui/uiskin.atlas");
		
		// inizializzo skin dandogli un file json contenente infomazioni
		// riguardanti font, labelStyle, ScrollPaneStyle, colori
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);
			
		// inizializzo lo spriteBatch la texture e la region
		batch = new SpriteBatch();       																				// serve a disegnare il background
        texture = new Texture(Gdx.files.internal("img/team.png"));				// contiene l'immagine
        
     // creazione pulsante IN ed aggancio di un listener
		in = new ImageButton(skin, "in");
		in.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(ownedIndex != null && User.getInstance().teamGetSize() < 9){
				soundDrag.play();
				User.getInstance().addToTeam(User.getInstance().showOwnedMonster(ownedIndex));
				User.getInstance().removeFromOwned(ownedIndex);
				ownedIndex = null;
				hp = 0; mp = 0; ad = 0; ap = 0; def = 0; mDef = 0;
				name = ""; denomination = "";
				dispose();
				init();
				}
			}
		});
		
		
		
		// creazione pulsante OUT ed aggancio di un listener
		out = new ImageButton( skin, "out");
		out.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(teamIndex != null){
				soundDrop.play();
				User.getInstance().addToOwned(User.getInstance().showTeamMonster(teamIndex));
				User.getInstance().removeFromTeam(teamIndex);
				teamIndex = null;
				hp = 0; mp = 0; ad = 0; ap = 0; def = 0; mDef = 0;
				name = ""; denomination = "";
				dispose();
				init();
				}
			}
		});
		
		
		 // creazione pulsante back ed aggancio di un listener
		back = new ImageButton( skin, "back");
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				soundButton.play();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gsm.setState(1);		// esce dall'app
			}
		});
		
		// creazione arrayList di bottoni
		//teamList = new ArrayList<TextButton>();
		ownedList = new ArrayList<ImageButton>();	
		teamList = new ArrayList<ImageButton>();
		
		
		
		for(i=0; i<User.getInstance().teamGetSize(); i++){	
			teamList.add(new ImageButton(skin, User.getInstance().showTeamMonster(i).getDenomination()));
			teamList.get(i).addListener(new MonsterButton(i, User.getInstance().showTeamMonster(i), "team"));
		}
		
		
		for(j=0; j<User.getInstance().OwnedGetSize(); j++){
			ownedList.add(new ImageButton(skin, User.getInstance().showOwnedMonster(j).getDenomination()));	
			ownedList.get(j).addListener(new MonsterButton(j, User.getInstance().showOwnedMonster(j), "owned"));
		}
	
		// creazione tavoli
		teamTable = new Table(skin);
		ownedTable = new Table(skin);
		
		// inserimento bottoni da arrayList al tavolo
		
		for(i =0; i <User.getInstance().teamGetSize(); i++){
			teamTable.add(teamList.get(i)).size(106, 75);
			//teamTable.getCell(teamList.get(i)).spaceBottom(1).uniform().row();
			teamTable.getCell(teamList.get(i)).spaceLeft(1).uniform();
		}
		
		for(j =0; j <User.getInstance().OwnedGetSize(); j++){
			ownedTable.add(ownedList.get(j)).size(106, 75);
			//ownedTable.getCell(ownedList.get(j)).spaceBottom(1).row();
			ownedTable.getCell(ownedList.get(j)).spaceLeft(1).uniform();
		}
		
		// creazione scrollPane con tavoli
		teamScroll = new ScrollPane(teamTable, skin, "default");
		teamScroll.setScrollingDisabled(false, true);

		ownedScroll = new ScrollPane(ownedTable, skin, "default");
		ownedScroll.setScrollingDisabled(false, true);

		// settaggio dimensioni e posizione scrollPane
		ownedScroll.setSize(450, 200);
		teamScroll.setSize(450, 200);
		ownedScroll.setPosition(60, 325);
		teamScroll.setPosition(60, 25);
		
		// setaggio dimensioni e posizione pulsanti
		in.setSize(70, 40);
		in.setPosition(10,  260);
		out.setSize(70, 40);
		out.setPosition(Gdx.graphics.getWidth() - 10 - out.getWidth() , 260);
		back.setSize(105, 40);
		back.setPosition(5 , Gdx.graphics.getHeight() - back.getHeight() - 5);

		// inizializzo la Label 
		monsterTeam = new Label (User.getInstance().teamGetSize()  + "/9", skin);
	
		
		lb2 = new Label(denomination , skin, "lucida.medium");
		lb2.setPosition(140, 315);
		lb1 = new Label( name, skin, "lucida.small");
		lb1.setPosition(140, 290);
		lb3 = new Label("Type: " + type , skin, "lucida.12");
		lb3.setPosition(140, 260);
		lb4 = new Label("Class: " + clas, skin, "lucida.12");
	    lb4.setPosition(140, 240);
	    lb8 = new Label("Range: " + range, skin, "lucida.12");
	    lb8.setPosition(140, 220);  
	    lb5 = new Label("Hp: " + hp + "\n\nMp: " + mp, skin, "lucida.12");
		lb5.setPosition(260, 230);
		lb6 = new Label("Ad: " + ad + "\n\nAp: " + ap , skin, "lucida.12");
		lb6.setPosition(330, 230);
		lb7 = new Label("Def: " + def + "\n\nmDef: " + mDef , skin, "lucida.12");
		lb7.setPosition(400, 230);
		
		lb1.setVisible(false);
		lb2.setVisible(false);
		lb3.setVisible(false);
		lb4.setVisible(false);
		lb5.setVisible(false);
		lb6.setVisible(false);
		lb7.setVisible(false);
		lb8.setVisible(false);
		
	    
	    
	    
		stage.addActor(lb1);
		stage.addActor(lb2);
		stage.addActor(lb3);
		stage.addActor(lb4);
		stage.addActor(lb5);
		stage.addActor(lb6);
		stage.addActor(lb7);
		stage.addActor(lb8);
		
		// settaggio posizione e dimensione label	
	/*	monsterOwned.setFontScale( 0.8f);
		monsterTeam.setFontScale( 0.8f);*/

		monsterTeam.setPosition(Gdx.graphics.getWidth()/2 + 120 , 175);

		advice = new Label("\t\t\tClicca sul'eroe che desideri per vedere\nle sue statistiche\nPotrai portare in battaglia solo i mostri\nall'interno del tuo team\nSchegli i migliori Eroi per sfidare i tuoi avversari", skin, "lucida.12");
		advice.setPosition(140,230);
		advice.setVisible(true);
		stage.addActor(advice);
		
		
		// inserimento degli attori nello stage
		stage.addActor(in);
		stage.addActor(out);
		stage.addActor(back);
		stage.addActor(monsterTeam);
		stage.addActor(ownedScroll);
		stage.addActor(teamScroll);
	}
        
      

	@Override
	public void update(float dt) {
	
	}

	@Override
	public void draw() {
		// inizia la sessione dello SpriteBatch dove disegna il background
		batch.begin();	
		batch.draw(texture, 0, 0);
	
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
		soundDrag.dispose();
		soundDrop.dispose();
		soundButton.dispose();
	}

	@Override
	public void resize(int width, int height) {
		
	
		
	}
}
