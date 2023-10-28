package com.alphalaneous.Exceptions;

public class ColorNotFoundException extends RuntimeException {

    public ColorNotFoundException(String colorName){
        super("ThemeableColor: " + colorName + "not found");
    }

}
