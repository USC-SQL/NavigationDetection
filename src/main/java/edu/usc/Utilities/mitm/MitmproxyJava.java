package edu.usc.Utilities.mitm;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

//import edu.usc.sql.krawler.utilities.WriteToFile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class MitmproxyJava {

  private String mitmproxyPath;
  private String cachePath;
  private int proxyPort;
  private String pyScriptToWritePath;
  private String jsCodeToInjectPath;
  private String mode;
  private MitmproxyServer server;
  private Future<ProcessResult> mitmproxyProcess;
  public static final int WEBSOCKET_PORT = 8765;

  // CACHED_NOINJECTION mode if given following 3 parameters
  public MitmproxyJava(String mitmproxyPath, String cachePath, int proxyPort) {//, boolean injection
    this.mitmproxyPath = mitmproxyPath;
    System.out.println("MitmproxyJava: mitmproxyPath: " + mitmproxyPath);
    this.cachePath = cachePath;
    System.out.println("MitmproxyJava: cachePath: " + cachePath);
    this.proxyPort = proxyPort;
    this.mode = "CACHED_NOINJECTION";
    server = new MitmproxyServer(new InetSocketAddress("localhost", WEBSOCKET_PORT));
    server.start();
    System.out.println("MitmproxyJava: " + server.connections());
  }

  // NOCACHED_NOINJECTION mode if given following 2 parameters
  public MitmproxyJava(String mitmproxyPath, int proxyPort) {
    this.mitmproxyPath = mitmproxyPath;
    this.proxyPort = proxyPort;
    this.mode = "NOCACHED_NOINJECTION";
    server = new MitmproxyServer(new InetSocketAddress("localhost", WEBSOCKET_PORT));
    server.start();
  }

  // CACHED_INJECTION mode if given following 5 parameters
  public MitmproxyJava(String mitmproxyPath, String cachePath, int proxyPort, String pyScriptToWritePath, String jsCodeToInjectPath) {//, boolean injection
    this.mitmproxyPath = mitmproxyPath;
    this.cachePath = cachePath;
    this.proxyPort = proxyPort;
    this.pyScriptToWritePath = pyScriptToWritePath;
    this.jsCodeToInjectPath = jsCodeToInjectPath;
    this.mode = "CACHED_INJECTION";
    server = new MitmproxyServer(new InetSocketAddress("localhost", WEBSOCKET_PORT));
    server.start();
  }

  // NOCACHED_INJECTION mode if given following 4 parameters
  public MitmproxyJava(String mitmproxyPath, int proxyPort, String pyScriptToWritePath, String jsCodeToInjectPath) {
    this.mitmproxyPath = mitmproxyPath;
    this.proxyPort = proxyPort;
    this.pyScriptToWritePath = pyScriptToWritePath;
    this.jsCodeToInjectPath = jsCodeToInjectPath;
    this.mode = "NOCACHED_INJECTION";
    server = new MitmproxyServer(new InetSocketAddress("localhost", WEBSOCKET_PORT));
    server.start();
  }

  public void start() throws IOException, TimeoutException, URISyntaxException {
    //if (mode.equals("CACHED_INJECTION") || mode.equals("NOCACHED_INJECTION")) {
    //  System.out.println("MitmproxyJava: writing python injection script to file: " + "");
    //  writePythonScript(pyScriptToWritePath, jsCodeToInjectPath);
    //}

    System.out.println("MitmproxyJava: starting mitmproxy on port " + proxyPort);

    if (mode.equals("CACHED_NOINJECTION")) {   // no injection
      System.out.println("MitmproxyJava: mitmproxy CACHED_NOINJECTION mode...");
      System.out.println(mitmproxyPath + " --set " + " upstream_cert=false " + " --set " + " server_replay_kill_extra=true " + " --set " + " server_replay_nopop=true " + " -S " + cachePath + " -p " + proxyPort);
      mitmproxyProcess = new ProcessExecutor().command(mitmproxyPath, "--set", "upstream_cert=false", "--set", "server_replay_kill_extra=true", "--set", "server_replay_nopop=true", "-S", cachePath, "-p " + proxyPort)/*"--set", "upstream_cert=false", "--set", "server_replay_nopop=true", "-S", "/home/paul-sql/Downloads/mitmproxy-4.0.4-linux/sites_cache/vimeo")"--anticache", "-s", pythonScriptPath)*/.destroyOnExit().start().getFuture();
    } else if (mode.equals("CACHED_INJECTION")) {   // inject js script to override
      System.out.println("MitmproxyJava: mitmproxy CACHED_INJECTION mode...");
      mitmproxyProcess = new ProcessExecutor().command(mitmproxyPath, "--set", "upstream_cert=false", "--set", "server_replay_kill_extra=true", "--set", "server_replay_nopop=true", "-S", cachePath, "-p " + proxyPort + "", "-s", pyScriptToWritePath)/*"--set", "upstream_cert=false", "--set", "server_replay_nopop=true", "-S", "/home/paul-sql/Downloads/mitmproxy-4.0.4-linux/sites_cache/vimeo")"--anticache", "-s", pythonScriptPath)*/.destroyOnExit().start().getFuture();
    } else if (mode.equals("NOCACHED_NOINJECTION")) {   // no injection
      System.out.println("MitmproxyJava: mitmproxy NOCACHED_NOINJECTION mode...");
      mitmproxyProcess = new ProcessExecutor().command(mitmproxyPath, "-p", proxyPort + "")/*"--set", "upstream_cert=false", "--set", "server_replay_nopop=true", "-S", "/home/paul-sql/Downloads/mitmproxy-4.0.4-linux/sites_cache/vimeo")"--anticache", "-s", pythonScriptPath)*/.destroyOnExit().start().getFuture();
    } else if (mode.equals("NOCACHED_INJECTION")) {   // inject js script to override
      System.out.println("MitmproxyJava: mitmproxy NOCACHED_INJECTION mode...");
      mitmproxyProcess = new ProcessExecutor().command(mitmproxyPath, "-p " + proxyPort + "", "-s", pyScriptToWritePath)/*"--set", "upstream_cert=false", "--set", "server_replay_nopop=true", "-S", "/home/paul-sql/Downloads/mitmproxy-4.0.4-linux/sites_cache/vimeo")"--anticache", "-s", pythonScriptPath)*/.destroyOnExit().start().getFuture();
    }

    waitForPortToBeInUse(proxyPort);
    System.out.println("MitmproxyJava: mitmproxy started on port " + proxyPort);
  }

  //public void writePythonScript(String pyScriptPath, String jsScriptPath) {
  //  WriteToFile wtf = new WriteToFile(pyScriptPath);
  //  String pyInjectionCode = "from mitmproxy import ctx\n" + "\n" + "js_prefoix = \"<script type = 'text/javascript'> \"\n" + "js_suffix = \" </script> \\n\"\n" + "# Load in the javascript to inject.\n" + "with open('" + jsScriptPath + "', 'r') as f:\n" + "    injected_javascript = f.read()\n" + "\n" + "def response(flow):\n" + "	for k in flow.response.headers.items():\n" + "		if \"Content-Type\" in str(k) or \"content-type\" in str(k):\n" + "			if \"text/html\" in str(k):\n" + "				html = flow.response.text\n" + "				headtag_open_index = html.find('<head') + 5\n" + "				string_after_headtag_open = html[headtag_open_index:]\n" + "				headtag_close_index_in__string_after_headtag_open = string_after_headtag_open.find('>')\n" + "				headtag_close_index = headtag_close_index_in__string_after_headtag_open + headtag_open_index + 1\n" + "				injected_html = html[:headtag_close_index] + js_prefoix + injected_javascript + js_suffix + html[headtag_close_index:]\n" + "				flow.response.text = injected_html";
  //  wtf.write(pyInjectionCode);
  //}


  public void stop() throws IOException, InterruptedException {
    if (mitmproxyProcess != null) {
      mitmproxyProcess.cancel(true);
    }
    server.stop(1000);
    Thread.sleep(1000); // this pains me. but it seems that it takes a moment for the server to actually relinquish the port it uses.
  }

  private void waitForPortToBeInUse(int port) throws TimeoutException {
    boolean inUse = false;
    Socket s = null;
    int tries = 0;
    int maxTries = 60 * 1000 / 90;

    while (!inUse) {
      try {
        s = new Socket("localhost", port);
      } catch (IOException e) {
        inUse = false;
      } finally {
        if (s != null) {
          inUse = true;
          try {
            s.close();
          } catch (Exception e) {
          }
          break;
        }
      }
      tries++;
      if (tries == maxTries) {
        throw new TimeoutException("Timed out waiting for mitmproxy to start. Max tries: " + maxTries);
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}