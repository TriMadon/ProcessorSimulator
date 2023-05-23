# ProcessorSimulator
A simplified attempt at simulating processor execution using multithreading.
The user is asked to input the number of processors, the time (number of clocks) of the simulation, and the list tasks in a file listed as follows:

```bash
<number of tasks>
<creation time of Task 1 (at which clock cycle?)> <execution time of Task 1 (in clock cycles)> <Priority of Task 1 (0 or 1)>
<creation time of Task 2> <execution time of Task 2> <Priority of Task 2>
<creation time of Task 3> <execution time of Task 3> <Priority of Task 3>
... ...
...
```

examples can be seen in the inputExamples file

To test this, just open a terminal in main directory and type:

```bash
java -jar ProcessorSim.jar
```

The output will be a cycle-by-cycle analysis of the creation of tasks that follows the following format:
```bash
Cycle   P1(processor 1)                                      P2           P3       ...
C1      T1:3(Task 1:Remaining clock cycles for Task 1)       T3:2         T2:5     
C2      T1:2                                                 T3:1         T2:4
C3      T1:1                                                 Idle         T2:3
......
...
```
