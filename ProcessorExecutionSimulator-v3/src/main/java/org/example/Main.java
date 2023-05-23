package org.example;
import org.example.task.TaskRecord;
import org.example.io.UserInputReader;
import org.example.simulator.Simulator;

import java.util.List;


public class Main {
  private static final UserInputReader userInputReader = new UserInputReader();

  public static void main(String[] args) throws InterruptedException {
    userInputReader.readUserInput();
    int numberOfProcessors = userInputReader.getNumberOfProcessorUnits();
    int simulationTime = userInputReader.getSimulationTime();
    List<TaskRecord> futureTaskRecordList = userInputReader.getFutureTaskInfoList();

    new Simulator(numberOfProcessors, simulationTime, futureTaskRecordList).startSimulation();
  }


}