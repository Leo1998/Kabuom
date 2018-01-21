package controller;

import static utility.Utility.random;

public class Test {

    /**
     * Test für Array-Breitensuche
     */
    public static void main(String[] args){
        boolean[][] nodeMap = new boolean[5][5];

        int x = random.nextInt(5), y = random.nextInt(5);

        System.out.println(x+" "+y);

        String result = "";

        for(int range = 0; range <= Math.max(Math.max(x, nodeMap.length-x),Math.max(y, nodeMap[x].length-y)); range++) {
            for (int i = Math.max(0, x - range); i < Math.min(nodeMap.length, x + range + 1); i++) {
                boolean full = (i == x - range || i == x + range);
                for (int j = full ? Math.max(0, y - range) : (y - range < 0) ? y + range : y - range; j < Math.min(nodeMap[i].length, y + range + 1); j += full ? 1 : 2 * range) {
                    result += "("+i+"|"+j+")";
                    nodeMap[i][j] = true;
                }
            }
            result+="\n";
        }
        System.out.println(result);

        for(boolean[] booleans:nodeMap){
            for(boolean b:booleans){
                if(!b)
                    System.out.println("Fehler");
            }
        }
    }
}
