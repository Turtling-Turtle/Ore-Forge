# OreForge

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

## The Game 
In Ore Forge you assemble your tycoon out of tons of unique items. Each time you prestige you gain access to increasingly
powerful and unique items. 

## Controls:

## Adding/Creating Your Own Custom Items
You can use [this](https://github.com/NathanUlmen/OreForge-Item-Json-Generator) tool to generate item data for Ore Forge. Make sure you read its README so ou know how items
work!

Once you've created your item [INSTRUCTIONS FOR HOW TO ADD IT TO PROJECT DIRECTORY] (Look into making it so
the python script adds it to directory automatically.)
### Key Components:
* The [GameWorld](https://github.com/NathanUlmen/OreForge/blob/main/core/src/main/java/ore/forge/Screens/GameWorld.java) class is where the game is rendered and the game logic is driven from.
* Under the [Strategies Folder](https://github.com/NathanUlmen/OreForge/tree/main/core/src/main/java/ore/forge/Strategies) you can find the classes that are responsible upgrade logic and effect logic.
* The [Ore](https://github.com/NathanUlmen/OreForge/blob/main/core/src/main/java/ore/forge/Ore.java) class is pretty important as it's what the game revolves around.
