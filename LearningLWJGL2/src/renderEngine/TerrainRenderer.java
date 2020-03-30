package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.List;

/**
 * This class handles the rendering of our terrain.
 */
public class TerrainRenderer {

    private TerrainShader shader;

    /**
     * Constructor, takes in a projection matrix and loads it into the shader.
     */
    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * This method renders all the terrains in the given List.
     */
    public void render(List<Terrain> terrains){
        for (Terrain terrain : terrains){
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }


    /**
     * This method prepares a terrain for rendering.
     */
    private void prepareTerrain(Terrain terrain){
        RawModel rawModel = terrain.getModel();

        // Binds the VAO of the raw model to OpenGL.
        GL30.glBindVertexArray(rawModel.getVaoID());

        // These two lines below enable the attribute lists (vbos) 0 and 1
        // Allowing position and texture data to be retrieved from them.
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // These lines load the model's damper and reflectivity values into the shader.
        ModelTexture texture = terrain.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        //These two lines load the texture into OpenGL and bind it to OpenGL.
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
    }

    /**
     * This method unbinds the textured model of the terrains.
     */
    private void unbindTexturedModel() {
        // These lines disable the attribute lists 0, 1, and 2.
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // unbinds the VAO
        GL30.glBindVertexArray(0);
    }

    /**
     * This method creates a transformation matrix from our terrain and loads it into shader code.
     */
    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
