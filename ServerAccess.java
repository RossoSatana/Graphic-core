package ServerConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import Entities.Monster;

public class ServerAccess {
	private static final String SERVERURL = "http://localhost:8080/";
	protected ClientResource resource;	
	
	
	public String login(String user, String pw) throws IOException{
		resource = new ClientResource(SERVERURL + "Login/" + user + "/" + pw);
		return resource.get().getText();
	}
	
	public String insertUser (String user, String pw) throws IOException{
		resource = new ClientResource(SERVERURL + "Register/" + user + "/" + pw);
		return resource.get().getText();
	}
	
	public String joinMatchMaking (String user) throws IOException{
		resource = new ClientResource(SERVERURL + "joinMatchMaking/" + user);
		return resource.get().getText();
	}
	
	public String showMonsterStat (int codM) throws IOException{
		resource = new ClientResource(SERVERURL + "showMonsterStat/" + codM);
		return resource.get().getText();
	}
	
	public String mCollection (String user) throws IOException{
		resource = new ClientResource(SERVERURL + "mCollection/" + user);
		return resource.get().getText();
	}
	
	public String showInTeam(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "showInTeam/" + user);
		return resource.get().getText();
	}
	
	public String showNotInTeam(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "showNotInTeam/" + user);
		return resource.get().getText();
	}
	
	public String mAddTeam(int codM) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mAddTeam/" + codM);
		resource.release();
		return resource.get().getText();
	}
	
	public String mRemoveTeam(int codM) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mRemoveTeam/" + codM);
		resource.release();
		return resource.get().getText();
	}
	
	public boolean searchMatch (String user) throws IOException{
		resource = new ClientResource(SERVERURL + "searchMatch/" + user);
		if (resource.get().getText().equalsIgnoreCase("Still searching for a foe")){
			resource.release();
			return false;
		}
		else{
			resource.release();
			return true;
		}
	}
	
	public String findFoe (String user) throws ResourceException, IOException{
		resource = new ClientResource(SERVERURL + "findFoe/" + user);
		//if(resource.get().getText().equals("User not in game")) ERROR
		return resource.get().getText();
	}
	
	public String addToFighting(String mJarr) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "addToFighting/" + URLEncoder.encode(mJarr, "UTF-8"));
		return resource.get().getText();
	}

	public String round(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "round/" + user);
		return resource.get().getText();
	}
	
	public int loadingsec(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "loadingsec/" + user);
		return Integer.parseInt(resource.get().getText());
	}

	public String mFighting(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mFighting/" + user);
		return resource.get().getText();
	}

	public String aAttack(int COD_M, int COD_MA) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "aAttack/" + COD_M + "/" + COD_MA);
		return resource.get().getText();
	}

	public String mfInfo(int COD_M) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mfInfo/" + COD_M);
		return resource.get().getText();
	}

	public String clearActionQueue(String id) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "clearActionQueue/" + id);
		return resource.get().getText();
	}

	public String chatWrite(String id, String text) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "chatWrite/" + id + "/" + text);
		return resource.get().getText();
	}
	
	public String chatRead(String id) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "chatRead/" + id);
		return resource.get().getText();
	}

	public String mInfo(String denomination) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mInfo/" + denomination);
		return resource.get().getText();
	}

	public String tfInfo(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "tfInfo/" + user);
		return resource.get().getText();
	}
	
	public String classInfo(String clas) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "classInfo/" + clas);
		return resource.get().getText();
	}
	
	public String getFirstPlayer(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "getFirstPlayer/" + user);
		return resource.get().getText();
	}

	public String checkGameStatus(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "checkGameStatus/" + user);
		return resource.get().getText();
	}

	public String endVisualized(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "endVisualized/" + user);
		return resource.get().getText();
	}
	
	public String mt(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mt/" + user);
		return resource.get().getText();
	}
	
	public String mnt(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "mnt/" + user);
		return resource.get().getText();
	}
}