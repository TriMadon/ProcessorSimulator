package org.example.processor;

public interface ITask extends Runnable{
  boolean isFinished();
  int getPriority();
}
