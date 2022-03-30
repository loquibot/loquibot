function command(){
	var intArg = parseInt(args[1]);
	if(isNaN(intArg) || args.length === 1){
		intArg = ReqUtils.getSelection() +1;
	}
	if(ReqUtils.getSize() > 0 && intArg <= ReqUtils.getSize()){
	    return Utilities.format("$SONG_MESSAGE$", user,
			ReqUtils.getLevel(intArg-1, 'songName'),
			ReqUtils.getLevel(intArg-1, 'songAuthor'),
			ReqUtils.getLevel(intArg-1, 'songID'))
	}
	else {
	    return '';
	}
}