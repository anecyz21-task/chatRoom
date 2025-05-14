package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a QueryResp object containing all user currently connected to the server
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class QueryResp extends Message {

  private List<String> usernames;

  /**
   * Construct a QueryResp object containing all user currently connected to the server
   *
   * @param usernames a list of usernames indicating users currently connected to the server
   */
  public QueryResp(List<String> usernames) {
    this.messageType = Protocol.QUERY_USER_RESPONSE;
    this.usernames = usernames;
  }

  /**
   *
   * @return a list of usernames indicating users currently connected to the server
   */
  public List<String> getUsernames() {
    return usernames;
  }

  /**
   * Send the list of usernames to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeInt(usernames.size());
    for (String username : usernames) {
      out.writeUTF(username);
    }
    out.flush();
  }

  /**
   * Receive a list of usernames indicating users currently connected to the server
   *
   * @param in the given input stream
   * @return  a new QueryResp object containing the received username list
   * @throws IOException if the given input stream is invalid
   */
  public static QueryResp receive(DataInputStream in) throws IOException {
    int numberOfUsers = in.readInt();
    List<String> usernames = new ArrayList<>();
    for (int i = 0; i < numberOfUsers; i++) {
      String username = in.readUTF();
      usernames.add(username);
    }
    return new QueryResp(usernames);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QueryResp that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getUsernames(), that.getUsernames());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getUsernames());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "BroadcastMessage{messageType:" + super.messageType + ", current users connected to server: " + this.usernames.toString() + "}";
  }
}
