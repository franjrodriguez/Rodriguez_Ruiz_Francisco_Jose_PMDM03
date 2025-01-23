package com.rodriguezruiz.pokedex.listener;

/**
 * Interfaz que define los callbacks para operaciones genéricas.
 * Esta interfaz se utiliza para manejar el resultado de operaciones asíncronas
 * que pueden tener éxito o fallar.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public interface OperationCallBack {
    /**
     * Llamado cuando la operación se completa exitosamente.
     */
    void onSuccess();

    /**
     * Llamado cuando la operación falla.
     *
     * @param e Excepción que contiene los detalles del error
     */
    void onFailure(Exception e);
}
