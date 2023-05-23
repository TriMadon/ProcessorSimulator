package org.example.task;

import java.util.Objects;

public final class TaskRecord {
  private final int taskID;
  private final int creationTime;
  private final int executionTime;
  private final int priority;

  public TaskRecord(int taskID, int creationTime, int executionTime, int priority) {
    this.taskID = taskID;
    this.creationTime = creationTime;
    this.executionTime = executionTime;
    this.priority = priority;
  }

  public int getTaskID() {
    return taskID;
  }

  public int getCreationTime() {
    return creationTime;
  }

  public int getExecutionTime() {
    return executionTime;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TaskRecord) obj;
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
    return "TaskRecord[" +
        "getTaskID=" + taskID + ", " +
        "getCreationTime=" + creationTime + ", " +
        "getExecutionTime=" + executionTime + ", " +
        "getPriority=" + priority + ']';
  }

}
