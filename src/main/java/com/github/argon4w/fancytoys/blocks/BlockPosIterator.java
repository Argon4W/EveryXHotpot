package com.github.argon4w.fancytoys.blocks;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

public class BlockPosIterator implements Iterator<LevelBlockPos> {

    private final LinkedList<LevelBlockPos> list;
    private final Predicate<LevelBlockPos> filter;
    private Node node;

    public BlockPosIterator(LevelBlockPos pos, Predicate<LevelBlockPos> filter) {
        this.node = new Node(pos, null);
        this.list = new LinkedList<>();
        this.filter = filter;
    }

    @Override
    public LevelBlockPos next() {
        if (!hasNext()) {
            return null;
        }

        LevelBlockPos result = node.getPos();
        list.add(result);
        node = getNode(node);

        return result;
    }

    @Override
    public boolean hasNext() {
        return node != null;
    }

    private Node getNode(Node node) {
        Node next;

        while (node.hasNextNode()) {
            next = node.next();
            LevelBlockPos pos = next.getPos();

            if (list.contains(pos)) {
                continue;
            }

            if (!filter.test(pos)) {
                continue;
            }

            return next;
        }

        if (node.getParent() == null) {
            return null;
        }

        return getNode(node.getParent());
    }

    public static class Node {
        private final LevelBlockPos[] neighbors;
        private final LevelBlockPos pos;
        private final Node parent;
        private int index;

        public Node(LevelBlockPos pos, Node parent) {
            index = 0;
            this.pos = pos;
            this.parent = parent;
            neighbors = new LevelBlockPos[] {pos.north(), pos.south(), pos.east(), pos.west()};
        }

        public boolean hasNextNode() {
            return index < 4;
        }

        public Node next() {
            return new Node(neighbors[index++], this);
        }

        public LevelBlockPos getPos() {
            return pos;
        }

        public Node getParent() {
            return parent;
        }
    }
}
