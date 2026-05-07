# Quality of Queso Version 1.7 (MC Fabric/NeoForged 26.1):

---
<img src="https://cdn.modrinth.com/data/cached_images/3f52366fa307bc8b96964e580ea29c0c28c1fc9a.png" width="100" height="100" alt="QoQ logo">

# Regular QoQ users: You will have to reconfigure your settings when installing this new verison.

## Warning: This update is *BIG*. Get ready! ;)

---

# Locked Slots
- Introducing a `new mechanic` for management, `locking slots.`
- Hold `ALT + Middle Click` on any slot in your inventory or container to `"lock"` it.
- If a slot is `already locked,` holding ALT + Middle Click will simply unlock it.
- "Locked" slots are slots that can `never be affected by transferring, quick dropping, or sorting.` This is useful if you want to lock your "hotbar" slots, lock slots in a container, or any other slot in your inventory, from ever being affected.
- `"Locked" slots will render blue,` instead of white. Additionally, `a small lock will display beside locked slots,`indicating that slot is locked. 
  - The "lock" texture will not render if the search bar has a query or the user is drag sorting. You can also toggle this lock from rendering.
- `Locked slots are stored per container, per player, per world.`
- Holding the `Lock Slot modifier key (alt)` will display a small unlock texture beside the cursor, as if you're placing a lock on a slot.
  - If you are hovering over an already locked slot, you will see a small "key" appear beside your cursor, like you're literally unlocking the slot.
- You can still search locked slots via the search bar, they just won't be affected when managing.
- There is also `an option to "hard lock" slots,` which will prevent any interaction with locked slots (disabled by default). This including picking, manually moving, or throwing an item.
- Additionally, whenever you are locking slots, or drag sorting, your cursor `becomes a pointing hand,` simulating picking-slots.
- You can only modify locked slots in container screen and in your inventory.
  - Locked slots will render in any screen. The lock will not render.

# New Sorting Modes
- Introducing `Count Sorting`, where you can `sort a container by item count, ascending or descending.`
- Introducing `Creative Menu` sorting, where `items are sorted based on the order of the creative search menu.`
- Removed "Tag Sorting" option. This has been replaced by the new `"Sorting Mode"` option!
- Additionally, `to change the sort mode, you must scroll on the sort button.` You cannot change it via config screen anymore.
- `Sort modes` are now stored per container, meaning you can give certain containers different sort modes, ahd the mod will remember the mode for containers.
- You can now `sort your inventory.`
  - You can only sort your inventory from the player inventory screen. Sorting in a container screen will just sort the container.
- Additionally, you can now `sort items based on drag-selected slots.`
- Locked slots, including/excluding hotbar and drag sorting will `work accordingly when sorting in your inventory.`

# Singular Moving and Dropping
- Introducing a new mechanic to management, called `singular moving,` which works together with `transferring` and `dropping / quick dropping.`
- Hover over any item and hold `CTRL` to only move *one* item out of that hovered stack.
  - For example, lets say you have `64 oak planks.` Holding `CTRL + clicking` will only quick-move 1 oak plank, instead of the entire stack.
- For dropping (or quick dropping), you must hold `SHIFT.`
- In addition to this, while hovering over the item with the shortcut key down (`CTRL` for transferring, `SHIFT` for dropping / quick dropping), you can `scroll up/down` to `increase/decrease` the amount you want to transfer/throw.
  - For example, hovering over `a stack` of oak planks and holding `CTRL` and scrolling up `3 times,` will move `3 oak planks` out of that stack of planks.
  - The same works with dropping, only you press your drop key to throw said *amount* out.
  - You can also right-click the item while the shortcut key is down to reset the count back to `0.`
- Additionally, you can do this with the transferring buttons, which will transfer *one* (or your desired amount by `scrolling`) item out of each stack in the container.
- The already existing logic for `quick dropping` is the same, the only addition is that you can scroll on top of the quick drop button to drop `said amount` of items out of each stack in the container / your inventory.
- `Moving singular items` only works in containers. `Dropping singular items` works in both containers and your inventory.
- This works cleanly with `including/excluding hotbar` and `perpendicular quick moving.` It acts just like normal quick-moving, only it moves a certain amount out of the stack.

# Fill Stacks
- `A new management feature,` which allows you to `only fill the current stacks in a container.`
- Similar to the `move matching items` feature, this will only move whats already present in the container.
- With this, however, `it only fills the current stacks up to their max stack size.`
  - For example, if you have 1 spruce log in a container, and you have a bunch of spruce logs in your inventory, this will only move `63 logs`.
  - Another example, you have 3 diamonds in a container, and each diamond is in a different slot. Lets say you have a ton of diamonds in your inventory! When you move, it will fill those 3 slots in the container, and then stop.
- The same works when transferring from a container to your inventory!
- `Disabled by default.` 

# Data and Storage Changes (important!)
- `Filtered items and locked slots are now stored per world file name, instead of the world name.`
  - This prevents conflict when creating multiple worlds with the same name, ensuring that each world has it's own configuration for filtered items and locked slots.
- Because of this change, `you will have to re-configure your filtered items in all your containers, if you are a regular Quality of Queso user.`
- For shulker boxes, `filtered items and locked slots are stored per shulker box,` meaning you can now add filtered items or lock slots in a specific shulker box, break it and move it anywhere else, and `those filtered items and locked slots will remain in that shulker box.`
  - `This requires that the mod be installed on the server-side to keep locked slots and filtered items per shulker box.` If it's not installed server-side, locked slots and filtered items for shulker boxes will not be kept. It will work perfectly fine in singleplayer, as long as you have the mod installed (duh).

## Filtering Changes
- The filter items screen is now a `scrollable list,` holding up to a total of `504 items,` instead of a generic 56.
- Attempting to add an item that is `already filtered in a container` no longer works.
- In the placeholder item screen (when adding/remove filtering items in a container), you can now `shift-click` to `quick move items to a placeholder slot.`
- You can now `filter items in your ender chest,` as well as `add locked slots`.
  - Filtering your ender chest applies anywhere around your world, unlike normal chest blocks, where that data is determined by block position.
- `You can no longer filter containers if you have a tool in your hand` (to prevent accidentally filtering a container when trying to break it).
- New message when filtering shulker boxes.
- Increased the filtering cooldown from 1 tick to 2 ticks.

# Trade All Button
- A new management buttons, which appears in the villager trading GUI!
- This button allows you to use all resources to buy an item from a villager.
  - For example, if you want to buy an entire stock of glowstone from a villager, toggling this will buy it all in 1 click when taking the item from the trade, rather than having to continuously click to buy the entire stock.

# Craft All Button
- A new management button, which appears in inventory screens and crafting table screens.
- Similar to the trade all button, this button allows you to bulk craft a selected recipe.
- Select a recipe via the recipe book and turn this on to use all of your resources in your inventory to bulk craft that selected item.
- Selected recipes will highlight in green.

# Armor Status and Item Count Changes
- The armor status and item count display now have `fixed positions.`
    - `For right-handed users,` the armor status displays on the `right-hand` side of the screen (and doesn't move from the item counter), and the item counter now displays on the `left side`.
    - `For left-handed users,` the armor status displays on the `left-hand` side of the screen, and the item counter now displays on the `right side`.
      - Inspired by the "Quark" mod.
- Added `animations` for the item counter and armor status (toggleable).
  - Added `animation time` option, to set the `animation time (default = 1.5 seconds).`
- Added `display time` option for picked up and dropped items, and for armor items `(default = 4 seconds).`
- Added the option to count contents inside the player's `ender chest.`
  - This option only counts the contents inside the player's *last known* ender chest, meaning content is updated only when the player re-opens their ender chest.
- New option to display empty armor sprites for empty for armor slots (on by default).
- New option to display armor outlines when the armor is changed.
- Renamed **"Armor Status: ON"** to `"Armor Status: Always"`.
- Added support for users who use the hotbar indicator for their attack indicator.
- Renamed "Show Arrow Count" to `"Arrow Count"`.
- Option to always show arrow counter, if there is no other option to display.
- Picking up an `arrow entity` now displays the item count accordingly.
- (Technical change) The item counter is now `tick-based`, rather than real-time based.

# Quick Dropping Addition
- You can now `hover over any item and use the quick drop feature to only drop all of what you are hovering.`
- For example, `hovering over jungle planks` and using quick drop `will only drop all jungle planks.`
- Additionally, quick dropping normally will display a white outline around the quick drop button. Quick dropping only a hovered item will now display the green outline.

# Searching Changes
- The `search bar` is `now transparent by default,` with a `magnifying glass` as it's unfocused state.
- A new option, `"Underline Text"`, which underlines the search bar's text if it's transparent (off by default).
- Removed "Use Old Search Bar Texture" and "Transparent Search Bar" option, because they have been replaced with a new option, `Search Bar Color`.
  - By default, it is set to `"Vanilla"`, which uses the default, vanilla-looking search bar color.
  - You can set this option to `"transparent"` or `"black"` if you wish.
- A new option, `"Search Bar Position"`, which lets you place the search bar *over-top* of GUIs, instead of overlaying!
- By default, it's set to overlay (like it is normally), but you can change it if you'd like.
- Setting it to `"Top"` will render the search bar over top of the GUI, instead of overlayed.
  - When displaying the search bar over top of GUIs, a nice little outline will appear around it.
  - If you set the `"Search Bar Color"` to `"Black"`, the outline will not appear.
- Moved the `"Search Bar Text Color"` option to searching options (no longer under accessibility options).

## Management Additions and Changes
- New texture for the `always quick move` button.
- You can now `always quick move` in your inventory.
- Additionally, dragging over slots while `always quick move` is enabled will constantly quick move items.
  - You can also hold `SHIFT` and drag to quickly move items.
  - Togglable with the `"Drag Moving"` option.
- The `"display always quick move"` button is now disabled by default.
- `Completely overhauled the "Management" options screen,` to make it more user-friendly.
- Added an `OFF` option for the `include hotbar button,` so you can `disable the button from displaying entirely.`
- `In inventory screens, buttons now stack into a 3x3 layout,` preventing overlapping on the open recipe book button.
- More buttons appear in dropper/dispener and hopper screens.
- `Other screens (like furnaces and brewing stands)` now use the `correct button layout.`
- Putting an item into your cursor with nothing available to move will no longer render the buttons as "cursor inactive".
  - This has the same effect with excluding, tag searching, and match case.
- Management button sounds (the bundle sounds) `are now separate from Minecraft's bundle sound files,` meaning they are separate sound files, allowing for easy sound changing with resource packs. This also means management buttons have their own custom subtitle, ex. `"Management Succeeds"` or `"Management Drops".`
- You can no longer `"always quick move"` if the `button isn't visible on your screen.`
- You can no longer `search inside transportable containers if the button isn't visible.`
- Lowered the vertical button layout a little bit in container screens, so it's not so high up.

# New Keybinds
- Added `"Lock Slot"` keybind, for locking/unlocking slots (default = **ALT + middle click**).
- Renamed "Move Container" keybind to `"Move to Inventory"`.
- Renamed "Move Inventory" keybind to `"Move to Container"`.
- Renamed "Sort Container" keybind to `"Sort"`.
- Swapped the transferring keybinds, so `Move to Container` is now `CTRL + C`, and `Move to Inventory` is now `CTRL + I`.
- Quality of Queso keybinds now contain their modifiers (using the [Kumi API](https://github.com/TwelveIterations/KumaAPI/tree/26.1)).
  - For example, "Move to Inventory" is `CTRL + I`, not just *I*.
- Added keybind for quick dropping, so you can change it. Default = `CTRL + ALT + Q`.
- Added button in "Management..." options to configure keybinds.
- Removed the `"Hide Recipe Book" keybind.`

## New Options and Option Changes
#### Only Show Arrow Counter 
- Only displays the arrow counter, doesn't display held item's item count.
#### Show Lock
- An accessibility option which displays the small lock on locked slots.
#### Color Field Option Changes
- A option to change the color of locked slots.
- `Shift + left clicking a color option` will now open a link to pick a color (in HEX format).
- You now have to `shift + right click a color option` to `reset it back to default.`
#### Darker Overlay
- Uses the darker overlay when searching or excluding slots.
#### Dark Disc Option
- An accessibility option to toggle the dark sky rendering when the player is in the void.
- This is default Minecraft behavior, but you can disable it with this option if you'd like.

#### Option Changes
- Renamed "Button Sounds" option to `"Play Sounds"`, and it is now a simple `on/off` option.
- Removed "Show Button Shortcuts" option.

# Visual Time
- A new option, which allows you to override the client-side time!
- This means that you can change the visual time in your game, whether on a server or in singleplayer.
  - This does not actually change the world's time, it just appears as a visual.
- You can choose to set an exact time, or set the speed of how fast the time advances.
- Fully configurable via the Quality of Queso main menu.

# Widget Theme
- A new accessibility option, which allows you to change the theme of all Quality of Queso widgets without having to make resource pack!
- By default, it's set to "Vanilla", which uses the default, vanilla-themed widget textures.
- You can choose to set it to "Dark" or "Transparent", which are specifically designed to work with [VanillaTweak's Dark/Transparent UI.](https://vanillatweaks.net/picker/resource-packs/)

# Mob, Hit, Ding! Changes
- Killing a mob (or player) with an arrow now plays the `player levelup` sound, indicating that the `entity you shot was killed.`
- Fixed `volume` for the mob hit ding, to match the `vanilla player hit sound,` but slightly differs to deter the difference between hitting a player, and another mob.

# Fog Additions
- Added option to change the overworld fog intensity.

# Red Armor Tint
- Added the option to brings back Minecraft's old red-armor tint.
- This is heavily inspired from the ["Old Animations"](https://oldanimationsmod.net/index.php?page=downloads) mod.
- You can also disable this `function entirely,` from the mod's `universal config.`

# View Last Known Ender Chest
- Adds a button in the QoQ menu to view your last known ender chest, for each world.
  - You cannot modify your ender chest from this button, only view it.
- You can change where the button displays, either in the QoQ menu (default), or on the pause screen, or disable it entirely.
- Added a keybind for it as well, default key = `CTRL + N`.
- Configurable in the Accessibility tab.

# Cosmetic Change
- New color for basic graying, to give it a more user-friendly feel. This includes `searching` and `excluding slots.`
  - If using the `Dark` or `Transparent` Widget theme, the original gray color will be used.
- Slots grayed out from searching or excluding hotbar now use texture files to render the grayed overlay, instead of hardcoding a color overlay over slots.

## Tooltip Changes
- Tooltips are now displayed in a fixed position on the screen, mainly for management buttons.
- This comes withhange to the "Helpful Tooltips" option, which is now called "Tooltips". By default, it is set to `"Default".`
- If you want to go back to the normal way of displaying tooltips, simply set the `"Tooltips"` option to `"Overlay"`, and that will revert it back.
- If you want to disable `helpful tooltips entirely,` just disable the `"Tooltips"` option.

## New Sounds
- `2 new sounds,` for `locking and unlocking slots.`
  - They are literally the old opening/closing Minecraft door sounds.

# Date Hud Entry
- A new HUD entry that displays the date.

# Config Changes
- Moved all Quality of  a cQueso config files to `.minecraft/config/qoq`.
- All config files have been renamed to `"client.json, common.json, universal.json".`
- Renamed `"qualityofqueso_tracked-containers.json"` config file to `"container_data.json"`
- 2 new config files, `locked_player_slots.json` and `locked_container_slots.json`, which keep locked slot data, per world, per server.
  - This does not include shulker box data, as that data is stored directly as a component in the shulker box.
- Multi-Server configs are now stored in `".minecraft/config/qoq/server-configs/*servername*`.

# FOV Effect Changes
- The `"sprinting" FOV modifier is now a percentage,` allowing you to change how much the FOV effect from sprinting should increase your FOV.
- Setting the sprinting fov modifier to below 100% will `turn sprinting fov modification off.`

### FOV Effect Safe-Check
- Added a `safe check` for the following mods:
  - "Ok Zoomer"
  - "Zoomify"
  - "Tweakeroo"
- These mods mess with FOV modification, and so does this mod, resulting in incompatibility, and a game crash.
- If any of these mods are detected when starting the game, the "FOV Effects" feature will automatically disable, to prevent the game from crashing.
- Please report any mod incompatibilities to the [GitHub repository](https://github.com/Dillon8775/Quality-of-Queso/issues), and I can attempt to add a safe-check for those mods as well.

## Dependency Updates
- Runs on `Minecraft 26.1.2`, not 26.1.1 or 26.1.
- *(Fabric only)* Now requires `Fabric Loader 0.19.2` or above to run.
- *(NeoForged only)* Now requires `NeoForged-beta` version `26.1.2.31` or greater to load.
- Now requires `Balm` version `25.1.2.5` or above to run.

## Other Changes
- The `"Menu Button"` option is now a `universal option,` meaning it is not affected by multi-server configs.
- Increased the maximum item frame search distance to 512 blocks (which equals exactly 32 chunk searching).
- Added a "Resource Pack Template" link under "Resources..." so users can easily make their own texture packs for Quality of Queso.
- The blacklisted server button now simply displays if the server is enabled or diasbled or not, regardless of it's hovered.
- Playing on a server with multi-server configs enabled now renders a small `"file explorer"` icon next to the blacklist server button.
- Joining/leaving a server with a multi-server config no longer displays the IP address to the chat, to protect privacy.
- Added tip messages for `Multi-Server configs`, as well as warning messages for servers that ban certain features from this mod.
- Simplified mod description to "Chest searching, precise inventory management, and more!".
- Tweaked the options screen.
- Tweaked the icon file.
- Tweaked configuration files.
- Cleaned and polished lang.
- Simplified the Quality of Queso main menu.
- Renamed some texture files.
- Other improvements, tweaks, and optimizations.
- Psalms 127:4.

## Bugs Fixed
- Fixed critical crash where disabling certain functions via the "universal.json" config file crashes the game with some mods.
- [Bug 15](https://discord.com/channels/1420957089479655598/1420957091006255268/1491320139692507226) - Fixed bug where armor status renders off the screen (with new implementation seen above).
- Fixed bug where you cannot search for an item by its item name if the item has a custom name.
- Fully fixed bug where you sometimes have to press the sort button twice in order for stacks to be merged correctly.
- Fixed bug where if transparent search bar and "use old search bar texture" are both enabled at the same time, causes search box rendering issues.
- Fixed bug where if screenshots folder doesn't exist, nothing happens when pressing the "Open Screenshots Folder..." button.
- Fixed bug where using quick drop one of each item and hovering over an item drops the full hovered stack.
- Fixed bug where dropping/quick dropping a full stack from a GUI does not display the item counter.
- Fixed bug where armor status sometimes displays forever, specifically when switching worlds or going somewhere with a major game tick change.
- Fixed inconsistency with sort inactive texture.
- Fixed bug where excluded slots, and other stored values are lost when resizing the screen and "container searching" is disabled.
- Fixed bug where some management buttons do not get repositioned correctly when opening the recipe book on the vertical button layout option.
- Fixed bug with transparent search bar where typing in a big query and then backspacing does not show the full query.
- Fixed bug where picking up a non-stackable item overwrites the current item counter.
- Fixed bug where the enchantment helper displays "can be added to..." on an enchanted book with no enchantments.
- Fixed bug where disabling the "Enable Mod" option does not disable certain features.
- Fixed bug where using the "move to container" keybind in a brewing stand moves potions to inventory.
- Fixed bug on Fabric where respawning does not reset armor status counter for indicating that the slot was changed.