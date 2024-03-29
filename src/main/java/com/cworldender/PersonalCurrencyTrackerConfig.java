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

	@ConfigItem(
		keyName = "currencyName",
		name = "Currency Name",
		description = "The name of your made up currency."
	)
	default String currencyName()
	{
		return "Coins";
	}

	@ConfigItem(
		keyName = "balance",
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
		keyName = "cointype",
		name = "Coin Type",
		description = "The type of coin to use as your Currency"
	)
	default CoinType cointype()
	{
		return CoinType.BLOOD_MONEY;
	}

	@ConfigItem(
		keyName = "infopanel",
		name = "Info Panel",
		description = "Show the info panel"
	)
	default boolean infopanel()
	{
		return true;
	}


	@ConfigSection(
		name = "Casket Rewards",
		description = "Set your coins to be automatically updated when you open caskets.",
		closedByDefault = true,
		position = 1
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

	@ConfigItem(
		keyName = "beginnerReward",
		name = "Beginner Reward",
		description = "Reward for opening a Beginner Casket",
		section = casketsSection,
		position = 0
	)
	default int beginnerReward()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "easyReward",
		name = "Easy Reward",
		description = "Reward for opening a Easy Casket",
		section = casketsSection,
		position = 1
	)
	default int easyReward()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "mediumReward",
		name = "Medium Reward",
		description = "Reward for opening a Medium Casket",
		section = casketsSection,
		position = 2
	)
	default int mediumReward()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "hardReward",
		name = "Hard Reward",
		description = "Reward for opening a Hard Casket",
		section = casketsSection,
		position = 3
	)
	default int hardReward()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "eliteReward",
		name = "Elite Reward",
		description = "Reward for opening a Elite Casket",
		section = casketsSection,
		position = 4
	)
	default int eliteReward()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "masterReward",
		name = "Master Reward",
		description = "Reward for opening a Master Casket",
		section = casketsSection,
		position = 5
	)
	default int masterReward()
	{
		return 0;
	}

	@ConfigSection(
		name = "Miscellaneous Rewards",
		description = "Set your coins to be automatically updated misc. events.",
		closedByDefault = true,
		position = 2
	)
	String miscEventsSection = "miscEventsSection";

	@ConfigItem(
		keyName = "collLogReward",
		name = "Collection Log Reward",
		description = "Reward for any new collection log slot filled.",
		section = miscEventsSection,
		position = 0
	)
	default int collLogReward(){
		return 0;
	}

	@Range(
		min = Integer.MIN_VALUE,
		max = Integer.MAX_VALUE
	)
	@ConfigItem(
		keyName = "deathReward",
		name = "Reward for Player Death",
		description = "Reward for dying (typically negative)",
		section = miscEventsSection,
		position = 1
	)
	default int deathReward(){
		return 0;
	}

	@ConfigItem(
		keyName = "npcKillRewards",
		name = "Reward for NPC Kills",
		description = "Reward for killing an NPC. Formatted as comma-separated list of npc-name#reward pairs.",
		section = miscEventsSection,
		position = 2
	)
	default String npcKillRewards(){ return ""; }

	@ConfigItem(
		keyName = "questCompleteReward",
		name = "Quest Completion Reward",
		description = "Amount of coins to reward for a quest completion.",
		section = miscEventsSection,
		position = 3
	)
	default int questCompleteReward(){ return 0; }

	@ConfigItem(
		keyName = "timeReward",
		name = "Time-Based Reward",
		description = "Amount of coins to reward per minute.",
		section = miscEventsSection,
		position = 4
	)
	default int timeReward(){ return 0; }

	@ConfigItem(
		keyName = "timeRewardInterval",
		name = "Time Reward Interval (Seconds)",
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
	void setXpSinceReward(int xpSinceReward);

	@ConfigSection(
		name = "XP Reward",
		description = "Config Items for XP Reward",
		closedByDefault = true,
		position = 3
	)
	String xpSection = "xpSection";

	@ConfigItem(
		keyName = "xpRewardInterval",
		name = "XP Reward Interval",
		description = "Reward Coins every x XP. If set to 0, no rewards will take place (But the accumulated XP will still be counted).",
		section = xpSection
	)
	default int xpRewardInterval(){ return 0; }

	@ConfigItem(
		keyName = "xpReward",
		name = "XP Reward",
		description = "The amount of coins to reward.",
		section = xpSection
	)
	default int xpReward(){ return 0; }

	@ConfigItem(
		keyName = "totalLevelReward",
		name = "Total Level Reward",
		description = "Amount of coins to reward for a gained total level.",
		section = xpSection
	)
	default int totalLeveLReward(){ return 0; }

	@ConfigItem(
		keyName = "skillLevelRewards",
		name = "Reward for Skill Levels Gained",
		description = "Reward for gaining a skill level. Formatted as comma-separated list of skill-name#reward pairs.",
		section = xpSection,
		position = 2
	)
	default String skillLevelRewards(){ return ""; }
}
