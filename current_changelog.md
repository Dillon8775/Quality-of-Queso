# Version 1.7.15
## Update to 26.3.
### Now requires [Fabric Loader version 0.19.5](https://fabricmc.net/use/installer/) or greater.

## Ctrl Moving
- A new management feature, enabled by default, which allows you to quickly move all the same item type.
- By holding left control and left-clicking, you can easily quick-move these items between inventories.
  - Moving this way ignores search queries, but accounts for locked slots.
- With this change comes the removal of the "Scroll Move Modifier" keybind. Scroll moving still works like normal, but it is now bound to left-control and cannot be changed.

## Highlight Matching Items
- Highlight any matching items in your container or inventory with the green hint.
- Pairs with Ctrl Moving very well, so you can see what items you are moving.
- You can toggle this to always, on ctrl (when the user is holding ctrl), or disable it entirely.
  - Set to "On Ctrl" by default.
- You can change the color of highlighted matching items in the Management configuration tab.

## Debug Hud Changes
- Similar debug hud entries are now grouped together.
- Closing the debug options screen when opening it from the Quality of Queso main menu now properly sets the screen back to the main menu. You must be in the overworld to see it.
- The fastest flight debug entry now displays "Fastest Flight for Elytra (target = 40/-40): *value*".
- Added a "visual time debug entry" which displays whether visual time is enabled.
- You can no longer tell the in-game time via the in-game time debug hud when you are in other dimensions.

## Hud Changes
- The arrow counter now works with firework rockets when using a crossbow.
- Holding a charged crossbow now displays a small charged crossbow, with either a firework or arrow next to the arrow counter.
- A clock now displays on the Hud when visual time is enabled (this is toggleable in the Visual Time screen, and you can configure an offset in the Hud positions screen).
- Stretched minimum and maximum values for configuring Hud elements, like the item counter and armor status.
- Added a warning message to the Hud positions screen, which warns the user that positions may alter based on screen resolution.

## Other Changes
- The filtered item screen now uses the "matching items color" for selected items.
- The enhanced cursor now alternates between the scroll and crosshair cursor when holding control in any container screen.
  - When the user presses Q or attempts to scroll, the cursor is then fixed on the corresponding cursor for that action (so scroll moving would be scrolling cursor, dropping an item would be a crosshair). It resets each time you close the screen.
- Revamped the main menu screen and resources screen.
  - Removed the "Other QoL Mods" button in the resources screen.
- Optimized translations.
  - Renamed keybind "Scroll Move" to "Scroll Move Modifier".
  - Renamed keybind "Open Visual Time GUI" to "Open Visual Time UI".
  - Renamed keybind "Open Item Frame Search GUI" to "Open Item Frame Search UI".
- Removed "Scroll Move Modifier" keybind. It is now permanently bound to left-control.
- The "1" number now only appears on transfer buttons if the button is hovered and the user has control down.
- Backend optimizations.
- Moved some texture files around.

## Bugs Fixed
- Opening the creative mode menu refreshes search results based on the last saved query even if the selected tab isn't the search tab.
- Clearing the search bar by right-clicking doesn't update search results.
- Armor status overlay(s) display in the Hud positions screen, even if they are disabled.