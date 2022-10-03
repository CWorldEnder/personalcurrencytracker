package com.cworldender;

import com.google.inject.Provides;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.Skill;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
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

	// Custom Image
	private static final File CUSTOM_COIN_IMAGE = new File(RuneLite.RUNELITE_DIR, "coin.png");

	// Collection Log Reward
	private boolean notificationStarted;

	// NPC Kill Reward
	private HashMap<String, Integer> npcRewardsMap; // Stores NPC name -> kill reward with npc name in lowercase
	private final Set<Actor> taggedActors = new HashSet<>();

	// XP Reward
	private HashMap<Skill, Integer> skillXPs = new HashMap<>();
	private int ticksSinceLogin = 0; // Ticks since Login/Hop. Used to ignore StatChanges on login.

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
		updateNPCKillRewardMap(config.npcKillRewards());
	}

	@Override
	protected void shutDown()
	{
		infoBoxManager.removeInfoBox(balanceBox);
		notificationStarted = false;
		taggedActors.clear();
		log.info("Personal Currency Tracker stopped!");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals(config.GROUP))
		{
			updateInfobox(config.balance());
			updateNPCKillRewardMap(config.npcKillRewards());
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

		if(taggedActors.contains(actor)){
			incrementBalance(
				npcRewardsMap.getOrDefault(Objects.requireNonNull(actor.getName()).toLowerCase(), 0)
			);
			taggedActors.remove(actor);
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied){
		Actor actor = hitsplatApplied.getActor();

		if(actor != null && hitsplatApplied.getHitsplat().isMine() && npcRewardsMap.containsKey(actor.getName().toLowerCase())){
			taggedActors.add((NPC) actor);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		taggedActors.remove(npcDespawned.getNpc());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch(gameStateChanged.getGameState()){
			case HOPPING:
			case LOGGING_IN:
				taggedActors.clear();
				ticksSinceLogin = 0;
				skillXPs.clear();
		}
	}
	private void updateInfobox(int newCount)
	{
		if (balanceBox != null) { // Destroy the infopanel
			infoBoxManager.removeInfoBox(balanceBox);
		}
		if (config.infopanel()) // Recreate infopanel if it should be shown
		{
			createInfoBox();
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
	// TODO: Extract this into another file?
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
		switch (commandExecuted.getCommand().toLowerCase())
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
				break;
			case "clearxp":
				config.setXpSinceReward(0);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "XP since last reward set to 0.", null);
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		Skill skill = statChanged.getSkill();
		int xp = statChanged.getXp();
		if(ticksSinceLogin > 0)
		{	// This statChange is not due to a login/hop
			assert skillXPs.containsKey(skill);
			int deltaXP = xp - skillXPs.get(skill);
			config.setXpSinceReward(config.xpSinceReward() + deltaXP);
			// Update skill map for next iteration.
			skillXPs.put(skill, xp);
			log.info(deltaXP + " XP gained.");
			// Reward if applicable
			int xpSinceReward = config.xpSinceReward();
			int xpRewardInterval = config.xpRewardInterval();
			if(xpRewardInterval > 0 && xpSinceReward >= xpRewardInterval){
				int numRewards = xpSinceReward / xpRewardInterval; // Integer-Division
				int reward = numRewards * config.xpReward();
				incrementBalance(numRewards * config.xpReward());
				int newBalance = config.balance();
				int remainingXP = xpSinceReward % xpRewardInterval;
				config.setXpSinceReward(remainingXP);
			}
		} else { // Init/Update the Hashmap to calculate the delta XP's
			skillXPs.put(statChanged.getSkill(), statChanged.getXp());
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick){
		ticksSinceLogin++;
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

	private void updateNPCKillRewardMap(String configStr) throws IllegalArgumentException{
		/*
		 * configStr is a string of comma-separated 'npc-name#kill-reward' pairs.
		 * Turn this into hashmap mapping name to reward.
		 */

		HashMap<String, Integer> tempMap = new HashMap<>(); // We don't just want to write over the previous map in case items were deleted
		if(!configStr.equals("")){
			String[] pairs = configStr.split(",");
			for (String s : pairs)
			{
				if(s.contains("#")){
					String[] pair = s.trim().toLowerCase().split("#");
					if (pair.length != 2)
					{
						String msg = "PersonalCurrencyTracker: An (NPC, Kill-Reward) pair has more than 2 components!";
						log.error(msg);
						throw new IllegalArgumentException(msg);
					}
					else
					{
						tempMap.put(pair[0].trim(), Integer.parseInt(pair[1].trim()));
					}
				}
			}
		}
		npcRewardsMap = tempMap;
	}

	@Provides
	PersonalCurrencyTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PersonalCurrencyTrackerConfig.class);
	}
}



