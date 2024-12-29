package kowi2003.core.client.animation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class AnimationSerializer implements JsonDeserializer<Animation>
{
    @Override
    public Animation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if(json instanceof JsonObject jobj) 
        {
            JsonElement lengthJ = jobj.get("length");
            if(lengthJ == null) return null;
            float length = lengthJ.getAsFloat();
            
            JsonElement animations = jobj.get("animations");
            if(animations instanceof JsonArray animationsJ)
            {
                Animation animation = new Animation(deserializeAnimations(animationsJ, typeOfT, context));
                animation.maxTime = length;

                JsonElement loopJ = jobj.get("loop");
                if(loopJ != null) 
                    animation.loop(loopJ.getAsBoolean());
                return animation;
            }
        }
        return null;
    }

    @Nonnull
    private HashMap<String, PartAnimation> deserializeAnimations(JsonArray array, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        HashMap<String, HashMap<String, List<KeyFrame>>> temporaryStorage = new HashMap<>();

        for (JsonElement element : array) {
            if(element instanceof JsonObject jobj)
            {
                JsonElement boneJ = jobj.get("bone");
                if(boneJ == null) throw new JsonParseException("Missing bone entry in animation");
                var partName = boneJ.getAsString();

                JsonElement targetJ = jobj.get("target");
                if(targetJ == null) throw new JsonParseException("Missing target entry in animation");
                var target = targetJ.getAsString();

                // Handle keyframes, only put animation in if keyframes is present and size is larger than 0
                JsonElement keyframesJ = jobj.get("keyframes");
                if(keyframesJ instanceof JsonArray keyframeJArray) {
                    var keyFrames = deserializeKeyFrames(keyframeJArray, context, getConversion(target));
                    if(keyFrames.size() == 0) continue;

                    // Get or create map
                    var keyFrameMap = temporaryStorage.get(partName);
                    if(keyFrameMap == null) {
                        keyFrameMap = new HashMap<>();
                        temporaryStorage.put(partName, keyFrameMap);
                    }
                    
                    // putting keyframes into target
                    keyFrameMap.put(target, keyFrames);
                }
            }
        }

        HashMap<String, PartAnimation> result = new HashMap<>();
        for (var entry : temporaryStorage.entrySet()) {
            result.put(entry.getKey(), new PartAnimation(entry.getValue().get("position"), entry.getValue().get("rotation"), entry.getValue().get("scale")));
        }

        return result;
    }

    @Nonnull
    private List<KeyFrame> deserializeKeyFrames(JsonArray array, JsonDeserializationContext context, Function<Vector3f, Vector3f> conversion) throws JsonParseException {
        List<KeyFrame> list = new ArrayList<>();

        for(JsonElement element : array) {
            if(element instanceof JsonObject jobj)
            {
                var keyframe = deserializeKeyFrame(jobj, context, conversion);
                if(keyframe != null)
                    list.add(keyframe);
            }
        }

        return list;
    }

    @Nullable
    private KeyFrame deserializeKeyFrame(JsonObject json, JsonDeserializationContext context, Function<Vector3f, Vector3f> conversion) throws JsonParseException {
        var timestampJ = json.get("timestamp");
        if(timestampJ == null) return null;
        var timestamp = timestampJ.getAsFloat();

        Vector3f target = null;
        var targetJ = json.get("target");
        if(targetJ == null) return null;
        if(targetJ instanceof JsonArray jArray && jArray.size() == 3) {
            target = new Vector3f(
                jArray.get(0).getAsFloat(),
                jArray.get(1).getAsFloat(),
                jArray.get(2).getAsFloat()
            );
        }
        if(target == null) return null;

        return new KeyFrame(timestamp, conversion.apply(target));
    }

    private Function<Vector3f, Vector3f> getConversion(String target) {
        switch (target) {
            case "position":
                return this::positionConversion;
            case "rotation":
                return this::degreeToRadians;
            default:
                return this::noConversion;
        }
    }

    private Vector3f degreeToRadians(Vector3f in) {
        return in.mul((float)Math.toRadians(1));
    }

    private Vector3f positionConversion(Vector3f in) {
        return in.mul(1f/16f);
    }

    private Vector3f noConversion(Vector3f in) {
        return in;
    }
}
