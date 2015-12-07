package de.micromata.genome.util.matcher;

import org.apache.commons.lang.exception.ExceptionUtils;

import de.micromata.genome.util.runtime.RuntimeCallable;

/**
 * A Utility to run a RuntimeCallable in multiple threads in loops
 * 
 * @author roger
 * 
 */
public class GenomeCommonsThreadedRunner
{
  int loops = 1;

  int threadCount;

  final StringBuilder exeptions = new StringBuilder();

  public GenomeCommonsThreadedRunner(int loops, int threadCount)
  {
    super();
    this.loops = loops;
    this.threadCount = threadCount;
  }

  public void run(final RuntimeCallable caller)
  {
    long start = System.currentTimeMillis();
    Thread[] threads = new Thread[threadCount];
    for (int i = 0; i < threadCount; ++i) {
      final int intPrefix = i;
      threads[i] = new Thread(new Runnable() {

        @Override
        public void run()
        {
          try {
            for (int i = 0; i < loops; ++i) {
              synchronized (GenomeCommonsThreadedRunner.this) {
                if (exeptions.length() > 0) {
                  break;
                }
              }
              caller.call();
            }
          } catch (Exception ex) {
            synchronized (GenomeCommonsThreadedRunner.this) {
              exeptions.append("\n\nThread " + Thread.currentThread().getId() + " faield:\n").append(ExceptionUtils.getFullStackTrace(ex));
            }
          }
        }

      });
    }
    for (Thread t : threads) {
      t.start();
    }
    try {
      for (Thread t : threads) {
        t.join();
      }
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
    long end = System.currentTimeMillis();
    long dif = end - start;
    double difPerOp = (double) dif / (threadCount * loops);
    System.out.println("Runned threaded  test in "
        + dif
        + " ms with "
        + threadCount
        + " threads in "
        + loops
        + " loops. Per op: "
        + difPerOp
        + " ms");
    if (exeptions.length() > 0) {
      System.err.println(exeptions);
      throw new RuntimeException("One or more Threads failed: " + exeptions);
    }
  }
}
