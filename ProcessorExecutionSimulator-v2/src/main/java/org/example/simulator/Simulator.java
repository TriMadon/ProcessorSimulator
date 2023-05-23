package org.example.simulator;

import org.example.clock.Clock;
import org.example.data.ConsolePrinter;
import org.example.data.TaskInfo;
import org.example.processor.ITask;
import org.example.processor.NormalTaskComparator;
import org.example.processor.Processor;
import org.example.processor.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Simulator {
  private final Phaser singleThreadPhaser;
  private final Phaser multiThreadPhaser;
  private final Clock clock;
  private final Scheduler scheduler;
  private final List<Processor> processorList;
  private final int numberOfProcessors;
  private final List<TaskInfo> futureTaskInfoList;
  private ExecutorService executorService;
  private final ConsolePrinter consolePrinter;

  public Simulator(int numberOfProcessors, int simulationTime, List<TaskInfo> futureTaskInfoList){
    this.singleThreadPhaser = new Phaser(1);
    this.multiThreadPhaser = new Phaser(1+numberOfProcessors);
    this.numberOfProcessors = numberOfProcessors;
    this.futureTaskInfoList = futureTaskInfoList;
    this.clock = new Clock(simulationTime);
    this.processorList = new ArrayList<>();
    this.scheduler = new Scheduler(processorList, new NormalTaskComparator());
    for (int i = 1; i <= numberOfProcessors; i++)
      this.processorList.add(new Processor(i));
    this.consolePrinter = new ConsolePrinter(clock, processorList);
  }

  public void startSimulation() throws InterruptedException {
    executorService = Executors.newFixedThreadPool(4+numberOfProcessors);
    executorService.submit(clock.startClock(singleThreadPhaser));
    executorService.submit(scheduler.startScheduler(singleThreadPhaser));
    for (Processor processor: processorList)
      executorService.submit(processor.startProcessor(singleThreadPhaser, multiThreadPhaser));
    executorService.submit(consolePrinter.startPrinting(singleThreadPhaser, multiThreadPhaser));
    enterMainLoop();
  }

  private void enterMainLoop() throws InterruptedException {
    while (clock.isRunning()){
      while (singleThreadPhaser.getPhase() % 5 != 1);
      int currentCycle = clock.getCurrentCycle();
      List<TaskInfo> newTaskInfo = futureTaskInfoList.stream().filter(x -> x.creationTime() == currentCycle).toList();
      List<Task> newTasks = newTaskInfo.stream().map(Task::new).toList();
      futureTaskInfoList.removeAll(newTaskInfo);
      scheduler.addTasksToQueue(newTasks);
      singleThreadPhaser.arriveAndAwaitAdvance();
    }
    System.out.println("\nSimulation Ends");
    singleThreadPhaser.forceTermination();
    multiThreadPhaser.forceTermination();
    executorService.shutdown();
//    System.exit(1);
  }

}
