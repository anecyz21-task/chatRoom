package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents an InsultMessage object containing a sender's username and a receiver's username, indicating
 * the sender wants to send a random insult to the receiver
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class InsultMessage extends Message {
  private String senderUsername;
  private String recipientUsername;

  /**
   * Construct an InsultMessage object containing a sender's username and a receiver's username
   *
   * @param senderUsername the sender of the random insult message
   * @param recipientUsername the receiver of the random insult message
   */
  public InsultMessage(String senderUsername, String recipientUsername) {
    this.messageType = Protocol.SEND_INSULT;
    this.senderUsername = senderUsername;
    this.recipientUsername = recipientUsername;
  }

  /**
   *
   * @return the sender of the random insult message
   */
  public String getSenderUsername() {
    return senderUsername;
  }

  /**
   *
   * @return the receiver of the random insult message
   */
  public String getRecipientUsername() {
    return recipientUsername;
  }

  /**
   * Send the sender and receiver's username to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeUTF(senderUsername);
    out.writeUTF(recipientUsername);
    out.flush();
  }

  /**
   * receive a sender and a receiver's username of a random insult message and return a InsultMessage
   * containing the sender and receiver's username
   *
   * @param in the given input stream
   * @return  a new InsultMessage object containing the received errormessage
   * @throws IOException if the given input stream is invalid
   */
  public static InsultMessage receive(DataInputStream in) throws IOException {
    String senderUsername = in.readUTF();
    String recipientUsername = in.readUTF();
    return new InsultMessage(senderUsername, recipientUsername);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof InsultMessage that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getSenderUsername(), that.getSenderUsername()) && Objects.equals(getRecipientUsername(), that.getRecipientUsername());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSenderUsername(), getRecipientUsername());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "InsultMessage{messageType:" + super.messageType + ", sender username: " + this.senderUsername + "receiver username: " + this.recipientUsername + "}";
  }
}
