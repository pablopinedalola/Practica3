package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>
 * Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.
 * </p>
 *
 * <p>
 * Un árbol instancia de esta clase siempre cumple que:
 * </p>
 * <ul>
 * <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 * descendientes por la izquierda.</li>
 * <li>Cualquier elemento en el árbol es menor o igual que todos sus
 * descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
        extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        private Iterador() {
            // Aquí va su código.
            pila = new Pila<Vertice>();

            Vertice v = raiz;
            while (v != null) {
                pila.mete(v);
                v = v.izquierdo;
            }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override
        public boolean hasNext() {
            // Aquí va su código.
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override
        public T next() {
            // Aquí va su código.
            Vertice v = pila.saca();

            if (v.derecho != null) {
                Vertice aux = v.derecho;
                while (aux != null) {
                    pila.mete(aux);
                    aux = aux.izquierdo;
                }
            }

            return v.elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() {
        super();
    }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * 
     * @param coleccion la colección a partir de la cual creamos el árbol
     *                  binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * 
     * @param v        el vértice a partir del cual se buscará dónde agregar el
     *                 nuevo
     * @param elemento el elemento a agregar.
     */
    private void agrega(Vertice v, T elemento) {
        if (elemento.compareTo(v.elemento) <= 0) {
            if (v.izquierdo == null) {
                v.izquierdo = nuevoVertice(elemento);
                v.izquierdo.padre = v;
                ultimoAgregado = v.izquierdo;
            } else {
                agrega(v.izquierdo, elemento);
            }
        } else {
            if (v.derecho == null) {
                v.derecho = nuevoVertice(elemento);
                v.derecho.padre = v;
                ultimoAgregado = v.derecho;
            } else {
                agrega(v.derecho, elemento);
            }
        }
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * 
     * @param elemento el elemento a agregar.
     */
    @Override
    public void agrega(T elemento) {
        // Aquí va su código.
        if (elemento == null)
            throw new IllegalArgumentException();

        Vertice nuevo = nuevoVertice(elemento);
        elementos++;

        if (raiz == null) {
            raiz = nuevo;
            ultimoAgregado = raiz;
            return;
        }

        agrega(raiz, elemento);
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * 
     * @param elemento el elemento a eliminar.
     */
    @Override
    public void elimina(T elemento) {
        // Aquí va su código.
        Vertice v = vertice(busca(elemento));

        if (v == null)
            return;

        elementos--;
        if (elementos == 0) {
            raiz = null;
            return;
        }

        // Cuando es una hoja la despegamos de su padre
        if (!v.hayIzquierdo() && !v.hayDerecho()) {
            if (v.padre.izquierdo != null && v.padre.izquierdo.equals(v)) {
                v.padre.izquierdo = null;
            } else {
                v.padre.derecho = null;
            }
            return;
        }

        // Cuando tiene un hijo
        if ((v.hayIzquierdo() && !v.hayDerecho()) || (!v.hayIzquierdo() && v.hayDerecho())) {
            eliminaVertice(v);
            return;
        }

        // Cuando tiene dos hijos
        Vertice aux = intercambiaEliminable(v);
        eliminaVertice(aux);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * 
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        // Aquí va su código.
        Vertice nodoTemporal = vertice.izquierdo;

        while (nodoTemporal.hayDerecho())
            nodoTemporal = nodoTemporal.derecho;

        T elementoTemporal = vertice.elemento;
        vertice.elemento = nodoTemporal.elemento;
        nodoTemporal.elemento = elementoTemporal;

        return nodoTemporal;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * 
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        // Aquí va su código.
        Vertice hijo = vertice.izquierdo != null ? vertice.izquierdo : vertice.derecho;

        if (vertice.padre == null) {
            raiz = hijo;
            if (hijo != null)
                hijo.padre = null;
            return;
        }

        if (vertice.padre.izquierdo != null && vertice.padre.izquierdo.equals(vertice)) {
            vertice.padre.izquierdo = hijo;
        } else {
            vertice.padre.derecho = hijo;
        }

        if (hijo != null)
            hijo.padre = vertice.padre;
    }

    /**
     * Busca un elemento en el árbol.
     * 
     * @param v        el vértice a partir del cual se buscará el elemento.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    private VerticeArbolBinario<T> busca(Vertice v, T elemento) {
        if (v == null)
            return null;
        if (v.elemento.equals(elemento))
            return v;
        if (elemento.compareTo(v.elemento) < 0)
            return busca(v.izquierdo, elemento);
        return busca(v.derecho, elemento);
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * 
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override
    public VerticeArbolBinario<T> busca(T elemento) {
        // Aquí va su código.
        return busca(raiz, elemento);
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * 
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * 
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        // Aquí va su código.
        Vertice verticeActual = vertice(vertice);

        if (verticeActual.izquierdo == null)
            return;

        Vertice hijo = verticeActual.izquierdo;
        hijo.padre = verticeActual.padre;

        if (verticeActual.padre == null)
            raiz = hijo;
        else {
            if (verticeActual.padre.izquierdo == verticeActual)
                verticeActual.padre.izquierdo = hijo;
            else
                verticeActual.padre.derecho = hijo;
        }

        verticeActual.izquierdo = hijo.derecho;

        if (verticeActual.izquierdo != null)
            verticeActual.izquierdo.padre = verticeActual;

        hijo.derecho = verticeActual;
        verticeActual.padre = hijo;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * 
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        // Aquí va su código.
        if (!vertice.hayDerecho())
            return;

        Vertice v = vertice(vertice);
        Vertice der = v.derecho;
        Vertice padre = v.padre;

        if (padre != null) {
            if (padre.hayIzquierdo()) {
                if (padre.izquierdo.equals(v)) {
                    padre.izquierdo = der;

                } else {
                    padre.derecho = der;
                }
            } else {
                padre.derecho = der;
            }
        } else {
            raiz = der;
        }

        der.padre = padre;
        if (der.izquierdo != null) {
            der.izquierdo.padre = v;
        }

        v.derecho = der.izquierdo;
        v.padre = der;
        der.izquierdo = v;
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        // Aquí va su código.
        if (raiz == null)
            return;

        Pila<Vertice> pila = new Pila<Vertice>();
        pila.mete(raiz);

        while (!pila.esVacia()) {
            Vertice v = pila.saca();
            accion.actua(v);
            if (v.hayDerecho())
                pila.mete(v.derecho);
            if (v.hayIzquierdo())
                pila.mete(v.izquierdo);
        }
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        // Aquí va su código.
        if (raiz == null)
            return;

        Pila<Vertice> pila = new Pila<Vertice>();
        Vertice v = raiz;

        while (v != null) {
            pila.mete(v);
            v = v.izquierdo;
        }

        while (!pila.esVacia()) {
            v = pila.saca();
            accion.actua(v);
            if (v.hayDerecho()) {
                v = v.derecho;
                while (v != null) {
                    pila.mete(v);
                    v = v.izquierdo;
                }
            }
        }
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * 
     * @param v      el vértice a partir del cual se hará el recorrido.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    private void dfsPostOrder(Vertice v, AccionVerticeArbolBinario<T> accion) {
        if (v == null)
            return;

        dfsPostOrder(v.izquierdo, accion);
        dfsPostOrder(v.derecho, accion);
        accion.actua(v);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        // Aquí va su código.
        dfsPostOrder(raiz, accion);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * 
     * @return un iterador para iterar el árbol.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterador();
    }
}
