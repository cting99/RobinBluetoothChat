package cting.com.robin.support.common.model;

public interface IClick<I> {
    void onItemClick(I item);
    boolean onItemLongClick(I item);
}
