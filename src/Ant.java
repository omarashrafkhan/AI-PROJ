import javax.swing.*;
import java.util.*;

public class Ant implements Comparable<Ant> {
    //    LinkedList<Node> pathTaken;
    ImageIcon image;
    ArrayList<Node> openSet;
    Set<Node> closedSet;
    ArrayList<Node> antPath;
    int x, y;
    boolean completed;
    boolean openSetEmpty;
    ArrayList<Float> cumSum;


    public Ant() {
        this.image = new ImageIcon("src/ant_2.png");
        openSet = new ArrayList<>();
        closedSet = new HashSet<>();
        antPath = new ArrayList<>();
        cumSum = new ArrayList<>();

    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Ant other) {
        return Integer.compare(this.antPath.size(), other.antPath.size());
    }


    public void sort(ArrayList<Node> openSet) {
        // Define a custom comparator to compare Node objects based on their prob values
        Comparator<Node> comparator = (node1, node2) -> {
            // Compare the prob values of the two nodes
            // This will sort the nodes in ascending order of their prob values
            return Float.compare(node1.prob, node2.prob);
        };

        // Sort the openSet ArrayList using the custom comparator
        openSet.sort(comparator);
    }

    public synchronized void moveAnt(ArrayList<Node> singleIteration, float alpha, float beta) throws InterruptedException {


        openSet.add(PathPanel.startNode);


        while (!openSet.isEmpty()) {

            sort(openSet);


            //calculating cumulative probability sum for the given nodes in open set
            for (Node n : openSet) {
                int prevIndex = cumSum.size() - 1;

                if (prevIndex < 0) {

                    cumSum.add(n.prob);
                } else {

                    float prev = cumSum.get(prevIndex);

                    float probab = n.prob + prev;
                    cumSum.add(probab);
                }


            }

            Node curr = null;
            Random random = new Random();
            float randomFloat = random.nextFloat();

            if (randomFloat < 0.6) randomFloat += 0.2;
//            if (randomFloat < 0.7) randomFloat += 0.2;

            if (cumSum.size() == 1) {
                curr = openSet.get(0);
            } else {
                for (int i = 0; i < cumSum.size(); i++) {
                    if (randomFloat <= cumSum.get(i)) {
                        curr = openSet.get(i);
                        break; // Exit loop once a position is selected
                    }
                }
            }

            cumSum.clear();
            openSet.clear();

            setXY(curr.getX(), curr.getY());
            antPath.add(curr);
            singleIteration.add(curr);


            List<Node> neighbors = Utility.findAntNodes(curr);



            float[] denom = {0};
            for (Node neighbor : neighbors) {

                if (neighbor.equals(PathPanel.endNode)) {
                    completed = true;
                    System.out.println("found");

                    return;
                }


                if (antPath.contains(neighbor) || !Utility.inBounds(neighbor) || Utility.isInClosedAnt(neighbor, this) || Utility.isInWall(neighbor) || Utility.isAtDiagonal(neighbor, curr)) {
                    continue;
                }

                int h = Utility.calcHCost(neighbor, PathPanel.endNode);
                neighbor.setH(h);

                if (openSet.contains(neighbor)) {

                    Node existingNode = Utility.getNodeFromOpenSetForAnt(neighbor, openSet);

                    if (h < existingNode.getH()) {
                        existingNode.setH(h);
                    }
                }

                // if node does not exist in open list add it and append to the path
                else {
                    openSet.add(neighbor);
                }

                denom[0] += (Math.pow(neighbor.getPher(), alpha) * Math.pow(1 / (float) neighbor.getH(), beta));

                Thread.sleep(styleSheet.SLIDERMAX - PathPanel.slider.getValue()); // Adjust the delay as needed for animation
            }



            for (Node neighbor : neighbors) {

                if (antPath.contains(neighbor) || !Utility.inBounds(neighbor) || Utility.isInClosedAnt(neighbor, this) || Utility.isInWall(neighbor) || Utility.isAtDiagonal(neighbor, curr)) {
                    continue;
                }

                neighbor.prob = denom[0] == 0 ? 0 : (float) (Math.pow(neighbor.getPher(), alpha) * Math.pow(1 / (float) neighbor.getH(), beta)) / denom[0];
            }
        }
        System.out.println("open set empty");
        openSetEmpty = true;
    }
}
