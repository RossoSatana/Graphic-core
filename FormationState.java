package State;

import java.awt.event.ActionEvent;
import com.badlogic.gdx.audio.Sound;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Timer;

import org.json.JSONException;
import org.restlet.resource.ResourceException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import com.badlogic.gdx.graphics.Color;

import Entities.User;
import ServerConnection.ServerAccess;
import Domain.StateManager;

public class FormationState extends GameState{
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton back, reset, ok;
    private Texture texture, mob;
    private TextureRegion region;
    private Table teamTable;
    private ScrollPane teamScroll;
    private ArrayList<Image> teamList, position;
    private int i, j ,k, sec;
    private Timer t, t1;
    private ProgressBar pb, pb1;
    private boolean matchStarted = false;
    private Dialog dial;
    private Sound soundDrag, soundDrop, soundButton;
    
    private ServerAccess sa = new ServerAccess();
    
    public class LoadPosition implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {	
				try {
					for(i=0;i<User.getInstance().teamGetSize(); i++)
					sa.addToFighting(User.getInstance().showTeamMonster(i).getCodM(), User.getInstance().showTeamMonster(i).getPosition());
					User.getInstance().addToFighting(sa.mFighting(User.getInstance().getId()));
					
					dial = new Dialog("Starting the match", skin, "dialog");
					Label l = new Label("Feeding the monster", skin);
					
					dial.text("Feeding the monster");	
					dial.setResizable(true);
				//	pb1.setPosition(10, 10);
					pb1.setAnimateDuration(5);
					pb1.setValue(500);
					dial.addActor(pb1);
					dial.setSize(pb1.getWidth() + 20, pb1.getHeight() + l.getHeight() + 80);
					dial.setPosition(Gdx.graphics.getWidth()*1/2 - dial.getWidth()/2, Gdx.graphics.getHeight()*1/2 - dial.getHeight()/2 );

					stage.addActor(dial);	
					
					t.stop();
					t1.start();
					
				} catch (ResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
    }
    
    public class LoadGame implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
				try {
					User.getInstance().addToFoeTeam(sa.mFighting(User.getInstance().getFoe()));
				} catch (ResourceException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				matchStarted = true;
				t1.stop();	
		}
    }
    
    public class MonsterTarget extends Target{
    	int index;
    	
		public MonsterTarget(Actor actor, int index) {
			super(actor);
			this.index = index;
		}

		@Override
		public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
			getActor().setColor(Color.GREEN);
			return true;
		}

		public void reset (Source source, Payload payload) {
			getActor().setColor(Color.WHITE);
		}
		
		@Override
		public void drop(Source source, Payload payload, float x, float y, int pointer) {
			User.getInstance().showTeamMonster((int)payload.getObject()).setPosition(index);
			soundDrop.play();
			position.get(index).remove();
			teamList.get((int)payload.getObject()).remove();
			teamList.get((int)payload.getObject()).setPosition(position.get(index).getX(),position.get(index).getY());
			teamList.get((int)payload.getObject()).setTouchable(Touchable.disabled);
			User.getInstance().showTeamMonster((int)payload.getObject()).setPosition(index);
			System.out.println("Position: " + User.getInstance().showTeamMonster((int)payload.getObject()).getPosition());
			System.out.println("Position: " + User.getInstance().showTeamMonster((int)payload.getObject()).getName());
			stage.addActor(teamList.get((int)payload.getObject()));
			draw();
		}
    	
    }
    
    public class MonsterSource extends Source{
    	int index;
    	
		public MonsterSource(Actor actor, int index) {
			super(actor);		
			this.index = index;
		}

		@Override
		public Payload dragStart(InputEvent event, float x, float y, int pointer) {
			soundDrag.play();
			Payload payload = new Payload();
			payload.setObject(index);			
			payload.setDragActor(new Image(skin, "google"));
			
			Label validLabel = new Label("SI!", skin);
			validLabel.setColor(0, 1, 0, 1);
			payload.setValidDragActor(validLabel);

			Label invalidLabel = new Label("NO!", skin);
			invalidLabel.setColor(1, 0, 0, 1);
			payload.setInvalidDragActor(invalidLabel);
			

			return payload;
		}
    	
		public int getIndex(){
			return index;
		}
		
    }
	public FormationState(StateManager gsm) {
		super(gsm);
		
		try {
			sec = sa.loadingsec(User.getInstance().getId()) - 5;
			
			if (sec>0){
				LoadPosition lp = new LoadPosition();
				t = new Timer(sec*1000, lp);
				pb = new ProgressBar(0, sec*100, 1, false, skin);
				pb.setPosition(10, 10);
				pb.setAnimateDuration(sec);
				pb.setValue(sec*100);
				stage.addActor(pb);
				
				LoadGame lg = new LoadGame();
				t1 = new Timer(5000, lg);
				pb1 = new ProgressBar(0, 500, 1, false, skin);
						
				t.start();	
			}
			else
			{
				for(i=0;i<User.getInstance().teamGetSize(); i++)
				User.getInstance().addToFighting(sa.mFighting(User.getInstance().getId()));	
				User.getInstance().addToFoeTeam(sa.mFighting(User.getInstance().getFoe()));
				
				LoadGame lg = new LoadGame();
				t1 = new Timer(5000, lg);
				pb1 = new ProgressBar(0, 500, 1, false, skin);
				dial = new Dialog("Starting the match", skin, "dialog");
				Label l = new Label("Feeding the monster", skin);
				dial.text("Feeding the monster");	
				dial.setResizable(true);
				pb1.setAnimateDuration(5);
				pb1.setValue(500);
				dial.addActor(pb1);
				dial.setSize(pb1.getWidth() + 20, pb1.getHeight() + l.getHeight() + 80);
				dial.setPosition(Gdx.graphics.getWidth()*1/2 - dial.getWidth()/2, Gdx.graphics.getHeight()*1/2 - dial.getHeight()/2 );
				stage.addActor(dial);	
				
				t1.start();
			}
		} catch (ResourceException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		teamTable = new Table(skin);
		teamList = new ArrayList<Image>();	
		
		for(i=0; i<User.getInstance().teamGetSize(); i++){	
			teamList.add(new Image(skin, User.getInstance().showTeamMonster(i).getDenomination()));
			teamTable.add(teamList.get(i));
			teamTable.getCell(teamList.get(i)).row();
		}
		
		teamScroll = new ScrollPane(teamTable);
		teamScroll.setSize(106, 500);
		teamScroll.setPosition(0, 50);
		stage.addActor(teamScroll);
		
		position = new ArrayList<Image>();
		
		for(i = 0; i < 9; i++){
			position.add(new Image(skin, "google"));
		}
		
		for(k = 0; k < 3; k++)
			for( j=0; j<3;j++){
			position.get(j+k*3).setBounds(200+j*120, 380-k*90, 106, 75);
			position.get(j+k*3).setX(200+j*120);
			position.get(j+k*3).setY(380-k*90);
			stage.addActor(position.get(j+k*3));
			}
		
		DragAndDrop dragAndDrop = new DragAndDrop();
		dragAndDrop.setDragActorPosition(-53, 37);	
		
		for(i=0; i<User.getInstance().teamGetSize(); i++)
		dragAndDrop.addSource(new MonsterSource(teamList.get(i), i));
		
		for(i=0;i<9;i++)
		dragAndDrop.addTarget(new MonsterTarget(position.get(i), i));
		
		// inizializzo lo spriteBatch la texture e la region
		batch = new SpriteBatch();       																				// serve a disegnare il background
        texture = new Texture(Gdx.files.internal("img/neon.png"));				// contiene l'immagine
        region = new TextureRegion(texture, 0, 0, 600, 600);											// ritaglia un pezzo della texture
        
        
		 // creazione pulsante back ed aggancio di un listener
		back = new TextButton("  BACK ", skin);
		back.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				soundButton.play();
				t.stop();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gsm.setState(1);		// esce dall'app
			}
		});
		
		back.setSize(back.getWidth(), 50);
		back.setPosition(Gdx.graphics.getWidth() - back.getWidth() -10, 10);
		
		
		 // creazione pulsante back ed aggancio di un listener
		reset = new TextButton(" RESET ", skin);
		reset.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {				
				soundButton.play();				
				for(i=0; i<User.getInstance().teamGetSize(); i++){	
					teamList.add(new Image(skin, User.getInstance().showTeamMonster(i).getDenomination()));
					teamTable.add(teamList.get(i));
					teamTable.getCell(teamList.get(i)).row();
					teamList.get(i).setTouchable(Touchable.enabled);
				}
				for(i = 0; i < 9; i++){
					position.add(new Image(skin, "twitter"));
						
					for(k = 0; k < 3; k++)
						for( j=0; j<3;j++){
						position.get(j+k*3).setBounds(200+j*120, 380-k*90, 106, 75);
						position.get(j+k*3).setX(200+j*120);
						position.get(j+k*3).setY(380-k*90);
						stage.addActor(position.get(j+k*3));
						}
				}
				
			}
		});
				
		reset.setSize(reset.getWidth(), 50);
		reset.setPosition(Gdx.graphics.getWidth() - back.getWidth() -20 - reset.getWidth() , 10);
		
		ok = new TextButton(" OK ", skin);
		ok.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {				
				for(i=0;i<User.getInstance().teamGetSize(); i++)
					soundButton.play();
					reset.setVisible(false);
					//System.out.println("Monster: " + User.getInstance().showTeamMonster(i).getName() + " Position: " + User.getInstance().showTeamMonster(i).getPosition());
			}
		});
				
		ok.setSize(ok.getWidth(), 50);
		ok.setPosition(Gdx.graphics.getWidth() - back.getWidth() -30 - reset.getWidth() - ok.getWidth() , 10);
		
	
		// inserimento degli attori nello stage
		
		stage.addActor(back);
		stage.addActor(reset);
		stage.addActor(ok);
	}
        
	@Override
	public void update(float dt) {
	
	}

	@Override
	public void draw() {
		// inizia la sessione dello SpriteBatch dove disegna il background
		batch.begin();	
		batch.draw(region, 0, 0);
		
		if (matchStarted)
		{
			gsm.setState(2);
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
		texture.dispose();
		soundButton.dispose();
		soundDrag.dispose();
		soundDrop.dispose();
	}

	@Override
	public void resize(int width, int height) {
				
	}
}
