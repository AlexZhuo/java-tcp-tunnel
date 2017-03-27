package net.kanstren.tcptunnel;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import osmo.common.TestUtils;

import static org.testng.Assert.assertEquals;

/**
 * @author Teemu Kanstren.
 */
public class MainTests {
  @BeforeMethod
  public void before() {
    TestUtils.startOutputCapture();
  }
  
  @AfterMethod
  public void after() {
    TestUtils.endOutputCapture();
  }
  
  @Test
  public void noArgs() {
    Main.main(new String[] {});
    String output = TestUtils.getOutput();
    String expected = TestUtils.getResource(MainTests.class, "expected_help.txt")+"\n";
    output = TestUtils.unifyLineSeparators(output, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals(output, expected, "Output msg with no parameters");
  }

  @Test
  public void helpOnly() {
    Main.main(new String[] {"--help"});
    String output = TestUtils.getOutput();
    String expected = TestUtils.getResource(MainTests.class, "expected_help.txt")+"\n";
    output = TestUtils.unifyLineSeparators(output, "\n");
    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals(output, expected, "Output msg with no parameters");
  }
}
