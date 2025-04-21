// I need to see how to implement this because it looks like the maze thing was hardcoded in GameView
package com.example.catsandmazes;

import java.util.*;

public class MazeGenerator 
{
    private int rows, cols;
    private int[][] maze;
    private Random rand = new Random();
    private String algorithm;

    public MazeGenerator(String difficulty) // Setting diffculty
    {
        switch (difficulty) 
        {
            case "Easy":
                this.rows = 9;
                this.cols = 9;
                this.algorithm = "Prim";
                break;
            case "Medium":
                this.rows = 21;
                this.cols = 21;
                this.algorithm = "Prim";
                break;
            case "Hard":
                this.rows = 51;
                this.cols = 51;
                this.algorithm = "Aldous-Broder";
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        // Ensure rows and cols are odd
        this.rows = (this.rows % 2 == 0) ? this.rows + 1 : this.rows;
        this.cols = (this.cols % 2 == 0) ? this.cols + 1 : this.cols;

        // Initialize the maze with walls
        this.maze = new int[this.rows][this.cols];
        for (int[] row : maze) Arrays.fill(row, 1);
    }

    public int[][] generateMaze() 
    {
        if ("Prim".equals(algorithm)) 
        {
            return generatePrim();
        } else if ("Aldous-Broder".equals(algorithm)) 
        {
            return generateAldousBroder();
        } else {
            throw new IllegalStateException("Unsupported algorithm");
        }
    }

    private int[][] generatePrim() 
    {
        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        List<int[]> walls = new ArrayList<>();
        int r = 1, c = 1;
        maze[r][c] = 0;
        addWalls(walls, r, c);

        while (!walls.isEmpty()) 
        {
            int[] wall = walls.remove(rand.nextInt(walls.size()));
            int wr = wall[0], wc = wall[1];

            for (int[] d : dirs) 
            {
                int nr = wr + d[0], nc = wc + d[1];
                int betweenR = wr + d[0] / 2, betweenC = wc + d[1] / 2;

                if (inBounds(nr, nc) && maze[nr][nc] == 1) 
                {
                    int backR = wr - d[0], backC = wc - d[1];

                    if (inBounds(backR, backC) && maze[backR][backC] == 0) 
                    {
                        maze[wr][wc] = 0;
                        maze[nr][nc] = 0;
                        maze[betweenR][betweenC] = 0;
                        addWalls(walls, nr, nc);
                        break;
                    }
                }
            }
        }

        maze[1][1] = 2;  // Start
        maze[rows - 2][cols - 2] = 3;  // End

        return maze;
    }

    private int[][] generateAldousBroder() 
    {
        int r = 1, c = 1;
        maze[r][c] = 0;
        int unvisitedCount = (rows / 2) * (cols / 2) - 1; // Count of unvisited cells

        while (unvisitedCount > 0) 
        {
            int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
            int[] dir = dirs[rand.nextInt(dirs.length)];
            int nr = r + dir[0], nc = c + dir[1];

            if (inBounds(nr, nc)) 
            {
                if (maze[nr][nc] == 1) 
                {
                    maze[nr][nc] = 0;
                    maze[r + dir[0] / 2][c + dir[1] / 2] = 0; // Connect cells
                    unvisitedCount--;
                }
                r = nr;
                c = nc; // Move to the next cell
            } else 
            {
                r = rand.nextInt(rows);
                c = rand.nextInt(cols); // Pick a random cell
            }
        }

        // Add start (2) and end (3) tiles
        maze[1][1] = 2;  // Start
        maze[rows - 2][cols - 2] = 3;  // End

        return maze;
    }

    private void addWalls(List<int[]> walls, int r, int c) 
    {
        int[][] directions = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        for (int[] d : directions) {
            int wr = r + d[0], wc = c + d[1];
            if (inBounds(wr, wc) && maze[wr][wc] == 1) 
            {
                walls.add(new int[]{wr, wc});
            }
        }
    }

    private boolean inBounds(int r, int c) 
    {
        return r > 0 && r < rows - 1 && c > 0 && c < cols - 1;
    }
}
