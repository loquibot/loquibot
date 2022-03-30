function command(){
	if(isMod){
	    if(xArgs.length === 0){
    	    return Utilities.format("$MOVE_NO_ID_MESSAGE$", user);
    	}
    	if(xArgs.length === 1){
            return Utilities.format("$MOVE_NO_POS_MESSAGE$", user);
         }
		if(ReqUtils.getPosFromID(xArgs[0]) !== -1){
		    var newPos = xArgs[1]-1;
		    if(newPos <= 0 || isNaN(newPos)){
		        newPos = 0;
		    }
			ReqUtils.movePosition(ReqUtils.getPosFromID(xArgs[0]), newPos);
        	return Utilities.format("$MOVE_MESSAGE$", user, xArgs[0].toString(), (newPos + 1).toString())
        }
        else{
        	return Utilities.format("$MOVE_FAILED_MESSAGE$", user, xArgs[0].toString());
        }
	}
}