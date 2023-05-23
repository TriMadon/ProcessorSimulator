package org.example.processor;


import org.example.clock.Clock;

import java.util.Optional;
import java.util.concurrent.Phaser;

public class Processor {
  private final int id;
  private Task runningTask;
  private volatile boolean didExecuteTask;

  public Processor (int id){
    this.id = id;
    this.didExecuteTask = false;
  }

  public Optional<Task> getRunningTask() {
    return Optional.ofNullable(runningTask);
  }

  public void setRunningTask(Task runningTask) {
    this.runningTask = runningTask;
  }

  public Runnable startProcessor(Phaser singleThreadPhaser ,Phaser multiThreadPhaser){
    return () -> {
      while (!Thread.currentThread().isInterrupted()){
        while (singleThreadPhaser.getPhase() % 5 != 4 || didExecuteTask);
        multiThreadPhaser.arriveAndAwaitAdvance();
        getRunningTask().ifPresent(task -> {
          task.run();
          if (task.isFinished())
            setRunningTask(null);
        });
        setDidExecuteTask(true);
        multiThreadPhaser.arriveAndAwaitAdvance();
      }
    };
  }

  @Override
  public String toString() {
    return "P"+id;
  }

  public boolean isRunning(){
    return getRunningTask().isPresent();
  }

  public void setDidExecuteTask(boolean didExecuteTask) {
    this.didExecuteTask = didExecuteTask;
  }
}
