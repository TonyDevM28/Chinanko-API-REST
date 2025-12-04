package chinanko.chinanko.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Simplificado

import chinanko.chinanko.dto.ProfileUserRequest;
import chinanko.chinanko.dto.ProfileUserResponse;
import chinanko.chinanko.service.ProfileUserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor // Lombok genera el constructor
public class ProfileUserController {

    private final ProfileUserService service;

    @GetMapping
    public ResponseEntity<List<ProfileUserResponse>> list(){
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileUserResponse> getById(@PathVariable("id") Integer idProfileUser) {
        ProfileUserResponse response = service.getById(idProfileUser);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProfileUserResponse> create(@RequestBody ProfileUserRequest p) {
        // Podrías validar aquí el RequestBody con @Valid
        ProfileUserResponse created = service.create(p);
        return new ResponseEntity<>(created, HttpStatus.CREATED); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileUserResponse> update(
            @PathVariable("id") Integer idProfileUser, 
            @RequestBody ProfileUserRequest p) {
        
        ProfileUserResponse updated = service.update(idProfileUser, p);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}