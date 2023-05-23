package org.example.clock;
import org.example.simulator.CyclicThreadSequencer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Clock implements Runnable, AbstractClock {
  private final int totalCycles;
  private final AtomicInteger currentCycle;
  private final CyclicThreadSequencer cyclicThreadSequencer;

  public Clock(int totalCycles, CyclicThreadSequencer cyclicThreadSequencer){
    this.totalCycles = totalCycles;
    this.currentCycle = new AtomicInteger(0);
    this.cyclicThreadSequencer = cyclicThreadSequencer;
  }

  @Override
  public void run() {
    while (isRunning() && !Thread.currentThread().isInterrupted()){
      try {
        cyclicThreadSequencer.threadHasResumed(this);
        incrementCycle();
        TimeUnit.MILLISECONDS.sleep(1000);
        cyclicThreadSequencer.threadHasHalted(this);
      } catch (Exception e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public int getCurrentCycle(){
    return currentCycle.get();
  }

  @Override
  public synchronized boolean isRunning(){
    return currentCycle.get() < totalCycles;
  }

  public void incrementCycle(){
    this.currentCycle.incrementAndGet();
  }

  @Override
  public String toString() {
    return "C" + currentCycle.get();
  }

}
