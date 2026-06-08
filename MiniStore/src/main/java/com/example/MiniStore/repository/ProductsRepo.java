package com.example.MiniStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.MiniStore.model.Product;

@Repository
public interface ProductsRepo extends JpaRepository<Product, Long> {
 
}
