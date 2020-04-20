package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * This class represents our virtual camera.
 * It contains a position, pitch, yaw, and roll.
 */
public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0,0,0);
    private float pitch = 20;
    private float yaw = 20;
    private float roll;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    /**
     * This method moves the camera around. It is called every frame.
     */
    public void move(){
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
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

    private void calculateCameraPosition(float horizDistance, float vertDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + vertDistance;
    }

    private float calculateHorizontalDistance() {
        float out = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
        return out;
    }

    private float calculateVerticalDistance() {
        float out = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
        return out;
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if(Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        if(Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
}