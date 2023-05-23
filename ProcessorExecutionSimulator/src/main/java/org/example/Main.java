package org.example;
import org.example.data.TaskInfo;
import org.example.data.UserInputReader;
import org.example.simulator.Simulator;
import java.io.*;
import java.util.List;


public class Main {
  private static final UserInputReader userInputReader = new UserInputReader();

  public static void main(String[] args) throws IOException, InterruptedException {
    userInputReader.readUserInput();
    int numberOfProcessors = userInputReader.getNumberOfProcessorUnits();
    int simulationTime = userInputReader.getSimulationTime();
    List<TaskInfo> futureTaskInfoList = userInputReader.getFutureTaskInfoList();

    new Simulator(numberOfProcessors, simulationTime, futureTaskInfoList).startSimulation();
  }

}