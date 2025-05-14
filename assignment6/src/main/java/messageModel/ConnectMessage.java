package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a ConnectMessage object containing a username indicating this user is trying to connect to
 * the server
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class ConnectMessage extends Message {
  private String username;

  /**
   * Construct a ConnectMessage containing a username indicating this user is trying to connect to
   * the server
   * @param username the user who is trying to connect to the server
   */
  public ConnectMessage(String username) {
    this.messageType = Protocol.CONNECT_MESSAGE;
    this.username = username;
  }

  /**
   *
   * @return the user who is trying to connect to the server
   */
  public String getUsername() {
    return username;
  }

  /**
   * Send the request username to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if an invalid output stream is provided
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeUTF(username);
    out.flush();
  }

  /**
   * Receive a connect message and the user who is requesting a connection and return a ConnectMessage
   * object containing the request username
   *
   * @param in the given input stream
   * @return a new ConnectMessage object that contain the request username
   * @throws IOException if the provided input stream is invalid
   */
  public static ConnectMessage receive(DataInputStream in) throws IOException {
    String username = in.readUTF();
    return new ConnectMessage(username);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConnectMessage that)) {
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
    return "ConnectMessage{messageType:" + super.messageType + ", username:" + username + "}";
  }
}
