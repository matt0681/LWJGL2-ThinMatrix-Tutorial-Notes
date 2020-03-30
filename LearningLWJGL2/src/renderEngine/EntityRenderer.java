package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.List;
import java.util.Map;

/**
 * This class handles all the rendering for our entities.
 */
public class EntityRenderer {

    private StaticShader shader;

    /**
     * Constructor, takes in a projection matrix and loads it into the shader.
     */
    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }


    /**
     * This method renders all the entities in the given List using the given Map.
     * First it loops through all the textured model keys and for each model
     * it renders all the entities using that specific model.
     */
    public void render(Map<TexturedModel, List<Entity>> entities){
        for (TexturedModel model : entities.keySet()){
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch){
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    /**
     * This method prepares a textured model for rendering.
     */
    private void prepareTexturedModel(TexturedModel model){
        RawModel rawModel = model.getRawModel();

        // Binds the VAO of the raw model to OpenGL.
        GL30.glBindVertexArray(rawModel.getVaoID());

        // These two lines below enable the attribute lists (vbos) 0 and 1
        // Allowing position and texture data to be retrieved from them.
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        ModelTexture texture = model.getTexture();
        // This if loop checks for transparency, and disables culling if it finds transparency.
        if (texture.isHasTransparency()){
            MasterRenderer.disableCulling();
        }
        // This line loads the texture's fake lighting variable into the shader.
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        // This line loads the texture's damper and reflectivity values into the shader.
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        //These two lines load the texture into OpenGL and bind it to OpenGL.
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }

    /**
     * This method unbinds the textured model once all the entities have used it.
     */
    private void unbindTexturedModel(){
        // This is so that culling is enabled for the next model.
        MasterRenderer.enableCulling();
        // These lines disable the attribute lists 0, 1, and 2.
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // unbinds the VAO
        GL30.glBindVertexArray(0);
    }

    /**
     * This method prepares all the entities that use the prepared textured model.
     */
    private void prepareInstance(Entity entity){
        // These two lines create a transformation matrix and load it into the shader code.
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
