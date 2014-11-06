import java.io.*;
import java.security.*;
import javax.net.ssl.*;

public class HttpsDaemon {

	public static void main(String[] args) {
		String ksName = "keystore.jks";
		char ksPass[] = "123456".toCharArray();
		char ctPass[] = "123456".toCharArray();
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(ksName), ksPass);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, ctPass);
			
			int serverPort = 8888;
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(serverPort);
			System.out.println("Server started:");
			printServerSocketInfo(s);
			
			int i = 0;
			while(true) {
				// Listening to the port
				SSLSocket c = (SSLSocket) s.accept();
				printSocketInfo(c);
				
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
				BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
				
				String m = r.readLine();
				w.write("HTTP/1.0 200 OK");
				w.newLine();
				w.write("Content-Type: text/html");
				w.newLine();
				w.newLine();
				w.write("<html><body>Hello world ! " + i + "</body></html>");
				w.newLine();
				w.flush();
				w.close();
				r.close();
				c.close();
				
				i++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: " + s.getClass());
		System.out.println("   Remote address = " + s.getInetAddress().toString());
		System.out.println("   Remote port = " + s.getPort());
		System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
		System.out.println("   Local address = " + s.getLocalAddress().toString());
		System.out.println("   Local port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = " + ss.getCipherSuite());
		System.out.println("   Protocol = " + ss.getProtocol());
	}
	
	private static void printServerSocketInfo(SSLServerSocket s) {
		System.out.println("Server socket class: "+s.getClass());
		System.out.println("   Socker address = " + s.getInetAddress().toString());
		System.out.println("   Socker port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		System.out.println("   Want client authentication = " + s.getWantClientAuth());
		System.out.println("   Use client mode = " + s.getUseClientMode());
	} 
}