package ServerConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class ServerAccess {
	private static final String SERVERURL = "http://localhost:8080/";
	protected ClientResource resource;	
	
	
	/*public static void main(String[] args) throws ResourceException, IOException {
		// Create the client resource  
		ClientResource resource = new ClientResource("http://localhost:8081/User");
		ClientResource resource2 = new ClientResource("http://localhost:8081/Login/Kiki/pop");
		JSONParser parser;

		// Write the response entity on the console
		resource.get().write(System.out); 
		parser = new JSONParser();
	}*/
	
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
	
	public boolean searchMatch (String user) throws IOException{
		resource = new ClientResource(SERVERURL + "searchMatch/" + user);
		if (resource.get().getText().equalsIgnoreCase("Still searching for a foe"))
			return false;
		else
			return true;
	}

	public String showInTeam(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "showInTeam/" + user);
		return resource.get().getText();
	}
	
	public String showNotInTeam(String user) throws ResourceException, IOException {
		resource = new ClientResource(SERVERURL + "showNotInTeam/" + user);
		return resource.get().getText();
	}
	
}