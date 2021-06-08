function command(){
    if(isMod){
        if(xArgs.length === 0){
        	return Utilities.format("$DELETE_COMMAND_NO_ARGS_MESSAGE$", user);
        }
        Utilities.deleteCommand(user, xArgs[0]);
    }
}