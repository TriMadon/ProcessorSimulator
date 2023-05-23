package org.example.simulator;

import org.example.clock.Clock;
import org.example.processor.Processor;
import org.example.processor.Task;
import org.example.processor.TaskComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler {
  private final Simulator simulator;
  private final Clock clock;
  private final PriorityBlockingQueue<Task> taskPriorityQueue;
  private final List<Processor> processorList;

  public Scheduler(Clock clock, Simulator simulator, List<Processor> processorList, TaskComparator<Task> comparator){
    this.clock = clock;
    this.simulator = simulator;
    this.processorList = processorList;
    this.taskPriorityQueue = new PriorityBlockingQueue<>(4, comparator.reversed());
  }

  public Runnable startScheduler() {
    System.out.println("Scheduler started");
    return () -> {
      while (clock.isRunning()) {
        try {
          synchronized (simulator) {
            simulator.wait();
          }
          getIdleProcessors().forEach(processor -> {
            Task task = taskPriorityQueue.poll();
            System.out.println("Assigning " + task + " to " + processor);
            assignTaskToProcessor(task, processor);
          });
          synchronized (this) {
            notifyAll();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
          Thread.currentThread().interrupt();
        }
      }
    };
  }

  public void assignTaskToProcessor(Task task, Processor processor){
    processor.setRunningTask(task);
  }

  public void addTaskToQueue(Task task){
    taskPriorityQueue.add(task);
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
