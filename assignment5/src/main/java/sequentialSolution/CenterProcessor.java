package sequentialSolution;

public class CenterProcessor {
  private String dir;
  public CenterProcessor(String[] args) {
    if (args.length != 1) {
      throw new IllegalArgumentException("Please provide a directory");
    }
    this.dir = args[0];
  }
}
