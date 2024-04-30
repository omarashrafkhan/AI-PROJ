import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class Utility {

    public static boolean isInClosed(Node node) {
        for (Node n : PathPanel.closedSet) {
            if (n.compareTo(node) == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInClosedAnt(Node node,Ant ant) {
        for (Node n : ant.closedSet) {
            if (n.compareTo(node) == 0) {
                return true;
            }
        }
        return false;
    }


    public static boolean isInWall(Node node) {
        for (Node n : PathPanel.wall) {
            if (n.compareTo(node) == 0) {
                return true;
            }
        }
        return false;
    }


    public static boolean isAtDiagonal(Node node, Node curr) {
        if (Utility.isInWall(new Node(node.getX() - PathPanel.size, node.getY()))
                && isInWall(new Node(node.getX(), node.getY() + PathPanel.size))) {
            PathPanel.closedSet.add(node);
            return true;
        } else if (isInWall(new Node(node.getX() - PathPanel.size, node.getY()))
                && isInWall(new Node(node.getX(), node.getY() - PathPanel.size))) {
            PathPanel.closedSet.add(node);
            return true;
        } else if (!(node.getX() > curr.getX() && node.getY() > curr.getY())
                && isInWall(new Node(node.getX() + PathPanel.size, node.getY()))
                && isInWall(new Node(node.getX(), node.getY() + PathPanel.size))) {
            PathPanel.closedSet.add(node);
            return true;
        } else if (isInWall(new Node(node.getX() + PathPanel.size, node.getY()))
                && isInWall(new Node(node.getX(), node.getY() - PathPanel.size))) {
            PathPanel.closedSet.add(node);
            return true;
        }
        return false;
    }


    public synchronized static void addToArrayList(Node node, ArrayList<Node> list){
        list.add(node);
    }

    public static List<Node> findNodes(Node center) {
        List<Node> neighbors = new ArrayList<>();

        neighbors.add(new Node(center.getX() + PathPanel.size, center.getY()));
        neighbors.add(new Node(center.getX() - PathPanel.size, center.getY()));
        neighbors.add(new Node(center.getX(), center.getY() + PathPanel.size));
        neighbors.add(new Node(center.getX(), center.getY() - PathPanel.size));
        neighbors.add(new Node(center.getX() + PathPanel.size, center.getY() + PathPanel.size));
        neighbors.add(new Node(center.getX() - PathPanel.size, center.getY() - PathPanel.size));
        neighbors.add(new Node(center.getX() + PathPanel.size, center.getY() - PathPanel.size));
        neighbors.add(new Node(center.getX() - PathPanel.size, center.getY() + PathPanel.size));

        return neighbors;
    }

    public static List<Node> findAntNodes(Node center) {
        List<Node> neighbors = new ArrayList<>();

        neighbors.add(new Node(center.getX() + PathPanel.size, center.getY()));
        neighbors.add(new Node(center.getX() - PathPanel.size, center.getY()));
        neighbors.add(new Node(center.getX(), center.getY() + PathPanel.size));
        neighbors.add(new Node(center.getX(), center.getY() - PathPanel.size));
        neighbors.add(new Node(center.getX() + PathPanel.size, center.getY() + PathPanel.size));
        neighbors.add(new Node(center.getX() - PathPanel.size, center.getY() - PathPanel.size));
        neighbors.add(new Node(center.getX() + PathPanel.size, center.getY() - PathPanel.size));
        neighbors.add(new Node(center.getX() - PathPanel.size, center.getY() + PathPanel.size));

        return neighbors;
    }

    public static int calcHCost(Node A, Node B){
        int dx = Math.abs(A.getX() - B.getX());
        int dy = Math.abs(A.getY() - B.getY());
        int h = (dx + dy) + (2) * Math.min(dx, dy);
        return h;
    }


    public static float calcAntHCost(Node A, Node B ){

        int hCost = calcHCost(A,B);

//        Random random = new Random();
//        int randomFactor = random.nextInt(800);

//        return (float) (0.8*hCost+0.2*randomFactor);
        return (float) (hCost);


    }

    static void reconstructPath(Node endNode) {

        PathPanel.shortestPath = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            PathPanel.shortestPath.add(current);

            current = PathPanel.cameFrom.get(current);

        }
        Collections.reverse(PathPanel.shortestPath);
    }

    public static boolean inBounds(Node node) {
        return node.getX() + PathPanel.size <= Toolkit.getDefaultToolkit().getScreenSize().getWidth()-50 && node.getX() >= 25 && node.getY() + PathPanel.size <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()-50
                && node.getY() >= 25;
    }


    public static Node getNodeFromOpenSet(Node n, PriorityQueue<Node> nodes) {
        if (nodes.contains(n)) {
            PriorityQueue<Node> copy = new PriorityQueue<>(nodes);
            while (!copy.isEmpty()) {
                Node out = copy.poll();
                if (n.equals(out)) {
                    return out;
                }
            }
        }
        return null;
    }
    public static Node getNodeFromOpenSetForAnt(Node n, ArrayList<Node> nodes) {
        if (nodes.contains(n)) {
            PriorityQueue<Node> copy = new PriorityQueue<>(nodes);
            while (!copy.isEmpty()) {
                Node out = copy.poll();
                if (n.equals(out)) {
                    return out;
                }
            }
        }
        return null;
    }

}

