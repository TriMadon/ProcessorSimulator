package org.example.processor;

import org.example.task.AbstractTask;

import java.util.Optional;

public interface SingleTaskProcessor extends AbstractProcessor{

  void setRunningTask(AbstractTask runningTask);
  Optional<AbstractTask> getRunningTask();

}
