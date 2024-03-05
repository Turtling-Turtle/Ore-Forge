package ore.forge.game.Items.Strategies.UpgradeStrategies.game;

public class ForLoopTest {

    public static void main(String[] args) {

        //Goal is to make a method that prints out an array of differing length and width AND same length and width
        //with the bottom left most element of the 2d array located at the origin(mouse position)


        squareConfig();



    }


    private static void rectangleConfig()  {
        //so in this example element ten should have an (x,y) coordinate of (0,0)

        int[][] dropperConfig = {
                {1,2,3},
                {4,5,6},
                {7,8,9},
                {10,11,12},
        };
        int rows = dropperConfig.length;
        int columns = dropperConfig[0].length;

        //simulates the placement of items at 0,0
        int x = 0;
        int y = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                System.out.print("\t" + dropperConfig[i][j]);
                System.out.print("\ti:" +i + " j:" + j);
                System.out.print("\tX:" + (x + j) + " Y:" + (y + rows -i -1) + "\t");
            }
            System.out.println();
        }
    }

    private static void squareConfig() {
        //in this example the element "7" should have a coordinate of (0,0)
        int[][] dropperConfig = {
                {1,2,},
                {4,5,},
//                {7,8,9},
        };

        int rows = dropperConfig.length;
        int columns = dropperConfig[0].length;

        int x = 0;
        int y = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = columns-1; j >= 0; j--) {
                System.out.print("\t" + dropperConfig[i][j]);
                System.out.print("\ti:" + i + " j:" + j);
                System.out.print("\tX:" + (x + j) + " Y:" + (y + rows -i -1) + "\t");
            }
            System.out.println();
        }

    }


}
