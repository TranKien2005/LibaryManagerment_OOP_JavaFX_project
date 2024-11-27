package QR;
import org.bytedeco.javacv.*;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;

import util.ThreadManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public class QRScanner {
   
    private static QRScanner instance;

    private QRScanner() {
    }

    public static QRScanner getInstance() {
        if (instance == null) {
            synchronized (QRScanner.class) {
                if (instance == null) {
                    instance = new QRScanner();
                }
            }
        }
        return instance;
    }
    private CanvasFrame canvas;
    private AtomicBoolean running = new AtomicBoolean(false);

    public interface QRCodeListener {
        void onQRCodeDetected(String qrCodeText);
    }

    public static String decodeQRCode(BufferedImage bufferedImage) throws Exception {
        LuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }

    public static BufferedImage frameToBufferedImage(Frame frame) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        return converter.getBufferedImage(frame);
    }

    public void startQRScanner(QRCodeListener listener) {
        if (isRunning()) {
            return;
        }
         ThreadManager.execute(() -> {
            int deviceIndex = 0; // Try different device indices if needed
            running.set(true);
            try (OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(deviceIndex)) {
                grabber.start();
                canvas = new CanvasFrame("QRScanner");
                canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
                canvas.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        try {
                            grabber.stop();
                        } catch (FrameGrabber.Exception e) {
                            e.printStackTrace();
                        }
                        canvas.dispose();
                        stopQRScanner();
                    }
                });
                while (canvas.isVisible() && running.get()) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        System.err.println("Failed to grab frame");
                        continue;
                    }
                    canvas.showImage(frame);
                    BufferedImage bufferedImage = frameToBufferedImage(frame);
                    try {
                        String qrCodeText = decodeQRCode(bufferedImage);
                        if (qrCodeText != null) {
                            listener.onQRCodeDetected(qrCodeText);
                        }
                    } catch (NotFoundException e) {
                        // No QR code found in the image
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                grabber.stop();
                canvas.dispose();
                stopQRScanner();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        });
    }

    public void stopQRScanner() {  
        running.set(false);
        resetInstance();
    }

    public boolean isRunning() {
        return  running.get();
    }
    public void resetInstance() {
        instance = null;
    }

    public static void main(String[] args) {
        QRScanner scanner = new QRScanner();
        scanner.startQRScanner(qrCodeText -> {
            System.out.println("QR Code detected: " + qrCodeText);
            System.out.println(scanner.isRunning());
        });
    }
}