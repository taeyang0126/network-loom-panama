package com.lei.network.loom.panama.util;

import com.lei.network.loom.panama.constant.Constants;
import com.lei.network.loom.panama.exception.ExceptionType;
import com.lei.network.loom.panama.exception.FrameworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 使用 int 作为 key 的map   <br/>
 * <ol>
 *     <li>不考虑线程安全</li>
 *     <li>不考虑扩容</li>
 *     <li>put时不考虑key已存在，因为此map的key用来存放socket，底层的socket不会重复</li>
 * </ol>
 *
 * </p>
 *
 * @author 伍磊
 */
public final class IntMap<T> {

    private final IntMapNode<T>[] nodes;
    private final int mask;
    private int count = 0;

    @SuppressWarnings("unchecked")
    public IntMap(int size) {
        // 判断二进制中1的个数
        // 这里相当于写死了1的个数只能有1个，也就是 size = 2^n
        if (Integer.bitCount(size) != 1) {
            throw new FrameworkException(ExceptionType.CONTEXT, Constants.UNREACHED);
        }
        this.mask = size - 1;
        this.nodes = (IntMapNode<T>[]) new IntMapNode[size];
    }

    public T get(int val) {
        int slot = val & mask;
        IntMapNode<T> current = nodes[slot];
        while (current != null) {
            if (current.val == val) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public void put(int val, T value) {
        IntMapNode<T> n = new IntMapNode<>();
        n.val = val;
        n.value = value;
        int slot = val & mask;
        IntMapNode<T> current = nodes[slot];
        if (current != null) {
            n.next = current;
            current.prev = n;
        }
        nodes[slot] = n;
        count++;
    }

    public void replace(int val, T oldValue, T newValue) {
        int slot = val & mask;
        IntMapNode<T> current = nodes[slot];
        while (current != null) {
            if (current.val == val && current.value == oldValue) {
                current.value = newValue;
                return;
            } else {
                current = current.next;
            }
        }
        throw new FrameworkException(ExceptionType.CONTEXT, Constants.UNREACHED);
    }

    public boolean remove(int val, T value) {
        int slot = val & mask;
        IntMapNode<T> current = nodes[slot];
        while (current != null) {
            if (current.val != val) {
                current = current.next;
            } else if (current.value != value) {
                return false;
            } else {
                IntMapNode<T> prev = current.prev;
                IntMapNode<T> next = current.next;
                if (prev != null) {
                    prev.next = next;
                } else {
                    nodes[slot] = next;
                }
                if (next != null) {
                    next.prev = prev;
                }
                current.prev = null;
                current.next = null;
                count--;
                return true;
            }
        }
        return false;
    }

    public int count() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public List<T> asList() {
        List<T> result = new ArrayList<>();
        for (IntMapNode<T> n : nodes) {
            IntMapNode<T> t = n;
            while (t != null) {
                result.add(t.value);
                t = t.next;
            }
        }
        return result;
    }


    private static class IntMapNode<T> {
        private int val;
        private T value;
        private IntMapNode<T> prev;
        private IntMapNode<T> next;
    }
}
