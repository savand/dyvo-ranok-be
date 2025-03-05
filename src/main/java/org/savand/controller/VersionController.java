package org.savand.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
@CrossOrigin("#{@corsConfig.allowedOrigin}")
public class VersionController {

    private final BuildProperties buildProperties;

    @GetMapping("version")
    public ResponseEntity<Map<String, String>> getAppVersion() {

        return ResponseEntity.ok()
                .body(Map.of("version", buildProperties.getVersion()));
    }
}
