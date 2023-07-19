package org.example.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/2/13
 **/

@Data
public class CreateCacheTaskDTO {
    String taskName;
    String description="";
    String ip;
    String port;
    String pgName;
    String userName;
    String password;
    List<CacheLayersDTO> layers;
}
