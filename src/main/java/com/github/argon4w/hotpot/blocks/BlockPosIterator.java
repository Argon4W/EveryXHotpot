package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

public class BlockPosIterator implements Iterator<LevelBlockPos> {
    private final LinkedList<LevelBlockPos> filtered;
    private final Predicate<LevelBlockPos> filter;
    private Node node;

    public BlockPosIterator(LevelBlockPos selfPos, Predicate<LevelBlockPos> filter) {
        node = new Node(selfPos, null);
        filtered = new LinkedList<>();
        this.filter = filter;
    }

    @Override
    public LevelBlockPos next() {
        if (!hasNext()) {
            return null;
        }

        LevelBlockPos result = node.getSelfPos();
        filtered.add(result);
        node = getNode(node);

        return result;
    }

    @Override
    public boolean hasNext() {
        return node != null;
    }

    private Node getNode(Node node) {
        Node nextNode;

        while (node.hasNextNode()) {
            nextNode = node.getNextNode();
            LevelBlockPos pos = nextNode.getSelfPos();

            if (!filtered.contains(pos) && filter.test(pos)) {
                return nextNode;
            }
        }

        return node.getRoot() == null ? null : getNode(node.getRoot());
    }

    public static class Node {
        private final LevelBlockPos[] otherPos;
        private final LevelBlockPos selfPos;
        private final Node root;
        private int index;

        public Node(LevelBlockPos pos, Node root) {
            index = 0;
            selfPos = pos;
            this.root = root;
            otherPos = new LevelBlockPos[] {pos.north(), pos.south(), pos.east(), pos.west()};
        }

        public boolean hasNextNode() {
            return index < 4;
        }

        public Node getNextNode() {
            return new Node(otherPos[index ++], this);
        }

        public LevelBlockPos getSelfPos() {
            return selfPos;
        }

        public Node getRoot() {
            return root;
        }
    }
}
