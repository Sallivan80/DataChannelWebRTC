package org.webrtc.websocket.model;

public class Connection {
	
	String connectionId;
	String connectionState;	
	Peer initiatorPeer;
	Peer joinedPeer;
	
	
	public Connection(String connectionId) {
		super();
		this.connectionId = connectionId;
	}
	
	public Connection(String connectionId,Peer initiator,Peer joined){
		this.initiatorPeer=initiator;
		this.joinedPeer=joined;
		this.connectionId=connectionId;
	}
	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String roomId) {
		this.connectionId = roomId;
	}
	public Peer getInitiatorPeer() {
		return initiatorPeer;
	}
	public void setInitiatorPeer(Peer initiatorPeer) {
		this.initiatorPeer = initiatorPeer;
	}
	public Peer getJoinedPeer() {
		return joinedPeer;
	}
	public void setJoinedPeer(Peer joinerPeer) {
		this.joinedPeer = joinerPeer;
	}
	
	public String getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(String connectionState) {
		this.connectionState = connectionState;
	}
	
	
	
	
	

}
