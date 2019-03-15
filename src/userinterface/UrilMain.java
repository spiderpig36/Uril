package userinterface;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import statistics.ObjectFactory;
import statistics.Statistics;

/**
 * Main class of the userinterface. Starts and stops the javaFX scene. Makes
 * sure all threads are stopped before the application ends. Writes the
 * statistics to the xml File when the window is closed.
 * 
 * @author Nic Dorner
 *
 */
public class UrilMain extends Application {
	private GameController gameController;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GameView.fxml"));
			gameController = new GameController(this.readStatistics());
			loader.setController(gameController);
			HBox root = (HBox) loader.load();
			Scene scene = new Scene(root, 630, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		this.writeStatistics(gameController.getStatistics());
		gameController.close();
	}

	/**
	 * Write the statistics object to an xml File
	 * 
	 * @param statistics
	 *            The statistics object to be writen
	 */
	public void writeStatistics(Statistics statistics) {
		try {
			// The output XML file
			File file = new File("statistics.xml");
			// We need a JAXBContex which knows our package (generated code)
			JAXBContext context = JAXBContext.newInstance("statistics");
			// Use the context to receive a marshaller object
			Marshaller m = context.createMarshaller();
			// set property if you want a pretty printed XML file
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(statistics, file); // Create the file
		} catch (JAXBException jbe) {
			jbe.printStackTrace();
		}
	}

	/**
	 * Loads a statistics object from a file
	 * 
	 * @return The loaded statistics object
	 */
	public Statistics readStatistics() {
		try {
			// Get the context with out a specific class !!!
			JAXBContext context = JAXBContext.newInstance("statistics");

			// Get an unmarshaller object from the context
			Unmarshaller unmarshaller = context.createUnmarshaller();

			// Define a source (=our XML file)
			Source source = new StreamSource(new java.io.FileInputStream("statistics.xml"));

			// UnMarshall the XML content and get access to the root element
			JAXBElement<Statistics> root = unmarshaller.unmarshal(source, Statistics.class);

			return root.getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// First we need a factory object to get basic access!
			ObjectFactory objectFactory = new ObjectFactory();

			return objectFactory.createStatistics();
		}

		return null;
	}
}
