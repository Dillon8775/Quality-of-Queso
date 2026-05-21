# Quality of Queso Version 1.7.3

---

# Move Matching Items and Fill Stacks Change
- `"Move Matching Items"` and `"Fill Stacks"` have now been combined into `one button.`
- In addition to this, `you can filter containers with the fill stacks mode.`
    - For `filtered containers,` click on the button to change the sort mode (*move matching items* or *fill stacks*, which is now called `"Current Stacks"`).
    - Scroll on the button to change the filter *type* (`alphabetical` or `by tag`).
- Containers that are `filtered` can now use the `"fill stacks"` rule when filtering.
  - This means, if any item is *filtered* (according to the `"type"` rule, alphabetical or tag), only `current stacks in the container will be filled`.

# Management Versatility
- You can now sort `any container` (including `modded screens)`, by using the `sort shortcut key` (default = `CTRL + S`).
- `Transferring` and `quick dropping` now work in `any screen` by using their `shortcut key.`
  - No buttons appear. Only shortcut keys work.
- `Drag moving` and `scroll moving` now work in `any screen.`
  - In addition, `scroll moving` now actually moves items to the `crafting grid` in crafting menus.
  - `Scroll moving` in a `furnace screen` uses `fuel-determined` moving, so if the item being moved `is a fuel source`, it moves to the `fuel slot (if the fuel slot is available for the item)`. Otherwise, it moves to the `burning slot.`
    - This feature is `not present` in `1.20.1`.
- `Other optimizations` and `behind the scene changes` to some management features.

# Inventory Locking
- A new `server-side` functionality, where you can `lock` your inventory.
- The button appears only in your `inventory`.
- By default, your inventory is `unlocked`, allowing you to pickup anything.
  - There are `two modes` to `inventory locking:` `normal locking` and `soft locking`.
  - `Normal locking` prevents you from picking up any item, unless you `manually shift + left click` on the item.
  - `Soft locking` only allows items that are `already in your inventory to be picked up`. You can also manually `shift + left click` an item to `pick it up`.

## Bulk Crafting Changes
- Renamed `"Craft All"` to `"Bulk Craft"`.
- The `"bulk crafting"` button now appears `next to the inventory crafting-grid.`
- `Bulk crafting` now works on `all Minecraft versions!`

# New Textures
- New `button textures,` to look more appealing and user-friendly!
  - New texture for the `bulk crafting` button.
  - New texture for the `filtering/move matching items` button (now looks like a `hopper`).
  - Thanks to my `Trialists` in my Discord server for `their work on these textures.`
- The `"Move Matching Items"` texture has been matched to use the old `fill stacks` texture.
  - The `fill stacks` button now has a new, but similar texture.
- `Locked slot icons` now only render if the `slot contains an item.`

## New Options
- New option, `"Safe Bulk"`, which automatically disables bulk crafting and trade all when closing a screen, to prevent accidentally using the bulk features.
- New option, `"Shift Recipe Book"`, which allows you to `prevent the recipe book from shifting the screen.`
  - This is `enabled` by default, which means opening the recipe book will still shift the screen. `Disable it if you want to disable this behavior.`
- `"Auto Close Recipe Book"` is now a `misc` option, instead of an `accessibility` option.
- Renamed `"Trade All"` to `"Bulk Trade"`.

## Other Changes
- The `lock inventory` button will appear `inactive` if the client cannot send `quality of queso packets over to the server` (due to locking inventories being server-dependent).
- `Locked slots` now only render in `vanilla container screens`.
- Renamed `"Singular Moving"` to `"Scroll Moving"`.
  - Renamed the `"Move Single Item"` keybind to `"Scroll Move"`.
- Renamed the `"Filtering"` option to `"Container Filtering"`
  - The `"Container Filtering"` option now `actually disables container filtering altogether,` meaning if a container was previously filtered, `it will not appear as filtered if this option is disabled.`
- Renamed the `"Move Matching Items"` button display option to `"Filtering"`.
- The `blue outline` that appears around selected hotbar items now only appears if `"Prevent Dropping"` is enabled, or if locked slot icons are set to appear on the GUI's hotbar.
- Darkened the color of the `clear excluded slots` button due to the change of the base textures.
- `26.1.2 version` for this mod now works with `26.1 and 26.1.1`.
  - `For fabric users,` the `26.1.x` version also `requires Fabric loader version 0.19.2 or greater`.

## Incompatible Mods
- Certain mods have been officially marked as `"broken"` for Quality of Queso, meaning the game will simply `not launch` with these mods installed. Currently, these include:
  - [Controlify](https://modrinth.com/mod/controlify)
  - [No Recipe Book Shift](https://modrinth.com/mod/no-recipe-book-shift) *(because this mod adds it, lol)*

## Bugs Fixed
- Fixed small issues with `fill stacks` and `filtered containers.`
- Fixed bug where `move matching items/fill stacks` does not work at all in `dispensers, droppers`, or `hoppers`.
- Fixed bug where you `cannot enter in numbers` to the `creative menu's search bar.`
- Fixed bug where you `cannot search items by tag in the creative search menu`, due to shift being blacklisted when pressing a hotbar key.
- Fixed `1.21.1 and below bug` where `quick searching does not work in the crafting table menu`.
- Fixed `1.21.1 bug` where using the `transparent theme` and a `vanilla colored search bar` results in an `untextured search bar`.
- Fixed `1.20.1 bug` where right-clicking w/ scroll moving does not reset the counter.