function command(){
	if(ReqUtils.getSize() > 1){

	    return Utilities.format("$NEXT_MESSAGE$", user,
			ReqUtils.getLevel(1, 'name'),
			ReqUtils.getLevel(1, 'author'),
			ReqUtils.getLevel(1, 'id'),
			ReqUtils.getLevel(1, 'requester'));
	}
}