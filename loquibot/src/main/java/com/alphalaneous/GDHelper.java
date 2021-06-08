package com.alphalaneous;

import java.io.*;
import java.util.Random;

public class GDHelper {

    static float platSpeed = 0;
    static String command = Defaults.saveDirectory + "\\GDBoard\\bin\\ChaosMode.exe";
    private static boolean isDead = true;
    private static boolean isInLevel = false;
    private static boolean isPractice = false;
    private static String levelName = "";
    private static String creator = "";
    private static long levelID = 0;
    private static int attemptCount = 0;
    private static int normalBest = 0;
    private static int practiceBest = 0;
    private static int totalJumps = 0;
    private static int totalAttempts = 0;
    private static float percent = 0;
    private static float posX = 0;
    private static float posY = 0;
    private static float levelLength = 0;
    private static int objects = 0;
    private static final String gamemode = "";
    private static float speed = 0;
    private static float size = 0;

    private static final String[] responses = {"I can't believe you died at %d%%!",
            "%d%%? That's it?",
            "How did you fail at %d%% of all places?",
            "Only %d%%...",
            "%d%%...!!! HOW???",
            "You can only die at %d%% so many times...",
            "%d%%, I think that's passing, right?",
            "GG-wait, you didn't beat it?",
            "Ugh, fine, I guess you are my little PogChamp, come here... actually, you only got %d%%, never mind.",
            "%d%%, that's like, not very good.",
            "Will the next attempt be the attempt you don't get %d%%?",
            "Next time, don't die at %d%%, okay?",
            "Look at you, dying at %d%%.",
            "I'm sorry, you died where?",
            "There's only so many ways to die and you've got them all.",
            "Tell me why, ain't nothing but another death",
            "More like Geometry TRASH!",
            "Give me one reason why I should accept this death as a win?",
            "Error, cannot compute, too many deaths.",
            "Shhhh, be quiet, the attempt counter is sleeping.",
            "Boo hoo, you only got %d%%, I can do better than that.",
            "Dang, %d%% is actually quite good...not",
            "Heck off with all these fails, I wanted to watch the Winner POV.",
            "I'm tired of this",
            "How do you do fellow dashers",
            "Knock knock! Who's there? ME! Telling you that you died at %d%%!",
            "There is one impostor among us, and I think it's you.",
            "Remember fall guys? You managed to die quicker than it.",
            "Is your grandma playing for you?",
            "Sometimes I wonder, \"Will you beat this level?\" Then I laugh to myself.",
            "L",
            "Not a W",
            "F in chat",
            "Sad moment",
            "According to Twitch statistics, only %d%% of you are subscribed with Prime Gaming.",
            "2 + 2 = %d",
            "No more dying at %d%%, I can't take it.",
            "Hey, can I tell you a secret? You're bad, only %d%%...",
            "tahc ni F",
            "I blame the level",
            "Good job on getting %d%%!",
            "Wow, you're amazing! You can do it!",
            "Hotel? Trivago.",
            "%d%%, ok.",
            "It pains me to say this but... you died lmao.",
            "A small price to pay for 100%%",
            "IDK what to even do about this anymore, all you do is die at %d%%",
            "get gud",
            "You clicked, right?",
            "You know you have to click...",
            "Forgot to click?",
            "What a noob",
            "You know you're playing Geometry Dash, not Death Simulator, right?",
            "Go back to Minecraft",
            "My cat can play better than you!",
            "Alphalaneous did it",
            "RobTop did it",
            "...",
            "The Impossible Game is better anyways",
            "Happy Birthday!!! NOT!",
            "Hi",
            "",
            "That wasn't very Pog",
            "That wasn't very PogChamp of you.",
            "<insert insult here>",
            "<insert compliment here> jk",
            "That wasn't very poggers of you",
            "Cat go Pop",
            "Cube go brrr",
            "Why'd you die again?",
            "weirdChamp",
            "bwomp.mp3",
            ".rick",
            "riPepperonis"
    };
    private static final String[] rickResponses = {
            "Never gonna give you up",
            "Never gonna let you down",
            "Never gonna run around and desert you",
            "Never gonna make you cry",
            "Never gonna say goodbye",
            "Never gonna tell a lie and hurt you"
    };
    private static final Runtime rt = Runtime.getRuntime();
    private static BufferedReader processOutput;
    private static BufferedWriter processInput;
    private static Process pr;
    private static int rickPos = 0;

    static {
        try {
            pr = rt.exec(command);
            processOutput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            processInput = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void start() {

        boolean checkedDead = false;

        while (true) {
            try {
                String line = processOutput.readLine();
                if (line != null) {
                    if (line.startsWith(">>")) {
                        String type = line.split(": ", 2)[0].replace(">> ", "");
                        String response = line.split(": ", 2)[1];

                        if (type.equalsIgnoreCase("IsDead")) {
                            if (response.equalsIgnoreCase("true")) {
                                isDead = true;
                                if (!checkedDead && isInLevel) {
                                    if (Settings.getSettings("SendDeathMessages").asBoolean()) {
                                        if (!isPractice) {
                                            Random rand = new Random();
                                            int n = rand.nextInt(responses.length);
                                            String responseA = responses[n];
                                            if (responseA.equalsIgnoreCase(".rick") || rickPos > 0) {
                                                Main.sendMessage("☠ | " + rickResponses[rickPos]);

                                                rickPos++;
                                                if (rickPos == rickResponses.length) {
                                                    rickPos = 0;
                                                }
                                            }
                                            if (rickPos == 0) {
                                                Main.sendMessage(Utilities.format("☠ | " + responseA, (int) Math.floor(percent)));
                                            }
                                        }
                                    }
                                    checkedDead = true;
                                }
                                //System.out.println("IsDead: true");
                            } else if (response.equalsIgnoreCase("false")) {
                                isDead = false;
                                //System.out.println("IsDead: false");
                                checkedDead = false;

                            }
                        }
                        if (type.equalsIgnoreCase("InLevel")) {
                            if (response.equalsIgnoreCase("true")) {
                                isInLevel = true;
                                //System.out.println("InLevel: true");
                            } else if (response.equalsIgnoreCase("false")) {
                                isInLevel = false;
                                //System.out.println("InLevel: false");
                            }
                        }
                        if (type.equalsIgnoreCase("IsPractice")) {
                            if (response.equalsIgnoreCase("true")) {
                                isPractice = true;
                            } else if (response.equalsIgnoreCase("false")) {
                                isPractice = false;
                            }
                        }
                        if (type.equalsIgnoreCase("Name")) {
                            levelName = response;
                        }
                        if (type.equalsIgnoreCase("Creator")) {
                            if (levelID <= 21 || levelID == 3001) {
                                creator = "RobTop";
                            } else {
                                creator = response;
                            }
                        }
                        if (type.equalsIgnoreCase("ID")) {
                            levelID = Long.parseLong(response);
                        }
                        if (type.equalsIgnoreCase("Current Attempt")) {
                            attemptCount = Integer.parseInt(response);
                        }
                        if (type.equalsIgnoreCase("Speed")) {
                            speed = Float.parseFloat(response);
                            //System.out.println(speed);
                            if (speed != 0) {
                                platSpeed = Math.abs(speed);
                            }
                        }
                        if (type.equalsIgnoreCase("Total Jumps")) {
                            totalJumps = Integer.parseInt(response);
                        }
                        if (type.equalsIgnoreCase("Total Attempts")) {
                            totalAttempts = Integer.parseInt(response);
                        }
                        if (type.equalsIgnoreCase("Percent")) {
                            try {
                                percent = Float.parseFloat(response);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        if (type.equalsIgnoreCase("X")) {
                            posX = Float.parseFloat(response);
                            if (KeyListener.usePlatformer && KeyListener.goingLeft) {
                                if (posX <= 0) {
                                    GDMod.runNew("speed", "0");
                                    GDMod.runNew("x", "0");

                                }
                            }
                        }
                        if (type.equalsIgnoreCase("Y")) {
                            posY = Float.parseFloat(response);
                        }
                        if (type.equalsIgnoreCase("Speed")) {
                            speed = Float.parseFloat(response);
                        }
                        if (type.equalsIgnoreCase("Size")) {
                            size = Float.parseFloat(response);
                        }
                        if (type.equalsIgnoreCase("Length")) {
                            levelLength = Float.parseFloat(response);
                        }
                        if (type.equalsIgnoreCase("Normal Best")) {
                            normalBest = Integer.parseInt(response);
                        }
                        if (type.equalsIgnoreCase("Practice Best")) {
                            practiceBest = Integer.parseInt(response);
                        }
                        if (type.equalsIgnoreCase("Object Count")) {
                            objects = Integer.parseInt(response);
                        }
                    }
                    //System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static void send(String command) {
        new Thread(() -> {
            try {
                //System.out.println(command);
                while (!(!isDead && isInLevel)) {
                    Thread.sleep(100);
                }
                processInput.write(command + "\n");
                processInput.flush();
            } catch (Exception ignored) {
            }
        }).start();
    }

    static void close() {
        try {
            processInput.write("exit 0\n");
            processInput.flush();
            pr.destroy();
            pr.destroyForcibly();
        } catch (Exception ignored) {
        }
    }

    public static void refresh() {
        close();
        try {
            pr = rt.exec(command);
            processOutput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            processInput = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentLevelName() {
        return levelName;
    }

    public static String getCurrentLevelCreator() {
        return creator;
    }

    public static long getCurrentLevelID() {
        return levelID;
    }

    public static boolean getCurrentDeathStatus() {
        return isDead;
    }

    public static boolean getCurrentInLevelStatus() {
        return isInLevel;
    }

    public static int getCurrentAttempts() {
        return attemptCount;
    }

    public static int getTotalJumps() {
        return totalJumps;
    }

    public static int getTotalAttempts() {
        return totalAttempts;
    }

    public static float getPercent() {
        return percent;
    }

    public static int getNormalBest() {
        return normalBest;
    }

    public static int getPracticeBest() {
        return practiceBest;
    }

    public static float getLength() {
        return levelLength;
    }

    public static int getObjectCount() {
        return objects;
    }

    public static float getX() {
        return posX;
    }

    public static float getY() {
        return posY;
    }

    public static float getSpeed() {
        return speed;
    }

    public static float getPlatformerSpeed() {
        return platSpeed;
    }

    public static String getGamemode() {
        return gamemode;
    }

    public static float getSize() {
        return size;
    }

    public static void setX(float x) {
        send("x " + x);
    }

    public static void setY(float y) {
        send("y " + y);
    }

    public static void setSpeed(float speed) {
        send("speed " + speed);
    }

    public static void setSize(float size) {
        send("size " + size);
    }
}
