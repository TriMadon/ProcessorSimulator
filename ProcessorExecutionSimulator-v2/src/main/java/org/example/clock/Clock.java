package org.example.clock;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Clock {
  private final int totalCycles;
  private final AtomicInteger currentCycle;

  public Clock(int totalCycles){
    this.totalCycles = totalCycles;
    this.currentCycle = new AtomicInteger(0);
  }

  public int getCurrentCycle(){
    return currentCycle.get();
  }

  public Runnable startClock(Phaser singleThreadPhaser) {
    return () -> {
      while (isRunning() && !Thread.currentThread().isInterrupted()){
        try {
          while (singleThreadPhaser.getPhase() % 5 != 0);
          incrementCycle();
          TimeUnit.MILLISECONDS.sleep(1000);
          singleThreadPhaser.arriveAndAwaitAdvance();
        } catch (InterruptedException e){
          e.printStackTrace();
          Thread.currentThread().interrupt();
        }
      }
    };
  }

  public synchronized boolean isRunning(){
    return currentCycle.get() <= totalCycles;
  }

  public void incrementCycle(){
    this.currentCycle.incrementAndGet();
  }

  @Override
  public String toString() {
    return "C" + currentCycle;
  }
}
