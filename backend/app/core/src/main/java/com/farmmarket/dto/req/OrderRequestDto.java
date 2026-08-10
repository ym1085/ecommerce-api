package com.farmmarket.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class OrderRequestDto {

    @Getter
    @NoArgsConstructor
    public static class Create {
        @NotNull
        @Size(min = 1)
        @Valid
        private List<Item> items;
    }

    @Getter
    @NoArgsConstructor
    public static class Item {
        @NotNull
        private Long productId;

        @NotNull
        @Min(1)
        private Integer quantity;
    }
}