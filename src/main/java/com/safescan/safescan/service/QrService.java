package com.safescan.safescan.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

@Service
public class QrService {

    public String decodeQr(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No QR image was uploaded.");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new IllegalArgumentException(
                    "The uploaded file is not a valid image."
            );
        }

        BufferedImageLuminanceSource source =
                new BufferedImageLuminanceSource(image);

        BinaryBitmap bitmap =
                new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints =
                new EnumMap<>(DecodeHintType.class);

        hints.put(
                DecodeHintType.TRY_HARDER,
                Boolean.TRUE
        );

        Result result =
                new MultiFormatReader().decode(bitmap, hints);

        return result.getText();
    }
}