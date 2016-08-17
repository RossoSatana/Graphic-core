package Entities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.scenes.scene2d.Actor;

import ServerConnection.ServerAccess;

public class User {
	private static User utente = new User(null, null);
	private String id, pw;
	private ArrayList <Monster> team, owned, fighting;
	private String foe;
	private ArrayList <Monster> fteam;
	private boolean starter;
	private JSONArray jArr, tmp;
	private JSONObject jObj;
	private ServerAccess sa;
	
	private User(String id, String pw){
		this.id = id;
		this.pw = pw;
		sa = new ServerAccess();
		team = new ArrayList<Monster>();
		owned = new ArrayList<Monster>();
		fighting = new ArrayList<Monster>();
		
		this.foe = null;
		fteam = new ArrayList<Monster>();
	}
	
	public static User getInstance(){ 
		return utente;
	}
	
	public String getId() {
		return id;
	}

	public String getPw() {
		return pw;
	}

	public void setId(String id){
		this.id =id;
	}
	
	public void setPw(String pw){
		this.pw =pw;
	}
	
	public int teamGetSize(){
		return team.size();
	}
	
	public int OwnedGetSize(){
		return owned.size();
	}
	
	public void addToTeam(String monsters){
		try {
			team.clear();
			jArr = new JSONArray(monsters);
			for (int i=0; i<jArr.length(); i++){
				jObj = jArr.getJSONObject(i);
				
				team.add(new Monster(jObj.getString("DENOMINATION"), jObj.getString("NAME"), jObj.getInt("COD_M"), jObj.getInt("LVL"), jObj.getInt("EXP")));
				team.get(i).setAd(jObj.getInt("ADL"));
				team.get(i).setAp(jObj.getInt("APL"));
				team.get(i).setmDef(jObj.getInt("MDEFL"));
				team.get(i).setDef(jObj.getInt("DEFL"));
				team.get(i).setHp(jObj.getInt("HPL"));
				team.get(i).setClas(jObj.getString("CLASS"));
				team.get(i).setType(jObj.getString("TYPE"));
				team.get(i).setRange(jObj.getInt("ATTKRANGE"));
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToTeam(Monster monster){
		try {
	
			sa.mAddTeam(monster.getCodM());
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			team.add(monster);
	}
	
	public void addToOwned(Monster monster){
		try {		
		
		sa.mRemoveTeam(monster.getCodM());

		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		owned.add(monster);
	}
	
	
	public void addToOwned(String monsters){
		try {
			owned.clear();
			jArr = new JSONArray(monsters);
			for (int i=0; i<jArr.length(); i++){
				jObj = jArr.getJSONObject(i);
				
				owned.add(new Monster(jObj.getString("DENOMINATION"), jObj.getString("NAME"), jObj.getInt("COD_M"), jObj.getInt("LVL"), jObj.getInt("EXP")));
				owned.get(i).setAd(jObj.getInt("ADL"));
				owned.get(i).setAp(jObj.getInt("APL"));
				owned.get(i).setmDef(jObj.getInt("MDEFL"));
				owned.get(i).setDef(jObj.getInt("DEFL"));
				owned.get(i).setHp(jObj.getInt("HPL"));
				owned.get(i).setClas(jObj.getString("CLASS"));
				owned.get(i).setType(jObj.getString("TYPE"));
				owned.get(i).setRange(jObj.getInt("ATTKRANGE"));
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Monster showOwnedMonster(int index){
		return owned.get(index);
	}
	
	public Monster showTeamMonster(int index){
		return team.get(index);
	}
	
	public void removeFromOwned(int index){
		owned.remove(index);
	}
	
	public void removeFromTeam(int index){
		team.remove(index);
	}

	public String getFoe(){ 
		return foe;
	}
	
	public void setFoe(String foe){
		this.foe =foe;
	}
	
	public int foeTeamGetSize(){
		return fteam.size();
	}
	
	public void addToFoeTeam(String monsters){
		try {
			fteam.clear();
			jArr = new JSONArray(monsters);
			for (int i=0; i<jArr.length(); i++){
				jObj = jArr.getJSONObject(i);
				
				fteam.add(new Monster(jObj.getString("DENOMINATION"), jObj.getString("NAME"), jObj.getInt("COD_M"), jObj.getInt("LVL"), jObj.getInt("EXP")));
				fteam.get(i).setAd(jObj.getInt("AD"));
				fteam.get(i).setAp(jObj.getInt("AP"));
				fteam.get(i).setmDef(jObj.getInt("MDEF"));
				fteam.get(i).setDef(jObj.getInt("DEF"));
				fteam.get(i).setcHp(jObj.getInt("HP"));
				//fteam.get(i).setcMp(jObj.getInt("MP"));
				fteam.get(i).setPosition(jObj.getInt("POS"));
				
				JSONObject jObjMAX = new JSONObject(sa.showMonsterStat(fteam.get(i).getCodM()));
				fteam.get(i).setHp(jObjMAX.getInt("HP"));
				//fteam.get(i).setMp(jObjMAX.getInt("MP")));
			}
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Monster showFoeTeamMonster(int index){
		return fteam.get(index);
	}
	
	public void addToFighting(String monsters) throws JSONException, IOException{
		fighting.clear();
		System.out.println(monsters);
		jArr = new JSONArray(monsters);
		for (int i=0; i<jArr.length(); i++){
			jObj = jArr.getJSONObject(i);
			
			fighting.add(new Monster(jObj.getString("DENOMINATION"), jObj.getString("NAME"), jObj.getInt("COD_M"), jObj.getInt("LVL"), jObj.getInt("EXP")));
			fighting.get(i).setAd(jObj.getInt("AD"));
			fighting.get(i).setAp(jObj.getInt("AP"));
			fighting.get(i).setmDef(jObj.getInt("MDEF"));
			fighting.get(i).setDef(jObj.getInt("DEF"));
			fighting.get(i).setcHp(jObj.getInt("HP"));
			//fighting.get(i).setcMp(jObj.getInt("MP"));
			fighting.get(i).setPosition(jObj.getInt("POS"));
			
			JSONObject jObjc = new JSONObject(sa.showMonsterStat(fighting.get(i).getCodM()));
			fighting.get(i).setHp(jObjc.getInt("HP"));
			//fighting.get(i).setMp(jObjc.getInt("MP")));

			jObj = new JSONObject(sa.mInfo(fighting.get(i).getDenomination()));
			fighting.get(i).setClas(jObj.getString("CLASS"));
			fighting.get(i).setType(jObj.getString("TYPE"));

			jObj = new JSONObject(sa.classInfo(fighting.get(i).getClas()));
			fighting.get(i).setRange(jObj.getInt("ATTKRANGE"));
		}
	}

	public int fightingGetSize() {
		return fighting.size();
	}

	public Monster showFightingMonster(int index) {
		return fighting.get(index);
	}
	
	public void setStarter(boolean start) {
		this.starter = start;
	}
	
	public boolean getStarter() {
		return starter;
	}
}
