package QR;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * A JavaFX application that generates and displays a QR code.
 */
public class CreateQRCode extends Application {

    /**
     * Generates a QR code as an InputStream.
     * 
     * @param text The text to encode into the QR code.
     * @return An InputStream containing the QR code image in PNG format.
     * @throws WriterException If there is an error in generating the QR code.
     * @throws IOException If there is an error writing the QR code image to a stream.
     */
    public static InputStream generateQRCode(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        // Generate the QR code as a BitMatrix
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // Convert the BitMatrix to a PNG image
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        
        // Return the PNG image as an InputStream
        return new ByteArrayInputStream(pngData);
    }

    /**
     * Starts the JavaFX application, generating and displaying the QR code.
     * 
     * @param stage The primary stage for the JavaFX application.
     */
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void start(Stage stage) {
        try {
            // Generate the QR code and convert it to an InputStream
            InputStream qrCodeStream = generateQRCode("Hello, World!");

            // Create an Image from the InputStream
            javafx.scene.image.Image qrImage = new javafx.scene.image.Image(qrCodeStream);
            
            // Display the QR code in an ImageView
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(qrImage);

            // Set up the layout and scene
            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
            root.getChildren().add(imageView);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 300, 300);

            // Configure the stage and display it
            stage.setTitle("QR Code");
            stage.setScene(scene);
            stage.show();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method to launch the JavaFX application.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
