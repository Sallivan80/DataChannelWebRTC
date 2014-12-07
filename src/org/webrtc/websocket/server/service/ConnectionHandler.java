package org.webrtc.websocket.server.service;

import java.io.IOException;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.websocket.model.Connection;
import org.webrtc.websocket.model.ConnectionPool;
import org.webrtc.websocket.model.Peer;
import org.webrtc.websocket.model.Room;

public class ConnectionHandler {
	
	private static final ConnectionPool connectionPool=new ConnectionPool();
	
	public static void createConnectionsAndSendMessage(Room room,Peer newJoinedPeer) throws JSONException, IOException{
		
		for(Peer initiatorPeer : room.getPeers()){
			
			String connectionID=room.getRoomId()+"-"+initiatorPeer.getPeerId()+"-"+newJoinedPeer.getPeerId();
			Connection newConnection=new Connection(connectionID,initiatorPeer,newJoinedPeer);			
			connectionPool.addConnection(newConnection);			
			
			initiatorPeer.sendSyncMessage(JSONFactory.newConnectionMessage(connectionID,newJoinedPeer.getPeerId(),"initiator",room.getRoomId()));		
			newJoinedPeer.sendSyncMessage(JSONFactory.newConnectionMessage(connectionID, initiatorPeer.getPeerId(),"joined",room.getRoomId()));	
			
		}		
		
	}	

	public static void sendOfferToOtherPeer(JSONObject offerSdp,String connectionID) throws IOException, JSONException {
		
		Connection connection=connectionPool.getConnectionById(connectionID);
		connection.getInitiatorPeer().setSdpOffer(offerSdp);
		connection.getJoinedPeer().sendSyncMessage(JSONFactory.createSdpMessage(offerSdp,connectionID));		
	}

	public static void sendAnswerToOtherPeer(JSONObject answerSdp,String connectionID) throws IOException, JSONException {
		
		Connection connection=connectionPool.getConnectionById(connectionID);		
		connection.getJoinedPeer().setSdpAnswer(answerSdp);
		connection.getInitiatorPeer().sendSyncMessage(JSONFactory.createSdpMessage(answerSdp,connectionID));	
		
	}	

	public static void gatheringCandidate(JSONObject newIceCandidate,String connectionID, String peerID) {
		
		Connection connection=connectionPool.getConnectionById(connectionID);
		
		if(connection.getInitiatorPeer().getPeerId().equalsIgnoreCase(peerID))
			connection.getInitiatorPeer().addIceCandidate(newIceCandidate);
		else
			connection.getJoinedPeer().addIceCandidate(newIceCandidate);
		
	}

	public static void sendIceCandidate(String connectionID, String peerID) throws IOException, JSONException {
		
		Connection connection=connectionPool.getConnectionById(connectionID);
		
		if(connection.getInitiatorPeer().getPeerId().equalsIgnoreCase(peerID))
			connection.getJoinedPeer().sendSyncMessage(JSONFactory.createIceCandidateMessage(connection.getInitiatorPeer().getIceCandidate(),connectionID));
		else
			connection.getInitiatorPeer().sendSyncMessage(JSONFactory.createIceCandidateMessage(connection.getInitiatorPeer().getIceCandidate(),connectionID));
	}

	public static void destroyConnection(String connectionID) {
		System.out.println("Connection Pool Size:"+connectionPool.size());
		Connection connection=connectionPool.getConnectionById(connectionID);
		if(connection!=null)			
			connectionPool.removeConnection(connection);
		
		System.out.println("Connection Pool Size after remove:"+connectionPool.size());
		
	}

}
