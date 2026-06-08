package com.example.MiniStore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.MiniStore.model.Product;
import com.example.MiniStore.model.ProductDTO;
import com.example.MiniStore.repository.ProductsRepo;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductsRepo repo;

    @GetMapping({ "", "/" })
    public String productList(Model model) {
        List<Product> products = repo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/createProduct")
    public String ShowCreatePage(Model model) {
        ProductDTO productDTO = new ProductDTO();
        model.addAttribute("productDTO", productDTO);
        return "products/createProduct";
    }

    @PostMapping("/createProduct")
    public String addProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result) {
        if (productDTO.getImage() == null || productDTO.getImage().isEmpty()) {
            result.addError(new FieldError("productDTO", "image", "Image is required"));
        }
        if (result.hasErrors()) {
            return "products/createProduct";
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImage(productDTO.getImage());
        product.setStock(productDTO.getStock());
        repo.save(product);
        return "redirect:/products";
    }

    @GetMapping("/editProduct")
    public String editProduct(
        Model model,
        @RequestParam long id
    ) {
        try {
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDTO productDto = new ProductDTO();
            productDto.setName(product.getName());
            productDto.setImage(product.getImage());
            productDto.setDescription(product.getDescription());
            productDto.setPrice(product.getPrice());
            productDto.setStock(product.getStock());

            model.addAttribute("productDTO", productDto);
            
        } catch (Exception e) {
            System.out.println("Expraction: " + e.getMessage());
            return "redirect:/products";
        }
            return "products/editProduct";
    }

    @PostMapping("/editProduct/{id}")
    public String editProduct(
        Model model,
        @RequestParam(value = "id", required = false) Long requestParamId,
        @PathVariable(value = "id", required = false) Long pathVariableId,
        @Valid @ModelAttribute ProductDTO productDTO,
        BindingResult result
    ) {
        long id = (pathVariableId != null) ? pathVariableId : requestParamId;
        if (id == 0) {
            return "redirect:/products";
        }
        if (result.hasErrors()) {
            // re-render the edit page with validation errors
            return "products/editProduct";
        }
        try {
            Product product = repo.findById(id).orElseThrow();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setImage(productDTO.getImage());
            product.setStock(productDTO.getStock());

            repo.save(product);
            return "redirect:/products";
        } catch (Exception e) {
            System.out.println("Expection: " + e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(
        @RequestParam Long id
    ){

        try {
            Product product = repo.findById(id).orElseThrow();
            repo.delete(product);
        } catch (Exception e) {
            System.out.println("Expection:" + e.getMessage());
        }
    
        return "redirect:/products";
    }
}