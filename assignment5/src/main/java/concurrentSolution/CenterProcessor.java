package concurrentSolution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CenterProcessor {
  private String dir;
  private BlockingQueue<String> processQueue;
  private HashMap<String, Integer> count;
  private int threshold;

  public CenterProcessor(String[] args) {
    if (args.length != 1 && args.length != 2) {
      throw new IllegalArgumentException("Please provide a directory");
    }
    this.dir = args[0];
    this.threshold = Integer.parseInt(args[1]);
    this.processQueue = new LinkedBlockingQueue<String>();
    this.count = new HashMap<>();
  }
}
