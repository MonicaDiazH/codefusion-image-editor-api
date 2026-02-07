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

        BufferedImage result =
                new BufferedImage(
                        template.getWidth(),
                        template.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g = result.createGraphics();

        g.setComposite(AlphaComposite.SrcOver);

        g.drawImage(template, 0, 0, null);

        int x = (template.getWidth() - info.getWidth()) / 2 + req.getOffsetX();
        int y = (template.getHeight() - info.getHeight()) / 2 + req.getOffsetY();

        g.drawImage(info, x, y, null);

        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(result, "PNG", out);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(out.toByteArray());
    }

    private BufferedImage download(String url) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download: " + url);
        }

        return ImageIO.read(new ByteArrayInputStream(response.body()));
    }

}
