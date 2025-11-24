package com.example.restaurant.domain.table;                

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;                             
import lombok.*;                                            

@Entity                                                    
@Table(name = "restaurant_tables")                          
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RestaurantTable {                             

    @Id                                                    
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    private Long id;                                        

    @Column(nullable = false, unique = true, length = 20)   
    private String code;                                    

    @Column(nullable = false)                             
    private Integer capacity;                              

    @Builder.Default   
    @Enumerated(EnumType.STRING)                            
    @Column(nullable = false)                            
    private TableStatus status = TableStatus.FREE;         

    @Version                                               
    private Long version;                                  
}