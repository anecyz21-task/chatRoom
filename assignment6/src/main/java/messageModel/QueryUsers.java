package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;

/**
 * Represents a QueryUsers object containing a username indicating this user is asking for a list of
 * all username currently connected to the server
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class QueryUsers extends Message {

  private String username;

  /**
   * Construct a QueryUsers object containing a username indicating this user is asking for a list of
   * all username currently connected to the server
   *
   * @param username the user who is requesting a list of all current usernames connected to the server
   */
  public QueryUsers(String username) {
    this.messageType = Protocol.QUERY_CONNECTED_USERS;
    this.username = username;
  }

  /**
   *
   * @return the username requesting a list of all current usernames connected to the server
   */
  public String getUsername() {
    return username;
  }

  /**
   * Send the username requesting a list of all current usernames connected to the server to the
   * given output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeUTF(username);
    out.flush();
  }

  /**
   * Receive a username requesting a list of all current usernames connected to the server to the server
   * and return a QueryUsers object containing the username
   *
   * @param in the given input stream
   * @return  a new QueryUsers object containing the received username
   * @throws IOException if the given input stream is invalid
   */
  public static QueryUsers receive(DataInputStream in) throws IOException {
    String username = in.readUTF();
    return new QueryUsers(username);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QueryUsers that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getUsername(), that.getUsername());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getUsername());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "BroadcastMessage{messageType:" + super.messageType + ", user that is querying other users: " + this.username + "}";
  }
}
