package com.example.catsandmazes;

import java.util.Objects;

public class Node {
    public int x;
    public int y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Node other = (Node) obj;
        return other.x == this.x && other.y == this.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Node{x=" + x + ", y=" + y + "}";
    }
}
