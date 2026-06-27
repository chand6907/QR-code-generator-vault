package com.qrvault.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

@Service
public class QRCodeService {

    /**
     * Generates a QR code as a Base64-encoded PNG string.
     *
     * @param data      The content to encode
     * @param size      Width and height in pixels
     * @param ecLevel   Error correction level: L, M, Q, H
     * @param fgColor   Foreground color as hex (e.g. #000000)
     * @param bgColor   Background color as hex (e.g. #ffffff)
     * @return Base64 PNG data URI string
     */
    public String generateQRCodeBase64(String data, int size, String ecLevel,
                                       String fgColor, String bgColor) throws Exception {
        // Map EC level string to ZXing enum
        ErrorCorrectionLevel level = switch (ecLevel.toUpperCase()) {
            case "L" -> ErrorCorrectionLevel.L;
            case "Q" -> ErrorCorrectionLevel.Q;
            case "H" -> ErrorCorrectionLevel.H;
            default  -> ErrorCorrectionLevel.M;
        };

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, level);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix;
        try {
            matrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
        } catch (WriterException e) {
            throw new Exception("Failed to generate QR code: " + e.getMessage(), e);
        }

        int fg = parseColor(fgColor);
        int bg = parseColor(bgColor);

        MatrixToImageConfig config = new MatrixToImageConfig(fg, bg);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix, config);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] bytes = baos.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Builds the WIFI QR format string.
     */
    public String buildWifiData(String ssid, String password, String security) {
        return String.format("WIFI:T:%s;S:%s;P:%s;;", security, ssid, password);
    }

    /**
     * Builds the vCard contact format string.
     */
    public String buildContactData(String firstName, String lastName, String phone, String email) {
        return String.format(
            "BEGIN:VCARD\nVERSION:3.0\nFN:%s %s\nN:%s;%s\nTEL:%s\nEMAIL:%s\nEND:VCARD",
            firstName, lastName, lastName, firstName, phone, email
        );
    }

    // Parses hex color string (#RRGGBB) to ARGB int
    private int parseColor(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
