package cl.oursour.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
 
import java.time.LocalDateTime;
import java.util.List;
 
public final class OrderDtos {
 
    private OrderDtos() {}
 
    public record CartItemRequest(
            @NotNull(message = "productId es obligatorio") Long productId,
            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad mínima es 1") Integer quantity) {}
 
    public record UpdateQuantityRequest(
            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad mínima es 1") Integer quantity) {}
 
    public record CheckoutRequest(
            @NotBlank(message = "La dirección es obligatoria") String street,
            @NotBlank(message = "La ciudad es obligatoria") String city) {}
 
    /** Datos de tarjeta SIMULADOS. Nunca se guardan en la base de datos. */
    public record PaymentRequest(
            @NotBlank @Pattern(regexp = "\\d{16}", message = "La tarjeta debe tener 16 dígitos") String cardNumber,
            @NotBlank(message = "El titular es obligatorio") String cardHolder,
            @NotBlank @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Formato MM/AA") String expiry,
            @NotBlank @Pattern(regexp = "\\d{3,4}", message = "CVV inválido") String cvv) {}
 
    public record OrderItemResponse(Long itemId, Long productId, String productName,
                                    int unitPrice, int quantity, int subtotal) {}
 
    public record OrderResponse(Long id, String status, List<OrderItemResponse> items,
                                int subtotal, int shippingCost, int total,
                                String street, String city,
                                LocalDateTime createdAt, String paymentReference) {}
}

