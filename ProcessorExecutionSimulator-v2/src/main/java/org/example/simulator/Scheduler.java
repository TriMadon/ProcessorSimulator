package org.example.simulator;

import org.example.clock.Clock;
import org.example.processor.Processor;
import org.example.processor.Task;
import org.example.processor.TaskComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler {
  private final PriorityBlockingQueue<Task> taskPriorityQueue;
  private final List<Processor> processorList;

  public Scheduler(List<Processor> processorList, TaskComparator<Task> comparator){
    this.processorList = processorList;
    this.taskPriorityQueue = new PriorityBlockingQueue<>(4, comparator.reversed());
  }

  public Runnable startScheduler(Phaser singleThreadPhaser) {
    return () -> {
      while (!Thread.currentThread().isInterrupted()) {
        while (singleThreadPhaser.getPhase() % 4 != 2);
        getIdleProcessors().stream()
            .takeWhile(processor -> !taskPriorityQueue.isEmpty())
            .forEach(processor -> {
              Task task = taskPriorityQueue.poll();
              assignTaskToProcessor(task, processor);
            });
        for (Processor processor: processorList)
          processor.setDidExecuteTask(false);
        singleThreadPhaser.arriveAndAwaitAdvance();
      }
    };
  }

  public void assignTaskToProcessor(Task task, Processor processor){
    processor.setRunningTask(task);
  }

  public void addTasksToQueue(List<Task> tasks){
    taskPriorityQueue.addAll(tasks);
  }

  public List<Processor> getIdleProcessors(){
    List<Processor> idleProcessors = new ArrayList<>();
    for (Processor processor: processorList) {
      if (!processor.isRunning())
        idleProcessors.add(processor);
    }
    return idleProcessors;
  }


}
