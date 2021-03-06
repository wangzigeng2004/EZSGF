package cn.ezandroid.sgf.demo;

import java.util.ArrayList;

/**
 * 事件历史模型
 *
 * @author like
 */
public class History<E> extends ArrayList<E> {

    /**
     * 过去最近事件的索引
     */
    private int head;

    /**
     * 未来最远事件的索引
     */
    private int max;

    public History() {
        head = max = -1;
    }

    /**
     * 添加元素
     *
     * @param element
     * @return
     */
    @Override
    public boolean add(E element) {
        ++head;

        if (size() < head + 1) {
            super.add(element);
        } else {
            set(head, element);
        }

        max = head;
        return true;
    }

    /**
     * 清除所有历史记录
     */
    @Override
    public void clear() {
        head = max = -1;
    }

    /**
     * 过去最近事件的索引
     *
     * @return
     */
    public int getHead() {
        return head;
    }

    /**
     * 设置过去最近事件的索引
     *
     * @param head
     */
    public void setHead(int head) {
        this.head = head;
    }

    public int getMax() {
        return max;
    }

    /**
     * 获取并移到上一个事件
     *
     * @return
     */
    public E stepBack() {
        E latest = readLatest();
        if (latest != null) {
            --head;
        }
        return latest;
    }

    /**
     * 获取当前事件
     *
     * @return
     */
    public E readLatest() {
        if (head < 0 || head >= size()) {
            return null;
        }
        return get(head);
    }

    /**
     * 获取并移到下一个事件
     *
     * @return
     */
    public E stepForward() {
        if (head == max) {
            return null;
        }
        ++head;
        return get(head);
    }

    /**
     * 是否能撤销
     *
     * @return
     */
    public boolean hasPast() {
        return head > -1;
    }

    /**
     * 是否能重做
     *
     * @return
     */
    public boolean hasFuture() {
        return head < max;
    }
}
