

//Configurazione ICE protocol per PeerConnection
//ICE server
var iceServers = {
        iceServers: [{
            url: 'stun:stun.l.google.com:19302'
        }]
    };
//Opzioni PeerConnection
 var optionalRtpDataChannels = {
     optional: [{
         RtpDataChannels: true
     }]
 };

//sdpConstraints
 var sdpConstraints={};


//Gestione Peer Connection.... 
function createPeerConnection(role,connectionID){
	try{
		PeersConnection[connectionID]=new webkitRTCPeerConnection(iceServers,optionalRtpDataChannels);
		
		PeersConnection[connectionID].onicecandidate=function handleIceCandidate(event) {
			
			console.log("----ICE STATE : "+PeersConnection[connectionID].iceConnectionState);
			if (event.candidate) {
			    sendIceCandidate(event.candidate,connectionID,peerID);
			} else {
				sendIceCandidate({endGathering:'true'},connectionID,peerID);  
			    console.log('Fine dei candidati.');
			  }
		}
		
		console.log('Creato RTCPeerConnnection con:\n' +
				  '  connectionID: \'' + connectionID + '\';\n' +
			      '  iceServers: \'' + JSON.stringify(iceServers) + '\';\n' +
			      '  constraints: \'' + JSON.stringify(optionalRtpDataChannels) + '\'.'); 
		} 
	catch (e){
			    console.log('Ho fallito nel creare PeerConnection, eccezzione: ' + e.message);
			    alert('Non posso creare l\'oggetto RTCPeerConnection.');
			    return;
		}
	
	if(role=="initiator"){
		try{
			//Creo un data channel reliable
			sendChannels[connectionID] = PeersConnection[connectionID].createDataChannel(connectionID,{reliable:true});
			console.log("Creato send data channel--->connectionID{"+connectionID+"}");
		}
		catch(e){
			alert('Ho fallito nel creare un data channel. ');
		    console.log('createDataChannel() ha fallito con eccezione: ' + e.message);
		}
		
		sendChannels[connectionID].onopen =  handleSendChannelStateChange;
		sendChannels[connectionID].onmessage = handleMessage;
		sendChannels[connectionID].onclose = handleSendChannelStateChange;
	}
	else{
		//E' il Joiner
		PeersConnection[connectionID].ondatachannel=function gotReceiveChannel(event){
			console.log("Ricevuto un Channel Callback");
			receiveChannels[connectionID] = event.channel;
			receiveChannels[connectionID].onmessage = handleMessage;
			receiveChannels[connectionID].onopen = handleReceiveChannelStateChange;
			receiveChannels[connectionID].onclose = handleReceiveChannelStateChange;
			
		};
	}
}



//Gestori dei canali di invio e ricezione
function handleSendChannelStateChange() {
	
	  var readyState = this.readyState;
	  console.log('Stato Send Channel{'+this.label+'}: '+ readyState );
	  
	  if(readyState=="open")
		  addPeerToList(this.label);
	  
	  var isChannelsReady=true;
	  for(var i in sendChannels)
		  if(sendChannels[i].readyState!="open")
			  isChannelsReady=false;
	  // Se tutti i canali sono pronti , abilito user input
	  if (isChannelsReady) 	  	
		  enableInput();		 
	  else
		  disableInput();
}

function handleReceiveChannelStateChange() {
	  var readyState = this.readyState;
	  console.log('Stato Received Channel{'+this.label+'}: ' + readyState);
	  
	 	  
	  if(readyState=="open"){
		  sendConnectionEstablished(this.label);
		  addPeerToList(this.label);
	  }
		  
	  
	  var isChannelsReady=true;
	  for(var i in receiveChannels)
		  if(receiveChannels[i].readyState!="open")
			  isChannelsReady=false;
	  
	  // Se tutti i canali sono pronti , abilito user input
	  if (isChannelsReady) 	  	
		  enableInput();		 
	  else
		  disableInput();	  
}

//Creo l'SDP Offer
function doCall(connectionID) {
  console.log('Sto Creando l\'offerta per connectionID{'+connectionID+'}.......');
  
  PeersConnection[connectionID].createOffer(function (sessionDescription) {
	  	PeersConnection[connectionID].setLocalDescription(sessionDescription);	  	
	  	console.log('Offerta Locale creata per connectionID{'+connectionID+'}\n)');;
	  	sendSdp(PeersConnection[connectionID].localDescription,connectionID);
	}, onSignalingChannelError, sdpConstraints);
}



// Creo l'SDP Answer
function doAnswer(connectionID) {
  console.log('Invio l\'answer per connectionID{'+connectionID+'}.......');  
  PeersConnection[connectionID].createAnswer(function (sessionDescription) {
	  	PeersConnection[connectionID].setLocalDescription(sessionDescription);	  	
	  	console.log('Offerta Locale creata per connectionID{'+connectionID+'}------------------------------\n)');
	  	sendSdp(PeersConnection[connectionID].localDescription,connectionID);
	}, onSignalingChannelError, sdpConstraints);
}


//Invio e ricezione dati
function sendData(){
	var data = sendTextarea.value;
	addMessageToChat(peerID,data);
	
	for(var i in sendChannels){
			sendChannels[i].send(peerID+"-"+data);
			console.log("Invio dati da sendChannel:",data);
	}
	for(var i in receiveChannels){
			receiveChannels[i].send(peerID+"-"+data);
			console.log("Invio dati da receiveChannel:",data);
	}	
	
}

function handleMessage(event) {
	  console.log('Ricevuto messaggio: ' + event.data);
	  var splitStr=event.data.split("-");
	  addMessageToChat(splitStr[0],splitStr[1]);
	  
	  
}
