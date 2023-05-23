package org.example.io;

import org.example.task.TaskRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UserInputReader {
  private int numberOfProcessorUnits;
  private int simulationTime;
  private List<TaskRecord> futureTaskRecordList;
  private final BufferedReader keyboardReader;

  public UserInputReader() {
    this.keyboardReader = new BufferedReader(new InputStreamReader(System.in));
  }

  public void readUserInput() {
    while (true){
      try {
        System.out.println("Welcome to ProcessExecutionSimulator! please input the number of processors:");
        numberOfProcessorUnits = Integer.parseInt(keyboardReader.readLine().trim());
        System.out.println("input the number of clock cycles:");
        simulationTime = Integer.parseInt(keyboardReader.readLine().trim());
        System.out.println("Write input text file path:");
        Path filePath = Paths.get(keyboardReader.readLine().trim());
        futureTaskRecordList = parseTaskListFromFile(filePath);
        keyboardReader.close();
        break;
      } catch (Exception e){
        System.out.println("Invalid input, please try again");
      }
    }

  }

  private List<TaskRecord> parseTaskListFromFile(Path filePath) throws IOException {
    try (BufferedReader fileInputReader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
      int numOfTasks = Integer.parseInt(fileInputReader.readLine().trim());
      List<TaskRecord> taskList = new ArrayList<>();

      for (int i = 1; i <= numOfTasks; i++) {
        int[] taskCredentials = Arrays.stream(fileInputReader.readLine().replaceAll("\\s+$", "").split(" "))
            .mapToInt(Integer::parseInt).toArray();
        int creationTime = taskCredentials[0];
        int executionTime = taskCredentials[1];
        int priority = taskCredentials[2];
        taskList.add(new TaskRecord(i, creationTime, executionTime, priority));
      }
      return taskList;
    }
  }

  public int getNumberOfProcessorUnits() {
    return numberOfProcessorUnits;
  }

  public int getSimulationTime() {
    return simulationTime;
  }

  public List<TaskRecord> getFutureTaskInfoList() {
    return futureTaskRecordList;
  }
}
