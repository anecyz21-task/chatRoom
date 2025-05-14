import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The generator to generate random insult
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class InsultGenerator {

  /**
   *
   * @return random insult
   */
  public String generate() {
    /**
     * Generate by ChatGPT
     */
    List<String> list = Arrays.asList(new String[] {
        "If your brain was dynamite, there wouldn’t be enough to blow your hat off.",
        "You bring everyone so much joy when you leave the room.",
        "I’d agree with you but then we’d both be wrong.",
        "You're about as useful as a knitted condom.",
        "You have the charm and charisma of a soggy cardboard box."
    });

    Random random = new Random();
    return list.get(random.nextInt(list.size()));
  }
}
