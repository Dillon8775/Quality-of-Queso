# Version 1.7.18

## Changes
- All hud elements (including armor status, item counter, locked hotbar slots, visual clcok, etc.) are now rendered separately. Please report any issues you may find on the [GitHub Bug Tracker](https://github.com/Dillon8775/Quality-of-Queso/issues).
- New option category called "GUI". Some previous Miscellaneous options have been moved to this option category.
- Moving highlighted items now plays the management sort sound.
- New names for Quality of Queso sounds:
    - "Management Succeeds" → "Item(s) moved"
    - "Management Rejects" → "Item move(s) rejected"
    - "Management Drops" → "Item(s) dropped"
    - "Management Sorts" → "Item(s) sorted"
- Added a "banned servers" option to warn the user which servers the mod shouldn't be used on. You can only configure this via the config file under the "accessibility" options.
  - The only banned server by default right now is hypixel. You can remove or add more if you'd like.
- Added a warning message to the Lock Inventory button if the mod is not installed server-side.
- Renamed "Generic Fov Percent Change" option → "Generic Fov Modifier".
- Tweaks to lang.
- Optimizations.

## Enhanced Durability Tooltips
- A new feature located in the Miscellaneous tab, where you can configure how durability tooltips look.
- You can choose to display a small dot with a color to represent item durability, and/or the following:
  - The exact damage value (ex. 46/500)
  - The percentage (ex. 95%)
- By default, the damage value is the only one enabled. You can configure it to however you'd like.
- Unfortunately, due to NeoForge's limitations, this feature is only available on Fabric.

## Item/Arrow Counter Changes
- The color for the arrow counter text when a held bow has infinity is now white instead of green.
- Pulling back a normal bow now displays a mini-bow next to the arrow counter, similar to the crossbow.
  - If the bow is fully charged, you will see a "ready-to-shoot" mini-bow texture instead of the normal mini-bow.
- Added new option called "Mini-Bows" to toggle the mini bow and crossbow from displaying.
- Held bows w/ infinity no longer count normal arrows to the arrow counter when using a special type of arrow.
- Held bows w/ infinity do not display colored highlighting on the arrow counter or render the warning indicator when using a special type of arrow.
- Tapping a crossbow to attempt to charge it but not fully charging will no longer display the arrow counter for # of seconds.
- Several bugs fixed (some not mentioned below) related to arrow x item counter.

## Bugs Fixed
- [#38:](https://github.com/Dillon8775/Quality-of-Queso/issues/38) Count All Arrows feature doesn't work following the support for firework rockets on crossbows w/ the item counter.
- [#39:](https://github.com/Dillon8775/Quality-of-Queso/issues/39) Using a projectile weapon and then switching to any item other than a projectile weapon only displays the count for the arrow type that was shot, not counting all arrows if desired.