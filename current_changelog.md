# Version 1.7.6 | 2026-05-29

### Available versions:
- Fabric/NeoForged: **26.1.x, 1.21.11, 1.21.1**
- Fabric/Forge: **1.20.1**

## Enhanced Cursor
- A new option (on by default) which enhances the cursor by relating it to things you are doing in a gui screen.
    - This replaces some tooltips to make it cleaner.

## Changes
- Slightly tweaked tooltips (as mentioned above).
  - The "scroll to move 1" tooltip no longer renders. It has been replaced with the enhanced cursor to remove bulkiness.
  - The throw out *amount* tooltip no longer renders if the amount is exactly 1.
- Hoppers, droppers and dispeners now use the correct management layout.
- New color for colored highlighting, for items with durability percentages of 61-70%.
- New texture for selected bulk crafting recipes, to match the bulk craft button.
- New mod description.
- New changelog format (hi!).
- Improved the Enchantment Helper visually.
- Renamed "Widget Theme" option to "Theme".
- New buttons under the "Resources" tab:
  - A button that links the user to a [playlist containing all Quality of Queso features](https://www.youtube.com/playlist?list=PLwrfa61ofGnXhwobF8j2fBIUmpy6K9e43) (currently a W.I.P)!
  - A button that links the user to a [Modrinth collection](https://modrinth.com/collection/PDFZoFqE) of other QoL mods.
- The game no longer crashes when YetAnotherConfigLib isn't installed. Instead, if the user tries to press the "Configure" button in the main menu without the mod installed, the user will simply be warned and not be able to configurate the mod.

### MC 1.20.1 Exclusive Changes
- Colored highlighting and warning indicators are now supported for Minecraft 1.20.1!
- Colored text now renders appropriately with the arrow counter.

## Bugs Fixed
- Unbounding certain Quality of Queso keybinds causes screen bugs and key press bugs.
- Enabling the Swapping management feature always displays the "move one" texture on transfer buttons.
- Cannot change move amount if transfer button is hovered and only holding scroll modifier.
- Tooltips are positioned incorrectly in accordance to the "No Recipe Book Shift" option.