package org.example.data;

import org.example.clock.Clock;
import org.example.processor.Processor;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Phaser;

public class ConsolePrinter {
  private final Clock clock;
  private final List<Processor> processorList;

  private final PrintWriter writer;

  public ConsolePrinter(Clock clock, List<Processor> processorList) {
    this.clock = clock;
    this.processorList = processorList;
    this.writer = new PrintWriter(System.out);
  }

  public Runnable startPrinting(Phaser singleThreadPhaser, Phaser multiThreadPhaser) {
    return () -> {
      String[] row = new String[1+processorList.size()];
      String format = "%-10s".repeat(1+processorList.size()) + "\n";
      row[0] = "Cycle";
      int i = 1;
      for (Processor processor: processorList) {
        row[i] = processor.toString();
        i++;
      }
      printWithFormatAndFlush(format, row);

      int oldPhase = 0;
      int newPhase = 0;
      while (!Thread.currentThread().isInterrupted() && clock.isRunning()){
        if (newPhase % 5 != 3 || oldPhase == newPhase) {
          newPhase = singleThreadPhaser.getPhase();
          continue;
        }
        row[0] = String.valueOf(clock.toString());
        oldPhase = newPhase;
        int j = 1;
        for (Processor processor: processorList) {
          int finalJ = j;
          processor.getRunningTask().ifPresentOrElse(task -> row[finalJ] = task.toString() + ":" + task.getRemainingCycles()
              , () -> row[finalJ] = "idle");
          j++;
        }
        printWithFormatAndFlush(format, row);
        singleThreadPhaser.arriveAndAwaitAdvance();
        multiThreadPhaser.arriveAndAwaitAdvance();
        multiThreadPhaser.arriveAndAwaitAdvance();
        singleThreadPhaser.arriveAndAwaitAdvance();
      }
    };
  }


  public void printWithFormatAndFlush(String format, Object[] strings){
    writer.printf(format, strings);
    writer.flush();
  }


}
