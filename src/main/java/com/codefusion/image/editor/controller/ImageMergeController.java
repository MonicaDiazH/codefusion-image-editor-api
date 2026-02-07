package com.codefusion.image.editor.controller;

import com.codefusion.image.editor.dto.MergeRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@RestController
@RequestMapping("/api/image")
public class ImageMergeController {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostMapping(
            value = "/merge",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> merge(@RequestBody MergeRequest req)
            throws Exception {

        BufferedImage template = download(req.getTemplateUrl());
        BufferedImage info = download(req.getInfoUrl());

        // Redimensionar template al tamaño del info
        BufferedImage resizedTemplate =
                resize(template, info.getWidth(), info.getHeight());

        BufferedImage result =
                new BufferedImage(
                        info.getWidth(),
                        info.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g = result.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g.setComposite(AlphaComposite.SrcOver);

        // Fondo: template ajustado
        g.drawImage(resizedTemplate, 0, 0, null);

        // Capa info encima (ya calza perfecto)
        g.drawImage(info, 0, 0, null);

        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(result, "PNG", out);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(out.toByteArray());
    }

    private BufferedImage resize(BufferedImage src, int w, int h) {

        BufferedImage resized =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = resized.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g.drawImage(src, 0, 0, w, h, null);

        g.dispose();

        return resized;
    }

    private BufferedImage download(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(20))
                .build();

        HttpResponse<byte[]> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download: " + url);
        }

        return ImageIO.read(new ByteArrayInputStream(response.body()));
    }
}
