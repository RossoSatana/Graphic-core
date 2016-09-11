package State;

import java.awt.event.ActionEvent;
import com.badlogic.gdx.audio.Sound;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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

import Entities.Monster;
import Entities.User;
import ServerConnection.ServerAccess;
import Domain.StateManager;

public class FormationState extends GameState{
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    //private TextButton back, reset, ok;
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
    private ImageButton reset, ok;
    private Label  lb1, lb2, lb3, lb4, lb5, lb6, lb7, lb8;
    
    private ServerAccess sa = new ServerAccess();
    
    private int hp, mp, ad, ap, def, mDef, range;
    private String denomination, name, type, clas;
    
    public class MonsterButton extends ClickListener{
    	private int index;
    	private Monster monster;
    	private String source;
    	
    	public MonsterButton(int index, Monster monster){
    		this.index = index;
    		this.monster = monster;
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
			type = monster.getClas();
			clas = monster.getClas();
			range = monster.getRange();
			
			lb1.setText("Name: " + name);
			lb2.setText("Denomination: " + denomination);
			lb3.setText("Type: " + type);
			lb4.setText("Class: " + clas);
			lb5.setText("Hp: " + hp + "\nMp: " + mp);
			lb6.setText("Ad: " + ad + "\nAp: " + ap);
			lb7.setText("Def: " + def + "\nmDef: " + mDef );
			lb8.setText("Range: " + range);
			
    	}
    }
    
    public class LoadPosition implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {	
				try {
					/*for(i=0;i<User.getInstance().teamGetSize(); i++)
					sa.addToFighting(User.getInstance().showTeamMonster(i).getCodM(), User.getInstance().showTeamMonster(i).getPosition());
					User.getInstance().addToFighting(sa.mFighting(User.getInstance().getId()));*/
					
					String jarr = "[";
					for(i=0;i<User.getInstance().teamGetSize(); i++){
						if(i!=0)
							jarr+=",";
						jarr+= "{\"COD\": \"" + User.getInstance().showTeamMonster(i).getCodM() + "\" , "
								+ "\"POS\" : \"" + User.getInstance().showTeamMonster(i).getPosition() + "\"}";
						
					}
					jarr += "]";
					System.out.println("Test in FIGHT:" + jarr);
					sa.addToFighting(jarr);
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
			getActor().setColor(0, 0, 0, 0);
		}
		
		@Override
		public void drop(Source source, Payload payload, float x, float y, int pointer) {
			User.getInstance().showTeamMonster((int)payload.getObject()).setPosition(index);
			soundDrop.play();
			position.get(index).remove();
			teamList.get((int)payload.getObject()).remove();
			teamList.get((int)payload.getObject()).setPosition(position.get(index).getX()-5,position.get(index).getY()+10);
			teamList.get((int)payload.getObject()).setTouchable(Touchable.disabled);
			User.getInstance().showTeamMonster((int)payload.getObject()).setPosition(index);
			System.out.println("Position: " + User.getInstance().showTeamMonster((int)payload.getObject()).getPosition());
			System.out.println("Position: " + User.getInstance().showTeamMonster((int)payload.getObject()).getName());
			stage.addActor(teamList.get((int)payload.getObject()));
			
			lb1.setText("Name: ");
			lb2.setText("Denomination: ");
			lb3.setText("Type: ");
			lb4.setText("Class: ");
			lb5.setText("Hp: "+ "\nMp: ");
			lb6.setText("Ad: "+ "\nAp: ");
			lb7.setText("Def: " + "\nmDef: " );
			lb8.setText("Range: " );
			
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
			payload.setDragActor(new Image(skin, User.getInstance().showTeamMonster(index).getDenomination()));
			
			/*Label validLabel = new Label("SI!", skin);
			validLabel.setColor(0, 1, 0, 1);
			payload.setValidDragActor(validLabel);

			Label invalidLabel = new Label("NO!", skin);
			invalidLabel.setColor(1, 0, 0, 1);
			payload.setInvalidDragActor(invalidLabel);*/
			

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
			teamList.get(i).addListener(new MonsterButton(i,User.getInstance().showTeamMonster(i)));
			teamTable.getCell(teamList.get(i)).row();
		}
		
		teamScroll = new ScrollPane(teamTable, skin, "default");
		teamScroll.setSize(220, 420);
		teamScroll.setPosition(15, 110);
		stage.addActor(teamScroll);
		
		position = new ArrayList<Image>();
		
		Texture prova = new Texture(Gdx.files.internal("img/green.png"));
		//Image valid	 = new Image(prova);
		
		for(i = 0; i < 9; i++){
			position.add(new Image(prova));
		}
		
		for(k = 0; k < 3; k++)
			for( j=0; j<3;j++){
				position.get(j+k*3).setBounds(243+j*116, 386-k*116, 100, 100);
				position.get(j+k*3).setX(243+j*116);
				position.get(j+k*3).setY(386-k*116);
				stage.addActor(position.get(j+k*3));
				position.get(j+k*3).setColor(0, 0, 0, 0);
			}
		
		DragAndDrop dragAndDrop = new DragAndDrop();
		dragAndDrop.setDragActorPosition(-53, 37);	
		
		for(i=0; i<User.getInstance().teamGetSize(); i++)
		dragAndDrop.addSource(new MonsterSource(teamList.get(i), i));
		
		for(i=0;i<9;i++)
		dragAndDrop.addTarget(new MonsterTarget(position.get(i), i));
		
		// inizializzo lo spriteBatch la texture e la region
		batch = new SpriteBatch();       
		// serve a disegnare il background
		texture = new Texture(Gdx.files.internal("img/Formation.png"));											
		
		 // creazione pulsante back ed aggancio di un listener
		reset = new ImageButton( skin, "reset");
		reset.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {				
				soundButton.play();				
				for(i=0; i<User.getInstance().teamGetSize(); i++){	
					User.getInstance().showTeamMonster(i).setPosition(-1);
					teamList.add(new Image(skin, User.getInstance().showTeamMonster(i).getDenomination()));
					teamTable.add(teamList.get(i));
					teamTable.getCell(teamList.get(i)).row();
					teamList.get(i).setTouchable(Touchable.enabled);
				}
				for(i = 0; i < 9; i++){
					position.add(new Image(skin, "twitter"));
						
					for(k = 0; k < 3; k++)
						for( j=0; j<3;j++){
							position.get(j+k*3).setBounds(243+j*116, 386-k*116, 100, 100);
							position.get(j+k*3).setX(243+j*116);
							position.get(j+k*3).setY(386-k*116);
							stage.addActor(position.get(j+k*3));
						}
				}
				
			}
		});
				
		reset.setSize(100, 50);
		reset.setPosition(Gdx.graphics.getWidth()/120 *16  , Gdx.graphics.getHeight()*2/30 - reset.getHeight()/2);
	
		ok = new ImageButton( skin, "ready");
		ok.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {				
				for(i=0;i<User.getInstance().teamGetSize(); i++)
					soundButton.play();
					reset.setVisible(false);
				
					//System.out.println("Monster: " + User.getInstance().showTeamMonster(i).getName() + " Position: " + User.getInstance().showTeamMonster(i).getPosition());
			}
		});
				
		ok.setSize(100, 50);
		ok.setPosition(Gdx.graphics.getWidth()/120*16  , Gdx.graphics.getHeight()*4/30 - ok.getHeight()/2);
	
		// inserimento degli attori nello stage
		
		
		lb1 = new Label("Name: " + name, skin, "lucida.12");
		lb1.setPosition(270, 115);
		lb2 = new Label("Denomination: " + denomination , skin, "lucida.12");
		lb2.setPosition(400, 115);
		lb3 = new Label("Type: " + type , skin, "lucida.12");
		lb3.setPosition(270, 100);
		lb4 = new Label("Class: " + clas, skin, "lucida.12");
	    lb4.setPosition(400, 100);
	   
	    lb5 = new Label("Hp: " + hp + "\nMp: " + mp, skin, "lucida.12");
		lb5.setPosition(270, 65);
		lb6 = new Label("Ad: " + ad + "\nAp: " + ap , skin, "lucida.12");
		lb6.setPosition(340, 65);
		lb7 = new Label("Def: " + def + "\nmDef: " + mDef , skin, "lucida.12");
		lb7.setPosition(410, 65);
		lb8 = new Label("Range: " + range, skin, "lucida.12");
	    lb8.setPosition(480, 80);
	    
		stage.addActor(lb1);
		stage.addActor(lb2);
		stage.addActor(lb3);
		stage.addActor(lb4);
		stage.addActor(lb5);
		stage.addActor(lb6);
		stage.addActor(lb7);
		stage.addActor(lb8);
		
		// inserimento degli attori nello stage
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
		batch.draw(texture, 0, 0);
		
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
