package org.webrtc.websocket.model;

import java.io.IOException;
import java.util.HashSet;

import org.apache.tomcat.jni.Lock;
import org.json.JSONObject;

import javax.websocket.*;

public class Peer {
	
	
	
	String peerId;
	Session sessionPeer;
	JSONObject sdpOffer;
	JSONObject sdpAnswer;
	HashSet<JSONObject> iceCandidate;
	
	
	

	public void setSessionPeer(Session sessionPeer) {
		this.sessionPeer = sessionPeer;
	}	
	
	public Peer(String peerId, JSONObject sdpOffer, JSONObject sdpAnswer,
			HashSet<JSONObject> iceCandidate) {
		super();
		this.peerId = peerId;
		this.sdpOffer = sdpOffer;
		this.sdpAnswer = sdpAnswer;
		this.iceCandidate = iceCandidate;
	}
	
	public Peer(String peerId, Session session) {
		super();
		this.peerId = peerId;		
		this.sessionPeer=session;
	}
	
	public String getPeerId() {
		return peerId;
	}
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	public JSONObject getSdpOffer() {
		return sdpOffer;
	}
	public void setSdpOffer(JSONObject sdpOffer) {
		this.sdpOffer = sdpOffer;
	}
	public JSONObject getSdpAnswer() {
		return sdpAnswer;
	}
	public void setSdpAnswer(JSONObject sdpAnswer) {
		this.sdpAnswer = sdpAnswer;
	}
	public HashSet<JSONObject> getIceCandidate() {
		return iceCandidate;
	}
	public void setIceCandidate(HashSet<JSONObject> iceCandidate) {
		this.iceCandidate = iceCandidate;
	}
	
	public Session getSessionPeer() {
		return sessionPeer;
	}	

	public void addIceCandidate(JSONObject candidate){
		if(iceCandidate==null)
			iceCandidate=new HashSet<JSONObject>();
		iceCandidate.add(candidate);
	}
	
	public void sendSyncMessage(String message) throws IOException {
		try{
			
			synchronized (this) {
				this.sessionPeer.getBasicRemote().sendText(message);
			    }			
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
	}
	

}
