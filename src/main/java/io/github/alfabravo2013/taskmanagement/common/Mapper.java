package io.github.alfabravo2013.taskmanagement.common;

public interface Mapper <D, S> {
    D map(S source);
}
