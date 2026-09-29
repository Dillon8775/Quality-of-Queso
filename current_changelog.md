# Version 1.7.18

## Changes
- All hud elements (including armor status, item counter, locked hotbar slots, visual clcok, etc.) are now rendered separately. Please report any issues you may find on the [GitHub Bug Tracker](https://github.com/Dillon8775/Quality-of-Queso/issues).
- Moving highlighted items now plays the management sort sound.
- New names for Quality of Queso sounds:
    - "Management Succeeds" → "Item(s) moved"
    - "Management Rejects" → "Item move(s) rejected"
    - "Management Drops" → "Item(s) dropped"
    - "Management Sorts" → "Item(s) sorted"
- Added a "banned servers" option to warn the user which servers the mod shouldn't be used on. You can only configure this via the config file under the "accessibility" options.
  - The only banned server by default right now is hypixel. You can remove or add more if you'd like.
- The color for the arrow counter text when a held bow has infinity is now white instead of green.
- Held bows w/ infinity no longer count normal arrows to the arrow counter when using a special type of arrow.
- Held bows w/ infinity do not display colored highlighting on the arrow counter or render the warning indicator when using a special type of arrow.
- Several bugs fixed (some not mentioned below) related to arrow x item counter.

## Bugs Fixed
- [#38:](https://github.com/Dillon8775/Quality-of-Queso/issues/38) Count All Arrows feature doesn't work following the support for firework rockets on crossbows w/ the item counter.
- [#39:](https://github.com/Dillon8775/Quality-of-Queso/issues/39) Using a projectile weapon and then switching to any item other than a projectile weapon only displays the count for the arrow type that was shot, not counting all arrows if desired.