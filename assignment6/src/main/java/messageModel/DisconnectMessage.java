package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a DisconnectMessage object that contains a username indicating this user is trying to
 * disconnect from the server
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class DisconnectMessage extends Message{
  private String username;

  /**
   * Construct a DisconnectMessage object that contains a username indicating this user is trying to
   * disconnect from the server
   *
   * @param username user trying to disconnect from the server
   */
  public DisconnectMessage(String username) {
    this.messageType = Protocol.DISCONNECT_MESSAGE;
    this.username = username;
  }

  /**
   *
   * @return the user who is trying to disconnect from the server
   */
  public String getUsername() {
    return username;
  }

  /**
   * Send the username trying to disconnect from the server to the
   * given output stream
   *
   * @param out the given output stream
   * @throws IOException if the given output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeUTF(username);
    out.flush();
  }

  /**
   * receive a username indicating a user trying to disconnect from the given input stream and
   * return a DisconnectMessage object containing the username
   *
   * @param in the given input stream
   * @return a DisconnectMessage object containing the username
   * @throws IOException if the given input stream is invalid
   */
  public static DisconnectMessage receive(DataInputStream in) throws IOException {
    String username = in.readUTF();
    return new DisconnectMessage(username);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DisconnectMessage that)) {
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
    return "DisconnectMessage{messageType:" + super.messageType + ", username:" + username + "}";
  }
}
