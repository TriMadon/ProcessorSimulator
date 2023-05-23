package org.example.processor;

import java.util.Comparator;

public interface TaskComparator<T extends ITask> extends Comparator<T> {

  public int compare(T o1, T o2);
}
