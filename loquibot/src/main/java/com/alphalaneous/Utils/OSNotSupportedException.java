package com.alphalaneous.Utils;

public class OSNotSupportedException extends RuntimeException{

    public OSNotSupportedException(String errorMessage){
        super(errorMessage);
    }

}
