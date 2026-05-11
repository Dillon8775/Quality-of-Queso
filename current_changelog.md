# Quality of Queso Version 1.7.1 (MC Fabric/NeoForged 26.1):

---

# Sorting Changes
- You can now `change the sort mode if you only use the keybind for sorting.`
    - To do this, under `Management` options -> `Sorting...`, enable `Use Global Sorting Mode`.
    - `"Use Global Sorting Mode"` will use a global sorting mode for all containers, rather than giving each container it's own sorting mode.
    - This also allows you to `disable the feature where each container has it's own sorting mode.`

# Management Changes
- You can now `move single items`, `drop single items` and `drag quick-move` in *any* screen (not restricted to just container screens).
  - Note that some screens will `not fully work,` because `that's how they're supposed to function.`

# Visual Time Addition
- A new visual time option, to `match your visual time with your local IRL time.`

## Other Changes
- Added a `"Debug HUDs..."` option in the Quality of Queso main menu, which takes you straight to Minecraft's `debug options screen,` with `"qualityofqueso"` already entered in as the search query, so you can `easily toggle Quality of Queso's new debug huds.`

### Technical Changes
- Renamed all `"search_field"` texture files to `"search_bar".`
- Mixins that don't apply no longer use error messages, but instead `warning` messages, to appear less critical (because it's not that critical).

## Bugs Fixed
- Fixed small bug with shulker boxes stored sorting mode.