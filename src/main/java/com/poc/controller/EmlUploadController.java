package com.poc.controller;

import com.poc.vo.OutlookAttachmentVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/upload")
public class EmlUploadController {

    private static final String UPLOAD_DIR = "src/main/resources/";

    @PostMapping("/eml")
    public String uploadEml(@RequestBody OutlookAttachmentVO vo) {
        try {
            // Base64 decode
            byte[] emlBytes = Base64.getDecoder().decode(vo.getEmlContent());

            // get Email title
            String content = new String(emlBytes);
            Pattern pattern = Pattern.compile("^Subject: (.*)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(content);
            String title = "email";
            if (matcher.find()) {
                title = matcher.group(1).trim();
            }
            String emailSubject = title.replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", "_");
            // get current timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String filename = emailSubject + "_" + timestamp + ".eml";

            // store to resources directory
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.write(path, emlBytes);
            return "EML file saved successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to save EML file.";
        }
    }

}
