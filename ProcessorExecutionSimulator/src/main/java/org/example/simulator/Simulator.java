package org.example.simulator;

import org.example.clock.Clock;
import org.example.data.TaskInfo;
import org.example.processor.NormalTaskComparator;
import org.example.processor.Processor;
import org.example.processor.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Simulator {
  private final Clock clock;
  private final Scheduler scheduler;
  private final List<Processor> processorList;
  private final int numberOfProcessors;
  private final List<TaskInfo> futureTaskInfoList;

  private ExecutorService executorService;

  public Simulator(int numberOfProcessors, int simulationTime, List<TaskInfo> futureTaskInfoList){
    this.numberOfProcessors = numberOfProcessors;
    this.futureTaskInfoList = futureTaskInfoList;
    this.clock = new Clock(simulationTime);
    processorList = new ArrayList<>();
    scheduler = new Scheduler(clock, this, processorList, new NormalTaskComparator());
    for (int i = 1; i <= numberOfProcessors; i++)
      processorList.add(new Processor(clock, scheduler, i));
  }

  public void startSimulation() throws InterruptedException {
    executorService = Executors.newFixedThreadPool(2+numberOfProcessors);
    executorService.submit(clock.startClock());
    executorService.submit(scheduler.startScheduler());
    for (Processor processor: processorList)
      executorService.submit(processor.startProcessor());
    enterMainLoop();
  }

  private void enterMainLoop() throws InterruptedException {
    while (clock.isRunning()){
      synchronized (clock) {
        clock.wait();
      }
      int currentCycle = clock.getCurrentCycle();
      List<TaskInfo> newTaskInfo = futureTaskInfoList.stream().filter(x -> x.creationTime() == currentCycle).toList();
      List<Task> newTasks = newTaskInfo.stream().map(Task::new).toList();
      futureTaskInfoList.removeAll(newTaskInfo);
      scheduler.addTasksToQueue(newTasks);
      synchronized (this) {
        notifyAll();
      }
    }
    TimeUnit.MILLISECONDS.sleep(100);
    System.out.println("\nSimulation Ends");
    executorService.shutdownNow();
  }

}
