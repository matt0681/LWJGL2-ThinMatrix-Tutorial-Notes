package objConverter;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will handle the loading of our .obj files into the rest of our code.
 */
public class OBJLoader {

    /**
     * This method takes a .obj file and loads it up as a raw model.
     * It first tries to find the specified file.
     * Next it uses file parsing techniques to store the file's data in arrays.
     * Finally it stores those arrays into a VAO and then returns this as a raw model.
     */
    public static RawModel loadObjModel(String fileName, Loader loader) {
        // The try/catch gets the file from the fileName.
        FileReader fr = null;
        try {
            fr = new FileReader(new File("res/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load .obj file!");
            e.printStackTrace();
        }
        // This reader allows us to reader from the file.
        BufferedReader reader = new BufferedReader(fr);
        // This parser reads a line at a time.
        String line;
        // These arrays store data read in from the file
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        // These arrays store the same data as above but in a better format for by VAOs.
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        try{
            /**This loop reads through the file until it gets to the "faces" section of
             * the .obj file. First it parses through the file reading all the different
             * data respective to vertex coords, texture coords, and normal vectors.
             * it finally breaks when it gets down to the faces.
             * Once it finds the data type, it puts the data into a vector and adds that vector to
             * the respective list defined above.    */
            while(true) {
                line = reader.readLine();
                // This splits each line by a space
                String[] currentLine = line.split(" ");
                // Checks for a vertex position
                if(line.startsWith("v ")){
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                // Checks for a texture coordinate
                } else if(line.startsWith("vt ")){
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                // Checks for a normal vector
                } else if(line.startsWith("vn ")){
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                // Checks for a face
                } else if(line.startsWith("f ")){
                    textureArray = new float[vertices.size()*2];
                    normalsArray = new float[vertices.size()*3];
                    break;
                }
            }

            // This loop goes through all the faces.
            //
            while(line != null) {
                if(!line.startsWith("f ")){
                    line = reader.readLine();
                    continue;
                }
                // These 4 lines split up the face values into each vertex of the face triangle.
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                // These three lines put the vertex data from above into their corresponding arrays.
                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                line = reader.readLine();
            }
            reader.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        // The code below converts the vertices and indices arrays into float[] and int[] arrays.
        verticesArray = new float[vertices.size()*3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for(Vector3f vertex:vertices){
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        // This method returns a VAO created with all our parsed data.
        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
    }

    /**
     * This method processes the vertex data (indices, textures, normals, etc.)
     * It takes in the vertex data for each face triangle and puts that data into
     * respective lists in the correct order.
     */
    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
        textureArray[currentVertexPointer*2] = currentTex.x;
        textureArray[currentVertexPointer*2+1] = 1 - currentTex.y;
        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
        normalsArray[currentVertexPointer*3] = currentNorm.x;
        normalsArray[currentVertexPointer*3+1] = currentNorm.y;
        normalsArray[currentVertexPointer*3+2] = currentNorm.z;
    }
}
