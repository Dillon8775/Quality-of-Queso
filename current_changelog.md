# Quality of Queso Version 1.7.1 *(1.20.1, 1.21.1, 1.21.11 & 26.1.2)*

---

# Backports
- Backported to `1.21.11 (Fabric and NeoForged)`, `1.21.1 (Fabric and NeoForged)`, and `1.20.1 (Fabric and Forge)!`
- Some features *are not available* on the `backported versions,` which include:

### 1.21.11 *(and below)* Unsupported Features
- The `fluids` FOV modifier is `not available` in this version, or `any version below this.`

## 1.21.1 *(and below)* Unsupported Features
- `Bulk crafting`
- `Fading animations for armor status`
- `Red armor tint`
- `Transparent search bar`
- `Darker overlay`
- `Overworld fog intensity`
- `Keybind modifiers`

## 1.20.1 *(and below)* Unsupported Features
- `Colored highlighting`
- `Warning indicators`
- `Slot highlighting for armor status`

### Version Notes:
- `Options are laid out slightly different in this version.`
- `Certain colored texts` are `not the same` as in other versions (like item counter, button texts, and option texts).
- This version has `no resources screen`.
- `Tracked containers in ender chests do not count toward item counter`.
- Arrows  from a `charged crossbow` are not counted toward the `item counter.`.
- Picking up `arrows entities doesn't display the arrow counter`.

---

# New Version Changes

## Searching Addition
- You can now use `@` to search items by mod name (example `@minecraft` searches all items in default Minecraft ,`@speedrunnermod` searches all items in `the speedrunner mod`).

## Singular Moving Changes
- Scrolling up/down no longer changes the move amount when holding CTRL; `holding CTRL + scrolling now moves single items to container and inventory.`
- You can still hold `SHIFT and scroll` to drop a certain amount of items.
- *This change is not present in the 1.20.1 version of the mod, due to faulty and buggy functionality w/ 1.20.1.*

## Locked Slot Changes
- You can `no longer drop a slot that is locked.`
  - However, you can hold `SHIFT and use the drop amount` feature to `ignore locked slots`, and drop the item.
- In addition to this, `locked slot icons render on the hotbar, in-game.`
  - This comes with a change to the `"Lock Slot" option`: it now has 3 different modes
    - `"Everywhere"` renders the locked slot icon in `menu screens` and the `GUI hotbar.`
    - `"HUD Only"` _only_ renders the locked slot icon on the `HUD's hotbar.`
    - `"Screen Only (default option)"` _only_ renders the locked slot icon in `menu screens.`
    - `"OFF"` don't render locked slot icon at all
- Additionally, selected hotbar slots that are locked will highlight with a `light-blue outline`, if `"Colored Highlighting"` is enabled.
  - Colored outlines depending on `item durability` appear *first.*
- Locked slot icons now render in `most screens`.
- Lowered the sound of the `locked slot sound.`

## Sorting Additions
- You can now `change the sort mode if you only use the keybind for sorting.`
    - To do this, under `Management` options -> `Sorting...`, enable `Use Global Sorting Mode`.
    - `"Use Global Sorting Mode"` will use a global sorting mode for all containers, rather than giving each container it's own sorting mode.
    - This also allows you to `disable the feature where each container has it's own sorting mode.`
- `Default Sorting Mode option,` which determines the default sorting mode to use for containers that do not already have a sorting mode.
- `Global Sorting Mode option,` which uses a global sorting mode for *all* containers, instead of giving each container a unique sorting mode.

## Visual Time Addition
- A new visual time option, to `match your visual time with your local IRL time.`

## Management Changes
- You can now `move single items`, `drop single items` and `drag quick-move` in *any* screen (not restricted to just container screens).
  - Note that some screens will `not fully work,` because `that's how they're supposed to function for right now.`

## Other Changes
- Added a `"Debug HUDs..."` option in the Quality of Queso main menu, which takes you straight to Minecraft's `debug options screen,` with `"qualityofqueso"` already entered in as the search query, so you can `easily toggle Quality of Queso's new debug huds.`
- `For older Minecraft versions,` it simply takes you to a custom screen, which lets you enable the custom hud's there.
- Renamed `"Prevent Rage Quitting"` to `"Anti-Rage Quit"`
- Renamed `"Always Prevent Rage Quit"` to `"Force Anti-Rage Quit"`.
- `Tweaked tooltip positions.`
- Lowered  the sound of the armor ding.
- Other `tweaks` and `optimizations.`

### Technical Changes
- Renamed all `"search_field"` texture files to `"search_bar".`
- Mixins that don't apply no longer use error messages, but instead `warning` messages, to appear less critical (because it's not that critical).

## Bugs Fixed
- Fixed bug where attempting to filter a container in creative mode breaks the container.
- Fixed bug where armor ding plays multiple times when joining a world/server and it can be extremely loud.
- Fixed small bug with shulker boxes stored sorting mode.