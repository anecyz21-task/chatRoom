package concurrentSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConsumerTest {
  private BlockingQueue<String> queue;
  private ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>> data;

  @BeforeEach
  public void setUp() {
    queue = new LinkedBlockingQueue<>();
    data = new ConcurrentHashMap<>();
  }

  @Test
  public void testExecution() throws InterruptedException {
    queue.put("AAA,2013J,28400,546652,-10,4");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put(Producer.END_OF_FILE);

    Consumer consumer = new Consumer(queue, data);
    Thread consumerThread = new Thread(consumer);
    consumerThread.start();
    consumerThread.join();

    ConcurrentHashMap<Integer, AtomicInteger> aaa2013J = data.get("AAA_2013J");
    assertNotNull(aaa2013J);
    assertEquals(6, aaa2013J.get(-10).get());

    assertEquals(Producer.END_OF_FILE, queue.take());
    assertTrue(queue.isEmpty());
  }

  @Test
  public void testLengthLessThan6() throws InterruptedException {
    queue.put("AAA,2013J,28400,546652,-10");
    queue.put(Producer.END_OF_FILE);

    Consumer consumer = new Consumer(queue, data);
    Thread consumerThread = new Thread(consumer);
    consumerThread.start();
    consumerThread.join();
    assertTrue(data.isEmpty());

    assertEquals(Producer.END_OF_FILE, queue.take());
    assertTrue(queue.isEmpty());
  }

  @Test
  public void testEquals() throws InterruptedException {
    queue.put("AAA,2013J,28400,546652,-10,4");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put(Producer.END_OF_FILE);
    Consumer consumer1 = new Consumer(queue, data);
    Consumer consumer2 = new Consumer(queue, data);
    Thread consumerThread = new Thread(consumer1);
    consumerThread.start();
    consumerThread.join();
    assertEquals(consumer1, consumer2);
    assertEquals(consumer1, consumer1);
    assertNotEquals(consumer1, null);
    assertNotEquals(consumer1, new Object());
    assertNotEquals(consumer1, new Consumer(queue, new ConcurrentHashMap<>()));
    assertNotEquals(consumer1, new Consumer(new LinkedBlockingQueue<>(), data));
  }

  @Test
  public void testHashCode() throws InterruptedException {
    queue.put("AAA,2013J,28400,546652,-10,4");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put(Producer.END_OF_FILE);
    Consumer consumer1 = new Consumer(queue, data);
    Consumer consumer2 = new Consumer(queue, data);
    assertEquals(consumer1.hashCode(), consumer2.hashCode());
  }

  @Test
  public void testToString() throws InterruptedException {
    queue.put("AAA,2013J,28400,546652,-10,4");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put("AAA,2013J,28400,546652,-10,1");
    queue.put(Producer.END_OF_FILE);
    Consumer consumer1 = new Consumer(queue, data);
    String expected = "Consumer: {queue=" + queue.toString() + ", data=" + data.toString() + "}";
    assertEquals(expected, consumer1.toString());
  }
}
