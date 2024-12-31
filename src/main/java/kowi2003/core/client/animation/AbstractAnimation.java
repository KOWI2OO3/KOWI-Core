package kowi2003.core.client.animation;

public abstract class AbstractAnimation {
    
    protected float time = 0;
    protected float maxTime = -1;

    protected boolean isPaused = true;
    protected boolean isStopped = true;
    protected boolean loop = false;

    public void pause() {
        isPaused = true;
    }

    public void play() {
        isPaused = false;
        isStopped = false;
    }

    public void stop() {
        isStopped = true;
        pause();
        reset();
    }

    public void loop(boolean loop) {
        this.loop = loop;
    }

    public void increaseTime(float deltaTime) {        
        if(isPaused) return;

        time += deltaTime;
        if(time >= getMaxTime())
            reset();
        
        updateAnimation(deltaTime);
    }

    public final float getMaxTime() {
        if(maxTime <= -1)
            maxTime = maxTime();
        return maxTime;
    }

    public abstract void updateAnimation(float deltaTime);
    protected abstract float maxTime();
    protected abstract void resetAnimation();

    protected void reset() {
        isPaused |= !loop;
        isStopped = isPaused;
        time = 0;

        resetAnimation();
    }

}
