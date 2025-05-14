import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ErrorMessageTest {
  private ErrorMessage right;
  private ErrorMessage backup;


  @BeforeEach
  public void setUp() {
    right = new ErrorMessage();
    backup = new ErrorMessage();
  }

  @Test
  void testErrorMessageCreated() {
    assertEquals(true, right != null);
  }

  @Test
  void testgetErrorMessage() {
    assertTrue(ErrorMessage.getErrorMessage().contains("Invalid"));
    assertTrue(ErrorMessage.getErrorMessage().contains("!user"));
  }

  @Test
  void testGetMenu() {
    assertFalse(ErrorMessage.getMenu().contains("Invalid"));
    assertTrue(ErrorMessage.getMenu().contains("!user"));
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("menu"));
  }

  @Test
  void testHashCode() {
    assertEquals(right.hashCode(), right.hashCode());
    assertEquals(backup.hashCode() ,right.hashCode());
  }

  @Test
  void testEquals() {
    assertEquals(true,right.equals(right));

    assertEquals(true,right.equals(backup));
    assertEquals(true,backup.equals(right));

    ErrorMessage zvalue = new ErrorMessage();
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals("happy"));

  }
}

