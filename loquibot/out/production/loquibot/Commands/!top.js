function command(){
	if(isMod){
	    var pos = ReqUtils.getPosFromID(xArgs[0]);
	    if(xArgs.length === 0){
	        return Utilities.format("$TOP_NO_ID_MESSAGE$", user);
	    }
	    if(pos !== -1 && xArgs.length > 0){
		    Levels.movePosition(pos, 1);
        	return Utilities.format("$TOP_MESSAGE$", user, xArgs[0].toString())
		}
		else{
        	return Utilities.format("$TOP_FAILED_MESSAGE$", user, xArgs[0].toString());
		}
	}
}