package QR;

/**
 * Interface to handle the detection of QR codes.
 * This interface defines a method to be called when a QR code is detected.
 */
public interface QRCodeListener {

    /**
     * Method to be called when a QR code is detected.
     * 
     * @param qrCodeText The text encoded in the detected QR code.
     */
    void onQRCodeDetected(String qrCodeText);
}
