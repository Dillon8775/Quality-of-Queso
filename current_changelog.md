# Version 1.7.15
- Update to 26.3.

## Debug Hud Changes
- Similar debug hud entries are now grouped together.
- Closing the debug options screen when opening it from the Quality of Queso main menu now properly sets the screen back to the main menu. You must be in the overworld to see it.
- The fastest flight debug entry now displays "Fastest Flight for Elytra (target = 40/-40): *value*".
- Added a "visual time debug entry" which displays whether visual time is enabled.
- You can no longer tell the in-game time via the in-game time debug hud when you are in other dimensions.

## Hud Changes
- A clock now displays on the Hud when visual time is enabled (this is toggleable in the Visual Time screen, and you can configure an offset in the Hud positions screen).
- Stretched minimum and maximum values for configuring Hud elements, like the item counter and armor status.
- Added a warning message to the Hud positions screen, which warns the user that positions may alter based on screen resolution.

## Other Changes
- The enhanced cursor now alternates between the scroll and crosshair cursor when holding control in any container screen.
  - When the user presses Q or attempts to scroll, the cursor is then fixed on the corresponding cursor for that action (so scroll moving would be scrolling cursor, dropping an item would be a crosshair). It resets each time you close the screen.
- Revamped the main menu screen and resources screen.
  - Removed the "Other QoL Mods" button in the resources screen.
- Optimized translations.
  - Renamed keybind "Scroll Move" to "Scroll Move Modifier".
  - Renamed keybind "Open Visual Time GUI" to "Open Visual Time UI".
  - Renamed keybind "Open Item Frame Search GUI" to "Open Item Frame Search UI".

## Bugs Fixed
- Opening the creative mode menu refreshes search results based on the last saved query even if the selected tab isn't the search tab.
- Clearing the search bar by right-clicking doesn't update search results.
- Armor status overlay(s) display in the Hud positions screen, even if they are disabled.