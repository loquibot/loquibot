function command(){
    if(isMod){
		ReqUtils.clear();
	    return Utilities.format('$CLEAR_MESSAGE$', user);
	}
}

