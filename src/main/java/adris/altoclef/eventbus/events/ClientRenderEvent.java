package adris.altoclef.eventbus.events;

import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix3x2fStack;

public class ClientRenderEvent {
    public Matrix3x2fStack stack;
    public RenderTickCounter tickDelta;

    public ClientRenderEvent(Matrix3x2fStack stack, RenderTickCounter tickDelta) {
        this.stack = stack;
        this.tickDelta = tickDelta;
    }
}
