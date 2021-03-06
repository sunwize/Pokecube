/**
 * 
 */
package pokecube.core.entity.pokemobs.helper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pokecube.core.Mod_Pokecube_Helper;
import pokecube.core.ai.utils.PokeNavigator;
import pokecube.core.database.PokedexEntry;
import pokecube.core.events.SpawnEvent;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.utils.PokeType;
import pokecube.core.utils.PokecubeSerializer;
import thut.api.entity.IMultibox;
import thut.api.maths.Matrix3;
import thut.api.maths.Vector3;

/** @author Manchou, Thutmose */
public abstract class EntityPokemobBase extends EntityHungryPokemob implements IMultibox, IBossDisplayData
{

    static String[] unowns = { "Unown_A", "Unown_B", "Unown_C", "Unown_D", "Unown_E", "Unown_F", "Unown_G", "Unown_H",
            "Unown_I", "Unown_J", "Unown_K", "Unown_L", "Unown_M", "Unown_N", "Unown_O", "Unown_P", "Unown_Q",
            "Unown_Qu", "Unown_R", "Unown_S", "Unown_T", "Unown_U", "Unown_V", "Unown_W", "Unown_X", "Unown_Y",
            "Unown_Z", "Unown_Ex" };

    public static float   scaleFactor = 0.075f;
    public static boolean multibox    = true;

    private int   uid        = -1;
    protected int pokecubeId = 0;

    protected int    particleIntensity = 0;
    protected int    particleCounter   = 0;
    protected String particle;

    private int[] flavourAmounts = new int[5];

    protected String texture;

    public Matrix3                  mainBox;
    private Vector3                 offset  = Vector3.getNewVectorFromPool();
    public HashMap<String, Matrix3> boxes   = new HashMap<String, Matrix3>();
    public HashMap<String, Vector3> offsets = new HashMap<String, Vector3>();

    int           corruptedSum = -123586;
    private float nextStepDistance;

    public EntityPokemobBase(World world)
    {
        super(world);
        this.setSize(1, 1);
        this.width = 1;
        this.height = 1;
        nextStepDistance = 1;
    }

    @Override
    public void init(int nb)
    {
        super.init(nb);
        this.getPokedexEntry();

        if (multibox)
        {
            this.noClip = true;
        }

        Random random = new Random();
        int abilityNumber = random.nextInt(100) % 2;
        if (getPokedexEntry().getAbility(abilityNumber) == null)
        {
            if (abilityNumber != 0) abilityNumber = 0;
            else abilityNumber = 1;
        }
        getMoveStats().ability = getPokedexEntry().getAbility(abilityNumber);
        if (getMoveStats().ability != null) getMoveStats().ability.init(this);

        this.scale = 1 + scaleFactor * (float) (random).nextGaussian();
        setSize(scale);
        this.setSize(1, 1);
        this.initRidable();
        shiny = random.nextInt(8196) == 0;

        particle = null;
        particleCounter = 0;
        particleIntensity = 80;

        int rand = (random).nextInt(1048576);
        if (rand == 0)
        {
            rgba[0] = 0;
        }
        else if (rand == 1)
        {
            rgba[1] = 0;
        }
        else if (rand == 2)
        {
            rgba[2] = 0;
        }

        isImmuneToFire = isType(PokeType.fire);

        if (getPokedexNb() == 201)
        {
            int num = random.nextInt(unowns.length);
            changeForme(unowns[num]);
        }
    }

    @Override
    public void setPokedexEntry(PokedexEntry newEntry)
    {
        super.setPokedexEntry(newEntry);
        setSize(scale);
    }

    @Override
    public void popFromPokecube()
    {
        super.popFromPokecube();
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
    }

    @Override
    public void specificSpawnInit()
    {
        corruptedSum = -123586;
        super.specificSpawnInit();
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String modifyTexture(String texture)
    {
        texture = this.getPokedexEntry().getTexture(texture, this.getSexe(), this.ticksExisted);
        int red = rgba[0];
        int green = rgba[1];
        int blue = rgba[2];
        if (this.getPokedexEntry().hasSpecialTextures[0] && red == 0 && green != 0 && blue != 0)
        {
            String args = texture.substring(0, texture.length() - 4);
            return args + "Ra.png";
        }
        else if (this.getPokedexEntry().hasSpecialTextures[1] && blue == 0 && green != 0 && red != 0)
        {
            String args = texture.substring(0, texture.length() - 4);
            return args + "Ga.png";
        }
        else if (this.getPokedexEntry().hasSpecialTextures[2] && blue != 0 && green == 0 && red != 0)
        {
            String args = texture.substring(0, texture.length() - 4);
            return args + "Ba.png";
        }
        if (wasShadow && this.getPokedexEntry().hasSpecialTextures[3])
        {
            String args = texture.substring(0, texture.length() - 4);
            return args + "Sh.png";
        }

        if (!shiny) // || !getPokedexEntry().hasSpecialTextures[3])
            return texture;

        String args = texture.substring(0, texture.length() - 4);
        return args + "S.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTexture()
    {
        return modifyTexture(texture);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setInteger("PokeballId", getPokecubeId());
        nbttagcompound.setFloat("scale", scale);
        nbttagcompound.setInteger("PokemobUID", uid);
        nbttagcompound.setIntArray("flavours", flavourAmounts);
        if (corruptedSum == -123586) nbttagcompound.setInteger("checkSum", computeCheckSum());
        else nbttagcompound.setInteger("checkSum", corruptedSum);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setPokecubeId(nbttagcompound.getInteger("PokeballId"));
        scale = nbttagcompound.getFloat("scale");
        uid = nbttagcompound.getInteger("PokemobUID");
        if (nbttagcompound.hasKey("flavours")) flavourAmounts = nbttagcompound.getIntArray("flavours");

        int checkSum = nbttagcompound.getInteger("checkSum");

        if (checkSum != computeCheckSum())
        {
            if (getPokemonOwner() != null && getPokemonOwner() instanceof EntityPlayer)
            {
                // ((EntityPlayer)getPokemonOwner()).addChatMessage(new
                // ChatComponentText("This Pokemon is Corrupted"));

            }
            // corruptedSum = checkSum;
        }
        else
        {
            corruptedSum = -123586;
        }

        setSize(scale);

        this.initRidable();
    }

    @Override
    public int computeCheckSum()
    {
        int red = rgba[0];
        int green = rgba[1];
        int blue = rgba[2];
        int checkSum = getExp() * getPokedexNb() + getPokecubeId() + ((int) getSize() * 1000)
                + (shiny ? 1234 : 4321) * nature.ordinal() + red * green * blue;
        String movesString = dataWatcher.getWatchableObjectString(30);
        checkSum += movesString.hashCode();
        int[] IVs = PokecubeSerializer.byteArrayAsIntArray(ivs);
        int IVEV = dataWatcher.getWatchableObjectInt(24) + dataWatcher.getWatchableObjectInt(25) + IVs[0] + IVs[1];
        checkSum += IVEV;
        // checkSum += getSexe();
        return checkSum;
    }

    @Override
    public boolean interact(EntityPlayer player)
    {
        if (corruptedSum != -123586)
        {
            player.addChatMessage(new ChatComponentText("Corrupt Pokemon"));
            return false;
        }
        return super.interact(player);
    }

    /** Checks if the entity's current position is a valid location to spawn
     * this entity. */
    @Override
    public boolean getCanSpawnHere()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(getEntityBoundingBox().minY);
        int k = MathHelper.floor_double(posZ);
        here.set(i, j, k);
        return getBlockPathWeight(worldObj, here) >= 0.0F;
    }

    @Override
    protected boolean canDespawn()
    {
        boolean canDespawn = hungerTime > Mod_Pokecube_Helper.pokemobLifeSpan;
        boolean checks = getPokemonAIState(TAMED) || this.getPokemonOwner() != null || getPokemonAIState(ANGRY)
                || getAttackTarget() != null || this.hasCustomName() || isAncient();

        if (checks) return false;

        boolean cull = Mod_Pokecube_Helper.cull
                && worldObj.getClosestPlayerToEntity(this, Mod_Pokecube_Helper.mobDespawnRadius) == null;

        return canDespawn || cull;
    }

    /** Makes the entity despawn if requirements are reached */
    @Override
    protected void despawnEntity()
    {
        if (!this.canDespawn() || this.worldObj.isRemote) return;
        SpawnEvent.Despawn evt = new SpawnEvent.Despawn(here, worldObj, this);
        MinecraftForge.EVENT_BUS.post(evt);
        if (evt.isCanceled()) return;
        this.setDead();
    }

    @Override
    public String getLivingSound()
    {
        return getPokedexEntry().getSound();
    }

    @Override
    protected String getHurtSound()
    {
        return getLivingSound();
    }

    @Override
    protected String getDeathSound()
    {
        return getLivingSound();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.15F;
    }

    /** Hopefully this will fix the mod.pokemon kill messages */
    @Override
    public String getName()
    {
        return this.getPokedexEntry().getTranslatedName();
    }

    @Override
    public void onUpdate()
    {
        here.set(posX, posY, posZ);

        if (getPokedexNb() == 201 && (this.forme == null || this.forme.isEmpty() || this.forme.equals("unown")))
        {
            int num = rand.nextInt(unowns.length);
            changeForme(unowns[num]);
        }

        // TODO
        if (corruptedSum != -123586)
        {
            // return;
        }
        super.onUpdate();

        boolean aNan = false;

        if (Double.isNaN(motionX))
        {
            motionX = 0;
            aNan = true;
        }
        if (Double.isNaN(motionY))
        {
            motionY = 0;
            aNan = true;
        }
        if (Double.isNaN(motionZ))
        {
            motionZ = 0;
            aNan = true;
        }
        if (aNan)
        {
            System.err.println(this + " had a NaN component in velocity");
            new Exception().printStackTrace();
            this.returnToPokecube();
        }
    }

    @Override
    public void onLivingUpdate()
    {
        if (corruptedSum != -123586)
        {
            // this.tasks.taskEntries.clear();
            // this.targetTasks.taskEntries.clear();
            // return;
        }
        super.onLivingUpdate();

        if (uid == -1) this.uid = PokecubeSerializer.getInstance().getNextID();

        if (worldObj.isRemote)
        {
            showLivingParticleFX();
        }

        for (int i = 0; i < flavourAmounts.length; i++)
        {
            if (flavourAmounts[i] > 0)
            {
                flavourAmounts[i]--;
            }
        }

        if (multibox) checkCollision();

        if (isAncient())
        {
            BossStatus.setBossStatus(this, true);
            BossStatus.bossName = getPokemonDisplayName();
        }
    }

    // TODO Pokeblock Particle Effects
    void showLivingParticleFX()
    {
        if (flavourAmounts.length != 5) flavourAmounts = new int[5];
        Vector3 particleLoc = here.copy();
        if (flavourAmounts[SWEET] > 0)
        {
            particle = "powder.pink";
        }
        if (flavourAmounts[BITTER] > 0)
        {
            particle = "powder.green";
        }
        if (flavourAmounts[SPICY] > 0)
        {
            particle = "powder.red";
        }
        if (flavourAmounts[DRY] > 0)
        {
            particle = "powder.blue";
        }
        if (flavourAmounts[SOUR] > 0)
        {
            particle = "powder.yellow";
        }
        if (isShadow())
        {
            particle = "portal";
            particleIntensity = 100;
        }
        else if (particle == null && getPokedexEntry().particleData != null)
        {
            particle = getPokedexEntry().particleData[0];
            particleIntensity = Integer.parseInt(getPokedexEntry().particleData[1]);
            particleIntensity = 100;
        }

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH) == 25 && calendar.get(Calendar.MONTH) == 11)
        {
            float scale = width * 2;
            Vector3 offset = Vector3.getNewVectorFromPool().set(rand.nextDouble() - 0.5, rand.nextDouble() + height / 2,
                    rand.nextDouble() - 0.5);
            offset.scalarMultBy(scale);
            particleLoc.addTo(offset);
            offset.freeVectorFromPool();
            particle = "aurora";// Merry Xmas
            particleIntensity = 90;
        }
        if (particle != null && particleCounter++ >= 100 - particleIntensity)
        {
            PokecubeMod.core.spawnParticle(particle, particleLoc, null);
            particleCounter = 0;
        }
    }

    @Override
    public float getEyeHeight()
    {
        return height * 0.8F;
    }

    @Override
    public int getVerticalFaceSpeed()
    {
        if (getPokemonAIState(SITTING))
        {
            return 20;
        }
        else
        {
            return super.getVerticalFaceSpeed();
        }
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    @Override
    public boolean isInWater()
    {
        return super.isInWater();
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt entitylightningbolt)
    {
        // do nothing
    }

    @Override
    public void setPokecubeId(int pokeballId)
    {
        pokecubeId = pokeballId;
    }

    @Override
    public int getPokecubeId()
    {
        return pokecubeId;
    }

    @Override
    public float getSize()
    {
        return scale;
    }

    @Override
    public void setSize(float size)
    {
        scale = size;
        float a = 1, b = 1, c = 1;
        PokedexEntry entry = getPokedexEntry();
        if (isAncient()) scale = 2;
        if (entry != null)
        {
            a = entry.width * scale;
            b = entry.height * scale;
            c = entry.length * scale;
        }

        this.setSize((a), (b));

        this.width = a;
        this.height = b;
        this.length = c;

        if (a > 3 || b > 3 || c > 3)
        {
            this.ignoreFrustumCheck = true;
        }

        mainBox = new Matrix3(a, b, c);
        offset.set(-a / 2, 0, -c / 2);
    }

    /** Use this for anything that does not change or need to be updated. */
    @Override
    public void writeSpawnData(ByteBuf data)
    {
        if (uid == -1)
        {
            uid = PokecubeSerializer.getInstance().getNextID();
        }
        PokecubeSerializer.getInstance().addPokemob(this);
        data.writeInt(pokedexNb);
        data.writeFloat(scale);
        data.writeInt(pokecubeId);
        data.writeInt(uid);
        byte[] rgbaBytes = { (byte) (rgba[0] - 128), (byte) (rgba[1] - 128), (byte) (rgba[2] - 128),
                (byte) (rgba[3] - 128) };
        data.writeBytes(rgbaBytes);
        data.writeLong(getUniqueID().getMostSignificantBits());
        data.writeLong(getUniqueID().getLeastSignificantBits());

        super.writeSpawnData(data);

    }

    /** Use this for anything that does not change or need to be updated. */
    @Override
    public void readSpawnData(ByteBuf data)
    {
        this.pokedexNb = data.readInt();
        scale = data.readFloat();
        pokecubeId = data.readInt();
        this.uid = data.readInt();
        this.setSize(scale);
        this.initRidable();
        for (int i = 0; i < 4; i++)
            rgba[i] = data.readByte() + 128;
        this.entityUniqueID = new UUID(data.readLong(), data.readLong());

        super.readSpawnData(data);
    }

    @Override
    public double getWeight()
    {
        return scale * scale * scale * getPokedexEntry().mass;
    }

    @Override
    public int getPokemonUID()
    {
        if (uid == -1) this.uid = PokecubeSerializer.getInstance().getNextID();

        return uid;
    }

    /** Returns true if other Entities should be prevented from moving through
     * this Entity. */
    @Override
    public boolean canBeCollidedWith()
    {
        return !getPokemonAIState(SHOULDER);
    }

    /** Returns true if this entity should push and be pushed by other entities
     * when colliding. */
    @Override
    public boolean canBePushed()
    {
        return false;
    }

    /** Whether or not the current entity is in lava */
    @Override
    public boolean isInLava()
    {
        return getPokemonAIState(INLAVA);
    }

    /** Checks if this entity is inside of an opaque block */
    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return false;
    }

    @Override
    public void setBoxes()
    {
        if (mainBox == null)
        {
            setSize(scale);
        }
        mainBox.boxMin().clear();
        mainBox.boxMax().x = getPokedexEntry().width * scale;
        mainBox.boxMax().z = getPokedexEntry().length * scale;
        mainBox.boxMax().y = getPokedexEntry().height * scale;

        mainBox.set(2, mainBox.rows[2].set(0, 0, (-rotationYaw) * Math.PI / 180));
        boxes.put("main", mainBox);
    }

    @Override
    public void setOffsets()
    {
        if (offset == null) offset = Vector3.getNewVectorFromPool();
        offset.set(-mainBox.boxMax().x / 2, 0, -mainBox.boxMax().z / 2);
        offsets.put("main", offset);
    }

    @Override
    public HashMap<String, Matrix3> getBoxes()
    {
        if (boxes.isEmpty())
        {
            boxes.put("main", mainBox);
        }
        return boxes;
    }

    @Override
    public HashMap<String, Vector3> getOffsets()
    {
        if (offsets.isEmpty())
        {
            offsets.put("main", offset);
        }
        return offsets;
    }

    @Override
    public Matrix3 bounds(Vector3 target)
    {
        return mainBox.set(2, mainBox.rows[2].set(0, 0, -rotationYaw));
    }

    @Override
    public Entity getTransformedTo()
    {
        return transformedTo;
    }

    @Override
    public void checkCollision()
    {
        // TODO see if I need anything here, of if the LogicCollision will
        // handle it.
    }

    /** Tries to moves the entity by the passed in displacement. Args: x, y,
     * z */
    @Override
    public void moveEntity(double x, double y, double z)
    {
        if (!multibox)
        {
            super.moveEntity(x, y, z);
            return;
        }
        else
        {
            double x0 = x, y0 = y, z0 = z;
            setBoxes();
            setOffsets();
            IBlockAccess world = ((PokeNavigator) getNavigator()).pathfinder.chunks;
            if (world == null)
            {
                ((PokeNavigator) getNavigator()).refreshCache();
                world = ((PokeNavigator) getNavigator()).pathfinder.chunks;
                if (world == null) return;
            }

            Vector3 diffs = Vector3.getNewVectorFromPool();
            diffs.set(x, y, z);

            for (String s : getBoxes().keySet())
            {
                diffs.set(x, y, z);
                Matrix3 box = getBoxes().get(s);
                Vector3 offset = getOffsets().get(s);
                if (offset == null) offset = Vector3.empty;
                Vector3 pos = offset.add(here);
                Vector3 v;
                diffs.set(v = box.doTileCollision(world, this, pos, diffs));
                pos.freeVectorFromPool();
                v.freeVectorFromPool();
                x = diffs.x;
                y = diffs.y;
                z = diffs.z;
            }

            x = diffs.x;
            y = diffs.y;
            z = diffs.z;

            diffs.freeVectorFromPool();

            double dy = 0;
            if (this.riddenByEntity != null && worldObj.isRemote)
            {
                // dy = riddenByEntity.height + riddenByEntity.yOffset;
            }
            double yOff = this.yOffset;
            double newY = y + (double) yOff + dy;// - (double)this.ySize

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));

            this.posX += x;// (this.boundingBox.minX + this.boundingBox.maxX) /
                           // 2.0D;
            this.posY += newY;
            this.posZ += z;// (this.boundingBox.minZ + this.boundingBox.maxZ) /
                           // 2.0D;

            // this.resetPositionToBB();//TODO see waht this does
            this.isCollidedHorizontally = x0 != x || z0 != z;
            this.isCollidedVertically = y0 != y;
            this.onGround = y0 != y && y0 <= 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            BlockPos blockpos = getPosition().down();
            Block block1 = worldObj.getBlockState(blockpos).getBlock();

            this.updateFallState(y, this.onGround, block1, blockpos);

            if (this.canTriggerWalking() && this.ridingEntity == null)
            {
                double d15 = this.posX;
                double d16 = this.posY;
                double d17 = this.posZ;

                if (block1 != Blocks.ladder)
                {
                    d16 = 0.0D;
                }

                if (block1 != null && this.onGround)
                {
                    block1.onEntityCollidedWithBlock(this.worldObj, blockpos, this);
                }

                this.distanceWalkedModified = (float) ((double) this.distanceWalkedModified
                        + (double) MathHelper.sqrt_double(d15 * d15 + d17 * d17) * 0.6D);
                this.distanceWalkedOnStepModified = (float) ((double) this.distanceWalkedOnStepModified
                        + (double) MathHelper.sqrt_double(d15 * d15 + d16 * d16 + d17 * d17) * 0.6D);

                if (this.distanceWalkedOnStepModified > (float) this.nextStepDistance
                        && block1.getMaterial() != Material.air)
                {
                    this.nextStepDistance = (int) this.distanceWalkedOnStepModified + 1;

                    if (this.isInWater() && !swims())
                    {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D
                                + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D)
                                * 0.35F;

                        if (f > 1.0F)
                        {
                            f = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), f,
                                1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    // this.playStepSound(blockpos, block1);
                }
            }

            try
            {
                this.doBlockCollisions();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport
                        .makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }
        }

    }

    /** returns the bounding box for this entity */
    @Override
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return null;// boundingBox;
    }

    @Override
    public EntityAIBase getGuardAI()
    {
        return guardAI;
    }

    @Override
    public Team getPokemobTeam()
    {
        return getTeam();
    }

    @Override
    public String getSound()
    {
        return getLivingSound();
    }
}
