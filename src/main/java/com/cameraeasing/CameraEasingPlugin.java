package com.cameraeasing;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@PluginDescriptor(
        name = "Camera Easing"
)
public class CameraEasingPlugin extends Plugin implements MouseListener
{
    static final int period = 180;
    static final int fps = 60;
    static final double amplitude = 60;

    static final EaseUtility easeUtilities = new EaseUtility();

    @Inject
    private Client client;

    @Inject
    private CameraEasingConfig config;

    @Inject
    private MouseManager mouseManager;

    private int yawCycle; // 0 to 10000
    private Instant loginTime;
    private double delta;
    private int last_time;
    private int pivotPoint;
    private boolean mouseActive;
    private int mouseDirection;
    private double mouseDistanceNormalized;
    private double mouseTimeNormalized;
    private boolean didMouseJustRelease;

    private int mouseStartTimestamp;
    private int mouseStartX;
    @Override
    protected void startUp() throws Exception
    {
        mouseManager.registerMouseListener(this);
        yawCycle = 0;
        mouseDirection = 1;
        if(loginTime == null){
            loginTime = Instant.now();
        }
        //last_time = System.nanoTime();
        resetPivot();
    }

    @Override
    protected void shutDown() throws Exception
    {
        resetPivot();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
        {
            loginTime = Instant.now();
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick)
    {
        int timestamp = (int) (System.nanoTime()  / 1000000); // to milliseconds
        double timestep = 1000.0 / config.fps();
        delta += timestamp - last_time;
        last_time = timestamp;

        if(delta >= timestep) {

            if(!mouseActive) {

                double degree = (yawCycle++ / ((double) config.period())) * 360d;
                double rad = Math.toRadians(degree);
                double sin = Math.sin(rad);// output: -1.0 to 1.0
                double yawShift = sin;

                if(!config.infiniteLoop() && degree >= 90) {
                    mouseActive = true;
                    return;
                }

                switch (config.filterType())
                {
                    case easeInSine:
                        // Ease Left & right
                        yawShift = easeUtilities.easeInSine(sin);
                        break;
                    case easeInOutSine:
                        // bounce left, right twice
                        yawShift = easeUtilities.easeInOutSine(sin);
                        break;
                    case easeOutBounce:
                        yawShift = easeUtilities.easeOutBounce(sin);
                        break;
                    case easeOutBack:
                        yawShift = easeUtilities.easeOutBack(sin);
                        break;
                    case easeOutElastic:
                        yawShift = easeUtilities.easeOutElastic(sin);
                        break;
                    case NONE:
                        // None
                        break;
                }
                yawShift *= (config.amplitude() * (!config.infiniteLoop() ? 5 : 1)) * mouseDirection;

                if(config.highStaminaCondition()){
                    yawShift *= client.getEnergy() / 10;
                }
                if(config.lowStaminaCondition()){
                    yawShift *= 100 / client.getEnergy();
                }
                if(config.lowGameTimeCondition()){
                    Duration duration = Duration.between(loginTime, Instant.now());
                    yawShift *= 100 / duration.getSeconds();
                }
                if(config.highGameTimeCondition()){
                    Duration duration = Duration.between(loginTime, Instant.now());
                    yawShift += duration.getSeconds();
                }
                double newYaw = pivotPoint + yawShift * mouseDistanceNormalized * mouseTimeNormalized;
                if(didMouseJustRelease && config.infiniteLoop() && degree == 90 ){
                    mouseDistanceNormalized = 1;
                    mouseTimeNormalized = 1;
                    didMouseJustRelease = false;
                    resetPivot();
                }
                if (newYaw < 0) newYaw = 2047 + newYaw;
                if (newYaw > 2047) newYaw = newYaw % 2047;
                client.setCameraYawTarget((int) newYaw);
            }
            delta -= timestep;
        }
    }

    public void resetPivot(){
        delta = 0;
        yawCycle = 0;
        pivotPoint = client.getCameraYaw();
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isMiddleMouseButton(mouseEvent) ) {
            //log.info("mousePressed");
            resetPivot();
            mouseStartX = mouseEvent.getX();
            mouseStartTimestamp = (int) (System.nanoTime()  / 1000000); // milliseconds
            mouseActive = true;
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        if (SwingUtilities.isMiddleMouseButton(mouseEvent) ) {

            resetPivot();
            if(mouseStartX < mouseEvent.getX())
                mouseDirection = -1;
            else
                mouseDirection = 1;
            int diff = (int) (System.nanoTime()  / 1000000) - mouseStartTimestamp;

            double buffer100ms = Math.max((double)diff, 0d);
            mouseTimeNormalized = buffer100ms / 300;

            mouseDistanceNormalized = Math.abs((double)mouseStartX - (double)mouseEvent.getX()) / (double)client.getViewportWidth();
            didMouseJustRelease = true;

            //log.info("mouseReleased amp:{} time:{}ms", mouseDistanceNormalized, diff);
            mouseActive = false;
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isMiddleMouseButton(mouseEvent) ) {

            //log.info("mouseDragged");
            resetPivot();
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        //log.info("mouseClicked");
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent mouseEvent) {
        //log.info("mouseEntered");
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseExited(MouseEvent mouseEvent) {
        //log.info("mouseExited");
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        //log.info("mouseMoved");
        return mouseEvent;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!"cameraeasing".equals(event.getGroup()))
        {
            return;
        }

        //log.info("config updated");
        mouseActive = false;
        resetPivot();
    }
    @Provides
    CameraEasingConfig provideConfig(ConfigManager configManager)
    {
        //log.info("config updated");
        mouseActive = false;
        resetPivot();
        return configManager.getConfig(CameraEasingConfig.class);
    }
}

