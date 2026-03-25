# Quality of Queso Version 1.6.1 (MC Fabric 26.1, NeoForge 26.1):

# Searching and Management Changes
- Renamed "Inventory Management" options to "Management" options.
- Added "Transparent Search Bar" option, which gives the search bar in your inventory and containers a transparent texture. Only the text that you type into the search bar will render. This is OFF by default.
- Renamed "Quick Drop" option to "Quick Dropping".

- The "Search Transportables" button no longer displays next to the search bar. Instead, it displays along all other management buttons, and looks correct now.

# Vertical Layout for Management Buttons
- All management buttons are now displayed in a "vertical" layout, in a small, separate box, outside of the GUI screen.
- Don't worry, if you don't like this option, you can go back to the default layout, by enabling "Horizontal Layout", under Management options.

# Always Quick Move
- Added a new button, which essentially "locks" your shift key, so you can always quickly move items, without pressing shift.

# Accurate Facing Debug Hud
- A new debug hud, which accurately displays the direction you are facing (ex. northeast, southeast, northwest, etc.).

# Fastest Flight Debug Hud
- A new debug hud, which displays the players "x-rotation" / YAW (although, it seems like it's the y-rotation, but that's what Minecraft calls it for some reason, lol), which can be used to get the fastest flying speed.
- For those that don't know, getting your X-rotation / YAW to be around 40 (or -40) will give the player the best momentum when flying with an elytra. Displaying this can help you always gain that speed.
- This hud will only display if the player has an elytra equipped, to keep it off the screen when not needed.

# Option changes
- Added "Bottom Left" and "Bottom Right" options for the Quality of Queso configuration buttons to be displayed in the pause menu.
- These do not affect the title screen.
- Reimplemented the "on change" option for Armor Status (it is now called "on update").
- Renamed option "Armor Slot Outlines" to "Armor Hotbar". Also moved this option to "HUD..." options, instead of it being under accessibility.
- Changed the "Button Click Sounds" option to "Button Sounds", which can be toggled in 4 different options.
  - ALL: plays all button sounds (bundle noises and normal click sounds)
  - BUNDLE ONLY: only plays bundle noises when using management buttons, does not play any normal "clicking" sound.
  - CLICK ONLY: only plays clicking sounds when using management buttons, no bundle sounds.
  - OFF: disables all sounds when using management buttons.

# Technical changes / bug fixes
- Sorting now correctly alphabetically sorts enchanted books based on their enchantment name.