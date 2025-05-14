package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a BroadcastMessage containing a message that will be sent to all users within the server
 * and the sender's username
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class BroadcastMessage extends Message {
  private String senderUsername;
  private String message;

  /**
   * Construct a BroadcastMessage containing a message that will be sent to all users within the server
   * and the sender's username
   *
   * @param senderUsername the sender's username
   * @param message the message that will be sent to all users connected to the server currently
   */
  public BroadcastMessage(String senderUsername, String message) {
    this.messageType = Protocol.BROADCAST_MESSAGE;
    this.senderUsername = senderUsername;
    this.message = message;
  }

  /**
   *
   * @return the sender's username of the broadcast message
   */
  public String getSenderUsername() {
    return senderUsername;
  }

  /**
   *
   * @return the broadcast message content
   */
  public String getMessage() {
    return message;
  }

  /**
   * Send the broadcast message and sender username down to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if an invalid output stream is provided
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(this.messageType);
    out.writeUTF(this.senderUsername);
    out.writeUTF(this.message);
    out.flush();
  }


  /**
   * Receive a broadcast message and its sender's username from the given input stream and return
   * a BroadCastMessage object containing the received information
   *
   * @param in the given input stream
   * @return a new BroadCastMessage object that contain the received information
   * @throws IOException if the input stream provided is invalid
   */
  public static BroadcastMessage receive(DataInputStream in) throws IOException {
    String senderUsername = in.readUTF();
    String message = in.readUTF();
    return new BroadcastMessage(senderUsername, message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BroadcastMessage that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getSenderUsername(), that.getSenderUsername())
        && Objects.equals(getMessage(), that.getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSenderUsername(), getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "BroadcastMessage{messageType:" + super.messageType + ", senderUsername:" + senderUsername + ", message:" + message + "}";
  }
}
