package org.webrtc.websocket.server.service;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONFactory {
	

	public static String newConnectionMessage(String connectionID, String peerId, String role,String roomID) throws JSONException {
		JSONObject newConnectionMessage= new JSONObject();
		newConnectionMessage.put("newConnection", "Nuova connessione con {"+peerId+"}");
		newConnectionMessage.put("connectionID", connectionID);
		newConnectionMessage.put("role", role);	
		
		newConnectionMessage.put("roomID",roomID);
		return newConnectionMessage.toString();
	}
	
	public static String createRoom(String roomId, String peerId) throws JSONException{
		JSONObject createOrJoinMessage=new JSONObject();
		createOrJoinMessage.put("create","creata room " +roomId +" dal peer : " +peerId);
		return createOrJoinMessage.toString();
	}
	

	public static String createIceCandidateMessage(HashSet<JSONObject> iceCandidate, String connectionID) throws JSONException {
		JSONObject iceCandidateMessage=new JSONObject();
		iceCandidateMessage.put("connectionID",connectionID);
		JSONArray arrayCandidate=new JSONArray();
		for(JSONObject candidate : iceCandidate)
			arrayCandidate.put(candidate);
		iceCandidateMessage.put("iceCandidateArray", arrayCandidate);
		return iceCandidateMessage.toString();	
		
	}

	public static String createSdpMessage(JSONObject sdpObject,	String connectionID) throws JSONException {
		JSONObject sdpMessage=new JSONObject();
		sdpMessage.put("sdp", sdpObject.toString());
		sdpMessage.put("connectionID", connectionID);
		return sdpMessage.toString();
	}


}
