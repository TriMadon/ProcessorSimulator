package org.example.clock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Clock {
  private final int totalCycles;
  private final AtomicInteger currentCycle;

  public Clock(int totalCycles){
    this.totalCycles = totalCycles;
    this.currentCycle = new AtomicInteger(1);
  }

  public int getCurrentCycle(){
    return currentCycle.get();
  }

  public Runnable startClock() {
    System.out.println("Clock started");
    return () -> {
      try {
        while (isRunning()){
          TimeUnit.MILLISECONDS.sleep(1000);
          incrementCycle();
          synchronized (this){
            notifyAll();
          }
        }
      } catch (InterruptedException e){
        Thread.currentThread().interrupt();
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
