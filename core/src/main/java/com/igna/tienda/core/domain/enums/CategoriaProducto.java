package com.igna.tienda.core.domain.enums;

public enum CategoriaProducto {
    ELECTRONICA("Electronica"),
    HOGAR("Hogar"),
    MODA("Moda"),
    ALIMENTOS_PERECEDEROS("Alimentos perecederos"),
    ALIMENTOS_NO_PERECEDEROS("Alimentos no perecederos");

    private final String descripcion;

    CategoriaProducto(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("descripcion no puede ser nula o vacia");
        }
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static CategoriaProducto fromDescripcion(String descripcion) {
        if (descripcion == null) {
            throw new IllegalArgumentException("descripcion no puede ser nula");
        }
        for (CategoriaProducto categoria : values()) {
            if (categoria.descripcion.equalsIgnoreCase(descripcion)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("CategoriaProducto desconocida: " + descripcion);
    }
}
