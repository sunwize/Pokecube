package pokecube.core.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pokecube.core.client.Resources;
import pokecube.core.entity.pokemobs.ContainerPokemob;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.network.PokecubePacketHandler;
import pokecube.core.network.PokecubePacketHandler.PokecubeServerPacket;


public class GuiPokemob extends GuiContainer {
    private static final ResourceLocation pokemobGuiTextures = Resources.GUI_POKEMOB;
    private IInventory playerInventory;
    private IInventory pokeInventory;
    private IPokemob pokemob;
    private EntityLiving entity;
    private float xLoc;
    private float yLoc;
    private float yRenderAngle = 10;
    private float xRenderAngle = 0;
    private float yHeadRenderAngle = 10;
    private float xHeadRenderAngle = 0;
    Button stance;

    public GuiPokemob(IInventory playerInv, IPokemob pokemob)
    {
        super(new ContainerPokemob(playerInv, pokemob.getPokemobInventory(), pokemob));
        this.playerInventory = playerInv;
        this.pokeInventory = pokemob.getPokemobInventory();
        this.pokemob = pokemob;
        this.entity = (EntityLiving) pokemob;
        this.allowUserInput = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRendererObj.drawString(this.pokeInventory.hasCustomName() ? this.pokeInventory.getCommandSenderName() : I18n.format(this.pokeInventory.getCommandSenderName(), new Object[0]), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.hasCustomName() ? this.playerInventory.getCommandSenderName() : I18n.format(this.playerInventory.getCommandSenderName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(pokemobGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        this.drawTexturedModalRect(k + 79, l + 17, 0, this.ySize, 90, 18);
        
        this.drawTexturedModalRect(k + 7, l + 35, 0, this.ySize + 54, 18, 18);

        yRenderAngle = -30;
        xRenderAngle = 0;
        
        renderMob(pokemob, k, l, xSize, ySize, xRenderAngle, yRenderAngle, 0, 1);
    }
    
    public static void renderMob(IPokemob pokemob, int width, int height, int xSize, int ySize, float xRenderAngle, float yRenderAngle, float zRenderAngle, float scale)
    {
        try
        {
            EntityLiving entity = (EntityLiving) pokemob;
            
            float size = 0;
            int j = width;
            int k = height; 
            
            size = Math.max(entity.width, entity.height) * scale;
	            
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glPushMatrix();
            GL11.glTranslatef(j + 55, k + 50, 50F);
            float zoom = 30f/size;
            GL11.glScalef(-zoom, zoom, zoom);
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
            float f4 = (j + 51) - xSize;
            float f5 = ((k + 75) - 50) - ySize;
            GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);

            RenderHelper.enableStandardItemLighting();

            GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-(float)Math.atan(f5 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, (float) entity.getYOffset(), 0.0F);
            float offset = 0.4f;
            float f,f1,f2;
            
            if(zRenderAngle!=0)
            {
                entity.rotationYaw = 0;
                entity.rotationPitch = 0;
                entity.rotationYawHead = 0;
            }
            
            GL11.glRotatef(yRenderAngle, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(xRenderAngle, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(zRenderAngle, 0.0F, 0.0F, 1.0F);
            
            int i = 15728880;
            int j1 = i % 65536;
            int k1 = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j1 / 1.0F, k1 / 1.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(entity, 0, -0.123456, 0, 0,  1.5F);
            GL11.glPopMatrix();
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            
            float time = MathHelper.cos(entity.worldObj.getWorldTime()/3f * 0.6662f);

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.clear();
        int xOffset = 10;
        int yOffset = 33;
        String prev;
		if(pokemob.getPokemonAIState(IPokemob.GUARDING))
		{
    		prev = StatCollector.translateToLocal("pokemob.stance.guard");//"GUARD";
		}
		else if(pokemob.getPokemonAIState(IPokemob.STAYING))
		{
    		prev = StatCollector.translateToLocal("pokemob.stance.stay");//"STAY";
		}
		else
		{
			prev = StatCollector.translateToLocal("pokemob.stance.follow");//"FOLLOW";
		}

        String next;
		if(pokemob.getPokemonAIState(IPokemob.SITTING))
		{
    		next = StatCollector.translateToLocal("pokemob.stance.sit");//"SIT";
		}
		else
		{
			next = StatCollector.translateToLocal("pokemob.stance.stand");//"STAND";
		}
        buttonList.add(new GuiButton(2, width / 2 - xOffset + 50, height / 2 - yOffset, 40, 20, next));
        buttonList.add(stance = new Button(1, width / 2 - xOffset + 2, height / 2 - yOffset, 40, 20, prev));
    }
    
    /**
     * Called when the mouse is clicked.
     * @throws IOException 
     */
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        super.mouseClicked(x, y, button);
        
        if(stance.isMouseOver() && button==1)
        {
        	stance.playPressSound(this.mc.getSoundHandler());
    		byte type = -1;
    		if(type!=0)
    		{
    			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    			buffer.writeByte(8);
    			buffer.writeInt(entity.getEntityId());
    			buffer.writeByte(type);
    			PokecubeServerPacket packet = new PokecubeServerPacket(buffer);
    			PokecubePacketHandler.sendToServer(packet);
    		}
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
    	if(guibutton.id==1)
    	{
    		byte type = 1;
    		if(type!=0)
    		{
    			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    			buffer.writeByte(8);
    			buffer.writeInt(entity.getEntityId());
    			buffer.writeByte(type);
    			PokecubeServerPacket packet = new PokecubeServerPacket(buffer);
    			PokecubePacketHandler.sendToServer(packet);
    		}
    	}
    	else if(guibutton.id == 2)
    	{
    		byte type = 4;
    		if(pokemob.getPokemonAIState(IPokemob.SITTING))
    		{
        		guibutton.displayString = StatCollector.translateToLocal("pokemob.stance.stand");
    		}
    		else
    		{
        		guibutton.displayString = StatCollector.translateToLocal("pokemob.stance.sit");
    		}
    		if(type!=0)
    		{
    			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    			buffer.writeByte(8);
    			buffer.writeInt(entity.getEntityId());
    			buffer.writeByte(type);
    			PokecubeServerPacket packet = new PokecubeServerPacket(buffer);
    			PokecubePacketHandler.sendToServer(packet);
    		}
    	}
    }
    /**
     * Draws the screen and all the components in it.
     */
    @Override
	public void drawScreen(int x, int y, float z)
    {
        this.xLoc = x;
        this.yLoc = y;
        super.drawScreen(x, y, z);
		GuiButton b = (GuiButton) buttonList.get(0);
		GuiButton guibutton = (GuiButton) buttonList.get(1);
		if(!(pokemob.getPokemonAIState(IPokemob.GUARDING)||pokemob.getPokemonAIState(IPokemob.STAYING)))
		{
    		guibutton.displayString = StatCollector.translateToLocal("pokemob.stance.follow");
		}
		else if(pokemob.getPokemonAIState(IPokemob.GUARDING))
		{
    		guibutton.displayString = StatCollector.translateToLocal("pokemob.stance.guard");
		}
		else if(pokemob.getPokemonAIState(IPokemob.STAYING))
		{
    		guibutton.displayString = StatCollector.translateToLocal("pokemob.stance.stay");
		}
		if(pokemob.getPokemonAIState(IPokemob.SITTING))
		{
    		b.displayString = StatCollector.translateToLocal("pokemob.stance.sit");
		}
		else
		{
    		b.displayString = StatCollector.translateToLocal("pokemob.stance.stand");
		}
    }
    
    public static class Button extends GuiButton
    {
		public Button(int p_i1021_1_, int p_i1021_2_, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_)
		{
			super(p_i1021_1_, p_i1021_2_, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
		}
    }
}
