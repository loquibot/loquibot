package com.alphalaneous.Enums;

import com.alphalaneous.Utilities.Utilities;

public enum UserLevel {

    UNKNOWN(-1), EVERYONE(0), SUBSCRIBER(1), VIP(2), MODERATOR(3), OWNER(4);


    public final int value;

    UserLevel(int v){
        value = v;
    }

    public static UserLevel parse(int v){

        for (UserLevel u : values()) {
            if (u.value == v) {
                return u;
            }
        }
        return UNKNOWN;
    }

    public String toString(){
        return Utilities.toFirstUpper(name());
    }

    public static boolean checkLevel(UserLevel customLevel, UserLevel messageLevel){
        return messageLevel.value >= customLevel.value;
    }
}
