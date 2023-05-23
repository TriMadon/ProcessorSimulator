package org.example.task;

public class Task implements AbstractTask {

  private final int id;
  private final int executionTime;
  private final int priority;
  private int remainingCycles;

  public Task(TaskRecord info) {
    this.id = info.getTaskID();
    this.executionTime = info.getExecutionTime();
    this.priority = info.getPriority();
    this.remainingCycles = this.executionTime;
  }

  @Override
  public void run() {
    decrementRemainingCycles();
  }

  public void decrementRemainingCycles() {
    remainingCycles = remainingCycles - 1;
  }

  public int getId() {
    return id;
  }

  public boolean isFinished(){
    return remainingCycles == 0;
  }

  public int getExecutionTime() {
    return executionTime;
  }

  public int getRemainingCycles() {
    return remainingCycles;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public String toString() {
    return "T"+id;
  }
}
