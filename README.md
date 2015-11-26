Java TCP Tunnel
===============

A simple tool for capturing and inspecting data sent over a socket.

Can be used either from command line or as a Java library.

But why?
--------

I write software. In Java (yes, how dinosaurish is that?).
Pretty much every software I write these days seems to be networked to some extent.
Trying to test networked software often is just more complicated than it needs to be.

And then we end up using all kinds of libraries, frameworks, whatever.
Pretty much everything also seems to fail in mysterious ways under all those layers of libraries, factories, dependency injections, black boxes, or whatever.
Too many times I have tried to look for some solutions to get insight into this on the internet.
Too many times have I ended up on the website of Fiddler or some complex (for me) solution requiring installing too many dependencies (Mono etc).
Or with complex user interfaces when I just wanted to see what really went on (both ways client<->server) when making those network requests.

This is an attempt to make debugging and testing the networked stuff easier for me.
I am a simple kind of a guy (who likes the programmatic approach), so this is an attempt to make something simple enough for me.
So that's why.

Example use from command line (from the scripts directory):
-----------------------------------------------------------

Forward local port 5566 to www.github.com port 80, default options will print decoded (UTF8) strings to console:

java -jar tcptunnel-0.1.0.jar 5566 www.github.com 80

To request the site now:

curl localhost:5566 --header 'Host: www.github.com'

Note the need to fix the "Host" header for a regular HTTP website request.

Same as above but log to a file.

java -jar tcptunnel-0.1.0.jar 5566 www.github.com 80 --logger file-string

Log raw bytes to a file:

java -jar tcptunnel-0.1.0.jar 5566 www.github.com 80 --logger file-bytes

Log bytes as list of integers to console:

java -jar tcptunnel-0.1.0.jar 5566 www.github.com 80 --logger console-bytes

Log bytes as hexadecimal string to console:

java -jar tcptunnel-0.1.0.jar 5566 www.github.com 80 --logger console-bytes --hex

For the options

java -jar tcptunnel-0.1.0.jar --help

Example use from Java (from the tests directory):
-------------------------------------------------


Example test model:

```java
public class CaptureTests {
  @Test
  public void sendRequestMITM() throws Exception {
    //create a test server to give us a page to request
    TestServer server = new TestServer(5599, "test1");
    server.start();
    //configure the tunnel to accept connections on port 5598 and forward them to localhost:5599
    Params params = new Params(5598, "localhost", 5599);
    //we want to use the captured data in testing, so enable logging the tunnel data in memory with buffer size 8092 bytes
    params.enableInMemoryLogging(8092);
    //this gives us access to the data passed from client connected to port 5598 -> localhost:5599 (client to server)
    InMemoryLogger upLogger = params.getUpMemoryLogger();
    //this gives us access to the data passed from localhost:5599 -> client connected to port 5598 (server to client)
    InMemoryLogger downLogger = params.getDownMemoryLogger();
    //this is how we actually start the tunnel
    Main main = new Main(params);
    main.start();
    //send a test request to get some data in the tunnel
    String response = MsgSender.send("http://localhost:5598", "hi there");
    //check we got the correct response from the server
    assertEquals(response, "test1", "Response content");
    //assert the HTTP protocol data passed through the tunnel both ways
    assertTcpStream(upLogger, "expected_up1.txt");
    assertTcpStream(downLogger, "expected_down1.txt");
    //the test server sometimes seems cranky if we stop it too soon
    Thread.sleep(1000);
    server.stop();
  }

  private void assertTcpStream(InMemoryLogger logger, String filename) throws Exception {
    //here we get the actual data that was passed through the tunnel in one direction (depending if we get passed the upstream memorylogger or downstream)
    String actual = logger.getString("UTF8");
    //the rest of this is just making sure the test should run the same over different platforms and with varying date-times in HTTP headers
    actual = TestUtils.unifyLineSeparators(actual, "\n");
    String expected = TestUtils.getResource(CaptureTests.class, filename);

    String[] replaced = TestUtils.replace("##", expected, actual);
    actual = replaced[0];
    expected = replaced[1];

    expected = TestUtils.unifyLineSeparators(expected, "\n");
    assertEquals(actual, expected, "Request full content");
  }
}
```

Installing
----------

Either use Maven dependencies or download the jar directly (when I get it uploaded).

```xml
<dependency>
	<groupId>net.kanstren</groupId>
	<artifactId>tcptunnel</artifactId>
	<version>1.0.0</version>
</dependency>
```

or direct [link](http://central.maven.org/maven2/net/kanstren/tcptunnel/1.0.0/tcptunnel-1.0.0.jar)

License
-------

MIT License

