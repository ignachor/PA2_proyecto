package com.igna.tienda.core.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = false)
public class CategoriaProductoConverter implements AttributeConverter<CategoriaProducto, String> {

    @Override
    public String convertToDatabaseColumn(CategoriaProducto attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public CategoriaProducto convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        String normalizado = dbData.trim();
        String upper = normalizado.toUpperCase(Locale.ROOT);

        try {
            return CategoriaProducto.valueOf(upper);
        } catch (IllegalArgumentException ignored) {
            // Continua con fallback para datos legacy.
        }

        for (CategoriaProducto categoria : CategoriaProducto.values()) {
            if (categoria.getDescripcion().equalsIgnoreCase(normalizado)) {
                return categoria;
            }
        }

        if (normalizado.matches("\\d+")) {
            int ordinal = Integer.parseInt(normalizado);
            CategoriaProducto[] values = CategoriaProducto.values();
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
        }

        return switch (upper) {
            case "LACTEOS", "BEBIDAS", "CONGELADOS" -> CategoriaProducto.ALIMENTOS_PERECEDEROS;
            case "ALMACEN" -> CategoriaProducto.ALIMENTOS_NO_PERECEDEROS;
            case "LIMPIEZA", "HIGIENE", "HOGAR" -> CategoriaProducto.HOGAR;
            case "OTROS" -> CategoriaProducto.MODA;
            case "ELECTRONICA", "ELECTRO", "TECNOLOGIA" -> CategoriaProducto.ELECTRONICA;
            default -> throw new IllegalArgumentException("CategoriaProducto no mapeable: " + dbData);
        };
    }
}
