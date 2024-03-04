package com.giovaniwahl.dscommerce.domain.dtos;

import com.giovaniwahl.dscommerce.domain.entities.User;

public class ClientDTO {
    private Long id;
    private String name;

    public ClientDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public ClientDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
