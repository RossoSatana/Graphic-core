package GameState;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import Entities.Monster;
import Manager.GameStateManager;

public class FormationState extends GameState {
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton back;
    private Label  monsterTeam;
    private Texture texture, texture1;
    private TextureRegion region;
    private ScrollPane  teamScroll;
    private Table ownedTable, teamTable;
    private ArrayList<TextButton> ownedList, teamList;
    private ArrayList<Monster> ownedMonster, teamMonster;
    private ImageButton pro;
 
   
    
	public FormationState(GameStateManager gsm) {
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
        texture = new Texture(Gdx.files.internal("img/Background (1).png"));				// contiene l'immagine
        region = new TextureRegion(texture, 0, 0, 600, 600);											// ritaglia un pezzo della texture      
		

        
		 // creazione pulsante back ed aggancio di un listener
		back = new TextButton("  BACK ", skin);
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gsm.setState(0);		// esce dall'app
			}
		});
		
		// creazione arrayList di bottoni
		teamList = new ArrayList<TextButton>();
		ownedList = new ArrayList<TextButton>();
		String button = "button";
		
		// inserimento bottoni nell'arrayList
		int i, N=10;
		for(i=0; i<N; i++){
			button = button + i;
			teamList.add(new TextButton(button, skin));	
			button = "button";
		}
		
		int M=9;
		for(i=0; i<M; i++){
			button = button + i;
			ownedList.add(new TextButton(button, skin));	
			button = "button";
		}
	
		// creazione tavoli
		teamTable = new Table(skin);
		ownedTable = new Table(skin);
		
		// inserimento bottoni da arrayList al tavolo
		for(i =0; i <N; i++){
			teamTable.add(teamList.get(i));
			teamTable.getCell(teamList.get(i)).spaceBottom(1).uniform().row();
			
			
		}
		
		for(i =0; i <M; i=i+3){
			ownedTable.add(ownedList.get(i));
			ownedTable.getCell(ownedList.get(i)).spaceRight(10).spaceBottom(10);
			ownedTable.add(ownedList.get(i+1));
			ownedTable.getCell(ownedList.get(i+1)).spaceRight(10).spaceBottom(10);
			ownedTable.add(ownedList.get(i+2));
			ownedTable.getCell(ownedList.get(i+2)).spaceRight(10).spaceBottom(10);
			ownedTable.row();
			
		}
		
		// creazione scrollPane con tavoli
		teamScroll = new ScrollPane(teamTable);
		teamScroll.setScrollingDisabled(true, false);

		// settaggio dimensioni e posizione scrollPane
		
		teamScroll.setSize(200, 300);
		teamScroll.setPosition(Gdx.graphics.getWidth() -10 - teamScroll.getWidth() , 200);
		
		// setaggio dimensioni e posizione pulsanti
		back.setSize(back.getWidth(), 50);
		back.setPosition(Gdx.graphics.getWidth() - back.getWidth() -10, 10);
		

		// inizializzo la Label 
		monsterTeam = new Label (" TEAM ", skin);
		// settaggio posizione e dimensione label	
		monsterTeam.setFontScale( 0.8f);
		monsterTeam.setPosition(Gdx.graphics.getWidth() - monsterTeam.getWidth() - 10 , teamScroll.getHeight() + 250);
		ownedTable.setSize(300, 300);
		ownedTable.setPosition(100, 100);
		// inserimento degli attori nello stage
		
		stage.addActor(ownedTable);
		
		stage.addActor(back);
		stage.addActor(monsterTeam);
		stage.addActor(teamScroll);
		ownedTable.debug();
	}
        
      

	@Override
	public void update(float dt) {
	
	}

	@Override
	public void draw() {
		// inizia la sessione dello SpriteBatch dove disegna il background
		batch.begin();	
		batch.draw(region, 0, 0);
		//batch.draw(texture2,  50 ,Gdx.graphics.getHeight()/2 + 20);
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
