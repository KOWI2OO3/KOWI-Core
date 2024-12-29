package kowi2003.core.client.model;

import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;

public interface IAnimatedModel extends IModel {
    
    public void applyAnimation(Transforms transforms);

}
