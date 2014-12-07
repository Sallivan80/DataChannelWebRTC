package org.webrtc.websocket.server;


import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.websocket.model.Peer;
import org.webrtc.websocket.model.Room;
import org.webrtc.websocket.server.service.ConnectionHandler;
import org.webrtc.websocket.server.service.JSONFactory;
import org.webrtc.websocket.server.service.RoomHandler;
/**
 * Created for playjava.wordpress.com
 * @author abhishek.anne
 */
@ServerEndpoint(value = "/SignalingServer")
public class SignalingServer {

    private static final Logger LOGGER = 
            Logger.getLogger(SignalingServer.class.getName());
    
    //------------ ROOMS ------------------
    private static final HashSet<Room> rooms= new HashSet<Room>();    
    
    //------------ CONSTANTI ---------------
    private static final String sdp="sdp";
    private static final String createOrJoin="createOrJoin";
    private static final String connectionID="connectionID";
    private static final String peerID="peerID";
    private static final String iceCandidate="iceCandidate";   
    private static final String connectionEstablished="connectionEstablished";

    
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.log(Level.INFO, "------------Nuova connessione con il Peer: {0}  RoomSize {"+rooms.size()+"}--------------", 
                session.getId());         
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, JSONException  {    	
    	

        JSONObject jsonMessage=new JSONObject(message);        
        
        if(jsonMessage.has(createOrJoin))        
        	handleCreateOrJoinRoom(jsonMessage.getString(createOrJoin),jsonMessage.getString(peerID),session);        
        	
        if(jsonMessage.has(sdp))
        	handleSdp(jsonMessage.getJSONObject(sdp),jsonMessage.getString(connectionID));
                
        if(jsonMessage.has(iceCandidate))
        	handleIceCandidate(jsonMessage.getJSONObject(iceCandidate),jsonMessage.getString(connectionID),jsonMessage.getString(peerID)); 
        
        if(jsonMessage.has(connectionEstablished))
        	handleConnectionEstablished(jsonMessage.getString(connectionID));
       
    }		

	

	@OnClose
    public void onClose(Session session) {
        LOGGER.log(Level.INFO, "Close connection for clientps: {0}", 
                session.getId());
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        LOGGER.log(Level.INFO, "Error for client: {0} : "+exception.getMessage(), session.getId());
    }    
    
    
    //-------------------- PRIVATE METHOD----------------------------//
    
    private void handleCreateOrJoinRoom(String roomId, String peerID,Session session) throws IOException, JSONException {
		
		//verifico prima se ci sono room attive
		if(rooms.size()!=0 )
			if(RoomHandler.checkRoom(roomId,rooms))			
				AddOtherPeerToRoomAndSendBackJoinMessage(session,roomId,peerID);
			else
				CreateRoomAndSendBackCreatedMessage(session,roomId, peerID);
		
		//Se non ci sono room attive la creo e notifico il peer
		if(rooms.size()==0)			
			CreateRoomAndSendBackCreatedMessage(session,roomId, peerID);		
		
	}
    
    

	private void AddOtherPeerToRoomAndSendBackJoinMessage(Session session,
			String roomId, String peerID) throws JSONException, IOException {
		
		
		Room room = RoomHandler.getRoomById(roomId,rooms);		
		
		Peer newJoinedPeer = new Peer(peerID,session);		
		
		ConnectionHandler.createConnectionsAndSendMessage(room, newJoinedPeer);	
		
		room.addPeerToRoom(newJoinedPeer);
		
	}
	
	private void CreateRoomAndSendBackCreatedMessage(Session session,String roomId, String peerId) throws JSONException, IOException {
		
		
		Room newRoom=new Room(roomId);		
		
		Peer peer = new Peer(peerId,session);
		
		newRoom.addPeerToRoom(peer);
		
		rooms.add(newRoom);			
		
		LOGGER.log(Level.INFO, "-------------Creata room [{0}]: dal Peer {1} -----------------",
        		new Object[] {roomId, peerId});       
				
		peer.sendSyncMessage(JSONFactory.createRoom(roomId, peerId));
		
	}
	
	 //----------- HANDLE SDP OBJECT-----------------//
	 private void handleSdp(JSONObject sdp, String connectionID) throws JSONException, IOException {		 
		 if(sdp.getString("type").equalsIgnoreCase("offer"))
				 ConnectionHandler.sendOfferToOtherPeer(sdp,connectionID);
		 if(sdp.getString("type").equalsIgnoreCase("answer"))
			 ConnectionHandler.sendAnswerToOtherPeer(sdp,connectionID);
			 
	 }	
	 
	//---------- HANDLE ICE CANDIDATE------------//
	 private void handleIceCandidate(JSONObject newIceCandidate, String connectionID,String peerID) throws JSONException, IOException {			 
			if(newIceCandidate.has("endGathering"))
				ConnectionHandler.sendIceCandidate(connectionID,peerID);
			else
				ConnectionHandler.gatheringCandidate(newIceCandidate,connectionID,peerID);			
	 }
	 
	//---------- HANDLE CONNECTION ESTABLISHED------------//
	 private void handleConnectionEstablished(String connectionID) {
		 ConnectionHandler.destroyConnection(connectionID);
		 	
	}
	 
	

	
}