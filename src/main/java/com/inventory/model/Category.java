package com.inventory.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity public class Category {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@NotBlank @Column(nullable=false,unique=true) public String name;
public Category(){} }
