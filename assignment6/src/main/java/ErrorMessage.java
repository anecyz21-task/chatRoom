/**
 * Represents an Errormessage Object that helps generate error message and usage instructions when
 * users deliver incorrect commands
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class ErrorMessage {
  private final static String commandMenu = "Usage:\n"
      + " ? : show the command menu\n"
      + " logoff : disconnect from the server\n"
      + " who : show other connected users in the server\n"
      + " @user + message : sends a message directly to the given user\n"
      + " @all + message: sends a message to all connected users in the server\n"
      + " !user : sends a random insult message directly to the given user\n";
  private final static String error = "Invalid command, please use one of the following commands: \n";

  /**
   * Construct an Errormessage object that helps generate error message and usage instructions when
   * users deliver incorrect commands
   */
  public ErrorMessage() {}

  /**
   *
   * @return an error message and command menu to user when an invalid command is received
   */
  public static String getErrorMessage() {
    return error + commandMenu;
  }

  /**
   *
   * @return the command menu
   */
  public static String getMenu() {
    return commandMenu;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Command menu: " + commandMenu + ", error message indicator: " + error;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return commandMenu.hashCode() * 391 + error.hashCode() * 93;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ErrorMessage that = (ErrorMessage) o;
    return this.toString().equals(that.toString());
  }


}
