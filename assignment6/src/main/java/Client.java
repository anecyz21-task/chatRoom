import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import messageModel.BroadcastMessage;
import messageModel.ConnectMessage;
import messageModel.ConnectResp;
import messageModel.DirectMessage;
import messageModel.DisconnectMessage;
import messageModel.DisconnectResp;
import messageModel.FailedMessage;
import messageModel.InsultMessage;
import messageModel.Message;
import messageModel.QueryResp;
import messageModel.QueryUsers;

/**
 * Represents a Client object that allows a user to connect and disconnect to a chat room server,
 * send and receive direct, group or random generated insult message to other users in the chatroom,
 * and check other user's name that are currently within the server
 */
public class Client {

  private final String host;
  private final int port;
  private String username;

  /**
   * Construct a Client object that allows a user to connect and disconnect to a chat room server,
   * send direct, group or random generated insult message to other users in the chatroom,
   * and check other user's name that are currently within the server
   *
   * @param host the host address of the server
   * @param port the port number the server is listening
   * @param username the username of the user this client is talking to
   */
  public Client(String host, int port, String username) {
    this.host = host;
    this.port = port;
    this.username = username;
  }

  /**
   * Disconnect the client from the server
   *
   * @param socket the socket connecting the client and the server
   * @param username the username of the user this client is talking to; the username that is trying to
   *                 disconnect from the server
   * @param out given output stream
   * @param in given input stream
   * @throws IOException if input or output stream is invalid or if socket is invalid
   */
  public static void disconnect(Socket socket, String username, DataOutputStream out, DataInputStream in) throws IOException {
    DisconnectMessage disMessage = new DisconnectMessage(username);
    disMessage.send(out);
    DisconnectResp disResp = DisconnectResp.receive(in);
    System.out.println(disResp.getMessage());
    socket.close();
  }

  /**
   * Show other usernames that are currently connected to the server
   *
   * @param out given output stream
   * @param in given input stream
   * @param username the username of the user that wants to look at who else is currently connected to the server
   * @throws IOException if input or output stream is invalid
   */
  public static void queryUser(DataOutputStream out, DataInputStream in, String username) throws IOException {
    QueryUsers queryMessage = new QueryUsers(username);
    queryMessage.send(out);
    QueryResp queryResult = QueryResp.receive(in);
    System.out.println(queryResult.getUsernames().toString());
  }

  /**
   * Helper method that get all usernames who are currently connected to the server but don't show to
   * the user
   *
   * @param out given output stream
   * @param in given input stream
   * @param username the username of the user that wants to look at who else is currently connected to the server
   * @return a list of other username connected to the server
   * @throws IOException if input or output stream is invalid
   */
  public static List<String> queryUserQuiet(DataOutputStream out, DataInputStream in, String username)
      throws IOException {
    QueryUsers queryMessage = new QueryUsers(username);
    queryMessage.send(out);
    QueryResp queryResult = QueryResp.receive(in);
    return queryResult.getUsernames();
  }

  /**
   * Send a direct message to a specific user connected to the server
   *
   * @param receiverName the username of the receiver
   * @param out given output stream
   * @param content the content of the direct message
   * @param senderName the username of the sender
   * @throws IOException if output stream is invalid
   */
  public static void sendMessage(String receiverName, DataOutputStream out, String content, String senderName) throws IOException {
    DirectMessage dirMessage = new DirectMessage(senderName, receiverName, content);
    dirMessage.send(out);
  }

  /**
   * Send a broadcast message to all other users connected to the server, if failed, a failed message
   * will be shown
   *
   * @param out given output stream
   * @param in given input stream
   * @param content the content of the broadcast message
   * @param senderName the username of the sender
   * @throws IOException if input or output stream is invalid
   */
  public static void broadCast(DataOutputStream out, DataInputStream in, String content, String senderName) throws IOException {
    BroadcastMessage broadCast = new BroadcastMessage(senderName, content);
    broadCast.send(out);
    FailedMessage potentialFail = FailedMessage.receive(in);
    if (potentialFail != null || !potentialFail.getErrorMessage().isEmpty()) {
      System.out.println(potentialFail.getErrorMessage());
    }
  }

  /**
   * Send a random generated insult to a specific user connected to the server and everyone else can see
   * the action
   *
   * @param senderName the sender of the insult message
   * @param receiverName the receiver of the insult message
   * @param out given output stream
   * @throws IOException if output stream is invalid
   */
  public static void sendInsult(String senderName, String receiverName, DataOutputStream out)
      throws IOException {
    InsultMessage insult = new InsultMessage(senderName, receiverName);
    insult.send(out);
  }

  /**
   *
   * @return the host address of the server
   */
  public String getHost() {
    return this.host;
  }

  /**
   *
   * @return the port number the server is listening
   */
  public int getPort() {
    return this.port;
  }

  /**
   *
   * @return the username of the user this client is talking to
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * starts the client and receive commands from user to connect to the chatroom, send and receive message and
   * query other users within the chatroom
   *
   * @param args connection information provided by user
   * @throws IOException if connection information are not valid
   */
  public static void main(String[] args) throws IOException {
    String hostName = args[0];
    String username = args[1];
    int port = 18888;
    DataOutputStream out;
    DataInputStream in;
    try (Socket socket = new Socket(hostName, port)) {
      out = new DataOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());

      ConnectMessage connectQuest = new ConnectMessage(username);
      connectQuest.send(out);
      ConnectResp response = ConnectResp.receive(in);
      if (response.getSuccess()) {
        System.out.println("Successfully connected to server");
        Thread listener = new Thread(() -> {
          try {
            receiveMessage(in);
          } catch (IOException ignored) {
          }
        });
        listener.start();
        talker(in, out, socket, username);
        listener.interrupt();

      } else {
        System.out.println("Failed to connect to server");
      }

    } catch (UnknownHostException e) {
      System.out.println("Server not found");
    } catch (IOException ignored) {
    }
  }

  /**
   * helper method that assist in receiving command from user
   *
   * @param out given output stream
   * @param in given input stream
   * @param socket the socket connecting the client and the server
   * @param username  the username of the user this client is talking to
   * @throws IOException if input output stream or the socket is invalid
   */
  public static void talker(DataInputStream in, DataOutputStream out, Socket socket, String username) throws IOException {
    Scanner sc = new Scanner(System.in);
    String userNotFound = "user not found";
    System.out.println("type ? to see command menu");
    boolean running = true;
    while (running) {
      String command = sc.nextLine();
      if (command.equals("?")) {
        System.out.println(ErrorMessage.getMenu());
      } else if (command.equals("logoff")) {
        disconnect(socket, username, out, in);
        running = false;
      } else if (command.equals("who")) {
        queryUser(out, in, username);
      } else if (command.startsWith("@")) {
        String receiverName = command.split(" ")[0];
        String message = "";
        if (command.length() == receiverName.length()) {
          System.out.println("message cannot be empty");
        } else {
          message = command.substring(receiverName.length() + 1);
        }
        List<String> users = queryUserQuiet(out, in, username);
        if (!receiverName.equals("@all")) {
          boolean foundUser = false;
          for (String user : users) {
            if (receiverName.substring(1).equals(user)) {
              sendMessage(receiverName.substring(1), out, message, username);
              foundUser = true;
            }
          }
          if (!foundUser) {
            System.out.println(userNotFound);
          }
        } else {
          broadCast(out, in, message, username);
        }
      } else if (command.startsWith("!")) {
        List<String> users = queryUserQuiet(out, in, username);
        String receiverName = command.substring(1);
        boolean foundUser = false;
        for (String user : users) {
          if (receiverName.equals(user)) {
            sendInsult(username, receiverName, out);
            foundUser = true;
          }
        }
        if (!foundUser) {
          System.out.println(userNotFound);
        }
      } else {
        System.out.println(ErrorMessage.getErrorMessage());
      }
    }
    sc.close();
  }

  /**
   * Helper method that assist another thread actively listening on message received by this client sent
   * by other users within the chatroom
   *
   * @param in given input stream
   * @throws IOException if given input stream is invalid
   */
  public static void receiveMessage(DataInputStream in) throws IOException {
    try {
      while (true) {
        Message message = Message.receive(in);
        if (message instanceof BroadcastMessage) {
          BroadcastMessage broadcast = (BroadcastMessage) message;
          System.out.println(broadcast.getMessage() + "; sender: " + broadcast.getSenderUsername() + "to all.");
        } else if (message instanceof DirectMessage) {
          DirectMessage directMessage = (DirectMessage) message;
          System.out.println(directMessage.getMessage() + "; sender: " + directMessage.getSenderUsername());
        }
      }
    } catch (IOException ignored) {
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "username: " + this.username + ", port: " + this.port + ", host: " + this.host;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.host.hashCode() * 12 + this.username.hashCode() * 234 + this.port * 159;
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
    Client that = (Client) o;
    return this.username.equals(that.getUsername()) && this.port == that.getPort()
        && this.host.equals(that.getHost());
  }
}
