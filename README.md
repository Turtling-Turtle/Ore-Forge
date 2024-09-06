# OreForge

## What is Ore Forge? 
In Ore Forge you assemble your tycoon out of tons of unique items. Each time you prestige you gain access to increasingly
powerful and unique items. 

## Controls
* **Camera Movement** - You can use _**W**_, _**A**_, _**S**_, _**D**_ to move the camera up, left, down, and right respectively. Holding down _**Shift**_ while 
pressing one of the movement keys doubles the speed.
 

* **Camera Zoom** - You can use _**E**_ to zoom the camera inwards and _**Q**_ to zoom the camera outwards.
 

* **Ore Observer Mode** - To lock the camera onto a specific ore and observer its stats you can press _**F2**_. To cycle between other ore you can
use _**W**_ and _**S**_. To exit Ore Observer mode press _**ESC**_.
 

* **Selecting Items** - To select and item move your mose so that its hovering over the item the _**Left-Click**_ on it, The item should now
be highlighted green. From here you can move the item by pressing _**R**_, remove the item by pressing _**Z**_, {INSERT OTHER ACTIONS HERE}
 

* **Placing Items** - To place an item you can either press _**Left-Click**_ or _**SPACE**_. Holding down either of these keys
makes it so you don't have to press the button each time you want it to be placed, instead the item will automatically be placed if it's a valid position.
While in build mode placing an item on top of another already placed item will remove it unless that item was placed during the current build mode session.

 
* **Shop and Inventory** - The shop and inventory can be accessed by clicking on their respective buttons/icons or by pressing their hotkeys.
The hotkey for the inventory is _**F1**_ and the hotkey for the shop is _**F**_. To close these menus you can either press the respective hotkey, _**ESC**_ or
the X button/icon. To select an item from the shop simply click on the item you wish to select and it will either allow you to purchase more of that item
or allow you to place that item.

## Adding/Creating Your Own Custom Items
You can use [this](https://github.com/NathanUlmen/OreForge-Item-Json-Generator) tool to generate/create item data for Ore Forge and to manage the items that you have created so far.

## Key Components:
* The [GameWorld](https://github.com/NathanUlmen/OreForge/blob/main/core/src/main/java/ore/forge/Screens/GameWorld.java) class is where the game is rendered and the game logic is driven from.
* Under the [Strategies Folder](https://github.com/NathanUlmen/OreForge/tree/main/core/src/main/java/ore/forge/Strategies) you can find the classes that are responsible upgrade logic and effect logic.
* The [Ore](https://github.com/NathanUlmen/OreForge/blob/main/core/src/main/java/ore/forge/Ore.java) class is pretty important as it's what the game revolves around. 


## Libraries Used:
[LibGDX](https://github.com/libgdx/libgdx)

