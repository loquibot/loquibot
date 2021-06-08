package com.alphalaneous;

import com.alphalaneous.Panels.*;
import com.alphalaneous.SettingsPanels.BlockedSettings;
import com.alphalaneous.SettingsPanels.RequestsSettings;
import com.alphalaneous.SettingsPanels.OutputSettings;
import com.alphalaneous.Windows.DialogBox;
import com.alphalaneous.Windows.SettingsWindow;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class RequestFunctions {

	private static final LinkedHashMap<LevelData, Integer> undoQueue = new LinkedHashMap<>(5) {
		protected boolean removeEldestEntry(Map.Entry<LevelData, Integer> eldest) {
			return size() > 5;
		}
	};
	private static boolean didUndo = false;

	private static boolean onCool = false;

	public static void openGDViewer(int pos){
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://gdviewers.tk/embed?levelid=" + Requests.levels.get(pos).getLevelData().id());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void openGDBrowser(int pos){
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://www.gdbrowser.com/" + Requests.levels.get(pos).getLevelData().id());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void skipFunction(){
		skipFunction(LevelButton.selectedID);
	}


	public static void skipFunction(int pos) {
		if (RequestsUtils.bwomp) {
			Thread bwompThread;
			bwompThread = new Thread(() -> {
				try {
					BufferedInputStream inp = new BufferedInputStream(BotHandler.class
							.getResource("/Resources/bwomp.mp3").openStream());
					Player mp3player = new Player(inp);
					mp3player.play();
				} catch (JavaLayerException | NullPointerException | IOException f) {
					f.printStackTrace();
					DialogBox.showDialogBox("Error!", f.toString(), "There was an error playing the sound!", new String[]{"OK"});

				}
			});
			bwompThread.start();
		}
		if (Main.programLoaded) {
			if (Requests.levels.size() != 0) {
				if (didUndo) {
					undoQueue.clear();
					didUndo = false;
				}
				undoQueue.put(Requests.levels.get(pos), pos);

				Requests.levels.get(pos).remove();

				if (Requests.levels.size() > 0) {
					StringSelection selection = new StringSelection(
							String.valueOf(Requests.levels.get(0).getLevelData().id()));
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(selection, selection);
				}
				if (pos == 0 && Requests.levels.size() > 0) {
					if (!RequestsSettings.nowPlayingOption) {

						if (!onCool) {
							new Thread(() -> {
								onCool = true;
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								onCool = false;
							}).start();
							new Thread(() -> {
								if (Requests.levels.get(0).getContainsImage()) {
									Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
											Requests.levels.get(0).getLevelData().name(),
											Requests.levels.get(0).getLevelData().id(),
											Requests.levels.get(0).getRequester()) + " " + Utilities.format("$IMAGE_HACK$"));
								} else if (Requests.levels.get(0).getContainsVulgar()) {
									Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
											Requests.levels.get(0).getLevelData().name(),
											Requests.levels.get(0).getLevelData().id(),
											Requests.levels.get(0).getRequester()) + " " + Utilities.format("$VULGAR_LANGUAGE$"));
								} else {
									Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
											Requests.levels.get(0).getLevelData().name(),
											Requests.levels.get(0).getLevelData().id(),
											Requests.levels.get(0).getRequester()));
								}
							}).start();
						}
					}
					containsBadStuffCheck();
				}

				RequestFunctions.saveFunction();
			}
			OutputSettings.setOutputStringFile(RequestsUtils.parseInfoString(OutputSettings.outputString, 0));

			new Thread(() -> {
				CommentsPanel.unloadComments(true);
				if (Requests.levels.size() != 0) {
					CommentsPanel.loadComments(0, false);
				}
			}).start();

			InfoPanel.refreshInfo();
			LevelsPanel.setName(Requests.levels.size());

		}
	}

	static void containsBadStuffCheck() {
		if (Requests.levels.get(0).getContainsImage()) {
			Utilities.notify("Image Hack", Requests.levels.get(0).getLevelData().name() + " (" + Requests.levels.get(0).getLevelData().id() + ") possibly contains the image hack!");
		} else if (Requests.levels.get(0).getContainsVulgar()) {
			Utilities.notify("Vulgar Language", Requests.levels.get(0).getLevelData().name() + " (" + Requests.levels.get(0).getLevelData().id() + ") contains vulgar language!");
		}
	}

	public static void undoFunction() {
		if (undoQueue.size() != 0) {
			didUndo = true;
			int selectPosition = LevelButton.selectedID;
			LevelData data = (LevelData) undoQueue.keySet().toArray()[undoQueue.size()-1];
			int position = (int) undoQueue.values().toArray()[undoQueue.size()-1];
			if(position >= Requests.levels.size()){
				position = Requests.levels.size();
			}
			Requests.levels.add(position, data);
			LevelsPanel.refreshButtons();
			if(data.getPosition() > selectPosition){
				LevelsPanel.setSelect(selectPosition);
			}
			else if (Requests.levels.size() == 1){
				LevelsPanel.setSelect(selectPosition);
				InfoPanel.refreshInfo();
				new Thread(() -> {
					CommentsPanel.unloadComments(true);
					CommentsPanel.loadComments(0, false);
				}).start();
			}
			else{
				LevelsPanel.setSelect(selectPosition+1);
				InfoPanel.refreshInfo();
				new Thread(() -> {
					CommentsPanel.unloadComments(true);
					CommentsPanel.loadComments(0, false, selectPosition+1);
				}).start();
			}
			undoQueue.remove(data);
		}
	}

	public static void randomFunction() {
		if (Main.programLoaded) {
			Random random = new Random();
			int num = 0;
			if (Requests.levels.size() != 0) {
				if (didUndo) {
					undoQueue.clear();
					didUndo = false;
				}
				undoQueue.put(Requests.levels.get(LevelButton.selectedID), LevelButton.selectedID);
				Requests.levels.get(LevelButton.selectedID).remove();

				RequestFunctions.saveFunction();

				CommentsPanel.unloadComments(true);

				if (Requests.levels.size() != 0) {
					while (true) {
						try {
							num = random.nextInt(Requests.levels.size());
							break;
						} catch (Exception ignored) {
						}
					}

					LevelsPanel.setSelect(num);

					new Thread(() -> CommentsPanel.loadComments(0, false)).start();
					StringSelection selection = new StringSelection(
							String.valueOf(Requests.levels.get(num).getLevelData().id()));
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(selection, selection);
					if (!RequestsSettings.nowPlayingOption) {
						int finalNum = num;
						new Thread(() -> Main.sendMessage(Utilities.format("🎮 | $NOW_PLAYING_MESSAGE$",
								Requests.levels.get(finalNum).getLevelData().name(),
								Requests.levels.get(finalNum).getLevelData().id(),
								Requests.levels.get(finalNum).getRequester()))).start();

					}
					if (Requests.levels.get(num).getContainsImage()) {
						Utilities.notify("Image Hack", Requests.levels.get(num).getLevelData().name() + " (" + Requests.levels.get(num).getLevelData().id() + ") possibly contains the image hack!");
					} else if (Requests.levels.get(num).getContainsVulgar()) {
						Utilities.notify("Vulgar Language", Requests.levels.get(num).getLevelData().name() + " (" + Requests.levels.get(num).getLevelData().id() + ") contains vulgar language!");
					}
				}
			}
			OutputSettings.setOutputStringFile(RequestsUtils.parseInfoString(OutputSettings.outputString, num));
			InfoPanel.refreshInfo();
			RequestFunctions.saveFunction();
			LevelsPanel.setName(Requests.levels.size());
		}
	}

	public static void copyFunction() {
		copyFunction(LevelButton.selectedID);
	}

	public static void copyFunction(int pos) {
		if (Requests.levels.size() != 0) {
			StringSelection selection = new StringSelection(
					String.valueOf(Requests.levels.get(pos).getLevelData().id()));
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		}
	}

	public static void saveFunction() {
		//public static void forceAdd(String name, String author, long levelID, String difficulty, boolean epic, boolean featured, int stars, String requester,
		// int gameVersion, int coins, String description, int likes, int downloads, String length, int levelVersion, int songID, String songName, String songAuthor, int objects, long original){
		/*try {
			Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\saved.txt");
			if (!Files.exists(file)) {
				Files.createFile(file);
			}
			FileWriter fooWriter = new FileWriter(file.toFile(), false);
			StringBuilder message = new StringBuilder();
			for (int i = 0; i < Requests.levels.size(); i++) {
				message.append(Requests.levels.get(i).getLevelData().name())
						.append(",").append(Requests.levels.get(i).getLevelData().creatorName().get())
						.append(",").append(Requests.levels.get(i).getLevelData().id())
						.append(",").append(Requests.levels.get(i).getDifficulty())
						.append(",").append(Requests.levels.get(i).getEpic())
						.append(",").append(Requests.levels.get(i).getFeatured())
						.append(",").append(Requests.levels.get(i).getLevelData().stars())
						.append(",").append(Requests.levels.get(i).getRequester())
						.append(",").append(Requests.levels.get(i).getVersion())
						.append(",").append(Requests.levels.get(i).getCoins())
						.append(",").append(new String(Base64.getEncoder().encode(Requests.levels.get(i).getDescription().getBytes())))
						.append(",").append(Requests.levels.get(i).getLikes())
						.append(",").append(Requests.levels.get(i).getDownloads())
						.append(",").append(Requests.levels.get(i).getLength())
						.append(",").append(Requests.levels.get(i).getLevelVersion())
						.append(",").append(Requests.levels.get(i).getSongID())
						.append(",").append(new String(Base64.getEncoder().encode(Requests.levels.get(i).getSongName().getBytes())))
						.append(",").append(Requests.levels.get(i).getSongAuthor())
						.append(",").append(Requests.levels.get(i).getObjects())
						.append(",").append(Requests.levels.get(i).getOriginal())
						.append(",").append(Requests.levels.get(i).getContainsVulgar())
						.append(",").append(Requests.levels.get(i).getContainsImage())
						.append(",").append(Requests.levels.get(i).getPassword())
						.append(",").append(Requests.levels.get(i).getUpload())
						.append(",").append(Requests.levels.get(i).getUpdate())
						.append(",").append(Requests.levels.get(i).getVerifiedCoins())
						.append("\n");
			}
			fooWriter.write(message.toString());
			fooWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	public static void blockFunction() {
		blockFunction(LevelButton.selectedID);
	}


	public static void blockFunction(int pos) {
		if (Main.programLoaded) {
			if (pos == 0 && Requests.levels.size() > 1) {
				StringSelection selection = new StringSelection(
						String.valueOf(Requests.levels.get(1).getLevelData().id()));
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
			if (Requests.levels.size() != 0) {

				new Thread(() -> {

					String option = DialogBox.showDialogBox("$BLOCK_ID_TITLE$", "$BLOCK_ID_INFO$", "$BLOCK_ID_SUBINFO$", new String[]{"$YES$", "$NO$"}, new Object[]{Requests.levels.get(pos).getLevelData().name(), Requests.levels.get(pos).getLevelData().id()});

					if (option.equalsIgnoreCase("YES")) {
						BlockedSettings.addButton(Requests.levels.get(pos).getLevelData().id());
						Path file = Paths.get(Defaults.saveDirectory + "\\GDBoard\\blocked.txt");

						try {
							if (!Files.exists(file)) {
								Files.createFile(file);
							}
							Files.write(
									file,
									(Requests.levels.get(pos).getLevelData().id() + "\n").getBytes(),
									StandardOpenOption.APPEND);
						} catch (IOException e1) {
							DialogBox.showDialogBox("Error!", e1.toString(), "There was an error writing to the file!", new String[]{"OK"});

						}
						Requests.levels.get(pos).remove();
						RequestFunctions.saveFunction();
						LevelsPanel.setSelect(0);
						new Thread(() -> {
							CommentsPanel.unloadComments(true);
							if (Requests.levels.size() > 0) {
								CommentsPanel.loadComments(0, false);
							}
						}).start();
						LevelsPanel.setName(Requests.levels.size());

					}
					InfoPanel.refreshInfo();
					SettingsWindow.run = true;
				}).start();
			}
		}
	}

	public static void clearFunction() {
		if (Main.programLoaded) {
			new Thread(() -> {
				String option = DialogBox.showDialogBox("$CLEAR_QUEUE_TITLE$", "$CLEAR_QUEUE_INFO$", "$CLEAR_QUEUE_SUBINFO$", new String[]{"$CLEAR_ALL$", "$CANCEL$"});
				if (option.equalsIgnoreCase("CLEAR_ALL")) {
					if (Requests.levels.size() != 0) {
						/*for (int i = 0; i < Requests.levels.size(); i++) {
							LevelsPanel.removeButton();
						}*/
						Requests.levels.clear();
						LevelsPanel.refreshButtons();
						undoQueue.clear();
						RequestFunctions.saveFunction();
						InfoPanel.refreshInfo();
						CommentsPanel.unloadComments(true);
					}
					LevelsPanel.setSelect(0);
					SettingsWindow.run = true;
				}
			}).start();

		}

	}

	public static void requestsToggleFunction() {
		if (Main.programLoaded) {
			if (Requests.requestsEnabled) {
				Requests.requestsEnabled = false;
				Main.sendMessage(Utilities.format("/me 🟥 | $REQUESTS_OFF_TOGGLE_MESSAGE$"));

			} else {
				Requests.requestsEnabled = true;
				Main.sendMessage(Utilities.format("/me 🟩 | $REQUESTS_ON_TOGGLE_MESSAGE$"));
			}
		}
	}
}
