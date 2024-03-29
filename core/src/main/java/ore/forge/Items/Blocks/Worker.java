package ore.forge.Items.Blocks;


import ore.forge.Ore;

//The classes that implement the worker interface handle ore in a unique way such as moving ore upgrading it.
//Ore is "Allowed"/intended to be ont these blocks.
public interface Worker {
    void handle(Ore ore);
}
