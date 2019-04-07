# EvolutionSnake
Genetic algorithm + cancerous Java 

* All the lib and classpath added so all you need is Eclipse
+ Driver classes: <br>
### 1. AI_Tester.java (for showcase)
```java
public static void main(String[] args) {
    ...
    //                                                             Path to nnet file here
    snake.setNnet(NeuralNetwork.createFromFile(SnakeTrainer.NNPATH + "Gen250/Parent1.nnet"));
    ...
}
```
### 2. Population.java (for training)
```java
public static void main(String[] args) {
    new Population().simulate();
}
```
# NOTE
1. No comments at all in the code so try your best to read my spaghetti code (:
2. Population.java uses Threadpool which can take up A LOT of CPU usage,<br> much lag might be expected while running this
```java
ExecutorService pool = Executors.newWorkStealingPool();
pool.execute(SomeHeavyTask);
...
```
3. Tweaking constants (you are very welcomed to tweak these numbers to compare different results)
```java
  ...
  public static final double MUTATION_RATE = 0.1;   // Chance for each gene to mutate
  public static final double MUTATION_RANGE = 0.2;  // Amplitude/range of a gene can mutate
  public static final int DEFAULT_GENERATIONS = 10000;  // Keep the thing running over night for now lol
  public static final int DEFAULT_SELECT = 6;       // How many to choose during each natural selection process
  public static final int DEFAULT_POPULATION = 50;  // Population size
  public static final int DEFAULT_NEURONS = 25;     // Number of neurons in each hidden layers (2 hidden layers)
  public static final int DEFAULT_SAVE = 50;        // Save the neural network file every [this] number of generation
  ...
```
<br><br><br>
