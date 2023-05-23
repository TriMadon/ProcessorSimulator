package org.example.data;

import java.util.Objects;

public final class TaskInfo {
  private final int taskID;
  private final int creationTime;
  private final int executionTime;
  private final int priority;

  public TaskInfo(int taskID, int creationTime, int executionTime, int priority) {
    this.taskID = taskID;
    this.creationTime = creationTime;
    this.executionTime = executionTime;
    this.priority = priority;
  }

  public int taskID() {
    return taskID;
  }

  public int creationTime() {
    return creationTime;
  }

  public int executionTime() {
    return executionTime;
  }

  public int priority() {
    return priority;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TaskInfo) obj;
    return this.taskID == that.taskID &&
        this.creationTime == that.creationTime &&
        this.executionTime == that.executionTime &&
        this.priority == that.priority;
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskID, creationTime, executionTime, priority);
  }

  @Override
  public String toString() {
    return "TaskInfo[" +
        "taskID=" + taskID + ", " +
        "creationTime=" + creationTime + ", " +
        "executionTime=" + executionTime + ", " +
        "priority=" + priority + ']';
  }

}
