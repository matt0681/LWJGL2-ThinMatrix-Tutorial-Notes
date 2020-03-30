package renderEngine;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the loading of geometry data into VAOs. It also keeps track of all
 * the created VAOs and VBOs so that they can all be deleted when the game
 * closes.
 */
public class Loader {

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    /**
     * This method takes in geometry data of positions, texture coordinates, normals, and indices
     * It keeps track of them by storing them into VBOs and then storing those
     * VBOs as attribute lists in a VAO.
     * It returns this new VAO as a raw model.
     */
    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices){
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * This method loads a texture into OpenGL.
     * It takes in a filename and loads the file into OpenGL, and then
     * returns the ID of that loaded texture.
     * It also keeps track of this texture by adding it to the textures list.
     * Textures will always be in the res folder and will always be .PNG
     */
    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textures.add(texture.getTextureID());
        return texture.getTextureID();
    }

    /**
     * This method loops through the lists vbos, vaos, and textures and
     * clears them. This is so that when the game closes, all data is eliminated.
     */
    public void cleanUp(){
        for (int vao:vaos){
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo:vbos){
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture:textures){
            GL11.glDeleteTextures(texture);
        }
    }

    /**
     * This method creates an empty VAO and returns the VAO's ID.
     * It also keeps track of this VAO by adding it to the vaos list.
     * It returns the ID of the new VAO
     */
    private int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    /**
     * This method takes in data, the coordinate size of the data, and an attribute number.
     * It creates an attribute list (vbo) containing the data.
     * It also keeps track of this attribute list (vbo) by adding it tp the vbos list.
     */
    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        // OpenGL's vbos need the data in a FloatBuffer. Hence the transformation above.
        // This data will not be edited once stored, hence 'GL15.GL_STATIC_DRAW'
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * This method unbinds the vao, meaning it is no longer in OpenGL's focus.
     */
    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    /**
     * This method creates a vbo for indices. (Index Buffer)
     * It then binds the index buffer to the vertex buffer.
     * It adds the new indices buffer to the vbos list.
     */
    private void bindIndicesBuffer(int[] indices){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    /**
     * This method takes in geometry data and turns it into an IntBuffer.
     * It returns this new Buffer.
     */
    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * This method takes in geometry data and turns it into a FloatBuffer.
     * It returns this new Buffer.
     */
    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
