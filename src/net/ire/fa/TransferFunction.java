package net.ire.fa;

/**
 * Created on: 22.07.2010 23:48:22
 */
public interface TransferFunction<T> {
    TransferFunction<T> followedBy(TransferFunction<T> other);

    T next(T t);
}
