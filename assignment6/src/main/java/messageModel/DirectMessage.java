package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a DirectMessage object containing a message, its sender's username, and its receiver's
 * username
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class DirectMessage extends Message {
  private String senderUsername;
  private String recipientUsername;
  private String message;

  /**
   * Construct a DirectMessage object containing a message, its sender's username, and its receiver's
   * username
   *
   * @param senderUsername the direct message's sender's username
   * @param recipientUsername the direct message's receiver's username
   * @param message the content of the direct message
   */
  public DirectMessage(String senderUsername, String recipientUsername, String message) {
    this.messageType = Protocol.DIRECT_MESSAGE;
    this.senderUsername = senderUsername;
    this.recipientUsername = recipientUsername;
    this.message = message;
  }

  /**
   *
   * @return the sender's username of the direct message
   */
  public String getSenderUsername() {
    return senderUsername;
  }

  /**
   *
   * @return the receiver's username of the direct message
   */
  public String getRecipientUsername() {
    return recipientUsername;
  }

  /**
   *
   * @return the content of the direct message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Send the direct message, its sender and receiver's username to the
   * given output stream
   *
   * @param out the given output stream
   * @throws IOException if the given output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeUTF(senderUsername);
    out.writeUTF(recipientUsername);
    out.writeUTF(message);
    out.flush();
  }

  /**
   * receive a direct message, its sender and receiver's username from the given input stream and
   * return a DirectMessage object containing the message content and its sender and receiver
   *
   * @param in the given input stream
   * @return a new DirectMessage object containing the message content and its sender and receiver
   * @throws IOException if the provided input stream is invalid
   */
  public static DirectMessage receive(DataInputStream in) throws IOException {
    String senderUsername = in.readUTF();
    String recipientUsername = in.readUTF();
    String message = in.readUTF();
    return new DirectMessage(senderUsername, recipientUsername, message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DirectMessage that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getSenderUsername(), that.getSenderUsername())
        && Objects.equals(getRecipientUsername(), that.getRecipientUsername())
        && Objects.equals(getMessage(), that.getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSenderUsername(), getRecipientUsername(),
        getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DirectMessage{messageType:" + super.messageType + ", senderUsername:" +
        senderUsername + ", recipientUsername:" + recipientUsername + ", message:" + message + "}";
  }
}
