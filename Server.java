
import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Server extends Application {
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Text area for displaying contents
    TextArea ta = new TextArea();

    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    primaryStage.setTitle("Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread( () -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8099);
        Platform.runLater(() ->
          ta.appendText("Server started at " + new Date() + '\n'));
  
        // Listen for a connection request
        Socket socket = serverSocket.accept();
  
        // Create data input and output streams
        DataInputStream inputFromClient = new DataInputStream(
          socket.getInputStream());
        DataOutputStream outputToClient = new DataOutputStream(
          socket.getOutputStream());
  
        while (true) {
          // Receive number from the client to show array elements
        	
          int number = inputFromClient.readInt();
  
          //Set Array
          try {
			get(number);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
          
  		
  
          // Send answer back to the client
          outputToClient.writeUTF(finalArray.toString());
  
          Platform.runLater(() -> {
            ta.appendText("Number from Client: " + number);
            ta.appendText("Entries Sent: " + number); 
          });
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }

  Stage window;
	Scene scene1;
	Scene scene2;
	static ResultSet rs = null;
	static ArrayList<String> finalArray = new ArrayList<String>();
	
	
	public static ArrayList<String> get(int n) throws Exception{
		try {
		Connection con = getConnection();
		PreparedStatement statement = con.prepareStatement("SELECT word , COUNT(*) FROM `wordappdb`.`word` GROUP BY word ORDER BY COUNT(*) DESC LIMIT " + n + "; ");
		
		ResultSet result = statement.executeQuery();
		
		ArrayList<String> array = new ArrayList<String>();
		while(result.next()) {
			System.out.print(result.getString("word"));
			System.out.print(": ");
			System.out.println(result.getString("COUNT(*)"));
			
			array.add(result.getString("word") + " :" + result.getString("COUNT(*)"));
		}
		System.out.println("Top 20 records Selected!");
		finalArray = array;
		
		return array;
		
		}catch (Exception e) {System.out.println(e);}
		return null;
		}
  
public static void post(String w) throws Exception{
		
		
		try {
			Connection con = getConnection();
			PreparedStatement posted = con.prepareStatement("INSERT INTO word (word) VALUES ('"+w+"')");
			posted.executeUpdate();
			posted.close();

		}catch(Exception e) {
			System.out.println(e);
		}
		
		finally { System.out.println("Insert completed");
		
		}
	}
	
	public static void returnTopTW() throws Exception{
		try {
			 Connection con = getConnection();
			 PreparedStatement create = con.prepareStatement("SELECT word , COUNT(*) FROM `wordappdb`.`word` GROUP BY word ORDER BY COUNT(*) DESC LIMIT 20; ");
			rs = create.executeQuery();
			create.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		finally {System.out.println("Function complete.");}
	}
	
	public static void createTable() throws Exception{
		try {
			 Connection con = getConnection();
			 PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS word(word VARCHAR(50))");
			create.executeUpdate();
			create.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		finally {System.out.println("Function complete.");}
	}
	
	public static Connection getConnection() throws Exception{
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/wordappdb";
			String username = "george";
			String password = "1234";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, username, password);

			
			
			System.out.println("Connected");
			return conn;
		} catch(Exception e) {
			System.out.println(e);
		}
		
		
		
		
		return null;
	}
  
  
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) throws Exception{
	  createTable();
	//This is the actual code that counts the words
		
			/*First I download the text from the webpage and turn it into a long string
			 */
			
			String raven ="";
			
			try {
				String webPage = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";
				URL url = new URL(webPage);
				URLConnection urlConnection = url.openConnection();
				InputStream is = urlConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);

				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();

				raven=result;
				
			} catch (MalformedURLException q) {
				q.printStackTrace();
			} catch (IOException q) {
				q.printStackTrace();
			}
			
			/*This next part removes all unnecessary pieces and symbols from the text*/
			
			String ravena = raven.substring(3167,11430);
			

			
			ravena = ravena.replaceAll("<[^>]*>", "");
			ravena = ravena.replaceAll("â€™", "");
			ravena = ravena.replaceAll("&mdash;", " ");
			ravena = ravena.replaceAll(";", " ");
			ravena = ravena.replaceAll("â€œ", "");
			ravena = ravena.replaceAll("â€", "");

			ravena = ravena.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " ");

			
			String[] ravray = ravena.split(" ");
			
			System.out.println("I will wait to post");
			Thread.sleep(5000);
			
			post("Testing 1 post");
			
			for (int i=0; i<=ravray.length-1; i++) {
				post(ravray[i]);
				System.out.println(i +" Posted");
//				Thread.sleep();
				
			}
			System.out.println("This works");
			
			
			returnTopTW();
			System.out.println(rs + " This should be the results");
			
	  
    launch(args);
  }
}
