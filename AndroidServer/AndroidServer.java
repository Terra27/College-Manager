import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AndroidServer {
	public static void main(String[] args) throws IOException {
		
		final String HOSTNAME = "localhost";
		final String MYSQL_PORT = "3306";
		
		final String SUBPROTOCOL = "mysql";
		final String DATABASE = "collegemanager";
		
		Connection databaseConnection = null;
		
		AndroidDownloadServer downloadServer = null;
		AndroidUploadServer uploadServer = null;
		AndroidDatabaseHandler databaseServer = null;
		
		try {
		    // Database Parameters
		    String url = "jdbc:"+ SUBPROTOCOL +"://"+ HOSTNAME +":"+ MYSQL_PORT + "/"+ DATABASE;
		    String username = "root";
		    String password = "";
			
		    // Create a connection to the Database
		    databaseConnection = DriverManager.getConnection(url, username, password);
		    if ( databaseConnection != null ) {
		    	System.out.println("MONITOR SERVER: Established connection to database, initializating Download, Upload and Database servers..");
		    	
		    	// Objects cannot be static of course, we're only storing references to them in static variables.
		    	downloadServer = new AndroidDownloadServer(true);
		    	new Thread(downloadServer).start();
		    	
		    	uploadServer = new AndroidUploadServer(true);
		    	new Thread(uploadServer).start();
		    	
		    	databaseServer = new AndroidDatabaseHandler(databaseConnection, true);
		    	new Thread(databaseServer).start();
		    }
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		BufferedReader commandReader = new BufferedReader( new InputStreamReader( System.in ) );
		String command = commandReader.readLine();
		while ( !command.equals("quit") )
		{
			System.out.println("MONITOR SERVER: Unknown command, type 'quit' to shut the server.");
			command = commandReader.readLine();
		}
		
		commandReader.close();
		System.out.println("MONITR SERVER: Closing server.. please wait..");
		
		downloadServer.keepRunning = false;
		uploadServer.keepRunning = false;
		databaseServer.keepRunning = false;
		
	}	
}
