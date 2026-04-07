# Quality of Queso Version 1.7 (MC Fabric/NeoForged 26.1, and 1.21.1 Fabric/NeoForged/Forge):

## Sort by Count, Ascending/Descending
- Introducing count sorting, where you can sort a container by item count, ascending or descending.
- Removed "Tag Sorting" option. This has been replaced by the new "Sorting Mode" option!
- Additionally, to change the sort mode, you must scroll on the sort button. You cannot change it via config screen anymore.

## Armor Status and Item Count Changes
- The armor status and item count display now have fixed positions.
    - **For right-handed users,** the armor status displays on the right-hand side of the screen (and doesn't move from the item counter), and the item counter now displays on the left side.
    - **For left-handed users,** the armor status displays on the left-hand side of the screen, and the item counter now displays on the right side.
- Inspired by the "Quark" mod layout.

## Management Changes
- Management button sounds (the bundle sounds) are now separate from Minecraft's bundle sound files, meaning they are separate sound files, allowing for easy sound changing with resource packs. This also means management buttons have their own custom subtitle, ex. "Management Succeeds" or "Management Drops".

## Bugs Fixed
- Fixed bug where you cannot search for an item by its item name if the item has a custom name.
- Fixed bug where you sometimes have to press the sort button twice in order for stacks to be merged correctly (mostly).
- Fixed bug where if transparent search bar and "use old search bar texture" are both enabled at the same time, causes search box rendering issues.
- Fixed bug on Fabric where respawning does not reset armor status counter for indicating that the slot was changed.

## Other Changes
- Tweaks and optimizations.