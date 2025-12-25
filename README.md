# A quality of life mod, with a bunch of cool _QOL_ features.

###

<a href="https://files.minecraftforge.net/" target="_blank">
  <img src="https://i.imgur.com/gbZFxKU.png" width="427" height="115" alt="Compatible with Forge">
</a>

####

<a href="https://fabricmc.net/" target="_blank">
  <img src="https://i.imgur.com/vIbuVv8.png" width="433" height="149" alt="Compatible with Forge">
</a>

####

<a href="https://modrinth.com/mod/fabric-api/versions" target="_blank">
  <img src="https://i.imgur.com/yFnszAw.png" width="286" height="96" alt="Compatible with Forge">
</a>

### Please report *any* bugs you may find <a href="https://github.com/Dillon8775/Quality-of-Queso/issues" target="_blank">here.</a>

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

![Chest searching feature.](https://i.imgur.com/wrOeB2D.gif)

---

# _Inventory Management!_
<img src="https://i.imgur.com/WxGOap3.png" width="36" height="36" alt="Swap button.">
<img src="https://i.imgur.com/uErcaYu.png" width="36" height="36" alt="Swap button.">

#### Adds two small buttons to chests and shulker boxes, which allow the player to transfer the items in the chest to the inventory, and vice-versa.
- #### By utilizing the Chest Search feature, you can transfer _only_ the searched items.
- #### If you hover over the button with an item in the cursor, only items which are equal to the cursor's item will be moved. [since: v1.2.1]
    - The transfer button will have a hint of green to represent stack filtering. [since: v1.3.6]
    - The transfer button will have a hint of red when searching by exclude (!) [since: v1.3.6]
    - The transfer button will have a hint of blue when searching by tag (#) [since: v1.3.6]
    - The transfer button will have a hint of light green when searching with match case (:) [since: v1.4.2]
- #### You can also quickly transfer items by using CTRL + C (move items from container -> inventory) and CTRL + I (move items from inventory -> container) [since: v1.4].

![Chest transferring feature.](https://i.imgur.com/91vGfnG.gif)

## Along with these buttons, we have:

---

<img src="https://i.imgur.com/glqaxnv.png" width="36" height="36" alt="Swap button.">

### The ***Quick Drop*** button, which allows you to drop ***all*** highlighted items in a chest (or your inventory).
- If you use this button in a container screen, it will drop selected items in the container. Using the button while your inventory is open will drop selected items in your inventory.
- You can quickly do this by pressing *CTRL + ALT + Q.*

---

<img src="https://i.imgur.com/OSQdEuo.png" width="36" height="36" alt="Swap button.">

### The ***Swap*** button, which swaps all items in the container and inventory to the other container's respective slot
- You can quickly do this as well by pressing *CTRL + S* (you can change or disable this).
- You cannot utilize the search feature with this button.

---

<img src="https://i.imgur.com/0KM5Wsx.png" width="36" height="36" alt="Include hotbar">
<img src="https://i.imgur.com/DFAlF1R.png" width="36" height="36" alt="Exclude hotbar">

### The ***Include/Exclude Hotbar*** button, which blocks any hotbar items from being highlighted via search, or moved when transferring, swapping, or dropping.
- When searching items, if you press the exclude hotbar button, all hotbar items will be grayed out, even if the item matches the search query.
- When transferring items *(via moving, quick dropping, or swapping),* the hotbar slots **will not** be affected *(this doesn't include when moving items from container -> inventory).*

---

## Below is an example of all these buttons in action.
### You can disable any of these buttons if you would like.

![Other buttons.](https://i.imgur.com/4XBrfP0.gif)
#### (since: v1.2, quick drop and swap button since v1.4)

---

# _Quick Equip!_
#### Hover over an equippable item (like a piece of armor or an elytra) in your inventory and quickly equip it!

### Right-click the hovered item to quickly equip it!
[since: v1.3.6]

#### You can also use the hotkey to quick equip. Default Hotkey = _B_, you can change this in the Controls menu.

![Quick Equip feature in action.](https://i.imgur.com/f3yUwAA.gif)
#### (since v1.2.1)

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
### There is also a command to use this feature if you don't want to use the GUI screen. However, the command will _only work_ in singleplayer.
#### To disable this feature on the server-side, either don't install the mod on the server, or go to the "config" directory in your server files and look for "qualityofqueso-common_config.json" (if you are on forge, the file will be called "qualityofqueso-common.toml". Open that file and set "itemFrameSearching" to "false".
#### If you are in a singleplayer world and have the mod installed, the feature will work as long as you have "Item Frame Searching" enabled.

#### (since v1.3)

---

# _Better GUI Closing!_
Clicking off of any GUI screen (as long as your cursor isn't holding anything) will close the screen.

![Closing GUI menu by clicking off.](https://i.imgur.com/tMVWv3y.gif)
#### (since: v1.0)

---

# _Better Searching!_
If you are in the creative inventory menu screen, and you want to search for an item, you no longer have to click on the compass to search for an item. Just begin typing and it will begin searching. The same applies to recipe book screens and crafting screens.
Note: if your cursor is hovered over a slot with an item that is bound to a hotbar key, it will not automatically search. You must move your mouse away from the bounded hotbar slot.

![Typing anywhere.](https://i.imgur.com/kC3tg7B.gif)
#### (since: v1.0)

---
# Enable Mod and Blacklisted Servers
If you want, without removing the mod from your mods folder entirely, there is an option to disable the mod entirely from functioning, and you can also add blacklisted servers to make the mod not work on those specific servers. Just go to the Quality of Queso options GUI to configure that.

#### (since: v1.3.2)

---

Features and ideas created by MannyQUESO. Implemented by Dillon8775.