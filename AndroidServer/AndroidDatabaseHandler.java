import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AndroidDatabaseHandler implements Runnable {
	
	private final int PORT = 1602;
	private Connection databaseConnection = null;
	public boolean keepRunning = false;
	
	private final int TIMEOUT = 2000;
	
	private int clientCount = 0;
	
	public AndroidDatabaseHandler(Connection connection, boolean running) {
		databaseConnection = connection;
		keepRunning = running;
	}
	
	public void run() {
		try {
			
			if ( databaseConnection != null ) {
			    System.out.println("DATABASE SERVER: listening for client connections on port "+ PORT +"..");
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
										System.out.println("DATABASE SERVER: Connection established to new client. (ID: "+ clientCount +")");
										
										// Add operating classifier
										
										BufferedReader queryReader = new BufferedReader( new InputStreamReader( serverSocket.getInputStream() ));
										PrintWriter resultWriter = new PrintWriter( serverSocket.getOutputStream() );
										
										String sqlQuery = queryReader.readLine();
										
										Statement statement  = databaseConnection.createStatement();
										ResultSet resultSet = statement.executeQuery( sqlQuery );
										
										StringBuilder resultBuilder = new StringBuilder();
										while (resultSet.next()) {
											int i = 1;
											while ( i <= resultSet.getMetaData().getColumnCount() ) {
												resultBuilder.append(resultSet.getString(i) +";");
												i++;
											}
											resultBuilder.append(";");
										}
										
										System.out.println("Wrote result: "+ resultBuilder.toString() +" to client. (ID: "+ clientCount +")");
										resultWriter.println(resultBuilder.toString());
										resultWriter.flush();
										
										queryReader.close(); // Only 1 query at a time.
										resultWriter.close();
									}
									catch (IOException e) {
										System.out.println(e.getMessage());
									}
									catch(SQLException e) {
										System.out.println(e.getMessage());
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
			    
			    System.out.println("DATABASE SERVER: Stopped listening on port "+ PORT +" Shutting down.");
			    listener.close();
			    databaseConnection.close();
		    }
			
		}
		catch(IOException e ) {
			System.out.println("DATABASE SERVER: "+ e.getMessage());
		} 
		catch(SQLException e) {
			System.out.println("DATABASE SERVER: "+ e.getMessage());
		}
	}

}
