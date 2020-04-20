package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

/**
 * This class is an implementation of the Shader Program.
 * It is used to create all our static models.
 */
public class StaticShader extends ShaderProgram{

    /**
     * Declares the location of the shader code files.
     */
    private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

    // These variables hole the positions of their respective uniform variables.
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColour;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColour;

    // Constructor from ShaderProgram.
    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * This method binds our various uniform variables to attribute lists
     * in our VAO.
     */
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    /**
     * This method finds the locations of our uniform variables and stores them.
     */
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColour = super.getUniformLocation("skyColour");
    }

    /**
     * This method loads the rgb colour value into the skyColour uniform variable.
     */
    public void loadSkyColour(float r, float g, float b){
        super.loadVector(location_skyColour, new Vector3f(r, g, b));
    }

    /**
     * This method stores(loads) a float value representing a boolean for
     * whether or not we want to use fake lighting.
     */
    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(location_useFakeLighting, useFake);
    }

    /**
     * This method stores(loads) float values into our damper and reflectivity uniform variables.
     */
    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    /**
     * This method stores(loads) a matrix into our transformationMatrix uniform variable.
     */
    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    /**
     * This method stores(loads) a light position vector and colour vector into our lightPosition
     * and lightColour uniform variables in our shader code.
     */
    public void loadLight(Light light){
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColour, light.getColour());
    }

    /**
     * This method stores(loads) a camera instance into our viewMatrix uniform variable.
     * It turns the camera into a matrix, and then loads that matrix into the variable.
     */
    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    /**
     * This method stores(loads) a projection matrix into our projectionMatrix uniform variable.
     */
    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix, projection);
    }
}