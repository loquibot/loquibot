function command(){
    var intArg = parseInt(args[1]);
    if(isNaN(intArg) || args.length === 1){
    	intArg = 1;
    }
    var pages = (((ReqUtils.getSize()-1)/queueLength) + 1)|0;
    if(ReqUtils.getSize() === 0){
        return Utilities.format("$QUEUE_NO_LEVELS_MESSAGE$", user);
    }
    if(intArg > pages){
        return Utilities.format("$QUEUE_NO_PAGE_MESSAGE$", user, intArg|0);
    }
    if(intArg < 1){
        intArg = 1;
    }
    var message = Utilities.format("$QUEUE_MESSAGE$", user, intArg|0, pages|0) + ' | ';
    for(var i = (intArg - 1)*queueLength; i < intArg * queueLength; i++){
        if(i < ReqUtils.getSize()){
            if(i % queueLength !== 0){
                message = message.concat(", ")
            }
            message = message.concat(i+1 + ': ' + ReqUtils.getLevel(i, 'name') + ' (' + ReqUtils.getLevel(i, 'id') + ')');
        }
    }
    return message;
}