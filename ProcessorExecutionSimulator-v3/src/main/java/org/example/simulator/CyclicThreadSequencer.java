package org.example.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * This class is designed to order separate looping threads into a repetitive series of steps,
 * to make it easy to implement complex sequences that include both single and parallel threads.
 *  @author  Mohammad AlSabbagh
 */
public class CyclicThreadSequencer {
  private final Map<Runnable, Integer> stepOfRunnable;
  private final Map<Integer, Integer> numberOfThreadsInStep;
  private final int numberOfStepsInSequence;
  private volatile Integer stepPointer;
  private static final Map<Integer, CyclicBarrier> cyclicBarrierInstances = new HashMap<>();

  public CyclicThreadSequencer(int numberOfStepsInSequence){
    this.stepOfRunnable = new HashMap<>();
    this.numberOfThreadsInStep = new HashMap<>();
    this.numberOfStepsInSequence = numberOfStepsInSequence;
    this.stepPointer = 0;
  }

  /**
   * Create a series of looping Runnables. these runnables will run in the specified sequence using the {@link #stepPointer}.
   *  @param runnable specify the Runnable (thread) to be included in the sequence.
   *  @param stepInSequence specify the step this Runnable will take place in the sequence (MUST begin
   *                        from 0 and end at numberOfStepsInSequence - 1).
   */
  public void placeRunnableInSequence(Runnable runnable, Integer stepInSequence){
    stepOfRunnable.putIfAbsent(runnable, stepInSequence);
    numberOfThreadsInStep.compute(stepInSequence, (step, numOfThreads) ->
        (numOfThreads == null) ? 1 : numOfThreads+1);
  }

  /**
   * Call this method at the start of a loop in a Runnable.
   */
  public void threadHasResumed(Runnable callingThread)
      throws InterruptedException, BrokenBarrierException {
    int numberOfParallelThreads = numberOfThreadsInStep.get(stepOfRunnable.get(callingThread));
    if (numberOfParallelThreads > 1)
      parallelThreadsHaveResumed(callingThread, numberOfParallelThreads);
    else singleThreadHasResumed(callingThread);
  }

  /**
   * Call this method at the end of a loop in a Runnable. This method, together with the {@link #threadHasResumed(Runnable)} method
   * wraps the loop logic as a sequential step, thus preventing said loop from continuing until its turn in the next cycle.
   */
  public void threadHasHalted(Runnable callingThread)
      throws BrokenBarrierException, InterruptedException {
    int numberOfParallelThreads = numberOfThreadsInStep.get(stepOfRunnable.get(callingThread));
    if (numberOfParallelThreads > 1)
      parallelThreadsHaveHalted(callingThread, numberOfParallelThreads);
    else singleThreadHasHalted(callingThread);
  }
  
  private synchronized void singleThreadHasResumed(Runnable callingThread) throws InterruptedException {
    while (!stepOfRunnable.get(callingThread).equals(stepPointer))
      wait();
  }

  private synchronized void singleThreadHasHalted(Runnable callingThread) {
    stepPointer = (stepOfRunnable.get(callingThread) + 1) % numberOfStepsInSequence;
    notifyAll();
  }

  private void parallelThreadsHaveResumed(Runnable callingParallelThread, int numberOfParallelThreads)
      throws InterruptedException, BrokenBarrierException {
    synchronized (this) {
      while (!stepOfRunnable.get(callingParallelThread).equals(stepPointer))
        wait();
    }
    getCyclicBarrierInstance(numberOfParallelThreads).await();
  }

  private void parallelThreadsHaveHalted(Runnable callingParallelThread, int numberOfParallelThreads)
      throws BrokenBarrierException, InterruptedException {
    getCyclicBarrierInstance(numberOfParallelThreads).await();
    synchronized (this) {
      if (stepOfRunnable.get(callingParallelThread).equals(stepPointer)){
        stepPointer = (stepOfRunnable.get(callingParallelThread) + 1) % numberOfStepsInSequence;
        notifyAll();
      }
    }
  }

  private static synchronized CyclicBarrier getCyclicBarrierInstance(int numberOfParallelThreads){
    cyclicBarrierInstances.putIfAbsent(numberOfParallelThreads,
        new CyclicBarrier(numberOfParallelThreads));
    return cyclicBarrierInstances.get(numberOfParallelThreads);
  }

}
