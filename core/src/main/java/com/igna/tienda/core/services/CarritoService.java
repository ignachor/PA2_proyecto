package com.igna.tienda.core.services;

import com.igna.tienda.core.domain.Carrito;
import com.igna.tienda.core.domain.DetalleCarrito;
import com.igna.tienda.core.domain.DetallePedido;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.CarritoRepository;
import com.igna.tienda.core.repositories.PedidoRepository;
import com.igna.tienda.core.repositories.ProductoRepository;
import com.igna.tienda.core.repositories.UsuarioRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CarritoService {
    private final CarritoRepository carritoRepo;
    private final PedidoRepository pedidoRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;

    public CarritoService(CarritoRepository carritoRepo, PedidoRepository pedidoRepo, ProductoRepository productoRepo, UsuarioRepository usuarioRepo) {
        this.carritoRepo = carritoRepo;
        this.pedidoRepo = pedidoRepo;
        this.productoRepo = productoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // CU: ver carrito del usuario (si no existe, se crea).
    public Carrito ObtenerCarrito(UUID clienteId) {
        return obtenerOCrearCarrito(clienteId);
    }

    // CU: anadir producto al carrito.
    public Carrito AgregarProducto(UUID clienteId, Long productoId, int cantidad) {
        // Paso 1: validar datos de entrada.
        if (productoId == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }

        // Paso 2: cargar producto y validar disponibilidad.
        Producto producto = productoRepo.buscarPorID(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        if (!producto.getStock()) {
            throw new IllegalArgumentException("El producto esta dado de baja o sin stock");
        }
        if (cantidad > producto.getCantidad()) {
            throw new IllegalArgumentException("Cantidad solicitada supera el stock disponible");
        }

        // Paso 3: obtener carrito del cliente y agregar item.
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        carrito.anadirProducto(new DetalleCarrito(producto, cantidad));

        // Paso 4: persistir carrito actualizado.
        return carritoRepo.guardar(carrito);
    }

    // CU: modificar cantidad de un producto dentro del carrito.
    public Carrito ModificarCantidadProducto(UUID clienteId, Long productoId, int nuevaCantidad) {
        // Paso 1: validar entrada.
        if (productoId == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        if (nuevaCantidad == 0) {
            // Paso 1.1: si llega 0, se interpreta como eliminar item del carrito.
            return EliminarProducto(clienteId, productoId);
        }

        // Paso 2: cargar carrito y ubicar item correspondiente.
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        DetalleCarrito item = buscarItem(carrito.getItems(), productoId);
        if (item == null) {
            throw new IllegalArgumentException("Producto no existe en el carrito");
        }

        // Paso 3: validar stock disponible antes de modificar cantidad.
        Producto productoActual = productoRepo.buscarPorID(productoId);
        if (productoActual == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        if (!productoActual.getStock()) {
            throw new IllegalArgumentException("El producto esta inactivo");
        }
        if (nuevaCantidad > productoActual.getCantidad()) {
            throw new IllegalArgumentException("Cantidad solicitada supera el stock disponible");
        }

        // Paso 4: actualizar cantidad y persistir carrito.
        item.setCantidad(nuevaCantidad);
        return carritoRepo.guardar(carrito);
    }

    // CU: eliminar producto del carrito.
    public Carrito EliminarProducto(UUID clienteId, Long productoId) {
        // Paso 1: validar entrada.
        if (productoId == null) {
            throw new IllegalArgumentException("Producto no proporcionado");
        }

        // Paso 2: cargar carrito y buscar item.
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        DetalleCarrito item = buscarItem(carrito.getItems(), productoId);
        if (item == null) {
            throw new IllegalArgumentException("Producto no existe en el carrito");
        }

        // Paso 3: remover item y persistir.
        carrito.removerProducto(item);
        return carritoRepo.guardar(carrito);
    }

    // CU: finalizar compra y generar pedido.
    public Pedido FinalizarCompra(UUID clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente no proporcionado");
        }

        // Paso 1: obtener carrito y validar que tenga items.
        Carrito carrito = carritoRepo.buscarPorClienteId(clienteId);
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito no encontrado");
        }
        if (carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio");
        }

        // Paso 2: crear pedido para el cliente.
        Pedido pedido = new Pedido(carrito.getUsuarioCliente());

        // Paso 3: convertir cada item de carrito en detalle de pedido y descontar stock real.
        for (DetalleCarrito itemCarrito : carrito.getItems()) {
            Producto producto = productoRepo.buscarPorID(itemCarrito.getProducto().getId());
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado en catalogo");
            }
            if (!producto.getStock()) {
                throw new IllegalArgumentException("Producto sin stock: " + producto.getNombre());
            }
            if (itemCarrito.getCantidad() > producto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
            }

            // Paso 3.1: snapshot de precio al momento de la compra.
            BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecio());
            pedido.addItem(new DetallePedido(producto, itemCarrito.getCantidad(), precioUnitario));

            // Paso 3.2: descontar unidades del producto y guardar cambio.
            producto.descontarCantidad(itemCarrito.getCantidad());
            productoRepo.guardar(producto);
        }

        // Paso 4: persistir pedido.
        Pedido pedidoGuardado = pedidoRepo.guardar(pedido);

        // Paso 5: vaciar carrito luego de confirmar compra.
        carrito.vaciar();
        carritoRepo.guardar(carrito);

        return pedidoGuardado;
    }

    private Carrito obtenerOCrearCarrito(UUID clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente no proporcionado");
        }

        Carrito carrito = carritoRepo.buscarPorClienteId(clienteId);
        if (carrito != null) {
            return carrito;
        }

        // Paso interno: crear carrito nuevo si aun no existe para el cliente.
        Usuario cliente = usuarioRepo.buscarPorId(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }

        Carrito nuevoCarrito = new Carrito(cliente);
        Carrito carritoGuardado = carritoRepo.guardar(nuevoCarrito);

        // Paso interno: sincroniza la referencia inversa con la instancia ya gestionada por JPA.
        cliente.crearCarrito(carritoGuardado);
        return carritoGuardado;
    }

    private DetalleCarrito buscarItem(List<DetalleCarrito> items, Long productoId) {
        for (DetalleCarrito item : items) {
            if (item.getProducto() != null && productoId.equals(item.getProducto().getId())) {
                return item;
            }
        }
        return null;
    }
}
