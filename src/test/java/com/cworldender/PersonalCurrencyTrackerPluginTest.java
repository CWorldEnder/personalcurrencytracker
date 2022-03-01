package com.cworldender;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PersonalCurrencyTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PersonalCurrencyTrackerPlugin.class);
		RuneLite.main(args);
	}
}