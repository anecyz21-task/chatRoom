package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a FailedMessage object containing a message indicating a potential fail message from the server
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class FailedMessage extends Message {

  private String errorMessage;

  /**
   * Construct a FailedMessage object containing a message indicating a potential fail message from the server
   *
   * @param errorMessage a potential errormessage sent from the server
   */
  public FailedMessage(String errorMessage) {
    this.messageType = Protocol.FAILED_MESSAGE;
    this.errorMessage = errorMessage;
  }

  /**
   *
   * @return the errormessage sent by the server
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Send the errormessage to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeUTF(errorMessage);
    out.flush();
  }

  /**
   * Receive an errormessage and return a FailedMessage object containing the received message
   *
   * @param in the given input stream
   * @return  a new FailedMessage object containing the received errormessage
   * @throws IOException if the given input stream is invalid
   */
  public static FailedMessage receive(DataInputStream in) throws IOException {
    String errorMessage = in.readUTF();
    return new FailedMessage(errorMessage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FailedMessage that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getErrorMessage(), that.getErrorMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getErrorMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "FailedMessage{messageType:" + super.messageType + ", error message: " + this.errorMessage + "}";
  }
}
