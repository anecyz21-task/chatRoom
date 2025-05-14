package concurrentSolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Producer representation in this program who is responsible for reading the file
 */
public class Producer implements Runnable {

  /**
   * The end of file constant in the queue
   */
  public static final String END_OF_FILE = "END_OF_FILE";

  private BlockingQueue<String> queue;
  private String filePath;

  /**
   * Create a new Producer by the given queue and file path
   * @param queue for file reading
   * @param filePath the directory for files
   */
  public Producer(BlockingQueue<String> queue, String filePath) {
    this.queue = queue;
    this.filePath = filePath;
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
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        queue.put(line);
      }
      queue.put(END_OF_FILE);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @return the queue of this Producer
   */
  public BlockingQueue<String> getQueue() {
    return queue;
  }

  /**
   *
   * @return the file path of this Producer
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Producer producer = (Producer) o;
    return this.queue.equals(producer.getQueue()) && this.filePath.equals(producer.getFilePath());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = this.queue.hashCode();
    result = 31 * result + this.filePath.hashCode();
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Producer: {queue=" + this.queue.toString() + ", filePath=" + this.filePath + "}";
  }
}
