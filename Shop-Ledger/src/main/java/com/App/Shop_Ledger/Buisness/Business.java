package com.App.Shop_Ledger.Buisness;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Businesses")
public class Business {
    @Id
    private String id;
    private String name;
    private String type;
    
}
