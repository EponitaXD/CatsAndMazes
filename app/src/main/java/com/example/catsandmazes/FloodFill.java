package com.example.catsandmazes;

import java.util.ArrayList;
import java.util.HashMap;


/*
 * This is the class to solve the maze
 */
public class FloodFill {

    private final int[][] maze;

    // Data Structures to track nodes in dijkstra's algorithm
    private final HashMap<Node, Integer> pathTable;
    private final HashMap<Node, Node> prevNodeTable;
    private final ArrayList<Node> unvisited;

    public FloodFill(int[][] maze) {
        this.maze = maze;
        pathTable = new HashMap<Node, Integer>(); // Maps nodes to their distance from the source
        prevNodeTable = new HashMap<Node, Node>(); // Maps nodes to their previous node
        unvisited = new ArrayList<Node>(maze.length * maze.length);

        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                if (maze[y][x] == 0) {
                    continue;
                }
                Node node = new Node(x, y);
                pathTable.put(node, Integer.MAX_VALUE);
                prevNodeTable.put(node, null);
                unvisited.add(node);
            }
        }
    }

    public static void printPath(Node[] path) {
        if (path == null) {
            System.out.println("null");
        } else {
            for (Node node : path) {
                System.out.print(node + " ");
            }
            System.out.println();
        }
    }

    public void printPathTable() {
        for (Node key : pathTable.keySet()) {
            System.out.print(key);
            System.out.println(": " + pathTable.get(key).toString());
        }
        System.out.println();
    }

    public void printPrevNodeTable() {
        for (Node key : prevNodeTable.keySet()) {
            System.out.print(key);
            System.out.println(": " + prevNodeTable.get(key));
        }
        System.out.println();
    }

    public Node[] solve() {
        // find the start and the end nodes
        Node start = new Node(-1, -1);
        Node end = new Node(-1, -1);
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[y].length; x++) {
                if (maze[y][x] == 3) {
                    end.x = x;
                    end.y = y;
                } else if (maze[y][x] == 2) {
                    start.x = x;
                    start.y = y;
                }
            }
        }

        // If the start and end nodes were not found, the input was bad, return null to signify error
        if (end.x == -1 && end.y == -1) {
            return new Node[0];
        }
        if (start.x == -1 && start.y == -1) {
            return new Node[0];
        }

        // initialize the start node shortest path to 0
        pathTable.put(start, 0);
        Node currentNode = start;
        while (unvisited.size() > 0) {
            int currentNodePath = pathTable.get(currentNode);
            // For each adjacent node:
            // put the current node's shortest path + 1 into the pathTable (key is currentNode)

            // north node
            Node northNode = new Node(currentNode.x, currentNode.y - 1);
            if (northNode.y >= 0 &&
                    (maze[northNode.y][northNode.x] == 1 || maze[northNode.y][northNode.x] == 3) &&
                    unvisited.contains(northNode)
            ) {
                pathTable.put(northNode, currentNodePath + 1);
                prevNodeTable.put(northNode, currentNode);
            }

            // south node
            Node southNode = new Node(currentNode.x, currentNode.y + 1);
            if (southNode.y < maze.length &&
                    (maze[southNode.y][southNode.x] == 1 || maze[southNode.y][southNode.x] == 3) &&
                    unvisited.contains(southNode)
            ) {
                pathTable.put(southNode, currentNodePath + 1);
                prevNodeTable.put(southNode, currentNode);
            }

            // east node
            Node eastNode = new Node(currentNode.x - 1, currentNode.y);
            if (eastNode.x >= 0 &&
                    (maze[eastNode.y][eastNode.x] == 1 || maze[eastNode.y][eastNode.x] == 3) &&
                    unvisited.contains(eastNode)
            ) {
                pathTable.put(eastNode, currentNodePath + 1);
                prevNodeTable.put(eastNode, currentNode);
            }

            // west node
            Node westNode = new Node(currentNode.x + 1, currentNode.y);
            if (westNode.x < maze[0].length &&
                    (maze[westNode.y][westNode.x] == 1 || maze[westNode.y][westNode.x] == 3) &&
                    unvisited.contains(westNode)
            ) {
                pathTable.put(westNode, currentNodePath + 1);
                prevNodeTable.put(westNode, currentNode);
            }

            unvisited.remove(currentNode);

            // Set currentNode to the node in the unvisited list with the shortest path
            if (unvisited.size() <= 0) {
                continue;
            }

            Node minimum = unvisited.get(0);
            for (Node node : unvisited) {
                if (pathTable.get(node) < pathTable.get(minimum)) {
                    minimum = node;
                }
            }
            currentNode = minimum;
        }

        return getPath(pathTable.get(end), start, end);
    }

    private Node[] getPath(int pathLen, Node start, Node end) {
        // If this is true, there is no path from start to end
        if (pathLen == Integer.MAX_VALUE) {
            return null;
        }

        Node[] bestPath = new Node[pathLen + 1];
        int insertionIdndex = bestPath.length - 1;

        Node currentNode = end;
        while (currentNode != null) {
            bestPath[insertionIdndex] = currentNode;
            currentNode = prevNodeTable.get(currentNode);
            insertionIdndex--;
        }

        return bestPath;
    }

    public void printMaze() {
        for (int[] row : maze) {
            for (int cell : row) {
                System.out.print(cell);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}