# ![https://modrinth.com/mod/fabric-api](https://cdn.modrinth.com/data/cached_images/deafc20c4eb1ab02e3f8ac364a61aa78e90ee056.png)

# A quality of life mod, with a bunch of cool _QOL_ features.

### Currently compatible with Minecraft versions **1.21.4-1.21.5.**

---

# _Chest Searching!_
### A search bar for chests, ender chests, barrels, and shulker boxes, which highlight the items you are looking for!
## Searching Features:
- #### Search by item name (just type the item name in the search bar, ex. grass) [since v1.2]
- #### Search for multiple items (separate each item with a command, ex. "grass,dirt,iron") [since v1.2.1]
- #### Search by tag (begin search query with a #, ex. "#logs", works with all vanilla and non-vanilla tags) [since v1.2, full tag support since v1.2.1]
    - Searching by tag will make all items display what tag(s) they are in if you hover over them. [since v1.2.1]
- #### Exclude item from search (begin search query with a !, ex. searching "log" will display any item that does _not_ contain "log" in its name) [since v1.2.1]

![Chest searching feature.](https://i.imgur.com/wrOeB2D.gif)

---

# _Inventory Sorting!_
#### Adds two small buttons to chests and shulker boxes, which allow the player to transfer the items in the chest to the inventory, and vice-versa.
- #### By utilizing the Chest Search feature, you can transfer _only_ the searched items.
- #### If you hover over the button with an item in the cursor, only items which are equal to the cursor's item will be moved. [since: v1.2.1]
- #### Note: in order to transfer items from the player's inventory to the container, you must hold down the ALT key for the button to become active. This is done to prevent moving items from the inventory to the container *by accident.* You can disable this if you'd like.
#### (since: v1.2)

![Chest transferring feature.](https://i.imgur.com/91vGfnG.gif)

---

# _Quick Equip!_
#### Hover over an equippable item (like a piece of armor or an elytra) in your inventory and quickly equip it!
### Default Hotkey = _B_, you can change this in the Controls menu.
#### (since v1.2.1)

![Quick Equip feature in action.](https://i.imgur.com/f3yUwAA.gif)

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
#### To disable this feature on the server-side, either don't install the mod on the server, or go to the "config" directory in your server files and look for "qualityofqueso-server_config.json". Open that file and set "itemFrameSearchingOnServer" to "false."
#### If you are in a singleplayer world and have the mod installed, the feature will work as long as you have "Item Frame Searching" enabled in your client options.

#### (since v1.3)

---

# _Better GUI Closing!_
Clicking off of any GUI screen (as long as your cursor isn't holding anything) will close the screen.
#### (since: v1.0)

![Closing GUI menu by clicking off.](https://i.imgur.com/tMVWv3y.gif)

---

# _Better Searching!_
If you are in the creative inventory menu screen, and you want to search for an item, you no longer have to click on the compass to search for an item. Just begin typing and it will begin searching. The same applies to recipe book screens and crafting screens.
Note: if your cursor is hovered over a slot with an item that is bound to a hotbar key, it will not automatically search. You must move your mouse away from the bounded hotbar slot.
#### (since: v1.0)

![Typing anywhere.](https://i.imgur.com/kC3tg7B.gif)

---

Features and ideas created by MannyQUESO. Implemented by Dillon8775.**