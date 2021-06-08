function command(){
    var intArg = parseInt(args[1]);
	if(isNaN(intArg) || args.length === 1){
		intArg = 1;
	}
	if(isMod){
	    if(isNaN(parseInt(args[1]))){
	        return;
	    }
	}
	return ReqUtils.remove(user, isMod, intArg);
}