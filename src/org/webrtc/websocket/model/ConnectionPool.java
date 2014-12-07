package org.webrtc.websocket.model;

import java.util.HashSet;



public class ConnectionPool {

	HashSet<Connection> connections;
	
	public ConnectionPool(){
		this.connections=new HashSet<Connection>();
		
	}	
	
	public void addConnection(Connection newConnection){
		connections.add(newConnection);
	}
	
	public void removeConnection(Connection connection){
		try{
			
			synchronized (this) {
				connections.remove(connection);
			    }			
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
	}
	
	public Connection getConnectionById(String connectionID){
		for(Connection c : connections)
			if(c.getConnectionId().equalsIgnoreCase(connectionID))
				return c;
		return null;
	}
	
	public int size(){
		return connections.size();
	}
}
