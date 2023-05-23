package org.example.io;

import org.example.clock.AbstractClock;
import org.example.processor.SingleTaskProcessor;
import org.example.simulator.CyclicThreadSequencer;
import org.example.task.AbstractTask;

import java.io.PrintWriter;
import java.util.List;

public class OutputPrinter implements Runnable {
  private final List<SingleTaskProcessor> processorList;
  private final PrintWriter writer;
  private final AbstractClock clock;
  private final CyclicThreadSequencer cyclicThreadSequencer;

  public OutputPrinter(List<SingleTaskProcessor> processorList, AbstractClock clock, CyclicThreadSequencer cyclicThreadSequencer) {
    this.processorList = processorList;
    this.writer = new PrintWriter(System.out);
    this.clock = clock;
    this.cyclicThreadSequencer = cyclicThreadSequencer;
  }

  @Override
  public void run() {
    String[] row = new String[1+processorList.size()];
    String format = "%-10s".repeat(1+processorList.size()) + "\n";
    row[0] = "\nCycle ";
    int i = 1;
    for (SingleTaskProcessor processor: processorList) {
      row[i] = " "+ processor.toString();
      i++;
    }
    printWithFormatAndFlush(format, row);
    while (!Thread.currentThread().isInterrupted() && clock.isRunning()){
      try {
        cyclicThreadSequencer.threadHasResumed(this);
        row[0] = clock.toString();
        int j = 1;
        for (SingleTaskProcessor processor: processorList) {
          int finalJ = j;
          processor.getRunningTask().ifPresentOrElse(task ->
                  row[finalJ] = task + ":" + task.getRemainingCycles()
              , () -> row[finalJ] = "idle");
          j++;
        }
        printWithFormatAndFlush(format, row);
        cyclicThreadSequencer.threadHasHalted(this);
      } catch (Exception e){
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
    writer.println("\nSimulation Ends");
    writer.flush();
    writer.close();
  }

  public void printWithFormatAndFlush(String format, Object[] strings){
    writer.printf(format, strings);
    writer.flush();
  }
}
