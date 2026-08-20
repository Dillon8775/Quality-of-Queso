# Version 1.7.15

## Changes
- Update to 26.3.
- Revamped the main menu screen and resources screen.
  - Removed the "Other QoL Mods" button in the resources screen.
- Similar debug entries are now grouped together.
  - The fastest flight debug entry now displays "Fastest Flight for Elytra (target = 40/-40): *value*".
  - Added a "visual time debug entry" which displays whether visual time is enabled.
- A clock now displays on the Hud when visual time is enabled (this is toggleable in the Visual Time screen, and you can configure an offset in the Hud positions screen).
- You can no longer tell the in-game time when you are in other dimensions. You must be in the overworld  to see it.
- Stretched minimum and maximum values for configuring Hud elements, like the item counter and armor status.
- Added a warning message to the Hud positions screen, which warns the user that positions may alter based on screen resolution.
- Optimized translations.
  - Renamed keybind "Scroll Move" to "Scroll Move Modifier".
  - Renamed keybind "Open Visual Time GUI" to "Open Visual Time UI".
  - Renamed keybind "Open Item Frame Search GUI" to "Open Item Frame Search UI".
- Closing the debug options screen when opening it from the Quality of Queso main menu now properly sets the screen back to the main menu.

## Bugs Fixed
- Opening the creative mode menu refreshes search results based on the last saved query even if the selected tab isn't the search tab.
- Clearing the search bar by right-clicking doesn't update search results.
- Armor status overlay(s) display in the Hud positions screen, even if they are disabled.