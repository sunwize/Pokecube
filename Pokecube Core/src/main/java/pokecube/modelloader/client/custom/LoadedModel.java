package pokecube.modelloader.client.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import pokecube.core.client.render.entity.RenderPokemobs;
import pokecube.core.database.PokedexEntry;
import pokecube.core.entity.pokemobs.EntityPokemob;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.utils.Vector4;
import pokecube.modelloader.client.custom.animation.AnimationBipedWalk;
import pokecube.modelloader.client.custom.animation.ModelAnimation;
import pokecube.modelloader.client.custom.animation.PartAnimation;
import pokecube.modelloader.client.custom.animation.AnimationLoader.Model;
import pokecube.modelloader.client.custom.tbl.TblModel;
import pokecube.modelloader.client.custom.x3d.X3dModel;
import thut.api.maths.Vector3;

public class LoadedModel extends RendererLivingEntity
{
    public static final String             DEFAULTPHASE = "idle";
    public String                          name;
    HashMap<String, PartInfo>              parts;
    HashMap<String, ArrayList<Vector5>>    global;
    HashSet<String>                        validPhases  = new HashSet();
    public HashMap<String, ModelAnimation> phaseMap     = new HashMap<String, ModelAnimation>();

    public Vector3 offset    = Vector3.getNewVectorFromPool();;
    public Vector3 scale     = Vector3.getNewVectorFromPool();;
    public Vector5 rotations = new Vector5();

    public IModel model;

    public int headDir  = 2;
    public int headAxis = -2;

    public float[] headCaps = { -180, 180 };

    public float  rotationPointX = 0, rotationPointY = 0, rotationPointZ = 0;
    public float  rotateAngleX   = 0, rotateAngleY = 0, rotateAngleZ = 0, rotateAngle = 0;
    private float timeFactor     = 1;

    ResourceLocation texture;

    public LoadedModel(HashMap<String, PartInfo> parts, HashMap<String, ArrayList<Vector5>> global, Model model)
    {
        super(Minecraft.getMinecraft().getRenderManager(), null, 0);
        name = model.name;
        this.parts = parts;
        this.texture = model.texture;
        if (model.model.getResourcePath().contains(".x3d")) this.model = new X3dModel(model.model);
        else this.model = new TblModel(model.model);
        initModelParts();
        if (headDir == 2)
        {
            headDir = (this.model instanceof X3dModel) ? 1 : -1;
        }
        if(headAxis == -2)
        {
            headDir = (this.model instanceof X3dModel) ? 1 : 2;
        }
        this.global = global;
    }

    public void updateModel(HashMap<String, PartInfo> parts, HashMap<String, ArrayList<Vector5>> global, Model model)
    {
        name = model.name;
        this.parts = parts;
        this.texture = model.texture;
        if (model.model.getResourcePath().contains(".x3d")) this.model = new X3dModel(model.model);
        else this.model = new TblModel(model.model);
        initModelParts();
        this.global = global;
    }

    private void initModelParts()
    {
        if (model == null) return;

        for (String s : model.getParts().keySet())
        {
            if (model.getParts().get(s).getParent() == null && !parts.containsKey(s))
            {
                PartInfo p = getPartInfo(s);
                parts.put(s, p);
            }
        }
        for (PartInfo p : parts.values())
        {
            String root = p.name;
            Set phases = p.getPhases();
            addChildrenPhases(validPhases, p);
            validPhases.addAll(phases);
        }
        for (String phase : validPhases)
        {
            ModelAnimation animation = new ModelAnimation();
            for (PartInfo p : parts.values())
            {
                PartAnimation anim = new PartAnimation(p.name);
                anim.info = p;
                anim.positions = p.getPhase(phase);
                animation.animations.put(p.name, anim);
                addChildrenToAnimation(animation, p, phase);
            }
            phaseMap.put(phase, animation);
        }
    }

    private void addChildrenPhases(HashSet toAddTo, PartInfo part)
    {
        for (PartInfo p : part.children.values())
        {
            toAddTo.addAll(p.getPhases());
            addChildrenPhases(toAddTo, p);
        }
    }

    private void addChildrenToAnimation(ModelAnimation animation, PartInfo p, String phase)
    {
        for (String s : p.children.keySet())
        {
            PartInfo p2 = p.children.get(s);

            PartAnimation anim = new PartAnimation(s);
            anim.info = p2;
            anim.positions = p2.getPhase(phase);
            if (!animation.animations.containsKey(p2.name) || animation.animations.get(p2.name).positions == null)
            {
                animation.animations.put(p2.name, anim);
                addChildrenToAnimation(animation, p2, phase);
            }
        }
    }

    private HashMap<String, PartInfo> getChildren(IExtendedModelPart part)
    {
        HashMap<String, PartInfo> partsList = new HashMap<String, PartInfo>();
        for (String s : part.getSubParts().keySet())
        {
            PartInfo p = new PartInfo(s);
            IExtendedModelPart subPart = part.getSubParts().get(s);
            p.children = getChildren(subPart);
            partsList.put(s, p);
        }
        return partsList;
    }

    private void addChildren(PartInfo info, IExtendedModelPart root, IModel model2)
    {
        for (PartInfo p : info.children.values())
        {
            if (model2.getParts().get(p.name) != null)
            {
                addChildren(p, model2.getParts().get(p.name), model2);
            }
            Set phases = p.getPhases();
            validPhases.addAll(phases);
            root.addChild(model2.getParts().get(p.name));
        }
    }

    public HashSet<IExtendedModelPart> getAllParts()
    {
        HashSet<IExtendedModelPart> ret = new HashSet<IExtendedModelPart>();

        ret.addAll(model.getParts().values());

        return ret;
    }

    @Override
    public void doRender(EntityLivingBase entity, double d, double d1, double d2, float f, float f1)
    {
        float f2 = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, f1);
        float f3 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, f1);
        float f4;
        if (entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase1 = (EntityLivingBase) entity.ridingEntity;
            f2 = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, f1);
            f4 = MathHelper.wrapAngleTo180_float(f3 - f2);

            if (f4 < -85.0F)
            {
                f4 = -85.0F;
            }

            if (f4 >= 85.0F)
            {
                f4 = 85.0F;
            }

            f2 = f3 - f4;

            if (f4 * f4 > 2500.0F)
            {
                f2 += f4 * 0.2F;
            }
        }
        
        float f13 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * f1;

        f4 = this.handleRotationFloat(entity, f1);
        float f5 = 0.0625F;
        this.preRenderCallback(entity, f1);
        float f6 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * f1;
        float f7 = entity.limbSwing - entity.limbSwingAmount * (1.0F - f1);

        if (entity.isChild())
        {
            f7 *= 3.0F;
        }

        if (f6 > 1.0F)
        {
            f6 = 1.0F;
        }
        GL11.glPushMatrix();
        
        int i = entity.getBrightnessForRender(f);
        if (entity.isBurning())
        {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);

        String currentPhase = getPhaseFromEntity(entity, f6, f7, f3 - f2, f13);

        transformGlobal(currentPhase, entity, d, d1, d2, f1, f3 - f2, f13);

        for (String partName : parts.keySet())
        {
            IExtendedModelPart part = model.getParts().get(partName);
            if (part == null) continue;
            try
            {
                if (part.getParent() == null)
                {
                    GL11.glPushMatrix();
                    part.getDefaultRotations().glRotate();
                    renderPart(partName, currentPhase, entity, f1);
                    GL11.glPopMatrix();
                }
                else
                {
                    GL11.glPushMatrix();
                    renderPart(partName, currentPhase, entity, f1);
                    GL11.glPopMatrix();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        GL11.glPopMatrix();

    }

    private String getPhaseFromEntity(Entity entity, float walkspeed, float time, float rotationYaw,
            float rotationPitch)
    {
        // TODO make this find out what action the entity is doing.
        if (entity instanceof EntityPokemob)
        {
            EntityPokemob mob = (EntityPokemob) entity;
            if (mob.getPokemonAIState(IPokemob.SITTING) && mob.onGround) { return "sitting"; }
            if (mob.onGround && walkspeed > 0.1) { return "walking"; }
            if (!mob.onGround) { return "flying"; }
            if (mob.onGround) { return "onground"; }
        }
        return DEFAULTPHASE;
    }

    public void transformGlobal(String currentPhase, Entity entity, double x, double y, double z, float partialTick,
            float rotationYaw, float rotationPitch)
    {
        float factor = 2.0f;
        if (rotations == null)
        {
            rotations = new Vector5();
        }

        this.setRotationAngles(rotations.rotations);
        this.setRotationPoint(offset);

        rotationYaw = -entity.rotationYaw + 180;

        if (model instanceof TblModel)
        {
            GL11.glRotated(180, 0, 0, 1);
            rotationYaw *= -1;
        }

        if (entity instanceof IPokemob)
        {
            IPokemob mob = (IPokemob) entity;
            PokedexEntry entry = mob.getPokedexEntry();
            float scale = (mob.getSize());
            GL11.glScalef(scale, scale, scale);
            shadowSize = entry.width * mob.getSize();
        }
        float yawOffset = 0;
        if (entity instanceof EntityLiving)
        {
            EntityLiving ent = (EntityLiving) entity;
            yawOffset = ent.renderYawOffset;
        }

        Vector4 yaw = new Vector4(0, 1, 0, rotationYaw);
        Vector4 pitch = new Vector4(1, 0, 0, entity.rotationPitch);
        this.rotate();
        yaw.glRotate();
        this.translate();

        if (!scale.isEmpty()) GL11.glScaled(scale.x, scale.y, scale.z);

    }

    public void renderPart(String partName, String currentPhase, Entity entity, float partialTick)
    {

        IExtendedModelPart part = this.getPart(partName);
        PartInfo info = getPartInfo(partName);

        if (part == null) { return; }
        // System.out.println(info);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        if (part.getParent() != null)
        {
            part.setPreTranslations(part.getParent().getDefaultTranslations());
        }
        else
        {
            part.getDefaultRotations().glRotate();
        }

        int red = 255, green = 255, blue = 255;

        if (entity instanceof IPokemob)
        {
            IPokemob poke = (IPokemob) entity;
            red = poke.getColours()[0];
            green = poke.getColours()[1];
            blue = poke.getColours()[2];
        }

        if (phaseMap.containsKey(currentPhase)
                && phaseMap.get(currentPhase).doAnimation(entity, partName, part, partialTick))
        {
//            AnimationBipedWalk walk = (AnimationBipedWalk) phaseMap.get(currentPhase);
//            System.out.println(walk.namesL+" "+partName+" "+part.getParent().getSubParts().keySet());
        }
        else if (phaseMap.containsKey(DEFAULTPHASE)
                && phaseMap.get(DEFAULTPHASE).doAnimation(entity, partName, part, partialTick))
        {
            ;
        }
        else
        {
            part.setPreRotations(new Vector4());
            part.setPostRotations(new Vector4());
        }

        boolean head = partName.toLowerCase().contains("head") && part.getParent() != null;
        if (head)
        {
            head = !part.getParent().getName().toLowerCase().contains("head");
        }

        if (head)
        {
            float ang = (entity.rotationYaw) % 360;
            float ang2 = entity.rotationPitch;
            float te = -entity.getRotationYawHead();

            ang += te;

            if (ang > 180) ang -= 360;

            ang = Math.max(ang, headCaps[0]);
            ang = Math.min(ang, headCaps[1]);

            ang2 = Math.max(ang2, headCaps[0]);
            ang2 = Math.min(ang2, headCaps[1]);
            Vector4 dir;
            if (headAxis == 0)
            {
                dir = new Vector4(headDir, 0, 0, ang);
            }
            else if (headAxis == 2)
            {
                dir = new Vector4(0, 0, headDir, ang);
            }
            else
            {
                dir = new Vector4(0, headDir, 0, ang);
            }
            Vector4 dir2;
            if (headAxis == 0)
            {
                dir2 = new Vector4(0, headDir, 0, -ang2);
            }
            else if (headAxis == 2)
            {
                dir2 = new Vector4(0, 0, headDir, -ang2);
            }
            else
            {
                dir2 = new Vector4(headDir, 0, 0, -ang2);
            }
            // dir = dir.addAngles(dir2);

            part.setPostRotations(dir);
            part.setPostRotations2(dir2);
        }
        part.setRGBAB(new int[] { red, green, blue, 255, entity.getBrightnessForRender(0) });
        part.renderPart(partName);// .render(red, green, blue)
        for (String s : part.getSubParts().keySet())
        {
            if (s != null) renderPart(s, currentPhase, entity, partialTick);
        }
        GL11.glPopMatrix();

    }

    public static class Vector5
    {
        public Vector4 rotations;
        public int     time;

        public Vector5(Vector4 rotation, int time)
        {
            this.rotations = rotation;
            this.time = time;
        }

        public Vector5()
        {
            this.time = 0;
            this.rotations = new Vector4();
        }

        @Override
        public String toString()
        {
            return "|r:" + rotations + "|t:" + time;
        }

        public Vector5 interpolate(Vector5 v, float time, boolean wrap)
        {
            if (v.time == 0) return this;
            // wrap = true;

            if (Double.isNaN(rotations.x))
            {
                rotations = new Vector4();
            }
            Vector4 rotDiff = rotations.copy();

            if (rotations.x == rotations.z && rotations.z == rotations.y && rotations.y == rotations.w
                    && rotations.w == 0)
            {
                rotations.x = 1;
            }

            if (!v.rotations.equals(rotations))
            {
                rotDiff = v.rotations.subtractAngles(rotations);

                rotDiff = rotations.addAngles(rotDiff.scalarMult(time));
            }
            if (Double.isNaN(rotDiff.x))
            {
                rotDiff = new Vector4(0, 1, 0, 0);
            }
            Vector5 ret = new Vector5(rotDiff, v.time);
            return ret;
        }
    }

    public void translate()
    {
        GL11.glTranslated(rotationPointX, rotationPointY, rotationPointZ);
    }

    protected void rotate()
    {
        GL11.glRotatef(rotateAngle, rotateAngleX, rotateAngleY, rotateAngleZ);
    }

    public void setRotationPoint(float par1, float par2, float par3)
    {
        this.rotationPointX = par1;
        this.rotationPointY = par2;
        this.rotationPointZ = par3;
    }

    public void setRotationPoint(Vector3 point)
    {
        setRotationPoint((float) point.x, (float) point.y, (float) point.z);
    }

    public void setRotationAngles(Vector4 rotations)
    {
        rotateAngle = rotations.w;
        rotateAngleX = rotations.x;
        rotateAngleY = rotations.y;
        rotateAngleZ = rotations.z;
    }

    private IExtendedModelPart getPart(String partName)
    {
        IExtendedModelPart ret = null;
        for (IExtendedModelPart part : model.getParts().values())
        {
            if (part.getName().equalsIgnoreCase(partName)) return part;
            ret = getPart(partName, part);
            if (ret != null) return ret;
        }

        return ret;
    }

    private IExtendedModelPart getPart(String partName, IExtendedModelPart parent)
    {
        IExtendedModelPart ret = null;
        for (IExtendedModelPart part : parent.getSubParts().values())
        {
            if (part.getName().equalsIgnoreCase(partName)) return part;
            ret = getPart(partName, part);
            if (ret != null) return ret;
        }

        return ret;
    }

    private PartInfo getPartInfo(String partName)
    {
        PartInfo ret = null;
        for (PartInfo part : parts.values())
        {
            if (part.name.equalsIgnoreCase(partName)) return part;
            ret = getPartInfo(partName, part);
            if (ret != null) return ret;
        }
        for (IExtendedModelPart part : model.getParts().values())
        {
            if (part.getName().equals(partName))
            {
                PartInfo p = new PartInfo(part.getName());
                p.children = getChildren(part);
                boolean toAdd = true;
                IExtendedModelPart parent = part.getParent();
                while (parent != null && toAdd)
                {
                    toAdd = !parts.containsKey(parent.getName());
                    parent = parent.getParent();
                }
                if (toAdd) parts.put(partName, p);
                return p;
            }
        }

        return ret;
    }

    private PartInfo getPartInfo(String partName, PartInfo parent)
    {
        PartInfo ret = null;
        for (PartInfo part : parent.children.values())
        {
            if (part.name.equalsIgnoreCase(partName)) return part;
            ret = getPartInfo(partName, part);
            if (ret != null) return ret;
        }

        return ret;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
        return RenderPokemobs.getInstance().getEntityTexturePublic(var1);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float f)
    {
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    /** Returns a rotation angle that is inbetween two other rotation angles.
     * par1 and par2 are the angles between which to interpolate, par3 is
     * probably a float between 0.0 and 1.0 that tells us where "between" the
     * two angles we are. Example: par1 = 30, par2 = 50, par3 = 0.5, then return
     * = 40 */
    public float interpolateRotation(float low, float high, float diff)
    {
        float f3;

        for (f3 = high - low; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return low + diff * f3;
    }
}
