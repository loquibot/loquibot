function command(){
    if(isMod){
        if(xArgs.length === 0){
        	return Utilities.format("$EDIT_COMMAND_NO_ARGS_MESSAGE$", user);
        }
        Utilities.editCommand(user, xArgs);
    }
}