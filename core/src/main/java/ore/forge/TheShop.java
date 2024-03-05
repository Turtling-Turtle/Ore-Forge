package ore.forge;

public class TheShop {
    static float deltaTime;
    static boolean isRunning;
    public static void main(String[] args) {
        //You have the origin of an obejct (which is the position)
        // You have the area of an object
        //When you move you go through all entities and compare origin and position to the area of all other enteties.
        int [][] conveyorConfig = {
                {1, 1},
                {1, 1},
                {1, 1, 1, 1, 1, 1},
        };

        for (int i = 0; i < conveyorConfig.length; i++) {
            for (int j = 0; j < conveyorConfig[i].length; j++) {
                System.out.print(conveyorConfig[i][j]);
            }
            System.out.println();
        }
    }

    public TheShop() {

    }

}
