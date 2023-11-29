package com.alphalaneous.Interactive.Commands;

import com.alphalaneous.ChatBot.ChatMessage;
import com.alphalaneous.Enums.UserLevel;
import com.alphalaneous.Interactive.CustomData;
import com.alphalaneous.Utilities.Utilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CommandHandlerTest {

    @ParameterizedTest
    @MethodSource("provideChatData")
    void testReplaceBetweenParentheses(ChatMessage message, String text, CustomData data, String expected) {

        assertEquals(expected, CommandHandler.replaceBetweenParentheses(message, text, data, null));

    }

    @Test
    void testIsCooldown() {

        CommandData data = new CommandData("test");
        data.setCooldown(1);
        CommandHandler.startCooldown(data);
        Utilities.sleep(2 * 1000);
        assertFalse(CommandHandler.isCooldown(data));

        CommandData data2 = new CommandData("test");
        data2.setCooldown(2);
        CommandHandler.startCooldown(data2);
        Utilities.sleep(1000);
        assertTrue(CommandHandler.isCooldown(data2));

    }

    @Test
    void testCounter() {

        CommandData data = new CommandData("test");
        data.setMessage("$(count)");

        QuickChatMessage chatMessage = new QuickChatMessage("Hello",
                UserLevel.EVERYONE, 0);

        CommandHandler.replaceBetweenParentheses(chatMessage, data.getMessage(), data, null);

        assertEquals(1, data.getCounter());


        CommandData data2 = new CommandData("test");
        data2.setMessage("$(count 23)");

        QuickChatMessage chatMessage2 = new QuickChatMessage("Hello",
                UserLevel.EVERYONE, 0);

        CommandHandler.replaceBetweenParentheses(chatMessage2, data2.getMessage(), data2, null);
        CommandHandler.replaceBetweenParentheses(chatMessage2, data2.getMessage(), data2, null);

        assertEquals(46, data2.getCounter());

        data2.setMessage("$(count -48)");

        CommandHandler.replaceBetweenParentheses(chatMessage2, data2.getMessage(), data2, null);

        assertEquals(-2, data2.getCounter());
    }

    @ParameterizedTest
    @MethodSource("provideUserLevelData")
    void checkUserLevel(ChatMessage message, CommandData data, boolean expected) {

        assertEquals(expected, CommandHandler.checkUserLevel(data, message));

    }

    public static Stream<Arguments> provideUserLevelData(){

        return Stream.of(
            Arguments.of(createTestMessage(UserLevel.EVERYONE), createTestCommand(UserLevel.EVERYONE), true),
            Arguments.of(createTestMessage(UserLevel.EVERYONE), createTestCommand(UserLevel.SUBSCRIBER), false),
            Arguments.of(createTestMessage(UserLevel.EVERYONE), createTestCommand(UserLevel.VIP), false),
            Arguments.of(createTestMessage(UserLevel.EVERYONE), createTestCommand(UserLevel.MODERATOR), false),
            Arguments.of(createTestMessage(UserLevel.EVERYONE), createTestCommand(UserLevel.OWNER), false),

            Arguments.of(createTestMessage(UserLevel.SUBSCRIBER), createTestCommand(UserLevel.EVERYONE), true),
            Arguments.of(createTestMessage(UserLevel.SUBSCRIBER), createTestCommand(UserLevel.SUBSCRIBER), true),
            Arguments.of(createTestMessage(UserLevel.SUBSCRIBER), createTestCommand(UserLevel.VIP), false),
            Arguments.of(createTestMessage(UserLevel.SUBSCRIBER), createTestCommand(UserLevel.MODERATOR), false),
            Arguments.of(createTestMessage(UserLevel.SUBSCRIBER), createTestCommand(UserLevel.OWNER), false),

            Arguments.of(createTestMessage(UserLevel.VIP), createTestCommand(UserLevel.EVERYONE), true),
            Arguments.of(createTestMessage(UserLevel.VIP), createTestCommand(UserLevel.SUBSCRIBER), true),
            Arguments.of(createTestMessage(UserLevel.VIP), createTestCommand(UserLevel.VIP), true),
            Arguments.of(createTestMessage(UserLevel.VIP), createTestCommand(UserLevel.MODERATOR), false),
            Arguments.of(createTestMessage(UserLevel.VIP), createTestCommand(UserLevel.OWNER), false),

            Arguments.of(createTestMessage(UserLevel.MODERATOR), createTestCommand(UserLevel.EVERYONE), true),
            Arguments.of(createTestMessage(UserLevel.MODERATOR), createTestCommand(UserLevel.SUBSCRIBER), true),
            Arguments.of(createTestMessage(UserLevel.MODERATOR), createTestCommand(UserLevel.VIP), true),
            Arguments.of(createTestMessage(UserLevel.MODERATOR), createTestCommand(UserLevel.MODERATOR), true),
            Arguments.of(createTestMessage(UserLevel.MODERATOR), createTestCommand(UserLevel.OWNER), false)

        );
    }

    public static QuickChatMessage createTestMessage(UserLevel level){

        return new QuickChatMessage("", level, 0);
    }

    public static CommandData createTestCommand(UserLevel level){

        return new CommandData("test"){{
            setUserLevel(level);
        }};
    }

    public static Stream<Arguments> provideChatData(){
        return Stream.of(
            Arguments.of(null, null, null, ""),
            createActionArgument(new QuickChatMessage("This is a",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(query) Test"), "is a Test"), //query removes the first word, assumed to be a command, use $(message) to not do that
            createActionArgument(new QuickChatMessage("This is a",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(message) Test"), "This is a Test"),
            createActionArgument(new QuickChatMessage("This is a",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(suppress $(message)) Test"), " Test"),
            createActionArgument(new QuickChatMessage("This is a",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(doesnt-exist) Test"), "$(doesnt-exist) Test"),
            createActionArgument(new QuickChatMessage("Hey",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(touser) Test"), "username1 Test"),
            createActionArgument(new QuickChatMessage("Hey person",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(touser) Test"), "person Test"),
            createActionArgument(new QuickChatMessage("Hey person",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(user)"), "username1"),
            createActionArgument(new QuickChatMessage("Hey person",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(displayname)"), "UserName1"),
            createActionArgument(new QuickChatMessage("!add 2 2",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(eval $(arg 1)+$(arg 2))"), "4"),
            createActionArgument(new QuickChatMessage("!ul",
                    UserLevel.EVERYONE, 0),
                    new FakeCustomData("$(userlevel)"), "Everyone"),
            createActionArgument(new QuickChatMessage("!ul",
                    UserLevel.MODERATOR, 0),
                    new FakeCustomData("$(userlevel)"), "Moderator"),
            createActionArgument(new QuickChatMessage("!ul",
                    UserLevel.SUBSCRIBER, 0),
                    new FakeCustomData("$(userlevel)"), "Subscriber"),
            createActionArgument(new QuickChatMessage("!ul",
                    UserLevel.VIP, 0),
                    new FakeCustomData("$(userlevel)"), "Vip"),
            createActionArgument(new QuickChatMessage("",
                    UserLevel.VIP, 0),
                    new FakeCustomData("$(test"), "Malformed Command String")
        );
    }

    public static Arguments createActionArgument(ChatMessage message, CustomData data, String expected){

        return Arguments.of(message, data.getMessage(), data, expected);
    }
}