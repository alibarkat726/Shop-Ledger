package com.App.Shop_Ledger.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "Products")
public class Products {
    @Id
    private String id;

    @TextIndexed
    private String prdName;
    @TextIndexed
    @DBRef
    private Category category;
    private Long price;

    private String userId;

}
