# Version 1.7.22

## Changes
- Updated to Minecraft 26.4.
- Now requires [DillonLib](https://modrinth.com/mod/dillon-lib) version 1.3 or greater.
- Due to how Minecraft renders it's sky, the Dark Disc option has been removed from the mod.
- Due to new DillonLib changes, the Quality of Queso main menu button is now placed in the new Dillon's Mods screen.
- The /itemframesearcher command has been renamed to /quesoitemframesearcher.


- Management buttons now have a blue focused texture.
- Revamped the default management button layout.
- You can now drop locked hotbar slots without disabling the Prevent Dropping option by holding ALT.


- Revamped Hud Positions screen.
- You can now modify the visual clock position in the Hud Positions screen.


- Completely revamped the mod's main menu screen.
- The Open World Directory and Blacklist Server buttons have been combined into one button, appearing in the Dillon's Mods screen and the Quality of Queso main menu. Additionally, the button will also play a open book sound if pressed in a world/server.
- If the [Essential]() mod is loaded, the Open Screenshots Folder button will play the Essential's screenshot sound.
- Removed the option to determine where the View Last Known Ender Chest button should appear. It now only appears in the Quality of Queso main menu.
- removed multi server configs
- fix lock overlay
- tooltip optimizations
- change to highlighted slots
- fixed things
- enable mod is now common option
- fixed client config loading on server
- optimized item frame search commands and added /help commands
- moved red armor tint to accessibility
- optimizations to options
- added dedicated debug screen entries config screen
- improved mod enabled option

- moved options around in file
- command updates
- increased item frame search radius to 512
- playtime entry
- bulk trade & craft into one button
- fixed mixin plugin not working


- Optimized tooltips.
- Backend optimizations to screens.

## Timer and Stopwatches
- Added a new command called /quesotimer where you can create in-game timers that run outside the game's tick.
- These alarms can act as an ordinary alarm for the user, to help them remember something, like to take a break, get ready for bed, or check the oven for their pizza.
  - When a timer reaches 5 minutes, 1 minute, or 30 seconds, a warning message is sent to the player, reminding them of their timer.
  - When a timer is up, a final message will be sent to the player, prompting them to dismiss the timer.
  - Until the timer is dismissed (or the player leaves their current world), a ding sound will play, like a normal alarm.
- Added another new command called /quesostopwatch where you can create in-game stopwatches that either outside or inside the game's tick.
- All active timers and stopwatches are displayed on the Hud via debug texts.
- Timers and stopwatches are discarded when the game is closed.

## Other New Options
- Added a new Accessibility option where you can determine which side the mod's debug text appears on (requires a restart to take effect).
- Added a new Accessibility option called Reposition Bulk Craft Button, which allows the user to determine if the bulk craft button should be placed next to the crafting grid, regardless of the Layout setting.
- Added a new Potion Effects debug entry, which shows all the player's active status effects.
- Removed the Visual Time debug entry.
- Removed some mixin options.
- Enabling the Force Anti-Rage Quit option now disables Minecraft's Quit Shortcuts option (to prevent the user from ALT-F4ing the game).

## Bugs Fixed
- [Bug #45:](https://github.com/Dillon8775/Quality-of-Queso/issues/45) Save search text doesn't work following *unknown* update.