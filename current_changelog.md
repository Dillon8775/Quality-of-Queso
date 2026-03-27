# Quality of Queso Version 1.6.2 (MC Fabric 26.1, NeoForge 26.1):

# Option Changes
- Removed the "Vertical Layout" option, and replaced it with a "Display Buttons" option, in which you can choose to display buttons "vertically" or "horizontally" on the screen.
- Added new separate options screen under "Management..." options, which allows you to set whether or not certain buttons should display at all.
- Moved "Button Sounds" option to "Management" options.
- You can now toggle to only auto-focus into certain search bars with the "Quick Search" option, unique to the recipe book search bar, and search bars that search containers/your inventory.
- Removed "Auto-Focus into Recipe Book" option, due to the new versatility of the "Quick Search" option.
- Added the option to change FOV effects for potions only if it's a consumable potion, so you can choose to only stop modification of FOV from beacon effects.
- Removed "Move Items If" option.
- The "Stacks" Item Count option now behaves like the "Remainder" item count option.
- Moved "Item Count" options to the main Quality of Queso main menu.

# Elytra Alarm Changes
- Holding any of the following items will NOT trigger the elytra alarm:
  - Mace
  - Wind charge
  - Ender pearl
  - Water bucket
  - Powder snow bucket
- This is because all of these items can negate fall damage entirely or partially, so there's no need to play the sound.  You can add more items to this list if you go into the client configuration file.
- You will find the items listed under an option called "blacklisted_elytra_alarm_items", and you can modify to your liking there.
- You can now configure the elytra alarm to only warn the user via armor status, and do not play the sound, by setting "Elytra Alarm" to "INDICATOR ONLY".

# Other Changes
- Lowered the default value of the minimum distance that the player has to be from a mob to play the "Mob Hit Ding" sound to 15 blocks (previously 20).
- The "Quick Equip" keybind is now unbounded by default.
- Removed the "transfer container" button from hoppers, droppers/dispensers, brewing stands and furnaces. You can still use the shortcut key to move items in these screens, however.

# Bug Fixes
- [Bug 11](https://github.com/Dillon8775/Quality-of-Queso/issues/11) - Fixed critical bug where the player can rarely get kicked when picking up an item.
- Fixed critical bug where configuration files can, although rare, equate to null. The game will now throw a "NullPointerException" when a configuration file is null. If one is null, you need to simply delete the file, and then relaunch your game.
- Fixed bug when "Quick Search" is OFF, attempting to focus into the inventory search field manually when previously focused into the recipe book search field will not fully focus unless pressing another time.
- Fixed bug where certain letters don't quick search under the right conditions.
- Fixed bug where "Prevent E from typing" always seems to be on.
- Fixed bug where the vertical layout for management button overlaps with status effects in the inventory screen.
- Fixed bug where excluding slots in inventory and opening the recipe book screen doesn't render anything right and throws a ConcurrentModificationException.
- Fixed bug where the "search transportable" button still plays a sound, even if "Button Sounds" are off.
- Fixed a small inconsistency with Multi-Server configs.
- Fixed a small grayout bug (I don't even know how I came across it it's such a small bug lol) with the inventory screen when excluding hotbar and holding shift.

# Technical Changes
- Moved some configuration options around in the config files, to keep it organized.