package org.example.scheduler;

import org.example.clock.AbstractClock;
import org.example.processor.SingleTaskProcessor;
import org.example.simulator.CyclicThreadSequencer;
import org.example.task.Task;
import org.example.task.TaskComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler implements Runnable {
  private static final int INITIAL_CAPACITY = 4;
  private final PriorityBlockingQueue<Task> taskPriorityQueue;
  private final List<SingleTaskProcessor> processorList;
  private final CyclicThreadSequencer cyclicThreadSequencer;
  private final AbstractClock clock;

  public Scheduler(List<SingleTaskProcessor> processorList, TaskComparator<Task> comparator,
                   AbstractClock clock, CyclicThreadSequencer cyclicThreadSequencer){
    this.processorList = processorList;
    this.taskPriorityQueue = new PriorityBlockingQueue<>(INITIAL_CAPACITY, comparator.reversed());
    this.clock = clock;
    this.cyclicThreadSequencer = cyclicThreadSequencer;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted() && clock.isRunning()) {
      try {
        cyclicThreadSequencer.threadHasResumed(this);
        getIdleProcessors().stream().takeWhile(processor -> !taskPriorityQueue.isEmpty())
            .forEach(processor -> {
          Task task = taskPriorityQueue.poll();
          assignTaskToProcessor(task, processor);
            });
        cyclicThreadSequencer.threadHasHalted(this);
      } catch (Exception e){
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }


  public void assignTaskToProcessor(Task task, SingleTaskProcessor processor){
    processor.setRunningTask(task);
  }

  public void addTasksToQueue(List<Task> tasks){
    taskPriorityQueue.addAll(tasks);
  }

  public List<SingleTaskProcessor> getIdleProcessors(){
    List<SingleTaskProcessor> idleProcessors = new ArrayList<>();
    processorList.forEach(processor -> {
      if (!processor.isBusy())
        idleProcessors.add(processor);
    });
    return idleProcessors;
  }

}
