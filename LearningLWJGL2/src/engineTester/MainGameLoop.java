package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import objConverter.OBJLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import terrains.Terrain;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class contains the main method and is used to test the engine.
 */
public class MainGameLoop {

    public static void main(String[] args) {

        /**
         * Creating our display, loader, shader, and renderer.
         */
        DisplayManager.createDisplay();
        Loader loader = new Loader();

        ModelData data = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());

        TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
                new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHasTransparency(true);
        fern.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<Entity>();
        Random rand = new Random();
        for (int i = 0; i < 500; i++){
            entities.add(new Entity(tree, new Vector3f(rand.nextFloat()*800 - 400, 0,
                    rand.nextFloat() * -600), 0, 0, 0, 0.75f));
            entities.add(new Entity(grass, new Vector3f(rand.nextFloat()*800 - 400, 0,
                    rand.nextFloat() * -600), 0, 0, 0, 1));
            entities.add(new Entity(fern, new Vector3f(rand.nextFloat()*800 - 400, 0,
                    rand.nextFloat() * -600), 0, 0, 0, 0.6f));
        }

        // Creating a light at certain coords.
        Light light = new Light(new Vector3f(20000,20000,20000), new Vector3f(1,1,1));

        Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("grass")));

        // Creating a camera.
        Camera camera = new Camera(new Vector3f(0,4.5f,0));
        // Creates a master renderer.
        MasterRenderer renderer = new MasterRenderer();

        // Main Event Loop
        while(!Display.isCloseRequested()){
            camera.move();

            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for (Entity entity : entities){
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        // Clears all our memory and various classes of old data and
        // exits out of our display.
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
