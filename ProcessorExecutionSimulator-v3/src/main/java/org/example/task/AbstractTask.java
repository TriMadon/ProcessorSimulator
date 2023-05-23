package org.example.task;

public interface AbstractTask extends Runnable{
  boolean isFinished();
  int getPriority();
  int getRemainingCycles();
}
