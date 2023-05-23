package org.example.processor;

import org.example.data.TaskInfo;

public class Task implements ITask {

  private final int id;
  private final int executionTime;
  private final int priority;
  private int remainingCycles;

  public Task (int id, int executionTime, int priority){
    this.id = id;
    this.executionTime = executionTime;
    this.priority = priority;
    this.remainingCycles = executionTime;
  }

  public Task(TaskInfo info) {
    this.id = info.taskID();
    this.executionTime = info.executionTime();
    this.priority = info.priority();
    this.remainingCycles = this.executionTime;
  }

  @Override
  public void run() {
    this.remainingCycles = this.remainingCycles - 1;
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

  public int getPriority() {
    return priority;
  }

//  @Override
//  public String toString() {
//    return "T"+id;
//  }


  @Override
  public String toString() {
    return "Task{" +
        "id=" + id +
        ", executionTime=" + executionTime +
        ", priority=" + priority +
        ", remainingCycles=" + remainingCycles +
        '}';
  }
}
