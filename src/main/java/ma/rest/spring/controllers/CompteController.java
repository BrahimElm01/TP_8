package ma.rest.spring.controllers;

import ma.rest.spring.entities.Compte;
import ma.rest.spring.entities.TypeCompte;
import ma.rest.spring.repositories.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/banque")
// @CrossOrigin(origins = "*") // décommente si tu testes depuis un front
public class CompteController {

    @Autowired
    private CompteRepository compteRepository;

    // ===== READ: Liste (JSON ou XML) =====
    @GetMapping(value = "/comptes", produces = {"application/json", "application/xml"})
    public List<Compte> getAllComptes() {
        return compteRepository.findAll();
        // En XML, la liste aura la racine <List> par défaut (Jackson).
        // Si tu veux <comptes><compte>...</compte></comptes>, on peut ajouter un wrapper DTO.
    }

    // ===== READ: Détail par id (JSON ou XML) =====
    @GetMapping(value = "/comptes/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<Compte> getCompteById(@PathVariable Long id) {
        return compteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== CREATE (JSON ou XML) =====
    @PostMapping(
            value = "/comptes",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"}
    )
    public ResponseEntity<Compte> createCompte(@RequestBody Compte compte) {
        // (optionnel) valeurs par défaut si non renseignées
        if (compte.getDateCreation() == null) compte.setDateCreation(new java.util.Date());
        if (compte.getType() == null) compte.setType(TypeCompte.COURANT);

        Compte saved = compteRepository.save(compte);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // ===== UPDATE (JSON ou XML) =====
    @PutMapping(
            value = "/comptes/{id}",
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"}
    )
    public ResponseEntity<Compte> updateCompte(@PathVariable Long id, @RequestBody Compte in) {
        return compteRepository.findById(id).map(c -> {
            c.setSolde(in.getSolde());
            c.setDateCreation(in.getDateCreation());
            c.setType(in.getType());
            return ResponseEntity.ok(compteRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===== DELETE =====
    @DeleteMapping("/comptes/{id}")
    public ResponseEntity<Void> deleteCompte(@PathVariable Long id) {
        return compteRepository.findById(id).map(c -> {
            compteRepository.delete(c);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
