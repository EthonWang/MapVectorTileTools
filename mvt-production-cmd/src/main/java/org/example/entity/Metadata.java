package org.example.entity;


import lombok.Data;

/**
 * @Description
 * @Author wyjq
 * @Date 2023/2/13
 */

@Data
public class Metadata {

    String name;
    String format="pbf";
    String description="test";
    String attribution="test";
    String version="3.11";
    String type="overlay";
    String bounds;
    String center;
    String minzoom;
    String maxzoom;
    String json;

}
