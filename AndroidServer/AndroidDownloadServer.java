import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AndroidDownloadServer implements Runnable {
	
	private final int PORT = 1600;
	private final int BUFFER_SIZE = 8*1024;
	private final String ROOT_PATH = System.getProperty("user.dir") + "\\src\\";
	private final int TIMEOUT = 2000;
	
	public boolean keepRunning = false;
	private int clientCount = 0;
	
	public AndroidDownloadServer(boolean running) {
		keepRunning = running;
	}
	
	public void run() {
		try {
			
			System.out.println("DOWNLOAD SERVER: Listening for client connections on port :"+ PORT);
			ServerSocket listener = new ServerSocket(PORT);
			listener.setSoTimeout(TIMEOUT);
			
			while ( keepRunning ) {
				
				// PROBLEM: THREAD BLOCKED NO EFFECT ON CHANGEI IN KEEP RUNNING
				// SOLUTION: USE TIMEOUT TO WAKE UP AFTER x SECONDS AND CHECK ON KEEP RUNNING
				try {
					Socket tempSocket = listener.accept();
					if ( tempSocket != null ) {
						System.out.println("DOWNLOAD SERVER: Returned no null, Accepted Connection, creating thread.");
						// Client connected!
						// Create an anonymous thread to service this client
						new Thread( ) {
							
							public void run() {
								try {
									// tempSocket keeps on changing, keep reference to your own socket.
									Socket serverSocket = tempSocket;
									
									clientCount++;
									System.out.println("DOWNLOAD SERVER: Connection established to new client. (ID: "+ clientCount +")");
									
									// Read file name
									BufferedReader reader = new BufferedReader( new InputStreamReader( serverSocket.getInputStream() ));
									String FILE_NAME = ROOT_PATH + reader.readLine();
										
									// Send file
									System.out.println("DOWNLOAD SERVER: Opening file "+ FILE_NAME);
									BufferedInputStream bufferedFileInput = new BufferedInputStream( new FileInputStream(FILE_NAME), BUFFER_SIZE ); // default 2KB
									BufferedOutputStream bufferedOutput = new BufferedOutputStream( serverSocket.getOutputStream(), BUFFER_SIZE ); // default 512 bytes
									
									System.out.println("DOWNLOAD SERVER: Copying to connection stream..");
									// int byteRead = fileStream.read(); // reading 1 byte from BufferedInputStream's buffer. (inefficient)
									byte[ ] buffer = new byte[BUFFER_SIZE];
									int bytesRead = bufferedFileInput.read(buffer); // *read 4KB from the BufferedInputStream's buffer. (The internal buffer is refilled as soon as it is completely read)
									
									//	int bytesTransfer = 0;
									//	double fileSize = (double)( ( new File(FILE_PATH) ).length( ) );
									
									while ( bytesRead > 0 ) {
										// bufferedOutput.write(byteRead) // writing 1 byte to BufferedOutputStream's buffer.
										bufferedOutput.write(buffer, 0, bytesRead); // *writing 4KB in one go to BufferedOutputStream's buffer.
										
										// The internal buffer of BufferedOutputStream only writes to the underlying OutputStream when the buffer is full.
										// Flushing flushes the internal buffer data to the underlying OutputStream forcefully.
										bufferedOutput.flush(); // Otherwise synchronization problems possible? 
									
										
										//bytesTransfer += bytesRead;
										bytesRead = bufferedFileInput.read(buffer);
										
										//System.out.printf("%% Completion: %1.2f %% %n", (((double)bytesTransfer)/fileSize)*100 );
									}
									
									System.out.println("DOWNLOAD SERVER: Complete! Closing "+ FILE_NAME);
									bufferedFileInput.close();
									
									// Closing a stream also closes the underlying connection socket.
									//reader.close();
									
									if ( serverSocket != null ) {
										serverSocket.close();
										System.out.println("DOWNLOAD SERVER: Connection closed to client (ID: "+ clientCount +")");
									}
								}
								catch(IOException e) {
									System.out.println("DOWNLOAD SERVER: "+ e.getMessage());
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
			System.out.println("DOWNLOAD SERVER: Stopped listening on port "+ PORT +" Shutting down.");
		}
		catch(IOException e) {
			System.out.println("DOWNLOAD SERVER: "+ e.getMessage());
		}
	}
	
}
