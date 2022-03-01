package com.cworldender;

import com.google.gson.Gson;
import com.google.inject.Provides;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;


@Slf4j
@PluginDescriptor(
		name = "Personal Currency Tracker"
)
public class PersonalCurrencyTrackerPlugin extends Plugin
{
	private static final File CUSTOM_COIN_IMAGE = new File(RuneLite.RUNELITE_DIR, "coin.png");

	@Inject
	private Client client;

	@Inject
	private PersonalCurrencyTrackerConfig config;

	@Inject private InfoBoxManager infoBoxManager;
	private BalanceCounter balanceBox;

	@Inject private ItemManager itemManager;

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
		log.info("Personal Currency Tracker stopped!");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged){
		if(configChanged.getGroup().equals("personalcurrencytracker")){
			updateInfobox(config.balance());
		}
	}


	private void updateInfobox(int newCount){
		if (balanceBox != null) {
//			balanceBox.setCount(config.balance());
			infoBoxManager.removeInfoBox(balanceBox);
			if(config.infopanel()){ // Only recreate the infobox if it should be shown
				createInfoBox();
			}
		}
	}

	private void createInfoBox(){
		if(config.infopanel()){
			final BufferedImage image = getCoinImage(250);
			balanceBox = new BalanceCounter(this, config.balance(), config.currencyName(), image);
			infoBoxManager.addInfoBox(balanceBox);
		}
	}

	private BufferedImage getCoinImage(int count){
		if (config.cointype() == CoinType.CUSTOM){
			//Read in an image
			try {
				BufferedImage image;
				synchronized (ImageIO.class) {
					image = ImageIO.read(CUSTOM_COIN_IMAGE);
				}
				return image;
			} catch (IOException e){
					log.error("error loading custom coin image", e);
					//Will then fall into the second return statement, where by default then the id for normal coins is returned
			}

		}

		return itemManager.getImage(getItemIdForCoin(config.cointype()), config.balance()*100, false);
	}

	private int getItemIdForCoin(CoinType coin){
		switch (coin){
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
	public void onCommandExecuted(CommandExecuted commandExecuted){
		switch (commandExecuted.getCommand()){
			case "count":
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You have "+ config.balance() + " " + config.currencyName(), null);
				break;
			case "add":
				int increment = Integer.parseInt(commandExecuted.getArguments()[0]);
				config.setBalance(config.balance() + increment);

				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You now have "+ config.balance() + " " + config.currencyName(), null);
				break;
			case "remove":
				int sub = Integer.parseInt(commandExecuted.getArguments()[0]);
				config.setBalance(config.balance() - sub);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You now have "+ config.balance() + " " + config.currencyName(), null);
				break;
			default:
				break;
		}
	}

	@Provides
	PersonalCurrencyTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PersonalCurrencyTrackerConfig.class);
	}
}



