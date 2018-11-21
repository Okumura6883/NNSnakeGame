# EvolutionSnake
Genetic algorithm + cancerous Java 

* All the lib and classpath added so all you need is Eclipse
+ Driver classes<br>
1. NeuralNetworkSnake.java
```java
public static void main(String[] args) {
    //                      read from ./Generations/Gen[number]/Parent[number].nnet
    //                                 [TEST/LEARN][TIMES][LIMIT][FILENAME] 
    SnakeTrainer trainer = new NeuralNetSnake(TEST, 1000, 1000, "Gen82/Parent1");
    //                [RENDER/NO RENDER]
    new SnakeGame(trainer, true);
}
```
2. Population.java
```java
public static void main(String[] args) {
    // Option1: Make a whole new Population. Warning: overwrites existing nnet files
    new Population();
    // Option2: Continue evolution from latest generation
    continueGeneration();
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
### 3. CSE 11 Suck ass. That's it.
