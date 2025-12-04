package chinanko.chinanko.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chinanko.chinanko.dto.SaleRequest;
import chinanko.chinanko.dto.SaleResponse;
import chinanko.chinanko.dto.SalesProductRequest;
import chinanko.chinanko.mapper.SaleMapper;
import chinanko.chinanko.model.Product;
import chinanko.chinanko.model.Sale;
import chinanko.chinanko.model.SalesProduct;
import chinanko.chinanko.model.ProfileUser;
import chinanko.chinanko.repository.ProductRepository;
import chinanko.chinanko.repository.SaleRepository;
import chinanko.chinanko.repository.ProfileUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository; 
    private final ProfileUserRepository userRepository; // Inyectamos el repo de usuarios

    @Override
    @Transactional
    public SaleResponse create(SaleRequest request) {
        // 1. Buscar al usuario para asignarlo a la venta
        ProfileUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        // 2. Crear la instancia de la Venta (Sale) y asignar usuario
        Sale sale = new Sale();
        sale.setUser(user); // Asignación vital para corregir el error de FK

        List<SalesProduct> salesProducts = new ArrayList<>();
        long grandTotal = 0L;

        // 3. Procesar cada producto solicitado
        for (SalesProductRequest itemRequest : request.getProducts()) {
            // A. Buscar el producto
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            // B. Validar Stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() 
                        + ". Available: " + product.getStock() + ", Requested: " + itemRequest.getQuantity());
            }

            // C. Descontar Stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product); // Guardar el nuevo stock

            // D. Calcular montos
            long unitPrice = product.getPrice();
            long subtotal = unitPrice * itemRequest.getQuantity();
            grandTotal += subtotal;

            // E. Crear la entidad intermedia SalesProduct
            SalesProduct salesProduct = SalesProduct.builder()
                    .product(product)
                    .sale(sale) // Relación bidireccional
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice) 
                    .subtotal(subtotal)
                    .build();

            salesProducts.add(salesProduct);
        }

        // 4. Asignar datos calculados a la Venta
        sale.setTotal(grandTotal);
        sale.setSalesProducts(salesProducts);

        // 5. Guardar Venta (Cascade guardará los SalesProducts automáticamente)
        Sale savedSale = saleRepository.save(sale);

        return SaleMapper.toResponse(savedSale);
    }

    @Override
    @Transactional
    public SaleResponse update(Integer id, SaleRequest request) {
        Sale existing = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found with id: " + id));
        
        // Nota: La actualización de ventas que afecte stock requiere lógica compleja de reversión.
        // Por ahora se mantiene simple para lectura.
        return SaleMapper.toResponse(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getById(Integer id) {
        Sale entity = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found with id: " + id));
        return SaleMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getByInterestPoint(Integer interestPointId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Sale> result = saleRepository.findDistinctBySalesProducts_Product_Catalog_InterestPoint_IdInterestPoint(interestPointId, pageable);
        return result.stream().map(SaleMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getByInterestPointAndProductType(Integer interestPointId, Integer typeProductId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Sale> result = saleRepository.findDistinctBySalesProducts_Product_Catalog_InterestPoint_IdInterestPointAndSalesProducts_Product_TypeOfProduct_IdTypeProduct(
                interestPointId, typeProductId, pageable);
        return result.stream().map(SaleMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getByInterestPointAndProduct(Integer interestPointId, Integer productId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Sale> result = saleRepository.findDistinctBySalesProducts_Product_Catalog_InterestPoint_IdInterestPointAndSalesProducts_Product_IdProduct(
                interestPointId, productId, pageable);
        return result.stream().map(SaleMapper::toResponse).collect(Collectors.toList());
    }
}