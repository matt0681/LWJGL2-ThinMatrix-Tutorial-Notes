package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

/**
 * This class represents our virtual camera.
 * It contains a position, pitch, yaw, and roll.
 */
public class Camera {

    private Vector3f position = new Vector3f(0,0,0);
    private float pitch = 10;
    private float yaw;
    private float roll;

    public Camera(){}

    public Camera(Vector3f position){
        this.position = position;
    }

    /**
     * This method moves the camera around. It is called every frame.
     */
    public void move(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            position.z -= 0.1f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            position.x += 0.1f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            position.x -= 0.1f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            position.z += 0.1f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            position.y += 0.05f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            position.y -= 0.05f;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}