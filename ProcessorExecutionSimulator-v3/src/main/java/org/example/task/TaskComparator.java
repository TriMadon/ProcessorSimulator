package org.example.task;

import java.util.Comparator;

public interface TaskComparator<T extends AbstractTask> extends Comparator<T> {

  int compare(T o1, T o2);
}
