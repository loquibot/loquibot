function command(){
    if(args.length > 1){
        return ReqUtils.getHelp(user, isMod, args[1]);
    }
    else{
	    return ReqUtils.getHelp(user, isMod);
	}
}