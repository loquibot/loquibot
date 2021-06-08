function command(){
    if(user === 'Alphalaneous' || isMod){
        return Board.eval(message);
    }
}