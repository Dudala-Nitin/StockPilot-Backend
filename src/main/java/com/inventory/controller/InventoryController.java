package com.inventory.controller;
import com.inventory.model.*;
import com.inventory.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api") public class InventoryController {
private final ProductRepository products;private final CategoryRepository categories;private final StockMovementRepository movements;
public InventoryController(ProductRepository p,CategoryRepository c,StockMovementRepository m){products=p;categories=c;movements=m;}
@GetMapping("/categories") public List<Category> categories(){return categories.findAll();}
@PostMapping("/categories") public Category addCategory(@Valid @RequestBody Category c){c.id=null;return categories.save(c);}
@DeleteMapping("/categories/{id}") public void deleteCategory(@PathVariable Long id){Category c=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(products.findAll().stream().anyMatch(p->p.category!=null&&id.equals(p.category.id)))throw new ResponseStatusException(HttpStatus.CONFLICT,"Category has products");categories.delete(c);}
@GetMapping("/products") public List<Product> products(){return products.findAll();}
@PostMapping("/products") public Product addProduct(@Valid @RequestBody Product p){p.id=null;resolveCategory(p);return products.save(p);}
@PutMapping("/products/{id}") public Product editProduct(@PathVariable Long id,@Valid @RequestBody Product p){if(!products.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);p.id=id;resolveCategory(p);return products.save(p);}
@DeleteMapping("/products/{id}") @Transactional public void deleteProduct(@PathVariable Long id){Product p=products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));movements.deleteAll(movements.findAll().stream().filter(m->m.product.id.equals(id)).toList());products.delete(p);}
private void resolveCategory(Product p){if(p.category!=null&&p.category.id!=null)p.category=categories.findById(p.category.id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid category"));else p.category=null;}
public record MovementRequest(Long productId,String type,Integer amount){}
@PostMapping("/movements") @Transactional public StockMovement move(@RequestBody MovementRequest r){if(r.productId()==null||r.amount()==null||r.amount()<=0||!("IN".equals(r.type())||"OUT".equals(r.type())))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid movement");Product p=products.findById(r.productId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));int next=p.quantity+("IN".equals(r.type())?r.amount():-r.amount());if(next<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient stock");p.quantity=next;products.save(p);return movements.save(new StockMovement(p,r.type(),r.amount()));}
@GetMapping("/movements") public List<StockMovement> movements(){return movements.findAll();}
@GetMapping("/dashboard") public Map<String,Object> dashboard(){List<Product> ps=products.findAll();return Map.of("products",ps.size(),"categories",categories.count(),"units",ps.stream().mapToInt(p->p.quantity).sum(),"lowStock",ps.stream().filter(p->p.quantity<=p.minStock).count());}
}
