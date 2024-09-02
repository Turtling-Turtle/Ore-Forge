# OreForge

## The Game 
In Ore Forge you assemble your tycoon out of tons of unique items. Each time you prestige you gain access to increasingly
powerful and unique items. 

## Controls
* **Camera Movement** - You can use W, A, S, D to move the camera up, left, down, and right respectively. Holding down **shift** while 
pressing one of the movement keys doubles the speed.
 

* **Camera Zoom** - You can use **E** to zoom the camera inwards and **Q** to zoom the camera outwards.
 

* **Ore Observer Mode** - To lock the camera onto a specific ore and observer its stats you can press **F2**. To cycle between other ore you can
use **W** and **S**. To exit Ore Observer mode press **ESC**.
 

* **Selecting Items** - To select and item move your mose so that its hovering over the item the **Left Click** on it, The item should now
be highlighted green. From here you can move the item by pressing **R**, remove the item by pressing **Z**, {INSERT OTHER ACTIONS HERE}
 

* **Placing Items** - To place an item you can either press **LEFT CLICK** or **SPACE**. Holding down either of these keys
makes it so you don't have to press the button each time you want it to be placed, instead the item will automatically be placed if it's a valid position.
While in build mode placing an item on top of another already placed item will remove it unless that item was placed during the current build mode session.

 
* **Shop and Inventory** - The shop and inventory can be accessed by clicking on their respective buttons/icons or by pressing their hotkeys.
The hotkey for the inventory is **F1** and the hotkey for the shop is **F**. To close these menues you can either press the respective hotkey, **ESC** or
the X button/icon. To select an item from the shop simply click on the item you wish to select and it will either allow you to purchase more of that item
or allow you to place that item.

## Adding/Creating Your Own Custom Items
You can use [this](https://github.com/NathanUlmen/OreForge-Item-Json-Generator) tool to generate/create item data for Ore Forge and to manage the items that you have created so far. 

### Key Components:
* The [GameWorld](https://github.com/NathanUlmen/OreForge/blob/main/core/src/main/java/ore/forge/Screens/GameWorld.java) class is where the game is rendered and the game logic is driven from.
* Under the [Strategies Folder](https://github.com/NathanUlmen/OreForge/tree/main/core/src/main/java/ore/forge/Strategies) you can find the classes that are responsible upgrade logic and effect logic.
* The [Ore](https://github.com/NathanUlmen/OreForge/blob/main/core/src/main/java/ore/forge/Ore.java) class is pretty important as it's what the game revolves around. 


## Libraries Used:
[LibGDX](https://github.com/libgdx/libgdx)

