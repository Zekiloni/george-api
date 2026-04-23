package com.zekiloni.george.attachment.api;

import com.zekiloni.george.attachment.api.dto.AttachmentDto;
import com.zekiloni.george.attachment.application.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-path:/api}/attachment")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService service;
    private final AttachmentApiMapper mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDto> createAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "description", required = false) String description
    ) throws IOException {
        return ResponseEntity.ok(mapper.toDto(service.createAttachment(file, type, description)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentDto> getAttachmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDto(service.getAttachmentById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID id) {
        service.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> getAttachmentFile(@PathVariable UUID id) throws IOException {
        AttachmentDto attachment = mapper.toDto(service.getAttachmentById(id));
        byte[] fileBytes = service.getAttachmentFile(attachment.getHref());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                .contentLength(fileBytes.length)
                .body(fileBytes);
    }
}
