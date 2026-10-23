# Version 1.7.20

## Changes
- Now requires [DillonLib](https://modrinth.com/mod/dillon-lib) version 1.2.5 or greater.
- You can now middle-click on the sort button to "lock" a container or shulker box, which prevents you from using the sort function in that specified container.
  - If you have the Sorting option set to Key Only, this feature will not work.
- Improved the management sound subtitles.
- You can now directly manage your container data from the Quality of Queso main menu.
- Revamped how container data is stored. It is now much more organized and compact. Your current container data may be erased following this new logic.
- Container data and locked container/player slot files are now stored universally, instead of using multi-server config files.
  - TLDR; data is still world/server specific, it's just all in one file now rather than switching between files when joining different worlds or servers.
- Modded dimension keys are now stored properly.
- Components in ender chest contents are no longer persisted.
- Closing the View Last Known Ender Chest screen now goes to the parent screen.
- Backend optimizations.

## Bugs Fixed
- [#43:](https://github.com/Dillon8775/Quality-of-Queso/issues/43) Mini bow overlay renders even if player has 0 arrows and right-clicks the bow.