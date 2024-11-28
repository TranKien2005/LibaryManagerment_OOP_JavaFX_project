package QR;

import org.junit.Test;
import static org.junit.Assert.*;

public class QRCodeListenerTest {

    @Test
    public void testQRCodeListener() {
        QRCodeListener listener = new QRCodeListener() {
            @Override
            public void onQRCodeDetected(String qrCodeText) {
                assertEquals("Test QR Code", qrCodeText);
            }
        };
        listener.onQRCodeDetected("Test QR Code");
    }
} 