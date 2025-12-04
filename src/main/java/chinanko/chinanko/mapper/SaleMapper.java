package chinanko.chinanko.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import chinanko.chinanko.dto.SaleResponse;
import chinanko.chinanko.dto.SalesProductResponse;
import chinanko.chinanko.model.Sale;

@Component
public class SaleMapper {

    public static SaleResponse toResponse(Sale e) {
        if (e == null) return null;

        List<SalesProductResponse> productResponses = e.getSalesProducts() != null
                ? e.getSalesProducts().stream().map(sp -> SalesProductResponse.builder()
                    .productName(sp.getProduct() != null ? sp.getProduct().getName() : "Unknown")
                    .quantity(sp.getQuantity())
                    .unitPrice(sp.getUnitPrice())
                    .subtotal(sp.getSubtotal())
                    .build()
                ).collect(Collectors.toList())
                : new ArrayList<>();

        return SaleResponse.builder()
                .idSale(e.getIdSale())
                .total(e.getTotal())
                .userName(e.getUser() != null ? e.getUser().getFirstName() + " " + e.getUser().getLastName(): null)
                .products(productResponses)
                .build();
    }
}