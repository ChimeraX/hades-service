package org.chimerax.hades.controller;

import lombok.AllArgsConstructor;
import org.chimerax.hades.api.dto.document.CreateDocumentDTO;
import org.chimerax.hades.api.dto.document.DataDocumentDTO;
import org.chimerax.hades.api.dto.document.NoDataDocumentDTO;
import org.chimerax.hades.service.DocumentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.net.URI;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 13-May-20
 * Time: 4:46 PM
 */

@RestController
@RequestMapping("/documents")
@AllArgsConstructor
public class DocumentController {

    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody final CreateDocumentDTO document) {
        final long id = documentService.save(document);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id:[0-9]+}/details")
    public ResponseEntity<NoDataDocumentDTO> findDocumentById(@PathVariable final long id) {
        return ResponseEntity.of(documentService.findDocumentById(id));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<byte[]> findDocumentWithDataById(@PathVariable final long id) {
        DataDocumentDTO document = documentService.findDocumentWithDataById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getType()))
                .body(document.getData());
    }

    @GetMapping(value = "/{id:[0-9]+}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> findDocumentDataById(@PathVariable final long id) {
        DataDocumentDTO document = documentService.findDocumentWithDataById(id);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(document.getData()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + document.getName())
                .contentLength(document.getSize())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable(name = "id")  Long aLong) {
        documentService.deleteById(aLong);
    }

}
