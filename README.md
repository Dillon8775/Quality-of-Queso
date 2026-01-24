# A quality of life mod.

###

<a href="https://files.minecraftforge.net/" target="_blank">
  <img src="https://i.imgur.com/gbZFxKU.png" width="356" height="96" alt="Compatible with Forge">
</a>

####

<a href="https://fabricmc.net/" target="_blank">
  <img src="https://i.imgur.com/vIbuVv8.png" width="361" height="124" alt="Compatible with Forge">
</a>

####

<a href="https://modrinth.com/mod/fabric-api/versions" target="_blank">
  <img src="https://i.imgur.com/yFnszAw.png" width="287" height="96" alt="Compatible with Forge">
</a>

### Please report *any* bugs you may find <a href="https://github.com/Dillon8775/Quality-of-Queso/issues" target="_blank">here.</a>

---
<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/_tPKgpBcTg4" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

---

# Note: every feature listed here is configurable and toggleable. *:)*
- You can find the mod's configuration button on the title screen or pause screen.
- You can also choose hide the config button to display on only on the title screen, or disable it entirely.
- **Note:** if you disable it entirely, the only way to re-enable it is by reverting it in the "qualityofqueso-client_config.json" file.
  - Unless you are a Fabric user and have the [Mod Menu](https://modrinth.com/mod/modmenu) mod installed. If so, you can navigate to the config screen from there.
  - Or unless you are a Forge user, you can navigate to the config screen by navigating to your "Mods" list.

---

# _Chest Searching!_
### A search bar for chests, ender chests, barrels, and shulker boxes, which highlight the items you are looking for!
## Searching Features:
- #### Search by item name (just type the item name in the search bar, ex. grass) [since: v1.2]
- #### Search for multiple items (separate each item with a command, ex. "grass,dirt,iron") [since: v1.2.1]
- #### Match case with a colon :, ex. ":chest" (will display all "Chest"s) [since: v1.4.2]
- #### Search by tag (begin search query with a #, ex. "#logs", works with all vanilla and non-vanilla tags) [since: v1.2, full tag support since v1.2.1]
    - Searching by tag will make all items display what tag(s) they are in if you hover over them. [since: v1.2.1]
- #### Exclude item from search (begin search query with a !, ex. searching "log" will display any item that does _not_ contain "log" in its name) [since: v1.2.1]

## You can also do this in your inventory!
[since: v1.2, inventory searching since v1.4]

<img src="https://i.imgur.com/wrOeB2D.gif" width="872" height="490" alt="Chest searching feature.">

---

# _Inventory Management!_
<img src="https://i.imgur.com/WxGOap3.png" width="36" height="36" alt="Transfer inventory button.">
<img src="https://i.imgur.com/uErcaYu.png" width="36" height="36" alt="Transfer container button.">

#### Adds two small buttons to chests and shulker boxes, which allow the player to transfer the items in the chest to the inventory, and vice-versa.
- #### By utilizing the Chest Search feature, you can transfer _only_ the searched items.
- #### If you hover over the button with an item in the cursor, only items which are equal to the cursor's item will be moved. [since: v1.2.1]
    - The transfer button will have a hint of green to represent stack filtering. [since: v1.3.6]
    - The transfer button will have a hint of red when searching by exclude (!) [since: v1.3.6]
    - The transfer button will have a hint of blue when searching by tag (#) [since: v1.3.6]
    - The transfer button will have a hint of light green when searching with match case (:) [since: v1.4.2]
- #### You can also quickly transfer items by using CTRL + C (move items from container -> inventory) and CTRL + I (move items from inventory -> container) [since: v1.4].

### Smart-moving
- Moving certain items that have similar components, ex. an enchanted book with sharpness on it, you can choose to move *only enchanted books with sharpness on them.*
- Just hover over the transfer button with your desired item, hold shift and it will move all items *only* that have matching components.
- This applies to **enchanted books, potions, tipped arrows, and firework rockets** (rockets use flight duration to move).

[smart moving since: v1.5]

<img src="https://i.imgur.com/91vGfnG.gif" width="872" height="490" alt="Chest transferring feature.">

## Along with these buttons, we have:

---

<img src="https://i.imgur.com/AG9Ygkj.png" width="36" height="36" alt="Sort button.">

## The ***Sort*** button, which alphabetically sorts all items in a container!
- Shortcut keybind = *CTRL + S*.
- You cannot utilize the search feature with this button.

---

<img src="https://i.imgur.com/glqaxnv.png" width="36" height="36" alt="Quick drop button.">

### The ***Quick Drop*** button, which allows you to drop ***all*** highlighted items in a chest (or your inventory).
- If you use this button in a container screen, it will drop the full stack of selected items in the container. Using the button while your inventory is open will drop selected items in your inventory.
- If you hold "shift", you can drop one of each item, instead of the full stack (since: v1.4.4)
- Shortcut keybind = *CTRL + ALT + Q + (SHIFT, only if you are dropping one of each item)*

---

<img src="https://i.imgur.com/OSQdEuo.png" width="36" height="36" alt="Swap button.">

### The ***Swap*** button, which swaps all items in the container and inventory to the other container's respective slot
- You can set a keybind to do this (CTRL + your desired keybind).
- You cannot utilize the search feature with this button.
- There is a 2-second cooldown between uses with this button.

---

<img src="https://i.imgur.com/0KM5Wsx.png" width="36" height="36" alt="Include hotbar">
<img src="https://i.imgur.com/DFAlF1R.png" width="36" height="36" alt="Exclude hotbar">

### The ***Include/Exclude Hotbar*** button, which blocks any hotbar items from being highlighted via search, or moved when transferring, swapping, or dropping.
- When searching items, if you press the exclude hotbar button, all hotbar items will be grayed out, even if the item matches the search query.
- When transferring items *(via moving, quick dropping, or swapping),* the hotbar slots **will not** be affected.

### This changes how Minecraft's default "quick moving" system works (when you Shift + Left Click an item, since: v1.4.4).
- The item will move to the next perpendicular slot, rather than going across the entire inventory. You can disable turning off **"Perpendicular Item Moving"** (it is disabled by default). Note that this system still applies when transferring items from inventory -> container if "Include Hotbar" is OFF.

---

## Below is an example of all these buttons in action.
### You can disable any of these buttons if you would like.

<img src="https://i.imgur.com/4XBrfP0.gif" width="640" height="360" alt="Other buttons.">
<img src="https://i.imgur.com/IJLQ1Ss.gif" width="640" height="360" alt="Sort button.">

#### (since: v1.2, quick drop and swap button since v1.4, sort since v1.5)

---

# _Item Frame Searching!_
#### Search for nearby item frames containing the item searched!
### Default Hotkey to open searching GUI = _I_, you can change this in the Controls menu.
### Searching features:
- #### Search by item name (just type the item name in the search bar, ex. grass)
- #### Search for multiple items (separate each item with a command, ex. "grass,dirt,iron")
- #### Search by tag (begin search query with a #, ex. "#logs", works with all vanilla and non-vanilla tags)
#### You can set how long the item frames will remain glowing after searching (ex. 1 second, 5 seconds, 30 seconds)
#### You can change the radius to search for item frames (ex. 50 blocks, 100 blocks, 300 blocks)

![Item frame searching in action.](https://i.imgur.com/6hF7F73.gif)

## Please note: this mod must be installed on the server-side, _and_ enabled on the server side in order for players to use this feature.
### There is also a command to use this feature if you don't want to use the GUI screen.
#### To disable this feature on the server-side, either don't install the mod on the server, or go to the "config" directory in your server files and look for "qualityofqueso-common_config.json" (if you are on forge, the file will be called "qualityofqueso-common.toml". Open that file and set "itemFrameSearching" to "false".
#### If you are in a singleplayer world and have the mod installed, the feature will work as long as you have "Item Frame Searching" enabled.

#### (since v1.3)

---

# _Show Time In-Game!_

### Display your real-life time and/or Minecraft's in-game time!

### To enable these, press F3 + F6, and search for "time".
- You can choose to always display them *or* only in the debug menu.
- If you are playing 1.20.1, you can just enable these via the mod's configuration, under "Misc...".

<img src="https://i.imgur.com/bfWrzxc.png" width="640" height="360" alt="Show in-game time.">

#### (since: v1.5)

---

# _Disable Fog!_

### Turn off Minecraft's extremely annoying and obnoxious fog!

<img src="https://i.imgur.com/7RUZwVB.gif" width="640" height="360" alt="Remove fog.">

Important note: if you experience mod conflicts, you can disable the fog from being changed at all by **disabling the "applyFogFunction"** in the **"qualityofqueso-client_universal.json"** config file.

Side-note: (Fabric only) if the **Simple Keybinds** mod is loaded, this feature won't work, because Simple Keybinds already has this feature, and you will need to use Simple Keybind's keybind to toggle fog.

#### (since: v1.5.1)

---

# _Armor Status!_
### Display the armor that you are wearing near your hotbar!
#### It automatically hides after a few seconds, but when the durability changes, or the item itself changes, it re-appears!
- You can choose to make it *always* display by setting the **"Armor Status"** option to **"ALWAYS"**.

<img src="https://i.imgur.com/xCkCBhA.gif" width="640" height="360" alt="Armor status.">

#### (since: v1.5.1)

---

# _Item Count!_

### Display the total amount of the item that you are holding!
###
#### This is highly configurable with the "Item Count" option:

- **"TOTAL"** will display the exact count.
- **"STACKS"** will display how many stacks of that item you have, if you have a perfect amount of stacks.
    - For example, if you have **exactly 3 stacks of dirt blocks,** it will display **"3x 64".** If you don't have exactly *said* amount of stacks, it will instead just display the total amount (so **192** in this case).
- **"REMAINDER"** - will display how many perfect stacks you have *PLUS* the additional amount remaining.
    - For example, if you have **3 stacks** ***and*** **12 additional dirt blocks,** it will display as **"3x64 & 12"**.


- If you have some shulker boxes/bundles in your inventory, and the shulker/bundle *contains* the item that you are holding, it will add it to the count! (this is toggleable, look for **"Count Containers"** option)

####

- Picking up or throwing an item will display the new count of that item for 4 seconds (you can disable this).
- You can also choose to display the *exact count* on top of the *stacks count* by enabling the **"Display Total w/ Stacks"** option (if you're tryna get real technical lol).

### Below is an example of all of these settings being used:

<img src="https://i.imgur.com/YQnECn5.gif" width="768" height="432" alt="Item count.">

#### (since: v1.5.1)

---

# _Drag Sorting!_
### Hold ALT and drag-click across slots to "exclude" them from being transferred or searched! (left picture)

### Hold ALT + SHIFT and drag click to select which slots to transfer or search! (right picture)

Note: when attempting to transfer to an excluded slot, that slot will still be affected. This feature only applies to *selecting slots to move/search.*

<img src="https://i.imgur.com/Q2d9ojS.gif" width="640" height="360" alt="ALT Excluding">
<img src="https://i.imgur.com/RekIaQn.gif" width="640" height="360" alt="ALT + SHIFT Excluding">

### You can also choose to save excluded slots by enabling the "Save Excluded Slots" option:

<img src="https://i.imgur.com/2YCncvz.gif" width="640" height="360" alt="Save excluded slots.">

#### (since: v1.5, shift + alt picking since v1.5.1)

---

# _Mob Hit Ding!_
### Plays the ding sound when hitting a mob with an arrow!
- By default, you need to be at least **20** blocks away for it to play the ding sound (you can change this).

<img src="https://i.imgur.com/12f8oFd.gif" width="480" height="270" alt="Mob Hit Ding.">

#### (since: v1.5.1)

---

# _Quick Equip!_
#### Hover over an equippable item (like a piece of armor or an elytra) in your inventory and quickly equip it!

### Right-click the hovered item to quickly equip it!
[since: v1.3.6]

#### You can also use the hotkey to quick equip. Default Hotkey = _B_, you can change this in the Controls menu.

<img src="https://i.imgur.com/f3yUwAA.gif" width="640" height="360" alt="Quick Equip in action.">

#### (since v1.2.1)

---

# _Better GUI Closing!_
Clicking off of any GUI screen (as long as your cursor isn't holding anything) will close the screen.

<img src="https://i.imgur.com/tMVWv3y.gif" width="640" height="360" alt="Close GUI menu by clicking off.">

#### (since: v1.0)

---

# _Better Searching!_
If you are in the creative inventory menu screen, and you want to search for an item, you no longer have to click on the compass to search for an item. Just begin typing and it will begin searching. The same applies to recipe book screens and crafting screens.
Note: if your cursor is hovered over a slot with an item that is bound to a hotbar key, it will not automatically search. You must move your mouse away from the bounded hotbar slot.

<img src="https://i.imgur.com/kC3tg7B.gif" width="640" height="360" alt="Typing anywhere.">

#### (since: v1.0)

---

# _Prevent Rage Quitting!_
### Are you like me, and get mad at bedwars pretty easily?
### No more rage quitting for you!

(just enable the "Prevent Rage Quitting" option to use this)

<img src="https://i.imgur.com/6O0FGuQ.gif" width="640" height="360" alt="Prevent rage quitting.">

---
# Enable Mod and Blacklisted Servers
If you want, without removing the mod from your mods folder entirely, there is an option to disable the mod entirely from functioning, and you can also add blacklisted servers to make the mod not work on those specific servers. Just go to the Quality of Queso options GUI to configure that.

#### (since: v1.3.2)

---
# Multi-Server Configs
By default, each server that you join will have its own Quality of Queso configuration, meaning each server that you play on can have different settings. You can disable this if you'd like (search for "Server-Config Presets" under "Misc..." options.)

#### (since: v1.5)

---

# Technical Features (probably not worth looking deep into):

---

## The _"Move Items If"_ option (paired with Inventory Management)

With the transferring buttons, you can choose when you are able to transfer items when your inventory is full, or the container is full.

- **CONTAINER ISN'T FULL**: at least 1 slot has to be empty in order to move items, regardless of if they can be combined.
- **LESS THAN MAX ITEM COUNT**:
  - This is pretty complicated to explain, but bear with me.
  - Let's say your container is full of netherite smithing templates, and you are trying to move another stack of smithing templates to that container. Only if one of the stacks in the container's *count* ***PLUS*** the stack you are trying to move *doesn't exceed* the *max item count* (in this case, for the netherite smithing template, doesn't exceed 64), then you can move the item. If it ***DOES*** exceed the 64 *(or the max count)*, the button will become inactive and you will be unable to move it.
- **CAN MOVE AT ALL** ***(DEFAULT)***:
  - Again, let's say your container is full of netherite smithing templates, and you are trying to move another stack of smithing templates to that container. *IF* one of the slot's *count* is **LESS** than the max stack size (again, in this case, *64*), you can move the item. If every stack equals it's max count and you are trying to move an item of the same, the button becomes inactive and you are unable to move.

## Below is a GIF that shows all of these options in action, and is easier to understand:

<img src="https://i.imgur.com/4138vz4.gif" width="960" height="540" alt="Typing anywhere.">

---

Features and ideas created by MannyQUESO. Implemented by Dillon8775.