package QR;

import org.junit.Test;
import static org.junit.Assert.*;

public class QRScannerTest {

    @Test
    public void testGetInstance() {
        QRScanner scanner1 = QRScanner.getInstance();
        QRScanner scanner2 = QRScanner.getInstance();
        assertSame(scanner1, scanner2);
    }

    @Test
    public void testIsRunning() {
        QRScanner scanner = QRScanner.getInstance();
        scanner.startQRScanner(qrCodeText -> {});
        assertTrue(scanner.isRunning());
        scanner.stopQRScanner();
        assertFalse(scanner.isRunning());
    }
} 