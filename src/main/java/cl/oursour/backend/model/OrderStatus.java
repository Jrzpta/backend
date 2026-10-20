package cl.oursour.backend.model;

public enum OrderStatus {
    EN_PREPARACION,   // es el carro de compras activo
    SOLICITADO,       // checkout hecho, falta el pago
    PAGADO            // pago sandbox aprobado
}

