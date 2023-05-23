package org.example.task;

public class NormalTaskComparator implements TaskComparator<Task> {

  @Override
  public int compare(Task o1, Task o2) {
    int comparison;
    comparison = o1.getPriority() - o2.getPriority();
    if (comparison == 0)
      comparison = o1.getExecutionTime() - o2.getExecutionTime();
    if (comparison == 0)
      comparison = o2.getId() - o1.getId();
    return comparison;
  }
}
