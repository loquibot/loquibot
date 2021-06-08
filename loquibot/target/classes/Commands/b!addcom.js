function command(){
    if(isMod){
        if(xArgs.length === 0){
        	return Utilities.format("$ADD_COMMAND_NO_ARGS_MESSAGE$", user);
        }
        if(xArgs.length === 1){
            return Utilities.format("$ADD_COMMAND_NO_RESPONSE_MESSAGE$", user);
        }
        Utilities.addCommand(user, xArgs);
    }
}