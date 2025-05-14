package concurrentSolution;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer representation in this program who is responsible for summarizing what the Producer read.
 */
public class Consumer implements Runnable {

  /**
   * Comma constant
   */
  public static final String COMMA = ",";
  /**
   * Underline constant
   */
  public static final String UNDERLINE = "_";

  private BlockingQueue<String> queue;
  private ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> data;

  /**
   * Create a new Consumer by the given queue and data
   * @param queue for file reading
   * @param data representation to store the data summary of the file
   */
  public Consumer(BlockingQueue<String> queue, ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> data) {
    this.queue = queue;
    this.data = data;
  }

  /**
   * When an object implementing interface {@code Runnable} is used to create a thread, starting the
   * thread causes the object's {@code run} method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method {@code run} is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    while (true) {
      try {
        String line = queue.take();
        if (Producer.END_OF_FILE.equals(line)) {
          queue.put(line);
          break;
        }
        String[] parts = line.replace("\"", "").split(COMMA);
        if (parts.length < 6) continue;
        String codeModule = parts[0];
        String codePresentation = parts[1];
        int date;
        int sumClick;
        date = Integer.parseInt(parts[4]);
        sumClick = Integer.parseInt(parts[5]);
        String key = codeModule + UNDERLINE + codePresentation;
        data.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(date, d -> new AtomicInteger(0))
            .addAndGet(sumClick);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   *
   * @return the queue of the Consumer
   */
  public BlockingQueue<String> getQueue() {
    return queue;
  }

  /**
   *
   * @return the data of the Consumer
   */
  public ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> getData() {
    return data;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Consumer consumer = (Consumer) o;
    return this.queue.equals(consumer.getQueue()) && this.data.equals(consumer.getData());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = this.queue.hashCode();
    result = 31 * result + this.data.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Consumer: {queue=" + this.queue.toString() + ", data=" + this.data.toString() + "}";
  }
}
