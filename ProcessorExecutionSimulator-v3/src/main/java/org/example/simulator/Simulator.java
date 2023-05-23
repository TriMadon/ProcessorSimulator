package org.example.simulator;

import org.example.clock.AbstractClock;
import org.example.clock.Clock;
import org.example.io.OutputPrinter;
import org.example.processor.SingleTaskProcessor;
import org.example.scheduler.Scheduler;
import org.example.task.TaskRecord;
import org.example.task.NormalTaskComparator;
import org.example.processor.Processor;
import org.example.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Simulator implements Runnable {
  private final AbstractClock clock;
  private final Scheduler scheduler;
  private final List<SingleTaskProcessor> processorList;
  private final int numberOfProcessors;
  private final List<TaskRecord> futureTaskRecordList;
  private ExecutorService executorService;
  private final OutputPrinter outputPrinter;
  private final CyclicThreadSequencer cyclicThreadSequencer;

  public Simulator(int numberOfProcessors, int simulationTime, List<TaskRecord> futureTaskRecordList){
    this.numberOfProcessors = numberOfProcessors;
    this.futureTaskRecordList = futureTaskRecordList;
    this.cyclicThreadSequencer = new CyclicThreadSequencer(5);
    this.clock = new Clock(simulationTime, cyclicThreadSequencer);
    this.processorList = new ArrayList<>();
    for (int i = 1; i <= numberOfProcessors; i++)
      this.processorList.add(new Processor(i, clock, cyclicThreadSequencer));
    this.scheduler = new Scheduler(processorList, new NormalTaskComparator(), clock, cyclicThreadSequencer);
    this.outputPrinter = new OutputPrinter(processorList, clock, cyclicThreadSequencer);
  }

  public void startSimulation() throws InterruptedException {
    cyclicThreadSequencer.placeRunnableInSequence((Runnable) clock, 0);
    cyclicThreadSequencer.placeRunnableInSequence(this, 1);
    cyclicThreadSequencer.placeRunnableInSequence(scheduler, 2);
    cyclicThreadSequencer.placeRunnableInSequence(outputPrinter, 3);
    processorList.forEach(processor -> cyclicThreadSequencer.placeRunnableInSequence((Runnable) processor, 4));

    executorService = Executors.newFixedThreadPool(3+numberOfProcessors);
    executorService.submit((Runnable) clock);
    executorService.submit(scheduler);
    executorService.submit(outputPrinter);
    processorList.forEach(processor -> executorService.submit((Runnable) processor));
    this.run();

    endSimulation();
  }

  @Override
  public void run() {
    while (clock.isRunning()){
      try {
        cyclicThreadSequencer.threadHasResumed(this);
        int currentCycle = clock.getCurrentCycle();
        List<TaskRecord> taskRecords = futureTaskRecordList.stream().filter(x ->
            x.getCreationTime() == currentCycle).toList();
        List<Task> newTasks = taskRecords.stream().map(Task::new).toList();
        futureTaskRecordList.removeAll(taskRecords);
        scheduler.addTasksToQueue(newTasks);
        cyclicThreadSequencer.threadHasHalted(this);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      } catch (BrokenBarrierException e) {
        e.printStackTrace();
      }
    }
  }

  public void endSimulation() throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(100);
    executorService.shutdownNow();
    System.exit(1);
  }
}
