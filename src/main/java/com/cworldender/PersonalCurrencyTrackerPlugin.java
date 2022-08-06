package com.cworldender;

import com.google.inject.Provides;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Actor;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import net.runelite.client.util.Text;


@Slf4j
@PluginDescriptor(
	name = "Personal Currency Tracker"
)
public class PersonalCurrencyTrackerPlugin extends Plugin
{
	private static final File CUSTOM_COIN_IMAGE = new File(RuneLite.RUNELITE_DIR, "coin.png");
	private boolean notificationStarted;

	@Inject
	private Client client;

	@Inject
	private PersonalCurrencyTrackerConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;
	private BalanceCounter balanceBox;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp()
	{
		log.info("Personal Currency Tracker started!");
		createInfoBox();
	}

	@Override
	protected void shutDown()
	{
		infoBoxManager.removeInfoBox(balanceBox);
		notificationStarted = false;
		log.info("Personal Currency Tracker stopped!");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals("personalcurrencytracker"))
		{
			updateInfobox(config.balance());
		}
	}

	private void incrementBalance(int amount)
	{
		config.setBalance(config.balance() + amount);
	}

	private void setBalance(int amount)
	{
		config.setBalance(amount);
	}

	@Subscribe
	protected void onChatMessage(ChatMessage message)
	{
		// A large portion of this is very heavily inspired by the screenshot plugin
		// https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/screenshot/ScreenshotPlugin.java
		if (message.getType() != ChatMessageType.GAMEMESSAGE
			&& message.getType() != ChatMessageType.SPAM
			&& message.getType() != ChatMessageType.TRADE
			&& message.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION)
		{
			return;
		}

		String msg = message.getMessage();

		// Clue completion
		if (
			config.useCaskets()
			&& msg.contains("You have completed") && msg.contains("Treasure")
		)
		{
			Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+)");
			Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(msg));
			if (m.find()){
				String clueType = msg.substring(msg.lastIndexOf(m.group()) + m.group().length() + 1, msg.indexOf("Treasure") - 1);

				// Honestly, there has got to be a better way
				switch(clueType.toLowerCase()){
					case "beginner":
						incrementBalance(config.beginnerReward());
						break;
					case "easy":
						incrementBalance(config.easyReward());
						break;
					case "medium":
						incrementBalance(config.mediumReward());
						break;
					case "hard":
						incrementBalance(config.hardReward());
						break;
					case "elite":
						incrementBalance(config.eliteReward());
						break;
					case "master":
						incrementBalance(config.masterReward());
						break;
					default:
						break;
				}
			} else if (msg.startsWith("New item added to your collection log:") && config.collLogReward() != 0)
			{
				// String[] itemname = msg.split("New item added to your collection log: ")[1];
				// New Collection log slot --> Update balance
				incrementBalance(config.collLogReward());
			}
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired scriptPreFired)
	{
		// Adapted from Screenshot plugin
		// https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/screenshot/ScreenshotPlugin.java
		switch (scriptPreFired.getScriptId())
		{
			case ScriptID.NOTIFICATION_START:
				notificationStarted = true;
				break;
			case ScriptID.NOTIFICATION_DELAY:
				if (!notificationStarted)
				{
					return;
				}
				String topText = client.getVarcStrValue(VarClientStr.NOTIFICATION_TOP_TEXT);
				// String bottomText = client.getVarcStrValue(VarClientStr.NOTIFICATION_BOTTOM_TEXT);
				if (topText.equalsIgnoreCase("Collection log") && config.collLogReward() != 0)
				{
					// String entry = Text.removeTags(bottomText).substring("New item:".length());
					incrementBalance(config.collLogReward());
				}
				// if (topText.equalsIgnoreCase("Combat Task Completed!") && config.combatTaskReward() != 0 && client.getVarbitValue(Varbits.COMBAT_ACHIEVEMENTS_POPUP) == 0)
				// {
				// 	//String entry = Text.removeTags(bottomText).substring("Task Completed: ".length());
				// 	incrementBalance(config.combatTaskReward());
				// }
				notificationStarted = false;
				break;
		}
	}


	@Subscribe
	public void onActorDeath(ActorDeath actorDeath)
	{
		Actor actor = actorDeath.getActor();
		if (actor instanceof Player)
		{
			Player player = (Player) actor;
			if (player == client.getLocalPlayer())
			{
				incrementBalance(config.deathReward());
			}
		}
	}

	private void updateInfobox(int newCount)
	{
		if (balanceBox != null)
		{
//			balanceBox.setCount(config.balance());
			infoBoxManager.removeInfoBox(balanceBox);
			if (config.infopanel())
			{ // Only recreate the infobox if it should be shown
				createInfoBox();
			}
		}
	}

	private void createInfoBox()
	{
		if (config.infopanel())
		{
			final BufferedImage image = getCoinImage();
			balanceBox = new BalanceCounter(this, config.balance(), config.currencyName(), image);
			infoBoxManager.addInfoBox(balanceBox);
		}
	}

	private BufferedImage getCoinImage()
	{
		if (config.cointype() == CoinType.CUSTOM)
		{
			//Read in an image
			try
			{
				BufferedImage image;
				synchronized (ImageIO.class)
				{
					image = ImageIO.read(CUSTOM_COIN_IMAGE);
				}
				return image;
			}
			catch (IOException e)
			{
				log.error("error loading custom coin image", e);
				//Will then fall into the second return statement, where by default then the id for normal coins is returned
			}

		}

		return itemManager.getImage(getItemIdForCoin(config.cointype()), config.balance() * 100, false);
	}

	private int getItemIdForCoin(CoinType coin)
	{
		switch (coin)
		{
			case COINS:
				return 995;
			case BLOOD_MONEY:
				return 13307;
			case PURPLE_SWEETS:
				return 10476;
			case HALLOWED_MARK:
				return 24711;
			case MARK_OF_GRACE:
				return 11849;
			default:
				// Default to normal coins
				return getItemIdForCoin(CoinType.COINS);
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		IntegerArgument arg = getIntegerFromCommandArguments(commandExecuted.getArguments());
		switch (commandExecuted.getCommand())
		{
			case "count":
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You have " + config.balance() + " " + config.currencyName(), null);
				break;
			case "add":
				if (arg.isValid())
				{
					int increment = arg.getValue();
					incrementBalance(increment);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You now have " + config.balance() + " " + config.currencyName(), null);
				}
				break;
			case "subtract":
			case "remove":
				if (arg.isValid())
				{
					int sub = Integer.parseInt(commandExecuted.getArguments()[0]);
					incrementBalance(-sub);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You now have " + config.balance() + " " + config.currencyName(), null);
				}
				break;
			case "set":
				if (arg.isValid())
				{
					int amount = Integer.parseInt(commandExecuted.getArguments()[0]);
					setBalance(amount);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You now have " + config.balance() + " " + config.currencyName(), null);
				}
			default:
				break;
		}
	}

	private IntegerArgument getIntegerFromCommandArguments(String[] args)
	{
		if (args.length == 0)
		{
			return new IntegerArgument(0, false);
		}
		String firstArg = args[0];
		try
		{
			return new IntegerArgument(Integer.parseInt(firstArg), true);
		}
		catch (NumberFormatException e)
		{
			log.error("Malformed Argument for a PersonalCurrencyTracker Command");
			return new IntegerArgument(0, false);
		}
	}

	private class IntegerArgument
	{
		private final boolean valid;
		private final int value;

		public IntegerArgument(int val, boolean isValid)
		{
			valid = isValid;
			value = val;
		}

		public boolean isValid()
		{
			return valid;
		}

		public int getValue()
		{
			return value;
		}
	}

	@Provides
	PersonalCurrencyTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PersonalCurrencyTrackerConfig.class);
	}
}



