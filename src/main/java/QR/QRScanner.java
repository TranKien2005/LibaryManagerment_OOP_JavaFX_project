package QR;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import util.ThreadManager;

/**
 * Singleton class responsible for scanning QR codes using the device's camera.
 * This class captures frames from the camera, decodes QR codes, and notifies listeners when a QR code is detected.
 */
public class QRScanner {

    private static QRScanner instance;

    /**
     * Private constructor for Singleton pattern.
     */
    private QRScanner() {
    }

    /**
     * Retrieves the singleton instance of the QRScanner.
     * 
     * @return The singleton instance of QRScanner.
     */
    @SuppressWarnings("DoubleCheckedLocking")
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

    private CanvasFrame canvas; // Cửa sổ quét
    private OpenCVFrameGrabber grabber; // Quản lý camera

    public AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Interface to handle QR code detection events.
     * The onQRCodeDetected method will be called when a QR code is detected.
     */
    public interface QRCodeListener {
        /**
         * Called when a QR code is detected.
         * 
         * @param qrCodeText The decoded text from the QR code.
         */
        void onQRCodeDetected(String qrCodeText);
    }

    /**
     * Decodes the QR code from the given BufferedImage.
     * 
     * @param bufferedImage The image containing the QR code.
     * @return The decoded text from the QR code.
     * @throws Exception If there is an error during decoding.
     */
    public static String decodeQRCode(BufferedImage bufferedImage) throws Exception {
        LuminanceSource source = new com.google.zxing.client.j2se.BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }

    /**
     * Converts a Frame object to a BufferedImage.
     * 
     * @param frame The frame to convert.
     * @return The converted BufferedImage.
     */
    public static BufferedImage frameToBufferedImage(Frame frame) {
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            return converter.getBufferedImage(frame);
        }
    }

    /**
     * Starts the QR scanner to capture frames from the camera and detect QR codes.
     * 
     * @param listener The listener to notify when a QR code is detected.
     */
    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
    public void startQRScanner(QRCodeListener listener) {
        if (isRunning()) {
            return;
        }
        running.set(true);
        ThreadManager.execute(() -> {
            int deviceIndex = 0; // Try different device indices if needed
            grabber = new OpenCVFrameGrabber(deviceIndex);
            try {
                grabber.start();
                canvas = new CanvasFrame("QRScanner");
                canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
                canvas.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                stopQRScanner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Stops the QR scanner, releasing camera resources and closing the scanning window.
     */
    public void stopQRScanner() {
        running.set(false); // Dừng vòng lặp quét
        if (grabber != null) {
            try {
                grabber.stop(); // Giải phóng tài nguyên camera
                grabber.close();
                grabber.release();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
        if (canvas != null) {
            canvas.dispose(); // Đóng cửa sổ
        }
    }

    /**
     * Checks if the QR scanner is currently running.
     * 
     * @return True if the scanner is running, false otherwise.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Resets the singleton instance of the QRScanner.
     */
    public void resetInstance() {
        instance = null;
    }

    /**
     * Main method to start the QR scanner and print detected QR code text.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        QRScanner scanner = new QRScanner();
        scanner.startQRScanner(qrCodeText -> {
            System.out.println("QR Code detected: " + qrCodeText);
            System.out.println(scanner.isRunning());
        });
    }
}
