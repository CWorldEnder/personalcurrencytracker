# Personal Currency Tracker [![](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/personalcurrencytracker)](https://runelite.net/plugin-hub) [![](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/rank/plugin/personalcurrencytracker)](https://runelite.net/plugin-hub) #
Tracks your personal made up currency; Can add/remove/check amount

# Changing the amount #
There are 2 ways to change the amount
1. Use the commands
   - ```::add x``` adds ```x``` coins to your balance
   - ```::remove x``` removes ```x``` coins from your balance
   - ```::set x``` sets your balance to ```x``` 
2. Change it manually through the Config Panel

# Checking the Amount #
1. Check the info panel if it is enabled
2. Use the command ```::count```

# Custom Coin Image #
To use a custom image, set the Coin Type option to `Custom` in the Config Panel, and place your image named ```coin.png``` in ```%userprofile%\.runelite``` on Windows or `~/.runelite/` on Linux/MacOS.

If this does not work, try renaming your coin image to just `coin`.

# Automatic Balance Updates

You can also have your balance automatically update by specified amounts

![](readme_img/automatic_balance_updates.png)

Notably:

- The casket opening rewards are ***only granted*** if the `Update on Clue Caskets` option is enabled (To make disabling them easy without setting them all to 0).
- In the miscellaneous section you can set rewards for *new collection log slots*, however this makes no distinction based on the category it belongs to in the collection log (*soon&trade;*).

---

If you have ideas/wishes for other balance update options, you can let me know by creating an issue :D .