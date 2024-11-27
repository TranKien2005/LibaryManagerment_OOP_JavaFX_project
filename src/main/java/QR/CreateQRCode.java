package QR;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.stage.Stage;



public class CreateQRCode extends Application {
    

public static InputStream generateQRCode(String text) throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    int width = 300;
    int height = 300;
    Map<EncodeHintType, Object> hints = new HashMap<>();
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
    
    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    byte[] pngData = pngOutputStream.toByteArray();
    return new ByteArrayInputStream(pngData);
}

@Override
public void start(Stage stage) {
    try {
        InputStream qrCodeStream = generateQRCode("Hello, World!");
        // Use the qrCodeStream as needed
        javafx.scene.image.Image qrImage = new javafx.scene.image.Image(qrCodeStream);
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(qrImage);

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.getChildren().add(imageView);

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 300, 300);

        stage.setTitle("QR Code");
        stage.setScene(scene);
        stage.show();
    } catch (WriterException | IOException e) {
        e.printStackTrace();
    }
}

public static void main(String[] args) {
    launch(args);
}
}
