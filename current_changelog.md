# Version 1.7.7 | 2026-06-18

### Available Versions:
- Fabric/NeoForge: 26.2, 1.21.11, 1.21.1
- Fabric/Forge: 1.20.1

## Changes
- Arrow counter text color is now always green if the arrow counter is equal or above 32.
- Arrow counter text colors is now always white if colored highlighting is disabled.
- A new keybind to open the Quality of Queso main menu (Tab + Q).
- Optimizations with Fov effects.
- Mod is now compatible with mods like [Zoomify](https://modrinth.com/mod/zoomify), [Tweakeroo](https://modrinth.com/mod/tweakeroo), and [Ok Zoomer](https://modrinth.com/mod/ok-zoomer).
- Renamed "FOV Effects" to "Fov Effects".
- Renamed "Functions" options to "Mixins".
- New mixin options:
    - "titleScreenMixin"
    - "pauseScreenMixin"
    - "clockManagerMixin"
- Renamed mixin options:
    - "applyFog" -> "fogMixins"
    - "applyFovEffects" -> "fovEffectsMixin"
    - "applyRedArmorTint" -> "redArmorTintMixin"

## Bugs Fixed
- Menu button (and other buttons) do not appear on the pause screen when on a multiplayer server (26.2).
- Menu button on title screen can sometimes be incorrectly positioned.
- "On Server" detection is always false (26.2).
- Fov is not changed with other things, like walking on soul sand and other speed modifications.

### Technical Changes
- Mod now uses Mixin extras (>=0.5.4).
- Bumped min version require to 0.8.