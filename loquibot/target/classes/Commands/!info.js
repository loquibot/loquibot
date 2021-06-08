function command(){
	var intArg = parseInt(args[1]);
	if(isNaN(intArg) || args.length <= 1 || args.length >= ReqUtils.getSize()){
		intArg = ReqUtils.getSelection()+1;
	}
	if(ReqUtils.getSize() > 0 && intArg <= ReqUtils.getSize()){
	    return Utilities.format("$INFO_COMMAND_MESSAGE$", user,
			ReqUtils.getLevel(intArg-1, 'name'),
			ReqUtils.getLevel(intArg-1, 'id'),
			ReqUtils.getLevel(intArg-1, 'author'),
			ReqUtils.getLevel(intArg-1, 'requester'),
			ReqUtils.getLevel(intArg-1, 'downloads'),
			ReqUtils.getLevel(intArg-1, 'likes'),
			ReqUtils.getLevel(intArg-1, 'objects'),
			ReqUtils.getLevel(intArg-1, 'difficulty'));
	}
}