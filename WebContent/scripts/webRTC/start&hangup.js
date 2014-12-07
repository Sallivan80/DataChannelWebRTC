// Oggetti html che gestiscono il Data channel 
var sendChannels=new Array();
var receiveChannels= new Array();
var sendButton      = document.getElementById("send-button");
var sendTextarea    = document.getElementById("dataChannelSend");
var receiveTextarea = document.getElementById("dataChannelReceive");

//Button inizio e fine sessione
var hangUpButton=document.getElementById("hangUpButton");
var startButton=document.getElementById("startButton");

//Elementi per feedback

var labelID=document.getElementById("peer-id");
$('#chat-container').hide();

//Handler associato con il 'Send' button & hangUpButton
sendButton.onclick=sendData;




//PeersConnection
var PeersConnection=new Array();


//Singnaling WebSocket Server
var signalingChannel ;
 
 var room;
 var peerID;
 function start(){
	 
	 signalingChannel=new SignalingChannel("signalingChannel");
	 
	 //Chiedo all'utente il nome della room dal prompt
	  room = prompt('Inserisci nome della room:'); 
	  
	  peerID=prompt('Inserisci un id');
	  
	  $('#label-start-button').hide();
	  $('#div-room-id').html("PEER CONNESSI NELLA ROOM "+room);
	  labelID.innerText="Sei connesso come: "+peerID;
	 //Invio il messaggio 'Create or join' al singnalling server
	 if (room !== '') {
		 console.log('Create or join room', room);
		 signalingChannel.send(JSON.stringify({"createOrJoin":room,"peerID":peerID}));
		 ;
	 }
 }

 function enableInput(){	 
	 $("#chat-container").show();
	 dataChannelSend.focus();
	 dataChannelSend.placeholder = "";	 
 }
 
 function disableInput(){
	 $("#chat-container").hide();
 }
 
 function hangup() {
	console.log('Hanging up.');
	
	for(var i in sendChannels)
		sendChannels[i].close();
	for(var i in receiveChannels)
		receiveChannels[i].close();
	for(var i in PeersConnection){
		PeersConnection[i].close();
		PeersConnection[i]=null;
	}
		
	sendButton.disabled=true;
	
}

 function addPeerToList(connectionID) {
	 
	 var splitString = connectionID.split("-");
	 var peerConnected;
	 if(splitString[1]==peerID)
		 peerConnected=splitString[2];
	 else
		 peerConnected=splitString[1];
	 
		 
	 $("#list-peer").append(
			 $("<li>").attr("class","media").append(
					 $("<div>").attr("class","media-body").append(
							 $("<div>").attr("class","media").append(
									 $("<a>").attr("class","pull-left").append($("<img>").attr("class","media-object img-circle").attr("style","max-height:40px;").attr("src","images/user.jpg"))
										).append($("<div>").attr("class","media-body").append($("<h5>").html(peerConnected)))
								)
					)
		);
		   
	
 }

 
 	function addMessageToChat(who,data){
 		var date=new Date();
 		var hour=date.getHours();
 		var min=date.getMinutes();
 		var sec=date.getSeconds();
 		
 		$("#chat-box").append(
 				$("<li>").attr("class","media").append(
 						$("<div>").attr("class","media-body").attr("style","display:block;").append(
 								$("<div>").attr("class","media")
 								.append($("<a>").attr("class","pull-left").append($("<img>").attr("class","media-object img-circle").attr("src","images/chat-ico.jpg")))
 								.append($("<p>").html(who))
 								.append($("<div>").attr("class","media-body").html(data).append($("<br>")).append($("<small>").attr("class","text-muted").html("alle "+hour+":"+min+":"+sec)).append($("<hr>")))
 						)
 				)
 		);
 		
 		autoScrollMessage();
 	}

 	
 function autoScrollMessage(){
	
			 $('#div-chat').animate({
		            scrollTop : $("ul#chat-box li:last-child").position().top,
		        }, 200);
		 
 
 }
 	