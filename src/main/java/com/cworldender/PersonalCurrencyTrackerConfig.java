package com.cworldender;

import java.time.Duration;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(PersonalCurrencyTrackerConfig.GROUP)
public interface PersonalCurrencyTrackerConfig extends Config
{
	String GROUP = "personalcurrencytracker";
	String BALANCE_KEY = "balance";
	String NPC_KILL_REWARD_KEY = "npcKillRewards";
	String SKILL_LEVEL_REWARD_KEY = "skillLevelRewards";
	String SHOW_INFOPANEL_KEY = "infopanel";
	String COIN_TYPE_KEY = "cointype";
	String CURRENCY_NAME_KEY = "currencyName";

	@ConfigItem(
		keyName = CURRENCY_NAME_KEY,
		name = "Currency Name",
		description = "The name of your made up currency."
	)
	default String currencyName()
	{
		return "Coins";
	}

	@ConfigItem(
		keyName = BALANCE_KEY,
		name = "Balance",
		description = "How much money do you have?"
	)
	default int balance()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "balance",
		name = "Balance",
		description = "How much money do you have?"
	)
	void setBalance(int balance);

	@ConfigItem(
		keyName = COIN_TYPE_KEY,
		name = "Coin Type",
		description = "The type of coin to use as your Currency"
	)
	default CoinType cointype()
	{
		return CoinType.BLOOD_MONEY;
	}

	@ConfigItem(
		keyName = SHOW_INFOPANEL_KEY,
		name = "Info Panel",
		description = "Show the info panel"
	)
	default boolean infopanel()
	{
		return true;
	}


	/*
	 * Clue Rewards Section
	 */
	@ConfigSection(
		name = "Casket Rewards",
		description = "Set your coins to be automatically updated when you open caskets.",
		closedByDefault = true,
		position = 0
	)
	String casketsSection = "casketsSection";

	@ConfigItem(
		keyName = "useCaskets",
		name = "Update on Clue Caskets",
		description = "Update Currency on Clue Casket Opens",
		section = casketsSection,
		position = -2
	)
	default boolean useCaskets()
	{
		return false;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "beginnerReward",
		name = "Beginner Casket",
		description = "Reward for opening a Beginner Casket",
		section = casketsSection,
		position = 0
	)
	default int beginnerReward()
	{
		return 0;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "easyReward",
		name = "Easy Casket",
		description = "Reward for opening a Easy Casket",
		section = casketsSection,
		position = 1
	)
	default int easyReward()
	{
		return 0;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "mediumReward",
		name = "Medium Casket",
		description = "Reward for opening a Medium Casket",
		section = casketsSection,
		position = 2
	)
	default int mediumReward()
	{
		return 0;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "hardReward",
		name = "Hard Casket",
		description = "Reward for opening a Hard Casket",
		section = casketsSection,
		position = 3
	)
	default int hardReward()
	{
		return 0;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "eliteReward",
		name = "Elite Casket",
		description = "Reward for opening a Elite Casket",
		section = casketsSection,
		position = 4
	)
	default int eliteReward()
	{
		return 0;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "masterReward",
		name = "Master Casket",
		description = "Reward for opening a Master Casket",
		section = casketsSection,
		position = 5
	)
	default int masterReward()
	{
		return 0;
	}


	/*
	 * XP Rewards Section
	 */
	@ConfigSection(
		name = "XP Rewards",
		description = "Config Items for XP Reward",
		closedByDefault = true,
		position = 1
	)
	String xpSection = "xpSection";

	@ConfigItem(
		keyName = "xpSinceReward",
		name = "XP Elapsed since last Reward. Do not touch",
		description = "",
		hidden = true
	)
	default int xpSinceReward(){ return 0; }

	@ConfigItem(
		keyName = "xpSinceReward",
		name = "XP Elapsed since last Reward. Do not touch",
		description = "",
		hidden = true
	)
	void setXpSinceReward(long xpSinceReward);

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "totalLevelReward",
		name = "Total Level",
		description = "Amount of coins to reward for a gained total level.",
		section = xpSection,
		position = 0
	)
	default int totalLeveLReward(){ return 0; }

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "xpReward",
		name = "XP Reward",
		description = "The amount of coins to reward.",
		section = xpSection,
		position = 1
	)
	default int xpReward(){ return 0; }

	@ConfigItem(
		keyName = "xpRewardInterval",
		name = "XP Reward Interval",
		description = "Reward Coins every x XP. If set to 0, no rewards will take place (But the accumulated XP will still be counted).",
		section = xpSection,
		position = 2
	)
	default int xpRewardInterval(){ return 0; }

	@ConfigItem(
		keyName = SKILL_LEVEL_REWARD_KEY,
		name = "Reward for Skill Levels Gained",
		description = "Reward for gaining a skill level. Formatted as comma-separated list of skill-name#reward pairs.",
		section = xpSection,
		position = 3
	)
	default String skillLevelRewards(){ return ""; }

	/*
	 * Miscellaneous Rewards Section
	 */
	@ConfigSection(
		name = "Miscellaneous Rewards",
		description = "Set your coins to be automatically updated misc. events.",
		closedByDefault = true,
		position = 2
	)
	String miscEventsSection = "miscEventsSection";

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "collLogReward",
		name = "Collection Log",
		description = "Reward for any new collection log slot filled.",
		section = miscEventsSection,
		position = 0
	)
	default int collLogReward(){
		return 0;
	}

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "deathReward",
		name = "Death",
		description = "Reward for dying (typically negative)",
		section = miscEventsSection,
		position = 1
	)
	default int deathReward(){
		return 0;
	}

	@ConfigItem(
		keyName = NPC_KILL_REWARD_KEY,
		name = "NPC Kills",
		description = "Reward for killing an NPC. Formatted as comma-separated list of npc-name#reward pairs.",
		section = miscEventsSection,
		position = 2
	)
	default String npcKillRewards(){ return ""; }

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "questCompleteReward",
		name = "Quest Completion",
		description = "Amount of coins to reward for a quest completion.",
		section = miscEventsSection,
		position = 3
	)
	default int questCompleteReward(){ return 0; }

	@Range(min = Integer.MIN_VALUE)
	@ConfigItem(
		keyName = "timeReward",
		name = "Playtime",
		description = "Amount of coins to reward per minute.",
		section = miscEventsSection,
		position = 4
	)
	default int timeReward(){ return 0; }

	@ConfigItem(
		keyName = "timeRewardInterval",
		name = "Playtime Interval (Seconds)",
		description = "Interval in which to apply the time-based reward (Seconds).",
		section = miscEventsSection,
		position = 5
	)
	default int timeRewardInterval(){ return 0; }

	@ConfigItem(
		keyName = "timeSinceLastTimeReward",
		name = "Time Since Last Time Reward",
		description = "Accumulated login time since the last time reward.",
		hidden = true
	)
	default Duration durationSinceLastTimeReward(){ return Duration.ZERO; }

	@ConfigItem(
		keyName = "timeSinceLastTimeReward",
		name = "Time Since Last Time Reward",
		description = "Accumulated login time since the last time reward.",
		hidden = true
	)
	void setDurationSinceLastTimeReward(Duration dur);
}
