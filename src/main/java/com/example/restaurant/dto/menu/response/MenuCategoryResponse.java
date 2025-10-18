// dto/menu/MenuCategoryResponse.java
package com.example.restaurant.dto.menu.response;
public class MenuCategoryResponse {
    private Long id; private String name;
    public MenuCategoryResponse() {}
    public MenuCategoryResponse(Long id,String name){this.id=id;this.name=name;}
    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public String getName(){return name;} public void setName(String v){this.name=v;}
}
