package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        private Iterador() {
            // Aquí va su código.
            cola = new Cola<Vertice>();
            if (raiz != null)
                cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            // Aquí va su código.
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            // Aquí va su código.
            if (cola.esVacia())
                throw new java.util.NoSuchElementException();

            Vertice v = cola.saca();
            if (v.izquierdo != null)
                cola.mete(v.izquierdo);
            if (v.derecho != null)
                cola.mete(v.derecho);

            return v.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Convierte un número entero a su representación en binario.
     * 
     * @param num el número a convertir.
     * @return la representación en binario de <code>num</code>.
     */
    private String decimalToBinario(int num) {
        String str = "";
        while (num > 0) {
            if ((num & 1) == 1)
                str += '1';
            else
                str += '0';

            num >>= 1; // es como dividir entre 2 pero mas cabron
        }
        return str;
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        // Aquí va su código.
        if (elemento == null)
            throw new IllegalArgumentException();

        Vertice nuevoVertice = nuevoVertice(elemento);
        elementos++;

        if (raiz == null) {
            raiz = nuevoVertice;
            return;
        }

        // Implementacion O(log n)
        Vertice nodoActual = raiz;
        String rutaBinaria = decimalToBinario(elementos);
        int i = rutaBinaria.length() - 2;
        while (i > 0) {
            if (rutaBinaria.charAt(i) == '0') {
                nodoActual = nodoActual.izquierdo;
            } else {
                nodoActual = nodoActual.derecho;
            }
            i--;
        }

        if (rutaBinaria.charAt(0) == '0') {
            nodoActual.izquierdo = nuevoVertice;
            nuevoVertice.padre = nodoActual;

        } else {
            nodoActual.derecho = nuevoVertice;
            nuevoVertice.padre = nodoActual;
        }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        // Aquí va su código.
        Vertice v = vertice(busca(elemento));

        if (v == null)
            return;

        elementos--;

        if (elementos == 0) {
            raiz = null;
            return;
        }

        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(raiz);

        Vertice ultimo = null;
        while (!cola.esVacia()) {
            ultimo = cola.saca();
            if (ultimo.izquierdo != null)
                cola.mete(ultimo.izquierdo);
            if (ultimo.derecho != null)
                cola.mete(ultimo.derecho);
        }

        T tmp = v.elemento;
        v.elemento = ultimo.elemento;
        ultimo.elemento = tmp;

        if (ultimo.padre.izquierdo == ultimo)
            ultimo.padre.izquierdo = null;
        else
            ultimo.padre.derecho = null;
    }

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
        // Aquí va su código.
        if (raiz == null)
            return -1;

        return (int) Math.floor(Math.log(elementos) / Math.log(2));
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        // Aquí va su código.
        if (raiz == null)
            return;

        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(raiz);

        while (!cola.esVacia()) {
            Vertice v = cola.saca();
            accion.actua(v);
            if (v.izquierdo != null)
                cola.mete(v.izquierdo);
            if (v.derecho != null)
                cola.mete(v.derecho);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}

