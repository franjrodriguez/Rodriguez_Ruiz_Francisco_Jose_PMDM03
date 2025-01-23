package com.rodriguezruiz.pokedex.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa los datos detallados de un Pokémon.
 * Implementa Serializable y Parcelable para permitir la transferencia de datos
 * entre componentes de Android y la serialización para almacenamiento en Firebase Firestore.
 * Es Parcelable para pasar una instancia desde MainActivity a un Fragment.
 *
 * @author Fco Jose Rodriguez Ruiz
 * @version 1.0.0
 * @since 1.0.0
 */
public class PokemonData implements Serializable, Parcelable {
    /**
     * Identificador único del Pokémon.
     */
    @PropertyName("id")
    private String id;

    /**
     * Nombre del Pokémon.
     */
    @PropertyName("name")
    private String name;

    /**
     * Altura del Pokémon.
     */
    @PropertyName("height")
    private Double height;

    /**
     * Peso del Pokémon.
     */
    @PropertyName("weight")
    private Double weight;

    /**
     * URL de la imagen del Pokémon.
     */
    @PropertyName("urlImage")
    private String urlImage;

    /**
     * Lista de tipos del Pokémon.
     */
    @PropertyName("types")
    private List<String> types;

    /**
     * Constructor por defecto requerido para Firebase.
     */
    public PokemonData() {}

    /**
     * Constructor que inicializa todos los campos de la clase.
     *
     * @param id Identificador único del Pokémon
     * @param name Nombre del Pokémon
     * @param height Altura del Pokémon
     * @param weight Peso del Pokémon
     * @param urlImage URL de la imagen del Pokémon
     * @param types Lista de tipos del Pokémon
     */
    public PokemonData(String id, String name, Double height, Double weight, String urlImage, List<String> types) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.urlImage = urlImage;
        this.types = types;
    }

    /**
     * Getters y Setters para cada uno de los atributos de la clase
     * @return
     */
    @PropertyName("id")
    public String getId() {
        return id;
    }

    @PropertyName("id")
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("height")
    public Double getHeight() {
        return height;
    }

    @PropertyName("height")
    public void setHeight(Double height) {
        this.height = height;
    }

    @PropertyName("weight")
    public Double getWeight() {
        return weight;
    }

    @PropertyName("weight")
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @PropertyName("urlImage")
    public String getUrlImage() {
        return urlImage;
    }

    @PropertyName("urlImage")
    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    @PropertyName("types")
    public List<String> getTypes() {
        return types;
    }

    @PropertyName("types")
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * Constructor para crear un PokemonData desde un Parcel.
     *
     * @param in Parcel que contiene los datos del Pokémon
     */
    protected PokemonData(Parcel in) {
        id = in.readString();
        name = in.readString();
        height = in.readDouble();
        weight = in.readDouble();
        urlImage = in.readString();
        types = new ArrayList<>();
        in.readStringList(types);
    }

    /**
     * Describe el contenido del Parcelable.
     *
     * @return 0, ya que no se contienen descriptores especiales de archivo
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Escribe los datos del objeto en un Parcel.
     *
     * @param dest El Parcel en el que se escribirán los datos
     * @param flags Flags adicionales sobre cómo debe escribirse el objeto
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(height);
        dest.writeDouble(weight);
        dest.writeString(urlImage);
        dest.writeStringList(types);
    }

    /**
     * Creator para PokemonData requerido por la interfaz Parcelable.
     */
    public static final Creator<PokemonData> CREATOR = new Creator<PokemonData>() {
        @Override
        public PokemonData createFromParcel(Parcel in) {
            return new PokemonData(in);
        }

        @Override
        public PokemonData[] newArray(int size) {
            return new PokemonData[size];
        }
    };
}
