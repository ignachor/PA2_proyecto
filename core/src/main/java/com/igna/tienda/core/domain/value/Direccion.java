package com.igna.tienda.core.domain.value;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Direccion {

    private String calle;
    private String numero;
    private String ciudad;
    private String provincia;
    private String codigoPostal;

    protected Direccion() {
        // requerido por JPA
    }

    public Direccion(String calle, String numero, String ciudad, String provincia, String codigoPostal) {
        this.calle = calle;
        this.numero = numero;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.codigoPostal = codigoPostal;
    }

    public String getCalle() { return calle; }
    public String getNumero() { return numero; }
    public String getCiudad() { return ciudad; }
    public String getProvincia() { return provincia; }
    public String getCodigoPostal() { return codigoPostal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Direccion)) return false;
        Direccion that = (Direccion) o;
        return Objects.equals(calle, that.calle)
            && Objects.equals(numero, that.numero)
            && Objects.equals(ciudad, that.ciudad)
            && Objects.equals(provincia, that.provincia)
            && Objects.equals(codigoPostal, that.codigoPostal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, numero, ciudad, provincia, codigoPostal);
    }
}
