package com.inventory.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Entity public class Product {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@NotBlank @Column(nullable=false) public String name;
@NotBlank @Column(nullable=false,unique=true) public String sku;
@NotNull @DecimalMin("0.0") @Column(nullable=false) public BigDecimal price=BigDecimal.ZERO;
@NotNull @Min(0) @Column(nullable=false) public Integer quantity=0;
@NotNull @Min(0) @Column(nullable=false) public Integer minStock=5;
@ManyToOne @JoinColumn(name="category_id") public Category category;
public Product(){} }
