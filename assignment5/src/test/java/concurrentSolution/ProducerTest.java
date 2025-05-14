package concurrentSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProducerTest {
  private BlockingQueue<String> queue;
  private String testFilePath;

  @BeforeEach
  public void setUp() throws IOException {
    queue = new LinkedBlockingQueue<>();
    File tempFile = File.createTempFile("testProducer", ".txt");
    testFilePath = tempFile.getAbsolutePath();
    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    writer.write("line1");
    writer.newLine();
    writer.write("line2");
    writer.newLine();
    writer.close();
  }

  @Test
  public void testNormalExecution() throws InterruptedException {
    Producer producer = new Producer(queue, testFilePath);
    Thread producerThread = new Thread(producer);
    producerThread.start();
    producerThread.join();

    assertEquals("line1", queue.take());
    assertEquals("line2", queue.take());
    assertEquals(Producer.END_OF_FILE, queue.take());
    assertTrue(queue.isEmpty());
  }

  @Test
  public void testEmptyFile() throws InterruptedException, IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(testFilePath));
    writer.close();

    Producer producer = new Producer(queue, testFilePath);
    Thread producerThread = new Thread(producer);
    producerThread.start();
    producerThread.join();

    assertEquals(Producer.END_OF_FILE, queue.take());
    assertTrue(queue.isEmpty());
  }

  @Test
  public void testRun_FileNotFound() {
    String invalidFilePath = "NotExisted.txt";
    Producer producer = new Producer(queue, invalidFilePath);
    Thread producerThread = new Thread(producer);
    producerThread.start();
    assertThrows(RuntimeException.class, producerThread::run);
  }

  @Test
  public void testEquals() {
    Producer producer1 = new Producer(queue, testFilePath);
    Producer producer2 = new Producer(queue, testFilePath);
    assertEquals(producer1, producer2);
    assertEquals(producer1, producer1);
    assertNotEquals(producer1, null);
    assertNotEquals(producer1, new Object());
    assertNotEquals(producer1, new Producer(queue, ""));
    assertNotEquals(producer1, new Producer(new LinkedBlockingQueue<>(), testFilePath));
  }

  @Test
  public void testHashCode() {
    Producer producer1 = new Producer(queue, testFilePath);
    Producer producer2 = new Producer(queue, testFilePath);
    assertEquals(producer1.hashCode(), producer2.hashCode());
  }

  @Test
  public void testToString() {
    Producer producer = new Producer(queue, testFilePath);
    String expected = "Producer: {queue=" + queue.toString() + ", filePath=" + testFilePath + "}";
    assertEquals(expected, producer.toString());
  }
}
