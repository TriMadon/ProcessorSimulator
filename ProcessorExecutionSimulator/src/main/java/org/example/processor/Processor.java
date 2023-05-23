package org.example.processor;


import org.example.clock.Clock;
import org.example.simulator.Scheduler;

import java.util.Optional;

public class Processor {

  private final Clock clock;
  private final Scheduler scheduler;
  private final int id;
  private Task runningTask;

  public Processor (Clock clock, Scheduler scheduler, int id){
    this.clock = clock;
    this.scheduler = scheduler;
    this.id = id;
  }

  public Optional<Task> getRunningTask() {
    return Optional.ofNullable(runningTask);
  }

  public void setRunningTask(Task runningTask) {
    this.runningTask = runningTask;
  }

  public Runnable startProcessor(){
    System.out.println("Processor " + this + " Started");
    return () -> {
      while (clock.isRunning()){
        try {
          synchronized (scheduler) {
            scheduler.wait();
          }
          getRunningTask().ifPresent(task -> {
            System.out.println(this + " now executes " + this.runningTask);
            task.run();
            if (task.isFinished())
              setRunningTask(null);
          });
        } catch (InterruptedException e) {
          e.printStackTrace();
          Thread.currentThread().interrupt();
        }
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

}
