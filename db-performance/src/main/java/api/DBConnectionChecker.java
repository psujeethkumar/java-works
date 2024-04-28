package api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import dao.DBConnection;

@SpringBootApplication
@RestController
public class DBConnectionChecker {

	public static void main(String[] args) {
		SpringApplication.run(DBConnectionChecker.class, args);
	}

	@GetMapping("/testDBConnection")
	public String testDBConnection() {
		String dbConStatus = "";
		DataSource dbConn = DBConnection.setupDataSource();
		try {
			if (dbConn != null) {
				Connection connection = dbConn.getConnection();
				if (connection != null) {
					dbConStatus = "Connection to Database is OK." + connection.hashCode();
				} else {
					dbConStatus = "Error during fetching connection to DB from the JDBC Connection Poool. Check DB Connection manager settings.";
					throw new Exception(dbConStatus);
				}
			} else {
				dbConStatus = "Error during fetching connection to DB from the JDBC Connection Poool. Check DB Connection manager settings.";
				throw new Exception(dbConStatus);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return dbConStatus;
	}

	@PostMapping("/generateLoad/{input}")
	public String generateLoad(@PathVariable int numberOfThreads) {
		String dbConStatus = "";
		for (int i = 0; i < numberOfThreads; i++) {
			DataSource dbConn = DBConnection.setupDataSource();
			try {
				if (dbConn != null) {
					Connection connection = dbConn.getConnection();
					if (connection != null) {
						dbConStatus = "Connection to Database is OK.";

						Statement stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(
								"SELECT DESTINATION_1 FROM MARKETMESSAGE_ROUTER WHERE BUSINESSPROCESS='Start Access' AND BUSINESSLABEL='Supplier Switch' AND DOCUMENTTYPE='Start Index Received';\n"
										+ "");
						while (rs.next())
							System.out.println("Iteration number # " + i + " : " + rs.getString(1));
						stmt.close();
						connection.close();

					} else {
						dbConStatus = "Error during fetching connection to DB from the JDBC Connection Poool. Check DB Connection manager settings.";
						throw new Exception(dbConStatus);
					}
				} else {
					dbConStatus = "Error during fetching connection to DB from the JDBC Connection Poool. Check DB Connection manager settings.";
					throw new Exception(dbConStatus);
				}
			} catch (Exception e) {

			}
		}
		return dbConStatus;
	}

}
