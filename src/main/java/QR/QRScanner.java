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

    private CanvasFrame canvas; // Cửa sổ quét
    private OpenCVFrameGrabber grabber; // Quản lý camera

    public AtomicBoolean running = new AtomicBoolean(false);

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
        running.set(true);
         ThreadManager.execute(() -> {
            int deviceIndex = 0; // Try different device indices if needed
            if (!isCameraAvailable(deviceIndex)) {
                System.err.println("Camera is not available");
                throw new RuntimeException("Camera is not available");
            }
            grabber = new OpenCVFrameGrabber(deviceIndex);
            try  {
                
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
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                stopQRScanner();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        });
    }

    public void stopQRScanner() {  
        running.set(false); // Dừng vòng lặp quét
        if (grabber != null) {
            try {
                grabber.stop(); // Giải phóng tài nguyên camera
                grabber.close();
                grabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (canvas != null) {
            canvas.dispose(); // Đóng cửa sổ
        }
    }

    public boolean isRunning() {
        return  running.get();
    }
    public void resetInstance() {
        instance = null;
    }

    private boolean isCameraAvailable(int cameraIndex) {
        OpenCVFrameGrabber testGrabber = new OpenCVFrameGrabber(cameraIndex);
        try {
            testGrabber.start();
            testGrabber.stop();
            testGrabber.release();
            return true; // Camera hoạt động
        } catch (Exception e) {
            System.err.println("Camera is unavailable: " + e.getMessage());
            return false;
        }
    }
    

    public static void main(String[] args) {
        System.setProperty("opencv.videoio.MSMF", "false");
        QRScanner scanner = new QRScanner();
        scanner.startQRScanner(qrCodeText -> {
            System.out.println("QR Code detected: " + qrCodeText);
            System.out.println(scanner.isRunning());
        });
    }
}