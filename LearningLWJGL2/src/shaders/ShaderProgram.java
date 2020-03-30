package shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * This class represents a generic shader program that contains all the necessary methods
 * needed in a regular shader program.
 * A shader Program has a program ID, a vertex shader ID and a fragment shader ID.
 */
public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    // This buffer is to be reused every time we load a matrix into a uniform variable.
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    /**
     * This constructor takes in the vertex and fragment shader files and loads them into OpenGL
     * and gets their ID numbers. It then creates a shader program in OpenGL and binds the two
     * shaders in OpenGL to the new shader program.
     * This allows them to cooperate and be linked together.
     */
    public ShaderProgram(String vertexFile,String fragmentFile){
        vertexShaderID = loadShader(vertexFile,GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();
    }

    /**
     * This method makes sure all the uniform locations are found.
     */
    protected abstract void getAllUniformLocations();

    /**
     * This method takes the String name of a uniform variable and returns
     * the location of it in the shader code as an int.
     */
    protected int getUniformLocation(String uniformName){
        return GL20.glGetUniformLocation(programID,uniformName);
    }

    /**
     * This method starts the shader program based on it's ID.
     */
    public void start(){
        GL20.glUseProgram(programID);
    }

    /**
     * This method stops the shader program.
     */
    public void stop(){
        GL20.glUseProgram(0);
    }

    /**
     * This method clears all the shaders' data. This is so when the program ends no data is
     * left behind in memory.
     */
    public void cleanUp(){
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    /**
     * This method binds all the inputs and outputs of the two shaders to
     * the attributes of the VAO.
     * It makes use of the bindAttribute() function.
     */
    protected abstract void bindAttributes();

    /**
     * This method takes in the attribute list number in the VAO that we want to bind
     * and it takes in the variable name of the variable in the shader code.
     * Essentially binds the Vertex data in the VAO to the input of the shader code.
     */
    protected void bindAttribute(int attribute, String variableName){
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    /**
     * This method takes the location of a uniform variable and stores
     * a float value into that variable.
     */
    protected void loadFloat(int location, float value){
        GL20.glUniform1f(location, value);
    }

    /**
     * This method takes the location of a uniform variable and stores
     * a vector value into that variable.
     */
    protected void loadVector(int location, Vector3f vector){
        GL20.glUniform3f(location,vector.x,vector.y,vector.z);
    }

    /**
     * This method takes the location of a uniform variable and stores a boolean
     * value into that variable. GLSL doesn't have booleans so we us 0 (false) and 1 (true).
     */
    protected void loadBoolean(int location, boolean value){
        float toLoad = 0;
        if(value){
            toLoad = 1;
        }
        GL20.glUniform1f(location, toLoad);
    }

    /**
     * This method takes the location of a uniform variable and stores a matrix
     * value into that variable. It loads it into a buffer and then into the uniform var.
     */
    protected void loadMatrix(int location, Matrix4f matrix){
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

    /**
     * This method takes in the name of a shader program file and an type telling it whether the file
     * is a vertex shader or fragment shader. The method is essentially a file reading method that
     * takes all the important data from the shader files and loads it into OpenGL for use.
     * It returns the ID number of the newly created shader.
     */
    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }

}