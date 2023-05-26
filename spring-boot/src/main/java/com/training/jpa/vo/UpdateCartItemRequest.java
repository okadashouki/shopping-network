package com.training.jpa.vo;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    private Long goodsId;
    private int newQuantity;
}
