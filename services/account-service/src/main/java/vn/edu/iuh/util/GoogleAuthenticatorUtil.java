package vn.edu.iuh.util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GoogleAuthenticatorUtil {
    public static BufferedImage generateQRCodeImage(String barcodeText, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 1);  // Optional: Adjust margin if needed
        BitMatrix matrix = new MultiFormatWriter().encode(barcodeText, BarcodeFormat.QR_CODE, width, height, hintMap);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (matrix.get(i, j)) {
                    image.setRGB(i, j, 0x000000);
                } else {
                    image.setRGB(i, j, 0xFFFFFF);
                }
            }
        }

        return image;
    }
}