function command(){
    if(xArgs.length === 0){
        return Utilities.format("$SPECIFY_ID_MESSAGE$", user);
    }
	return Requests.request(user, isMod, isSub, message, messageID, userID);
}