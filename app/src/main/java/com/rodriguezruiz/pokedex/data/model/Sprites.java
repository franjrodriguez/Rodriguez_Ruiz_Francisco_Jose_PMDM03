package com.rodriguezruiz.pokedex.data.model;

/**
 * Clase que representa las imágenes asociadas a un Pokémon.
 * Esta clase se utiliza para manejar las URLs de las diferentes vistas del Pokémon.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public class Sprites {

    /**
     * URL de la imagen de frente por defecto del Pokémon.
     */
    public String front_default;

    /**
     * Getters y Setters para el atributo de la clase
     * @return
     */
    public String getFront_default() {
        return front_default;
    }

    public void setFront_default(String front_default) {
        this.front_default = front_default;
    }
}
