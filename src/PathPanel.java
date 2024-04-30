import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;


public class PathPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {


    public static Node startNode;
    public static Node endNode;
    public static ACO aco;
    public static boolean paused = false;
    static Set<Node> closedSet;
    static int size; // size of the squares being printed
    static ArrayList<Node> wall; // wall for obstacles
    static PriorityQueue<Node> openSet;
    static Map<Node, Node> cameFrom;
    static List<Node> shortestPath; // final shortest path being drawn
    static boolean algorithmRunning = false;
    static JSlider slider;
    char currentKey = (char) 0; // used to define start and end nodes
    JButton start, reset;
    JButton stopContinue; // New button for stopping/continuing the process
    JComboBox<String> selector;

    public PathPanel() {
        size = 32;
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);
        aco = new ACO(this);
        start = new JButton("Start");
        reset = new JButton("Reset");
        stopContinue = new JButton("Stop");
        selector = new JComboBox<>(new String[]{"ACO", "A*"});
        slider = new JSlider(0, styleSheet.SLIDERMAX, styleSheet.SLIDERMAX);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(styleSheet.SLIDERMAX / 2);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel(""));
        labelTable.put(styleSheet.SLIDERMAX / 2, new JLabel("SET SPEED"));
        labelTable.put(styleSheet.SLIDERMAX, new JLabel(""));
        slider.setLabelTable(labelTable);

        add(start);
        add(reset);
        add(stopContinue);
        add(slider);
        add(selector);

        stopContinue.addActionListener(e -> {
            togglePause(); // Toggle the paused state on button click

            if (paused) {
                stopContinue.setText("Continue");
            } else {
                stopContinue.setText("Stop");
            }
        });

        openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        closedSet = new HashSet<>();
        cameFrom = new HashMap<>();
        wall = new ArrayList<>();

        start.addActionListener(e -> startAlgorithm());

        reset.addActionListener(e -> reset());


        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Toolkit.getDefaultToolkit().sync();
                repaint();
            }
        }, 0, 25);


    }


    public void reset() {
//        startNode = null;
//        endNode = null;
//        wall.clear();
        openSet.clear();
        closedSet.clear();
        cameFrom.clear();
        shortestPath = null;

        paused = false;
        repaint();
        requestFocus();

        aco.reset();
    }

    private void togglePause() {
        paused = !paused;
    }

    @Override
    protected void paintComponent(Graphics g) {


        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // grid being drawn
        Drawing.drawGrid(g, this);

        // wall being drawn
        Drawing.drawWalls(g);


        try {
            Drawing.drawNodes(g);


            for (Ant ant : aco.ants) {
                Drawing.drawAnt(ant, g, this);

            }


            for (Node node : aco.traversed) {
                g.drawString(String.valueOf(node.getPher()), node.getX() + 10, node.getY() + 20);
                repaint();
//                    this.getGraphics().dispose();
            }


            if (aco.traversed.size() == 0) {

                for (Node node : openSet) {
                    Drawing.drawNodeDetails(node, g);
                }

                for (Node node : closedSet) {
                    Drawing.drawNodeDetails(node, g);
                }

            }

        } catch (Exception ignored) {

        }


        g.setColor(styleSheet.BARCOLOR);
        g.fillRect(0, 0, 2000, 50);
        g.setColor(new Color(230, 230, 255));
        g.fillRoundRect(6, 4, 165, 44, 10, 10);
        g.setColor(Color.black);
        g.setFont(new Font("TimesRoman", Font.BOLD, 10));
        g.drawString("set walls: click or drag", 11, 15);
        g.drawString("set start node: hold s + click", 11, 30);
        g.drawString("set end node: hold e + click", 11, 45);


    }


    private void startAlgorithm() {
        SwingWorker<Void, Void> pathfindingWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                algorithmRunning = true;
                try {
                    if (selector.getSelectedIndex() == 0) {
                        aco.startACO();
                    } else {
                        openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
                        closedSet = new HashSet<>();
                        cameFrom = new HashMap<>();
                        pathFind();


                    }


                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            }

            @Override
            protected void done() {
                repaint();
            }
        };

        pathfindingWorker.execute();
    }


    public void pathFind() throws InterruptedException {
        if (endNode == null) {
            showMessage("End node is not set.", styleSheet.MESSAGETIME);
            System.out.println("End node is not set.");
            return;
        }

        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node curr = openSet.poll();
            closedSet.add(curr);

            if (curr.equals(endNode)) {
                showMessage("Path found", styleSheet.MESSAGETIME);
                System.out.println("Path found");
                Utility.reconstructPath(endNode); // Implement path reconstruction
                showMessage(String.valueOf(PathPanel.shortestPath.size()),styleSheet.MESSAGETIME);
                return;
            }

            List<Node> neighbors = Utility.findNodes(curr);

            for (Node neighbor : neighbors) {
                if (Utility.isInClosed(neighbor) || Utility.isInWall(neighbor) || Utility.isAtDiagonal(neighbor, curr) || !Utility.inBounds(neighbor)) {
                    continue;
                }

                // Computing g cost for the successor node
                int g = curr.getG() + curr.distance(neighbor);
                neighbor.setG(g);

                // Compute h cost
                int h = Utility.calcHCost(neighbor, endNode);
                neighbor.setH(h);

                // Compute f cost
                int f = h + g;
                neighbor.setF(f);

                // check if the node exists already in open list
                if (openSet.contains(neighbor)) {
                    // if it exists, get the node in open list, so we can access the g cost
                    Node existingNode = Utility.getNodeFromOpenSet(neighbor, openSet);
                    // if the g cost calculated for current neighbor is less than the existing node's g cost
                    assert existingNode != null;
                    if (g < existingNode.getG()) {
                        // update the node's cost respectively
                        existingNode.setG(g);
                        existingNode.setF(f);
                        existingNode.setH(h);
                        // update the node on path map
                        cameFrom.put(neighbor, curr);
                    }

                }
                // if node does not exist in open list add it and append to the path
                else {
                    cameFrom.put(neighbor, curr);
                    openSet.add(neighbor);
                }

            }

            repaint();

            while (paused) {
                Thread.sleep(100);
            }

            Thread.sleep(styleSheet.SLIDERMAX - slider.getValue()); // Adjust the delay as needed for animation
        }
        showMessage("No path", styleSheet.MESSAGETIME);
        System.out.println("No path");
    }


    private void createWall(MouseEvent e) {
        int xBorder = e.getX() - (e.getX() % size);
        int yBorder = e.getY() - (e.getY() % size);
        Node newBorder = new Node(xBorder, yBorder);

        if (SwingUtilities.isLeftMouseButton(e) && currentKey != styleSheet.START_NODE_KEY && currentKey != styleSheet.END_NODE_KEY) {
            wall.add(newBorder);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            wall.remove(newBorder);
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            createWall(e);

            if (currentKey == 's') {

                int xRollover = e.getX() % size;
                int yRollover = e.getY() % size;

                if (startNode == null) {
                    startNode = new Node(e.getX() - xRollover, e.getY() - yRollover);

                } else {
                    startNode.setXY(e.getX() - xRollover, e.getY() - yRollover);
                }
                repaint();
            }
            // If 'e' is pressed create end node
            else if (currentKey == 'e') {
                int xRollover = e.getX() % size;
                int yRollover = e.getY() % size;

                if (endNode == null) {
                    endNode = new Node(e.getX() - xRollover, e.getY() - yRollover);
                } else {
                    endNode.setXY(e.getX() - xRollover, e.getY() - yRollover);
                }
                repaint();
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            int mouseBoxX = e.getX() - (e.getX() % size);
            int mouseBoxY = e.getY() - (e.getY() % size);

            // If 's' is pressed remove start node
            if (currentKey == 's') {
                if (startNode != null && mouseBoxX == startNode.getX() && startNode.getY() == mouseBoxY) {
                    startNode = null;
                    repaint();
                }
            }
            // If 'e' is pressed remove end node
            else if (currentKey == 'e') {
                if (endNode != null && mouseBoxX == endNode.getX() && endNode.getY() == mouseBoxY) {
                    endNode = null;
                    repaint();
                }
            }
        }

    }


    public void showMessage(String message, int durationMs) {
        JLabel a = new JLabel(message);
        add(a);
        revalidate();
        Timer timer1 = new Timer(durationMs, (ActionEvent e) -> {
            remove(a);
            revalidate();
            repaint();
        });

        timer1.setRepeats(false);
        timer1.start();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

        currentKey = e.getKeyChar();

    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKey = (char) 0;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        createWall(e);

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

}
