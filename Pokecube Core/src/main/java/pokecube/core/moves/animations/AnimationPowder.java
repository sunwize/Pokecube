package pokecube.core.moves.animations;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldAccess;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pokecube.core.client.render.PTezzelator;
import pokecube.core.interfaces.Move_Base;
import thut.api.maths.Vector3;

public class AnimationPowder extends MoveAnimationBase
{

    String  particle;
    int     rgba;
    float   width   = 1;
    float   density = 1;
    boolean reverse = false;

    public AnimationPowder(String particle)
    {
        this.particle = particle;
        duration = 50;
        for (EnumDyeColor colour : EnumDyeColor.values())
        {
            if (colour.getName().equalsIgnoreCase(particle))
            {
                rgba = colour.getMapColor().colorValue + 0xFF000000;
                break;
            }
        }

        String[] args = particle.split(":");
        for (int i = 1; i < args.length; i++)
        {
            String ident = args[i].substring(0, 1);
            String val = args[i].substring(1);
            if (ident.equals("w"))
            {
                width = Float.parseFloat(val);
            }
            else if (ident.equals("d"))
            {
                density = Float.parseFloat(val);
            }
            else if (ident.equals("r"))
            {
                reverse = Boolean.parseBoolean(val);
            }
        }

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void clientAnimation(MovePacketInfo info, IWorldAccess world, float partialTick)
    {
        Vector3 source = info.source;
        Vector3 target = info.target;
        ResourceLocation texture = new ResourceLocation("pokecube", "textures/blank.png");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);

        Vector3 temp = Vector3.getNewVectorFromPool().set(source).subtractFrom(target);
        if (reverse) GlStateManager.translate(temp.x, temp.y, temp.z);
        PTezzelator tez = PTezzelator.instance;

        GL11.glPushMatrix();

        initColour(info.currentTick * 300, partialTick, info.move);
        
        float alpha = ((rgba >> 24) & 255) / 255f;
        float red = ((rgba >> 16) & 255) / 255f;
        float green = ((rgba >> 8) & 255) / 255f;
        float blue = (rgba & 255) / 255f;

        VertexFormat format = DefaultVertexFormats.field_181706_f;
        
        Random rand = new Random(info.currentTick);//

//      This will draw a textured, coloured quad
//        
//      texture = new ResourceLocation("pokecube", "textures/particles.png");
//      FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
//      format = DefaultVertexFormats.field_181709_i;
//      int u = 1, v = 2;
//      
//      double u1 = u * 1d/16d, v1 = v * 1d/16d;
//      double u2 = (u+1) * 1d/16d, v2 = (v+1) * 1d/16d;
//
//      //Face 1
//      tez.vertex(temp.x - size, temp.y - size, temp.z).tex(u1, v2).color(red, green, blue, alpha).endVertex();
//      tez.vertex(temp.x, temp.y - size, temp.z - size).tex(u2, v2).color(red, green, blue, alpha).endVertex();
//      tez.vertex(temp.x, temp.y + size, temp.z - size).tex(u2, v1).color(red, green, blue, alpha).endVertex();
//      tez.vertex(temp.x - size, temp.y + size, temp.z).tex(u1, v1).color(red, green, blue, alpha).endVertex();
//      //Face 2
//      tez.vertex(temp.x - size, temp.y - size, temp.z).tex(u1, v2).color(red, green, blue, alpha).endVertex();
//      tez.vertex(temp.x - size, temp.y + size, temp.z).tex(u1, v1).color(red, green, blue, alpha).endVertex();
//      tez.vertex(temp.x, temp.y + size, temp.z - size).tex(u2, v1).color(red, green, blue, alpha).endVertex();
//      tez.vertex(temp.x, temp.y - size, temp.z - size).tex(u2, v2).color(red, green, blue, alpha).endVertex();
        
        for (int i = 0; i < 500 * density; i++)
        {
            tez.begin(GL11.GL_LINE_LOOP, format);
            temp.set(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
            temp.scalarMult(0.010 * width);
            double size = 0.01;

            tez.vertex(temp.x, temp.y + size, temp.z).color(red, green, blue, alpha).endVertex();
            tez.vertex(temp.x - size, temp.y - size, temp.z - size).color(red, green, blue, alpha).endVertex();
            tez.vertex(temp.x - size, temp.y + size, temp.z - size).color(red, green, blue, alpha).endVertex();
            tez.vertex(temp.x, temp.y - size, temp.z).color(red, green, blue, alpha).endVertex();
            
            tez.end();
        }

        GL11.glPopMatrix();

        temp.freeVectorFromPool();

    }

    @Override
    public void initColour(long time, float partialTicks, Move_Base move)
    {
        if (particle.equals("airbubble"))
        {
            rgba = 0x78000000 + EnumDyeColor.CYAN.getMapColor().colorValue;
        }
        else if (particle.equals("aurora"))
        {
            int rand = ItemDye.dyeColors[new Random(time / 10).nextInt(ItemDye.dyeColors.length)];
            rgba = 0x61000000 + rand;
        }
        else if (particle.equals("iceshard"))
        {
            rgba = 0x78000000 + EnumDyeColor.CYAN.getMapColor().colorValue;
        }
        else
        {
            rgba = getColourFromMove(move, 255);
        }
    }

}
