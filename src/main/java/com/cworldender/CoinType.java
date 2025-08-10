package com.cworldender;

public enum CoinType
{
	COINS,
	BLOOD_MONEY,
	PURPLE_SWEETS,
	HALLOWED_MARK,
	MARK_OF_GRACE,
	COLLECTION_LOG,
	PIECES_OF_EIGHT,
	GOLDEN_NUGGET,
	PLATINUM_TOKEN,
	TOKKUL,
	WARRIOR_GUILD_TOKEN,
	CUSTOM;


	public int getItemId()
	{
		switch (this)
		{
			case BLOOD_MONEY:
				return 13307;
			case PURPLE_SWEETS:
				return 10476;
			case HALLOWED_MARK:
				return 24711;
			case MARK_OF_GRACE:
				return 11849;
			case COLLECTION_LOG:
				return 	22711;
			case PIECES_OF_EIGHT:
				return 8951;
			case GOLDEN_NUGGET:
				return 12012;
			case PLATINUM_TOKEN:
				return 13204;
			case TOKKUL:
				return 6529;
			case WARRIOR_GUILD_TOKEN:
				return 8851;
			case COINS:
			default: // Default to normal coins
				return 995;
		}
	}
}