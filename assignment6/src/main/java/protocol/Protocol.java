package protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Message Processing Protocol
 */
public class Protocol {

  /**
   * Connect message
   */
  public static final int CONNECT_MESSAGE = 19;
  /**
   * Connect response
   */
  public static final int CONNECT_RESPONSE = 20;
  /**
   * Disconnect Message
   */
  public static final int DISCONNECT_MESSAGE = 21;
  /**
   * Query users
   */
  public static final int QUERY_CONNECTED_USERS = 22;
  /**
   * Query response
   */
  public static final int QUERY_USER_RESPONSE = 23;
  /**
   * Broadcast Message
   */
  public static final int BROADCAST_MESSAGE = 24;
  /**
   * Direct Message
   */
  public static final int DIRECT_MESSAGE = 25;
  /**
   * Failed Message
   */
  public static final int FAILED_MESSAGE = 26;
  /**
   * Send Insult
   */
  public static final int SEND_INSULT = 27;
  /**
   * Other
   */
  public static final int OTHER = 28; //
}
