package models;

import textures.ModelTexture;

/**
 * This class is a textured model.
 * It contains both model data (raw model) and texture data (model texture)
 * to form a combined textured model.
 */
public class TexturedModel {

    private RawModel rawModel;
    private ModelTexture modelTexture;

    public TexturedModel(RawModel model, ModelTexture texture) {
        this.rawModel = model;
        this.modelTexture = texture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return modelTexture;
    }
}
