package org.webrtc.websocket.server.service;

import java.util.HashSet;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.websocket.model.Room;

public final class RoomHandler {
	

	public static boolean checkRoom(String roomId, HashSet<Room> rooms) {
		for(Room r : rooms)
			if(r.getRoomId().equalsIgnoreCase(roomId))
				return true;
		return false;
	}

	public static Room getRoomById(String roomId, HashSet<Room> rooms) {
		for(Room r : rooms)
			if(r.getRoomId().equalsIgnoreCase(roomId))
				return r;
		return null;
	}

	public static boolean removeRoomById(String roomId, HashSet<Room> rooms) {
		for(Room r : rooms)
			if(r.getRoomId().equalsIgnoreCase(roomId))
			{
				rooms.remove(r);
				return true;
			}
				
		return false;
	}
	
	public static JSONObject createInitiatorMessage(String roomId) throws JSONException{
		JSONObject initiatorMessage=new JSONObject();
		initiatorMessage.put("join", "Un altro peer a fatto una richiesta di join");
		initiatorMessage.put("room",roomId);
		return initiatorMessage;
	}
	
	public static JSONObject createJoinedMessage(String roomId) throws JSONException{
		JSONObject joinedMessage=new JSONObject();
		joinedMessage.put("joined", "Questo peer ha joinato");
		joinedMessage.put("room",roomId);
		return joinedMessage;
	}
	
	public static JSONObject createAnswerMessage(JSONObject sdpAnswer) throws JSONException{
		JSONObject answer = new JSONObject();
		answer.put("messaggio", "risposta ricevuta dal peer remoto");
		answer.put("sdp", sdpAnswer.toString());
		return answer;
	}
	
	public static JSONObject createOfferMessage(JSONObject sdpOffer) throws JSONException{
		JSONObject offer = new JSONObject();
		offer.put("messaggio", "offerta ricevuta dal peer remoto");
		offer.put("sdp", sdpOffer.toString());
		return offer;
	}
	
	public static JSONObject createIceCandidateMessage(JSONObject candidate) throws JSONException{
		JSONObject iceCandidate= new JSONObject();
		iceCandidate.put("messaggio", "ricevuto IceCandidate Remoto");
		iceCandidate.put("sdp",candidate.toString());
		return iceCandidate;
	}

//	public static boolean isRoomFull(String roomId, HashSet<Room> rooms) {
//		Room room = getRoomById(roomId, rooms);
//		if(room.getInitiatorPeer()==null || room.getJoinedPeer()==null)
//			return false;
//		return true;
//	}

	public static JSONObject createFullMessage() throws JSONException {
		JSONObject fullMessage= new JSONObject();
		fullMessage.put("full", "stanza piena");		
		return fullMessage;
	}

	
	
	
	
}
