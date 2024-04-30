import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Set;

public class Drawing {

    static int size = PathPanel.size;



    static void drawGrid(Graphics g, JPanel panel) {
        g.setColor(styleSheet.GRID_COLOR);
        for (int j = 0; j < panel.getHeight(); j += PathPanel.size) {
            for (int i = 0; i < panel.getWidth(); i += PathPanel.size) {
                g.drawRect(i, j, PathPanel.size, PathPanel.size);
            }
        }
    }

    static void drawWalls(Graphics g) {
        g.setColor(styleSheet.WALL_COLOR);
        for (Node value : PathPanel.wall) {
            g.fillRect(value.getX() + 1, value.getY() + 1, PathPanel.size - 1, PathPanel.size - 1);
        }
    }

    static void drawNodes(Graphics g) {


        if (PathPanel.algorithmRunning  && PathPanel.aco.traversed.size()==0) {
            drawOpenSet(g);
            drawClosedSet(g);
        }
        drawShortestPath(g);
        drawEndNode(g);
        drawStartNode(g);

    }

    private static void drawOpenSet(Graphics g) {
        g.setColor(styleSheet.OPEN_SET_COLOR);
        for (Node node : PathPanel.openSet) {
            g.fillRect(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
        }
    }

    private static void drawClosedSet(Graphics g) {
        g.setColor(styleSheet.CLOSED_SET_COLOR);
        for (Node node : PathPanel.closedSet) {
            g.fillRect(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
        }
    }

    private static void drawShortestPath(Graphics g) {
        if (PathPanel.shortestPath != null) {
            g.setColor(styleSheet.PATH_COLOR);
            for (Node node : PathPanel.shortestPath) {
                g.fillOval(node.getX() + 1, node.getY() + 1, size - 1, size - 1);
            }
        }
    }

    private static void drawEndNode(Graphics g) {
        if (PathPanel.endNode != null) {
            g.setColor(styleSheet.END_NODE_COLOR);
            g.fillRect(PathPanel.endNode.getX() + 1, PathPanel.endNode.getY() + 1, size - 1, size - 1);
        }
    }

    private static void drawStartNode(Graphics g) {
        if (PathPanel.startNode != null) {

            g.setColor(styleSheet.START_NODE_COLOR);
            g.fillRect(PathPanel.startNode.getX() + 1, PathPanel.startNode.getY() + 1, size - 1, size - 1);
        }
    }

    static void drawNodeDetails(Node node, Graphics g) {
        g.setColor(styleSheet.COST_COLOR);
        g.setFont(new Font("TimesRoman", Font.BOLD, 10));
        g.drawString(String.valueOf(node.getF()), node.getX() + 10, node.getY() + 25);
    }

    static void drawAnt(Ant ant, Graphics g, JPanel panel){

        g.drawImage(ant.image.getImage(), ant.x, ant.y, PathPanel.size-5,PathPanel.size-5,panel);




    }
}
