package com.alphalaneous.Components;

import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Interfaces.Function;
import com.alphalaneous.Interfaces.UserLevelChoice;
import com.alphalaneous.Window;

import java.awt.*;

public class UserLevelsMenu {



    public static void show(Rectangle bounds, UserLevelChoice choice){

        new Thread(() -> {
            ContextMenu menu = new ContextMenu();
            menu.setWidth(bounds.width);

            menu.addButton(createButton(UserLevel.EVERYONE, () -> choice.run(UserLevel.EVERYONE)));
            menu.addButton(createButton(UserLevel.SUBSCRIBER, () -> choice.run(UserLevel.SUBSCRIBER)));
            menu.addButton(createButton(UserLevel.VIP, () -> choice.run(UserLevel.VIP)));
            menu.addButton(createButton(UserLevel.MODERATOR, () -> choice.run(UserLevel.MODERATOR)));
            menu.addButton(createButton(UserLevel.OWNER, () -> choice.run(UserLevel.OWNER)));

            Window.addContextMenu(menu, new Point(bounds.x-8, bounds.y+2));
        }).start();
    }

    public static ContextButton createButton(UserLevel level, Function f){

        String levelText = level.toString();
        switch (levelText){
            case "Vip" :
                levelText = "VIP (Twitch)";
                break;
            case "Subscriber" :
                levelText = "Subscriber (Twitch)";
                break;
        }

        return new ContextButton(levelText, f);

    }

}
