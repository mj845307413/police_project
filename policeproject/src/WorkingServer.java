import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class WorkingServer implements Runnable {

	private ServerSocket server;

	WorkingServer(ServerSocket s) {
		server = s;
	}

	void communicat(Socket client) throws IOException {
		System.out.println("this sub thread: " + Thread.currentThread().getId());

		InputStream in = client.getInputStream();

		InputStreamReader read = new InputStreamReader(new BufferedInputStream(in));

		OutputStream out = (OutputStream) client.getOutputStream();

		OutputStreamWriter write = new OutputStreamWriter(new BufferedOutputStream(out));

		write.write("hello");
		write.flush();

		char[] cbuf = new char[100];
		Arrays.fill(cbuf, '\0');
		int len = read.read(cbuf, 0, 100);
		StringBuilder sb = new StringBuilder(100);
		sb.append(cbuf, 0, len);

		System.out.println(sb.toString());

		read.close();
		// /client.close();
		write.close();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread());
		while (true) {
			try {
				Socket client = server.accept();
				Thread.sleep(5000);
				communicat(client);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
