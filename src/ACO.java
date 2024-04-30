import javax.swing.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ACO {
    static PriorityQueue<Node> openSet;
    static Map<Node, Node> cameFrom;
    static Set<Node> closedSet;
    int noOfAnts = 3;
    int noOfIterations = 5;
    PathPanel panel;
    LinkedList<Ant> ants;
    ArrayList<Node> traversed;
    ArrayList<Node> singleIteration;


    public ACO(PathPanel panel) {


        this.panel = panel;
        ants = new LinkedList<>();
        traversed = new ArrayList<>();
        singleIteration = new ArrayList<>();
        openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getPher).reversed());
        closedSet = new HashSet<>();
        cameFrom = new HashMap<>();


    }

    public void reset() {
        ants.clear();
        traversed.clear();
        singleIteration.clear();
        openSet.clear();
        closedSet.clear();
        cameFrom.clear();

    }


    boolean iterationCompleted() {
        boolean completed = true; // Assume all ants have completed by default

        // Check each ant's completion status
        for (Ant ant : ants) {
            if (!ant.completed) {
                completed = false; // If any ant hasn't completed, set completed to false
                break; // No need to continue checking, we already know not all ants have completed
            }
        }
        return completed;
    }

    public synchronized void startACO() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        float alpha = 0;
        float beta = 1;


        //add another for loop which determines the number of iterations for each batch of ants
        for (int i = 0; i < noOfIterations; i++) {

            System.out.println("Alpha is: " + alpha + ", Beta  is: " + beta);
            for (int j = 0; j < noOfAnts; j++) {

                ants.add(new Ant());
            }

            for (Ant ant : ants) {
                float finalAlpha = alpha;
                float finalBeta = beta;


                executor.submit(() -> {
                    try {
                        ant.moveAnt(singleIteration, finalAlpha, finalBeta);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            while (!iterationCompleted()) {
                ants.removeIf(ant -> ant.openSetEmpty);
                Thread.sleep(500);
            }


            System.out.println(ants.size());
            if (ants.size() == 0) {
                //ignore
            } else {
                alpha += (float) 1 / noOfIterations;
                beta -= (float) 1 / noOfIterations;

            }


            int pher = noOfAnts;
            Collections.sort(ants);

            for (Ant ant : ants) {

                //check ants in the list for their paths, if their path is complete then update the node's pher level according to the
                //shortest path having the largest amount of pheromone and decreasing as the path gets longer

                int finalPher = pher;

                ant.antPath.forEach(node -> node.setPher(finalPher));
                pher--;

            }


            HashSet<Node> set1 = new HashSet<>(singleIteration);
            singleIteration.clear();
            singleIteration.addAll(set1);


            for (Node node : singleIteration) {
                if (traversed.contains(node)) {
                    Node n = traversed.get(traversed.indexOf(node));
                    n.setPher(node.getPher() + n.getPher());


                } else {
                    traversed.add(node);
                }
            }

            HashSet<Node> set = new HashSet<>(traversed);
            traversed.clear();
            traversed.addAll(set);


            singleIteration.clear();
            ants.clear();


            while (PathPanel.paused) {
                Thread.sleep(100);
            }

        }

        findOptimalPath();


        // Shutdown the executor once all tasks are submitted
        executor.shutdown();

        // Wait for all tasks to complete, or until timeout occurs
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void findOptimalPath() {


        PathPanel.openSet = openSet;
        PathPanel.closedSet = closedSet;
        PathPanel.cameFrom = cameFrom;

        openSet.add(PathPanel.endNode);

        while (!openSet.isEmpty()) {

            Node curr = openSet.poll();

            closedSet.add(curr);
//            openSet.clear();

            if (curr.equals(PathPanel.startNode)) {
                panel.showMessage("Path Found", styleSheet.MESSAGETIME);

                Utility.reconstructPath(PathPanel.startNode); // Implement path reconstruction
                panel.showMessage("--- Path's Length is: "+String.valueOf(PathPanel.shortestPath.size()),styleSheet.MESSAGETIME);
                return;
            }

            List<Node> neighbors = Utility.findNodes(curr);

            for (Node neighbor : neighbors) {
                if (!traversed.contains(neighbor) || closedSet.contains(neighbor)) {
                    continue;
                }

                neighbor = traversed.get(traversed.indexOf(neighbor));

                if (openSet.contains(neighbor)) {
                    // if it exists, get the node in open list, so we can access the g cost
                    Node existingNode = Utility.getNodeFromOpenSet(neighbor, openSet);
                    // if the g cost calculated for current neighbor is less than the existing node's g cost
                    assert existingNode != null;
                    if (curr.getPher() > existingNode.getPher()) {


                        cameFrom.put(neighbor, curr);
                    }
                }
                // if node does not exist in open list add it and append to the path
                else {
                    cameFrom.put(neighbor, curr);
                    openSet.add(neighbor);
                }
            }
        }




        panel.showMessage("No Path", styleSheet.MESSAGETIME);



    }


}
