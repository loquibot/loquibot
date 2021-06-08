function command(){
    if(isChaos){
        if(isModChaos && !isMod){
           return;
        }
        if(isSubChaos && (!isSub || !isMod)){
           return;
        }
        GD.doChaos('kill');
    }
    else if(user === 'Alphalaneous'){
        GD.doChaos('kill');
    }
}