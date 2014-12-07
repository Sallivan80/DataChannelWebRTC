package org.webrtc.websocket.model;

import java.util.HashSet;

public class Room {
	
	String roomId;
	HashSet<Peer> peers=new HashSet<Peer>();
	
	
	public Room(String roomId) {
		super();
		this.roomId = roomId;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
	public void addPeerToRoom(Peer peer){
		peers.add(peer);
	}
	
	public void removePeerToRoom(Peer peer){
		peers.remove(peer);
	}
	
	public HashSet<Peer> getPeers() {
		return peers;
	}
	public void setPeers(HashSet<Peer> peers) {
		this.peers = peers;
	}
	
	
	
	
	
	
	

}
