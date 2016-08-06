package State;


import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import org.w3c.dom.events.MouseEvent;

import com.badlogic.gdx.Gdx;
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

import Domain.StateManager;

public class Match extends GameState {
	
	private SpriteBatch batch;
    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private TextButton play, exit;
    private Label head;
    private Pixmap pixmap;
    private Texture texture, tabletex, bordertex;
    private TextureRegion region; 
    private ProgressBar bar1l, bar1m, bar2l, bar2m, bar3l, bar3m, bar4l, bar4m, bar5l, bar5m, bar6l, bar6m, bar7l, bar7m, bar8l, bar8m, bar9l, bar9m;
    private ProgressBar bare1l, bare1m, bare2l, bare2m, bare3l, bare3m, bare4l, bare4m, bare5l, bare5m, bare6l, bare6m, bare7l, bare7m, bare8l, bare8m, bare9l, bare9m;
    
    private ArrayList <Integer> posizione;
    
    private int health=75, mana=3;
    
	public Match(StateManager gsm) {
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
		
		// Tavolo da gioco
		pixmap = new Pixmap(380, 246, Format.RGBA8888 );
		pixmap.setColor(128,128,128, 0.7f);
		pixmap.fillRectangle(0, 1, 380, 246);
		tabletex = new Texture(pixmap);
		
		pixmap = new Pixmap(60, 50, Format.RGBA8888 );
		
	//	pixmap.drawRectangle(x, y, width, height) (0, 1, 360, 225);
		pixmap = new Pixmap(Gdx.files.internal("img/sunflower.png"));
		bordertex = new Texture(pixmap);
		
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
		
		posizione = new ArrayList<Integer>();
		posizione.add(1);
		posizione.add(2);
	//	posizione.add(3);
		posizione.add(4);
		posizione.add(5);
		posizione.add(6);
		posizione.add(7);
		posizione.add(8);
		posizione.add(9);
		
		// Inizializzazione delle barre della vita dei mostri del giocatore
		for(Integer pos : posizione)
		{
			switch(pos){
				case 1: bar1l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar1l.setPosition(Gdx.graphics.getWidth()*41/120 + 106, Gdx.graphics.getHeight()*5/120);
						bar1l.setSize(8, 75);
						bar1l.setAnimateDuration(1);
						stage.addActor(bar1l);
						
						bar1m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar1m.setPosition(Gdx.graphics.getWidth()*41/120 + 114, Gdx.graphics.getHeight()*5/120);
					    bar1m.setSize(6, 75);
					    bar1m.setAnimateDuration(1);
						stage.addActor(bar1m);		
						break;
				case 2:	bar2l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar2l.setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*5/120);
						bar2l.setSize(8, 75);
						bar2l.setAnimateDuration(1);
						stage.addActor(bar2l);
						
						bar2m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar2m.setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*5/120);
					    bar2m.setSize(6, 75);
					    bar2m.setAnimateDuration(1);
						stage.addActor(bar2m);
						break;
				case 3: bar3l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar3l.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*5/120);
						bar3l.setSize(8, 75);
						bar3l.setAnimateDuration(1);
						stage.addActor(bar3l);
					
						bar3m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar3m.setPosition(Gdx.graphics.getWidth()*43/120 +120+ 120+114, Gdx.graphics.getHeight()*5/120);
						bar3m.setSize(6, 75);
						bar3m.setAnimateDuration(1);
						stage.addActor(bar3m);
						break;
				case 4: bar4l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar4l.setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*6/120 + 75);
						bar4l.setSize(8, 75);
						bar4l.setAnimateDuration(1);
						stage.addActor(bar4l);
						
						bar4m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar4m.setPosition(Gdx.graphics.getWidth()*41/120+114, Gdx.graphics.getHeight()*6/120 +75);
					    bar4m.setSize(6, 75);
					    bar4m.setAnimateDuration(1);
						stage.addActor(bar4m);
						break;
				case 5: bar5l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar5l.setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*6/120 + 75);
						bar5l.setSize(8, 75);
						bar5l.setAnimateDuration(1);
						stage.addActor(bar5l);
				
						bar5m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar5m.setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*6/120 +75);
						bar5m.setSize(6, 75);
						bar5m.setAnimateDuration(1);
						stage.addActor(bar5m);
						break;
				case 6: bar6l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar6l.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*6/120 + 75);
						bar6l.setSize(8, 75);
						bar6l.setAnimateDuration(1);
						stage.addActor(bar6l);
		
						bar6m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar6m.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +114, Gdx.graphics.getHeight()*6/120 +75);
						bar6m.setSize(6, 75);
						bar6m.setAnimateDuration(1);
						stage.addActor(bar6m);
						break;
				case 7: bar7l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar7l.setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*7/120 +75 +75);
						bar7l.setSize(8, 75);
						bar7l.setAnimateDuration(1);
						stage.addActor(bar7l);
				
						bar7m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar7m.setPosition(Gdx.graphics.getWidth()*41/120+114, Gdx.graphics.getHeight()*7/120 +75 + 75);
					    bar7m.setSize(6, 75);
					    bar7m.setAnimateDuration(1);
						stage.addActor(bar7m);
						break;
				case 8: bar8l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar8l.setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*7/120 +75 +75);
						bar8l.setSize(8, 75);
						bar8l.setAnimateDuration(1);
						stage.addActor(bar8l);
		
						bar8m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar8m.setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*7/120 +75 + 75);
						bar8m.setSize(6, 75);
						bar8m.setAnimateDuration(1);
			    		stage.addActor(bar8m);
						break;
				case 9: bar9l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bar9l.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*7/120 +75 +75);
						bar9l.setSize(8, 75);
						bar9l.setAnimateDuration(1);
						stage.addActor(bar9l);

						bar9m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bar9m.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +114, Gdx.graphics.getHeight()*7/120 +75 + 75);
						bar9m.setSize(6, 75);
						bar9m.setAnimateDuration(1);
						stage.addActor(bar9m);
						break;
			}
		}
		
		// Inizializzazione barre della vita dei mostri avversari
		for(Integer pos : posizione)
		{
			switch(pos){
				case 1: bare1l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare1l.setPosition(Gdx.graphics.getWidth()*41/120 + 106, Gdx.graphics.getHeight()*70/120);
						bare1l.setSize(8, 75);
						bare1l.setAnimateDuration(1);
						stage.addActor(bare1l);
						
						bare1m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare1m.setPosition(Gdx.graphics.getWidth()*41/120 + 114, Gdx.graphics.getHeight()*70/120);
					    bare1m.setSize(6, 75);
					    bare1m.setAnimateDuration(1);
						stage.addActor(bare1m);		
						break;
				case 2:	bare2l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare2l.setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*70/120);
						bare2l.setSize(8, 75);
						bare2l.setAnimateDuration(1);
						stage.addActor(bare2l);
						
						bare2m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare2m.setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*70/120);
					    bare2m.setSize(6, 75);
					    bare2m.setAnimateDuration(1);
						stage.addActor(bare2m);
						break;
				case 3: bare3l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare3l.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*70/120);
						bare3l.setSize(8, 75);
						bare3l.setAnimateDuration(1);
						stage.addActor(bare3l);
					
						bare3m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare3m.setPosition(Gdx.graphics.getWidth()*43/120 +120+ 120+114, Gdx.graphics.getHeight()*70/120);
						bare3m.setSize(6, 75);
						bare3m.setAnimateDuration(1);
						stage.addActor(bare3m);
						break;
				case 4: bare4l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare4l.setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*71/120 + 75);
						bare4l.setSize(8, 75);
						bare4l.setAnimateDuration(1);
						stage.addActor(bare4l);
						
						bare4m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare4m.setPosition(Gdx.graphics.getWidth()*41/120+114, Gdx.graphics.getHeight()*71/120 +75);
					    bare4m.setSize(6, 75);
					    bare4m.setAnimateDuration(1);
						stage.addActor(bare4m);
						break;
				case 5: bare5l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare5l.setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*71/120 + 75);
						bare5l.setSize(8, 75);
						bare5l.setAnimateDuration(1);
						stage.addActor(bare5l);
				
						bare5m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare5m.setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*71/120 +75);
						bare5m.setSize(6, 75);
						bare5m.setAnimateDuration(1);
						stage.addActor(bare5m);
						break;
				case 6: bare6l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare6l.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*71/120 + 75);
						bare6l.setSize(8, 75);
						bare6l.setAnimateDuration(1);
						stage.addActor(bare6l);
		
						bare6m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare6m.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +114, Gdx.graphics.getHeight()*71/120 +75);
						bare6m.setSize(6, 75);
						bare6m.setAnimateDuration(1);
						stage.addActor(bare6m);
						break;
				case 7: bare7l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare7l.setPosition(Gdx.graphics.getWidth()*41/120 +106, Gdx.graphics.getHeight()*72/120 +75 +75);
						bare7l.setSize(8, 75);
						bare7l.setAnimateDuration(1);
						stage.addActor(bare7l);
				
						bare7m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare7m.setPosition(Gdx.graphics.getWidth()*41/120+114, Gdx.graphics.getHeight()*72/120 +75 + 75);
					    bare7m.setSize(6, 75);
					    bare7m.setAnimateDuration(1);
						stage.addActor(bare7m);
						break;
				case 8: bare8l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare8l.setPosition(Gdx.graphics.getWidth()*42/120 + 120+106, Gdx.graphics.getHeight()*72/120 +75 +75);
						bare8l.setSize(8, 75);
						bare8l.setAnimateDuration(1);
						stage.addActor(bare8l);
		
						bare8m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare8m.setPosition(Gdx.graphics.getWidth()*42/120 + 120+114, Gdx.graphics.getHeight()*72/120 +75 + 75);
						bare8m.setSize(6, 75);
						bare8m.setAnimateDuration(1);
			    		stage.addActor(bare8m);
						break;
				case 9: bare9l = new ProgressBar(8, 75, 0.1f, true, barStyleHP);
						bare9l.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +106, Gdx.graphics.getHeight()*72/120 +75 +75);
						bare9l.setSize(8, 75);
						bare9l.setAnimateDuration(1);
						stage.addActor(bare9l);

						bare9m = new ProgressBar(6, 75, 0.1f, true, barStyleMP);
						bare9m.setPosition(Gdx.graphics.getWidth()*43/120 + 120 +120 +114, Gdx.graphics.getHeight()*72/120 +75 + 75);
						bare9m.setSize(6, 75);
						bare9m.setAnimateDuration(1);
						stage.addActor(bare9m);
						break;
			}
		}
	}


@Override
public void update(float dt) {

}

@Override
public void draw() {
	// inizia la sessione dello SpriteBatch dove disegna il background
	
	batch.begin();	
	batch.draw(texture, 0, 0);

	batch.draw(region, 0, 0);
	
	batch.draw(tabletex, Gdx.graphics.getWidth()*40/120 , Gdx.graphics.getHeight()*4/120);	// scacchiera del giocatore
	batch.draw(tabletex, Gdx.graphics.getWidth()*40/120 , Gdx.graphics.getHeight()*69/120);	//scacchiera dell'avversario
	
	//batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*5/120, 106, 75);
	//batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*5/120, 106, 75);
	//batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120+120, Gdx.graphics.getHeight()*5/120, 106, 75);
	//batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*6/120 + 75, 106, 75);
	//batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*7/120 + 75 + 75, 106, 75);
	
	if(health>0) health--;
	if(mana<=60) mana++;
	
	// Disegna le immagini corrispondenti ai mostri del giocatore nelle posizioni scelte
	for(Integer pos: posizione){
		switch(pos){
			case 1:	batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*5/120, 106, 75);
					bar1l.setValue(health);
					bar1m.setValue(mana);
					break;
			case 2:	batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*5/120, 106, 75);
					bar2l.setValue(mana);
					bar2m.setValue(mana);
					break;
			case 3: batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120+120, Gdx.graphics.getHeight()*5/120, 106, 75);
					bar3l.setValue(mana);
					bar3m.setValue(health);
					break;
			case 4: batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*6/120 + 75, 106, 75);
					break;
			case 5:	batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*6/120 + 75, 106, 75);
					break;
			case 6: batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*6/120 + 75, 106, 75);
					break;
			case 7: batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*7/120 +75 + 75, 106, 75);
					break;
			case 8:	batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*7/120 +75 +75, 106, 75);
					break;
			case 9:	batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*7/120 +75 + 75, 106, 75);
					break;
		}
	}	
	
	// Disegna le immagini corrispondenti ai mostri dell'avversario nelle posizioni scelte
	for(Integer pos: posizione){
		switch(pos){
			case 1:	batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*70/120, 106, 75);
					bar1l.setValue(health);
					bar1m.setValue(mana);
					break;
			case 2:	batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*70/120, 106, 75);
					bar2l.setValue(mana);
					bar2m.setValue(mana);
					break;
			case 3: batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*70/120, 106, 75);
					bar3l.setValue(mana);
					bar3m.setValue(health);
					break;
			case 4: batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*71/120 + 75, 106, 75);
					break;
			case 5:	batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*71/120 + 75, 106, 75);
					break;
			case 6: batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*71/120 + 75, 106, 75);
					break;
			case 7: batch.draw(bordertex, Gdx.graphics.getWidth()*41/120, Gdx.graphics.getHeight()*72/120 +75 +75, 106, 75);
					break;
			case 8:	batch.draw(bordertex, Gdx.graphics.getWidth()*42/120 + 120, Gdx.graphics.getHeight()*72/120 +75 +75, 106, 75);
					break;
			case 9:	batch.draw(bordertex, Gdx.graphics.getWidth()*43/120 + 120 +120, Gdx.graphics.getHeight()*72/120 +75 +75, 106, 75);
					break;
		}
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
}

@Override
public void resize(int width, int height) {
	
}

}