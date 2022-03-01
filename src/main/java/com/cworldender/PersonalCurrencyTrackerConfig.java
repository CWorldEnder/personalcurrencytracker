package com.cworldender;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("personalcurrencytracker")
public interface PersonalCurrencyTrackerConfig extends Config
{
	@ConfigItem(
		keyName = "currencyName",
		name = "Currency Name",
		description = "The name of your made up currency."
	)
	default String currencyName()
	{
		return "ChunkCoin";
	}

	@ConfigItem(
			keyName = "balance",
			name = "Balance",
			description = "How much money do you have?"
	)
	default int balance() { return 0;}

	@ConfigItem(
			keyName = "balance",
			name = "Balance",
			description = "How much money do you have?"
	)
	void setBalance(int balance);

	@ConfigItem(
			keyName = "cointype",
			name = "Coin Type",
			description = "The type of coin to use as your Currency"
	)
	default CoinType cointype() { return CoinType.BLOOD_MONEY; }

	@ConfigItem(
			keyName = "infopanel",
			name = "Info Panel",
			description = "Show the info panel"
	)
	default boolean infopanel() { return true; }

// Removed until I figure out how to find out the different images for one type of item. Ideally getting an array of all different coin stacks for one type of coin, then just figure out which image to choose
//	@ConfigItem(
//			keyName = "thresholds",
//			name = "Coin Stack Change Thresholds",
//			description = "Thresholds for changing the coin stack image as comma-seperated list of integers (Not applicable if Custom Image is selected)"
//	)
//	default String thresholds(){ return "1,2,3,4,5,10,15,30,100";}
}
