package Entities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ServerConnection.ServerAccess;

public class User {
	private static User utente = new User(null, null); 
	private String id, pw;
	private ArrayList <Monster> team, owned;
	private JSONArray jArr, tmp;
	private JSONObject jObj;
	private ServerAccess sa;
	
	private User(String id, String pw){
		this.id = id;
		this.pw = pw;
		sa = new ServerAccess();
		team = new ArrayList<Monster>();
		owned = new ArrayList<Monster>();
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
				jObj = new JSONObject(sa.showMonsterStat(team.get(i).getCodM()));
				team.get(i).setAd(jObj.getInt("AD"));
				team.get(i).setAp(jObj.getInt("AP"));
				team.get(i).setmDef(jObj.getInt("MDEF"));
				team.get(i).setDef(jObj.getInt("DEF"));
				team.get(i).setHp(jObj.getInt("HP"));
				
			}
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToTeam(Monster monster){
		try {
			jObj = new JSONObject(sa.showMonsterStat(monster.getCodM()));
			monster.setAd(jObj.getInt("AD"));
			monster.setAp(jObj.getInt("AP"));
			monster.setmDef(jObj.getInt("MDEF"));
			monster.setDef(jObj.getInt("DEF"));
			monster.setHp(jObj.getInt("HP"));
			sa.mAddTeam(monster.getCodM());
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			team.add(monster);
	}
	
	public void addToOwned(Monster monster){
		try {
		jObj = new JSONObject(sa.showMonsterStat(monster.getCodM()));
		monster.setAd(jObj.getInt("AD"));
		monster.setAp(jObj.getInt("AP"));
		monster.setmDef(jObj.getInt("MDEF"));
		monster.setDef(jObj.getInt("DEF"));
		monster.setHp(jObj.getInt("HP"));
		sa.mRemoveTeam(monster.getCodM());
		} catch (JSONException | IOException e) {
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
				jObj = new JSONObject(sa.showMonsterStat(owned.get(i).getCodM()));
				owned.get(i).setAd(jObj.getInt("AD"));
				owned.get(i).setAp(jObj.getInt("AP"));
				owned.get(i).setmDef(jObj.getInt("MDEF"));
				owned.get(i).setDef(jObj.getInt("DEF"));
				owned.get(i).setHp(jObj.getInt("HP"));
				
			}
		} catch (JSONException | IOException e) {
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
}
