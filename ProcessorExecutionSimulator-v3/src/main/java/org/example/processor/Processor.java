package org.example.processor;


import org.example.clock.AbstractClock;
import org.example.simulator.CyclicThreadSequencer;
import org.example.task.AbstractTask;

import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;

public class Processor implements Runnable, SingleTaskProcessor {
  private final int id;
  private AbstractTask runningTask;
  private final AbstractClock clock;
  private final CyclicThreadSequencer cyclicThreadSequencer;

  public Processor (int id, AbstractClock clock, CyclicThreadSequencer cyclicThreadSequencer){
    this.id = id;
    this.clock = clock;
    this.cyclicThreadSequencer = cyclicThreadSequencer;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted() && clock.isRunning()){
      try {
        cyclicThreadSequencer.threadHasResumed(this);
        getRunningTask().ifPresent(task -> {
          task.run();
          if (task.isFinished())
            setRunningTask(null);
        });
        cyclicThreadSequencer.threadHasHalted(this);
      } catch (InterruptedException | BrokenBarrierException e){
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  public Optional<AbstractTask> getRunningTask() {
    return Optional.ofNullable(runningTask);
  }

  public void setRunningTask(AbstractTask runningTask) {
    this.runningTask = runningTask;
  }

  @Override
  public String toString() {
    return "P"+id;
  }

  public boolean isBusy(){
    return getRunningTask().isPresent();
  }


}
