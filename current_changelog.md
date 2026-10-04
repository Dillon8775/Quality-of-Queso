# Version 1.7.18

## Enhanced Durability Tooltips
- A new feature located under the GUI tab, where you can configure how durability tooltips look.
- You can choose to display a small dot with a color to represent item durability, and/or the following:
  - The exact damage value (ex. 46/500)
  - The percentage (ex. 95%)
- By default, the damage value is the only one enabled. You can configure it to however you'd like.
- Unfortunately, due to NeoForge's limitations, this feature is only available on Fabric.

## Changes
- All hud elements (including armor status, item counter, locked hotbar slots, visual clcok, etc.) are now rendered separately. Please report any issues you may find on the [GitHub Bug Tracker](https://github.com/Dillon8775/Quality-of-Queso/issues).
- New option category called "GUI". Some previous Miscellaneous options have been moved to this category.
  - Also moved Highlight Matching Items and Matching Items Color to this new category.
- Users can now scroll-move on hotbar slots even when excluding hotbar.
- Excluded hotbar slots now only grayout if the user is attempting to quick drop.
- Moving highlighted items now plays the management sort sound.
- Scroll moving now plays the management sort sound.
- New names for Quality of Queso sounds:
    - "Management Succeeds" → "Item(s) moved"
    - "Management Rejects" → "Item move(s) rejected"
    - "Management Drops" → "Item(s) dropped"
    - "Management Sorts" → "Item(s) sorted"
- Added a "banned servers" option to warn the user which servers the mod shouldn't be used on. You can only configure this via the config file under the "accessibility" options.
  - The only banned server by default right now is hypixel. You can remove or add more if you'd like.
- Added a warning message to the Lock Inventory button if the mod is not installed server-side.
- Added "ItemStackMixin" option.
- Renamed "Generic Fov Percent Change" option → "Generic Fov Modifier".
- Moved some textures around, tweaks to lang, and other optimizations.

## Item/Arrow Counter Changes
- Overall improved the item/arrow counter's functionality.
- Thrown and picked up items are now prioritized over the player's offhand item.
- The color for the arrow counter text when a held bow has infinity is now white instead of green.
- Pulling back a normal bow now displays a bow indicator next to the arrow counter, similar to the crossbow indicator introduced in a previous version.
  - If the bow is fully charged, you will see a ready-to-shoot bow texture instead of the normal bow.
- Added new option called Indicators to toggle the mini bow and crossbow from displaying.
- Bow and crossbow indicators render respectively to the arrow the player is using.
- A bundle now appears next to the counter if the count contains items inside transportable containers (toggleable with Indicators option).
- The arrow counter no longer counts arrows toward the counter that are inside transportable containers, but instead displays the bundle indicator to indicate that there are extra arrows in a transportable container.
- Display Total With Stacks no longer renders if a mini bow or mini bundle is currently displaying.
- Holding a bow with Infinity no longer counts normal arrows toward the arrow counter when using a special type of arrow.
  - In addition to this, a small infinity symbol in italic text will appear above the counter to indicate that the player still has Infinity to use after their special arrows.
- Holding a bow with Infinity does no longer displays colored highlighting or warning indicators when using a special type of arrow.
- Right-tapping a crossbow to attempt to charge it (but not fully charging it) no longer displays the arrow counter for # of seconds.
- Several bugs fixed (some not mentioned below) related to arrow x item counter.

## Bugs Fixed
- [#38:](https://github.com/Dillon8775/Quality-of-Queso/issues/38) Count All Arrows feature doesn't work following the support for firework rockets on crossbows w/ the item counter.
- [#39:](https://github.com/Dillon8775/Quality-of-Queso/issues/39) Using a projectile weapon and then switching to any item other than a projectile weapon only displays the count for the arrow type that was shot, not counting all arrows if desired.