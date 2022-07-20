package hu.icellmobilsoft.roaster.common.util;

/**
 * Exception base @FunctionalInterface
 * 
 * @author imre.scheffer
 * @since 0.8.0
 */
@FunctionalInterface
public interface ExceptionConsumer<T, E extends Exception> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t
     *            the argument
     * @throws E
     *             if an exception occurs
     */
    void accept(T t) throws E;
}