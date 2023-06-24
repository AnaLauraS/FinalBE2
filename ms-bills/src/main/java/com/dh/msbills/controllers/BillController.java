package com.dh.msbills.controllers;

import com.dh.msbills.models.Bill;
import com.dh.msbills.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService service;

    @GetMapping("/all")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(service.getAllBill());
    }

    @PostMapping("/create")
    public ResponseEntity<Bill> save(@RequestBody Bill bill){
        return ResponseEntity.ok().body(service.save(bill));
    }

    @GetMapping("/findBy")
    public ResponseEntity<List<Bill>> getAll(@RequestParam String customerBill) {
        return ResponseEntity.ok().body(service.findByCustomerId(customerBill));
    }
}
