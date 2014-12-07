function SignalingChannel(){
		
		console.log('Create Signaling Channel:' +window.location.host);
		
	    var connection = new WebSocket("ws://"+window.location.host + "/ProgettoTesiGit/SignalingServer");
	    
	    connection.onopen = onSignalingChannelOpen;

	    connection.onerror = onSignalingChannelError;
	    
	    connection.onmessage = onSignalingChannelMessage;
	    
	    return connection;
}

function onSignalingChannelOpen(){
		console.log("Accesso al Signaling Server");
}

function onSignalingChannelError(error) {
	console.log('Ho fallito nel creare il signaling channel : ' + error.name);
}

function onSignalingChannelMessage(message){		
	
	var msg=JSON.parse(message.data);
	
	if(msg.create){
		console.log(msg.create);
	}
	
	if(msg.newConnection){
		console.log(msg.newConnection+" connectionID:{"+msg.connectionID+"}");
		
		if(msg.role=="initiator"){
			console.log("Sei l'iniziatore di connectionID{"+msg.connectionID+"}");			
			createPeerConnection("initiator",msg.connectionID);
			doCall(msg.connectionID);
		}
		if(msg.role=="joined"){
			console.log("Hai joinato nella room "+msg.roomID+"--- connectionID{"+msg.connectionID+"}");			
		}
		
	}
	
	if(msg.sdp){
		var sdp=JSON.parse(msg.sdp)
		if(sdp.type=="offer"){			
			console.log('Offerta  RTCPeerConnection Remota connectionID{'+msg.connectionID+'}\n)');
			createPeerConnection("joined",msg.connectionID);
			var remoteDescription=new RTCSessionDescription(sdp);
			PeersConnection[msg.connectionID].setRemoteDescription(remoteDescription);
			doAnswer(msg.connectionID);
		}
		if(sdp.type=="answer"){
			console.log('Risposta  RTCPeerConnection Remota connectionID{'+msg.connectionID+'}\n)');
			var remoteDescription=new RTCSessionDescription(sdp);
			PeersConnection[msg.connectionID].setRemoteDescription(remoteDescription);
			
		}
	}	
	
	if(msg.iceCandidateArray){
		var candidate=msg.iceCandidateArray;
		for (var i = 0; i < candidate.length; i++)		    
			PeersConnection[msg.connectionID].addIceCandidate(new RTCIceCandidate(candidate[i]));		
	}	
	
}


function sendSdp(sdpObject,connectionID){
	console.log('Invio '+sdpObject.type+' SDP connectionID{'+connectionID+'}');
	signalingChannel.send(JSON.stringify({'sdp': sdpObject,'connectionID':connectionID}));
}

function sendIceCandidate(iceCandidate,connectionID,peerID){
	console.log('Invio iceCandidate connectionID{'+connectionID+'}');
	signalingChannel.send(JSON.stringify({'iceCandidate': iceCandidate,'connectionID':connectionID,'peerID':peerID}));
}

function sendConnectionEstablished(connectionID){
	console.log('Connesionne {'+connectionID+'} stabilita con successo');
	signalingChannel.send(JSON.stringify({'connectionEstablished': true,'connectionID':connectionID}));
}

