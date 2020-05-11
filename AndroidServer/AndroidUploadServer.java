import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AndroidUploadServer implements Runnable {

	private final int PORT = 1601;
	private final int BUFFER_SIZE = 8*1024;
	private final String ROOT_PATH = System.getProperty("user.dir") + "\\src\\";
	private final int TIMEOUT = 2000;
	
	public boolean keepRunning = false;
	private int clientCount = 0;
	
	public AndroidUploadServer(boolean running) {
		keepRunning = running;
	}
	
	public void run() {
		try {
			System.out.println("UPLOAD SERVER: Listening for client connections on port :"+ PORT);
			ServerSocket listener = new ServerSocket(PORT);
			listener.setSoTimeout(TIMEOUT);
			
			while( keepRunning ) {
				try {
					Socket tempSocket = listener.accept();
					if ( tempSocket != null ) {
						
						// Client connected!
						// Create an anonymous thread to service this client
						new Thread( ) {
							
							public void run() {
								try {
									// tempSocket keeps on changing, keep reference to your own socket.
									Socket serverSocket = tempSocket;
									
									clientCount++;
									System.out.println("UPLOAD SERVER: Connection established to new client. (ID: "+ clientCount +")");
									
									// Read file name
									BufferedReader reader = new BufferedReader( new InputStreamReader( serverSocket.getInputStream() ));
									String dataLine = reader.readLine();
									
									String professorName = dataLine.substring(0, dataLine.lastIndexOf(';'));
								
									// Make a Dedicated Directory for the faculty member if not present
									File directory = new File(ROOT_PATH + professorName);
									// 1. Nothing exists at path, make directory.
									// 2. Something exists at path, it is not a directory, make directory.
									if ( !directory.exists() || !directory.isDirectory() )
										directory.mkdir();
										
									String FILE_NAME = ROOT_PATH + professorName + "\\"+ dataLine.substring(dataLine.lastIndexOf(';') + 1);
									
									// Receive file
									BufferedInputStream bufferedInput = new BufferedInputStream( serverSocket.getInputStream(), BUFFER_SIZE );
									
									System.out.println("Creating file "+ FILE_NAME +" on server.");
									BufferedOutputStream bufferedFileOutput = new BufferedOutputStream( new FileOutputStream(FILE_NAME), BUFFER_SIZE );
									
									System.out.println("Copying file from client..");
									
									byte[ ] buffer = new byte[BUFFER_SIZE];
									int bytesRead = bufferedInput.read(buffer);
									while ( bytesRead > 0 ) {
																			// Important parameter otherwise unusable bytes written to stream.
										bufferedFileOutput.write(buffer, 0, bytesRead); // BufferedOutputStream only writes to underlying OutputStream when internal buffer full.
										bytesRead = bufferedInput.read(buffer);
									}
									
									System.out.println("File Copied!");
									bufferedFileOutput.flush();
									bufferedFileOutput.close();
									
									if ( serverSocket != null ) {
										serverSocket.close();
										System.out.println("UPLOAD SERVER: Connection closed to client (ID: "+ clientCount +")");
									}
								}
								catch(IOException e) {
									System.out.println("UPLOAD SERVER: "+ e.getMessage());
								}
							}
						}.start();
					}
				}
				catch (SocketTimeoutException e) {
					// Nothing, just wake up after every two seconds to check the value of keepRunning.
					if ( !keepRunning )
						break;
				}
				
			} // loop
				
			listener.close();
			System.out.println("UPLOAD SERVER: Stopped listening on port "+ PORT +" Shutting down.");
		}
		catch (IOException e) {
			System.out.println("UPLOAD SERVER: "+ e.getMessage());
		}
	}
}
