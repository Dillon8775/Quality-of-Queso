# Version 1.7.9 | 2026-06-30

## Changes
- Added more versatility for quick equipping, so it works with all equippable items, in any inventory menu.
    - Shields, animal armor items, maps and totems work with quick equipping now.
- Quick equip is now considered a management option, instead of miscellaneous.
- Optimizations with searching in the creative search menu.
- New quick search option, "Creative Menu", which only auto focuses into the creative search menu.

## Item Argument Command Optimization
- Ported from the speedrunner mod, Quality of Queso now takes the feature and optimizes the "item argument" syntax when using commands, to work with any mod, without having to spell out "modid:itemname" for each item.
- This feature is server dependent.
- This can be toggled in the "Accessibility" tab, with the option titled "Optimize Item Argument".
- New mixin option, "itemStackArgumentMixin", to disable this.

## Bugs Fixed
- Save search text in the creative menu search bar does not refresh the search results.
- Multi-Server configs not applying when enabling/disabling the option in a server from the YACL options menu.
- [Bug #30](https://github.com/Dillon8775/Quality-of-Queso/issues/30) - Shift + double-clicking on items to quick move crashes the game on 1.21.1 and below (Forge/NeoForge only).