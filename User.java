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
	
	public void addToTeam(String monsters){
		try {
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
	
	public void addToOwned(String monsters){
		try {
			jArr = new JSONArray(monsters);
			for (int i=0; i<jArr.length(); i++){
				jObj = jArr.getJSONObject(i);
				
				owned.add(new Monster(jObj.getString("DENOMINATION"), jObj.getString("NAME"), jObj.getInt("COD_M"), jObj.getInt("LVL"), jObj.getInt("EXP")));
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
	
	public Monster showMonster(int index){
		return team.get(index);
	}
}
