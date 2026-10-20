package com.inventory.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity public class StockMovement {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
@ManyToOne(optional=false) public Product product;
@Column(nullable=false) public String type;
@Column(nullable=false) public Integer amount;
public LocalDateTime createdAt=LocalDateTime.now();
public StockMovement(){} public StockMovement(Product p,String t,int a){product=p;type=t;amount=a;}
}
