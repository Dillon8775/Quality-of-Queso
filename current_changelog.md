# Version 1.7.7 | 2026-06-18

### Available Versions:
- Fabric/NeoForge: 26.2, 1.21.11, 1.21.1
- Fabric/Forge: 1.20.1

## Changes
- Arrow counter text color is now always green if the arrow counter is equal or above 32.
- Arrow counter text colors is now always white if colored highlighting is disabled.
- A new keybind to open the Quality of Queso main menu (Tab + Q).
- A new option called "Beta Warning", which displays the beta warning upon game startup of the current Quality of Queso version is in a beta state.
- You can now press F4 to open the config directory in any QoQ menu (except main YACL configuration screen).
- Additionally, the "Open World Folder" is replaced with a "Open Config Directory" button if the user is not currently in a world.

## Fov Effect Changes
- Optimizations with Fov effects.
- With new optimizations, this mod is now compatible with mods like [Zoomify](https://modrinth.com/mod/zoomify), [Tweakeroo](https://modrinth.com/mod/tweakeroo), and [Ok Zoomer](https://modrinth.com/mod/ok-zoomer).
- A new option, "Generic Fov Percent Change", which changes Minecraft's default "Fov Effects" option.
- Another new option, "Lock Fov", which locks the players Fov entirely, any doesn't allow anything else to change it (except for manual zoom mods or other external Fov modifications).
- Re-organized Fov effect options.
- Removed "Disable Fov Effects" option.
- Fixed a big bug with Fov effects, where any other external fov modification could not modify the Fov (like soul sand from soul speed, or increased speed attribute modifiers).

## Bugs Fixed
- Menu button (and other buttons) do not appear on the pause screen when on a multiplayer server (26.2).
- Menu button on title screen can sometimes be incorrectly positioned.
- "On Server" detection is always false (26.2).
- Fov is not changed with other things, like walking on soul sand and other speed modifications.

### Technical Changes
- Renamed "FOV Effects" to "Fov Effects".
- Renamed "Functions" options to "Mixins".
- Universal options are now stored in the "universal.json" file, and mixin options have been moved to a separate file, (which is also universal), called "mixins.json".
- New mixin options:
  - "titleScreenMixin"
  - "pauseScreenMixin"
  - "clockManagerMixin"
- Renamed mixin options:
  - "applyFog" -> "fogMixins"
  - "applyFovEffects" -> "fovEffectsMixin"
  - "applyRedArmorTint" -> "redArmorTintMixin"
- Mod now uses Mixin extras (>=0.5.4).
- Bumped min version require to 0.8.