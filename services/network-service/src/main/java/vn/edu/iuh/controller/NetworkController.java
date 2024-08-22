package vn.edu.iuh.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.model.Network;
import vn.edu.iuh.request.CreateNetworkRequest;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.service.NetworkService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/networks")
@RequiredArgsConstructor
public class NetworkController {
    private final NetworkService networkService;
    @PostMapping()
    public ApiResponse<Network> createNetwork(@RequestBody @Valid CreateNetworkRequest request,@RequestHeader("Authorization") String token) {
        return ApiResponse.<Network>builder()
                .result(networkService.createNetwork(request,token))
                .build();
    }

    @PostMapping("/join")
    public ApiResponse<Network> joinNetwork(@RequestParam("networkId") Long networkId, @RequestHeader("Authorization") String token) {
        return ApiResponse.<Network>builder()
                .result(networkService.joinNetwork(networkId, token))
                .build();
    }

    @PostMapping("/generate-qr")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam("networkId") Long networkId) {
        try {
            String qrData = "http://localhost:8888/api/v1/networks/join?networkId=" + networkId;

            int width = 300;
            int height = 300;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, width, height, hintMap);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");

            return new ResponseEntity<>(pngData, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
