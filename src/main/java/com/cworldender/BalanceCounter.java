package com.cworldender;

import lombok.Getter;

import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

public class BalanceCounter extends Counter
{
	@Getter
	private final String name;

	BalanceCounter(Plugin plugin, int count, String pName, BufferedImage image)
	{
		super(image, plugin, count);
		name = pName;
	}

	@Override
	public String getTooltip()
	{
		return name;
	}
}
