package QR;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.InputStream;

public class CreateQRCodeTest {

    @Test
    public void testGenerateQRCode() throws Exception {
        String text = "Hello, World!";
        InputStream qrCodeStream = CreateQRCode.generateQRCode(text);
        assertNotNull(qrCodeStream);
        // Thêm kiểm tra khác nếu cần
    }
} 