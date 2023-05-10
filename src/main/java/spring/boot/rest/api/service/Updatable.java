package spring.boot.rest.api.service;

public interface Updatable<T> {
    void updateFrom(T source);
}
