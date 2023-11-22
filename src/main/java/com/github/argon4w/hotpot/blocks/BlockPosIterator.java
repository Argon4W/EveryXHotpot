package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.BlockPosWithLevel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

public class BlockPosIterator implements Iterator<BlockPosWithLevel> {
    private final LinkedList<BlockPosWithLevel> filtered;
    private final Predicate<BlockPosWithLevel> filter;
    private Node node;

    public BlockPosIterator(BlockPosWithLevel selfPos, Predicate<BlockPosWithLevel> filter) {
        node = new Node(selfPos, null);
        filtered = new LinkedList<>();
        this.filter = filter;
    }

    @Override
    public BlockPosWithLevel next() {
        if (!hasNext()) {
            return null;
        }

        BlockPosWithLevel result = node.getSelfPos();
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
            BlockPosWithLevel pos = nextNode.getSelfPos();

            if (!filtered.contains(pos) && filter.test(pos)) {
                return nextNode;
            }
        }

        return node.getRoot() == null ? null : getNode(node.getRoot());
    }

    public static class Node {
        private final BlockPosWithLevel[] otherPos;
        private final BlockPosWithLevel selfPos;
        private final Node root;
        private int index;

        public Node(BlockPosWithLevel pos, Node root) {
            index = 0;
            selfPos = pos;
            this.root = root;
            otherPos = new BlockPosWithLevel[] {pos.north(), pos.south(), pos.east(), pos.west()};
        }

        public boolean hasNextNode() {
            return index < 4;
        }

        public Node getNextNode() {
            return new Node(otherPos[index ++], this);
        }

        public BlockPosWithLevel getSelfPos() {
            return selfPos;
        }

        public Node getRoot() {
            return root;
        }
    }
}
