package pokecube.core.world.gen.village.buildings;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.House1;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import pokecube.core.PokecubeItems;
import pokecube.core.blocks.pc.BlockPC;
import pokecube.core.blocks.tradingTable.BlockTradingTable;
import thut.api.maths.Vector3;

public abstract class ComponentVillageBase extends House1
{
    public ComponentVillageBase()
    {
    }

    protected ComponentVillageBase(Start startPiece, int orient, Random par3Random,
            StructureBoundingBox par4StructureBoundingBox, EnumFacing par5)
    {
        super(startPiece, orient, par3Random, par4StructureBoundingBox, par5);
    }

    // Helper methods for less writing and preparation for 1.7 changes
    protected void fill(World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY,
            int maxZ, Block placeBlock, Block replaceBlock, boolean alwaysreplace)
    {
        IBlockState state1 = placeBlock.getDefaultState();
        IBlockState state2 = replaceBlock.getDefaultState();

        super.fillWithBlocks(worldObj, structBB, minX, minY, minZ, maxX, maxY, maxZ, state1, state2, alwaysreplace);
    }

    protected void fill(World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY,
            int maxZ, Block placeBlock, Block replaceBlock)
    {
        IBlockState state1 = placeBlock.getDefaultState();
        IBlockState state2 = replaceBlock.getDefaultState();

        super.fillWithBlocks(worldObj, structBB, minX, minY, minZ, maxX, maxY, maxZ, state1, state2, false);
    }

    /** current Position depends on currently set Coordinates mode, is computed
     * here */
    protected void placeBlockAtCurrentPosition(World par1World, Block par2, int par3, int par4, int par5, int par6,
            StructureBoundingBox par7StructureBoundingBox)
    {
        int j1 = this.getXWithOffset(par4, par6);
        int k1 = this.getYWithOffset(par5);
        int l1 = this.getZWithOffset(par4, par6);

        IBlockState oldState = par2.getStateFromMeta(par3);
        IBlockState newState = super.func_175847_a(oldState);

        if (par7StructureBoundingBox.isVecInside(new Vec3i(j1, k1, l1)))
        {
            BlockPos pos = new BlockPos(j1, k1, l1);
            par1World.setBlockState(pos, newState, 0);
        }
    }

    protected void fillWithMetaBlocks(World par1World, StructureBoundingBox par2StructureBoundingBox, int minX,
            int minY, int minZ, int maxX, int maxY, int maxZ, Block placeBlockID, int placeBlockMeta,
            boolean alwaysReplace)
    {
        IBlockState oldState = placeBlockID.getStateFromMeta(placeBlockMeta);
        IBlockState newState = super.func_175847_a(oldState);

        super.fillWithBlocks(par1World, par2StructureBoundingBox, minX, minY, minZ, maxX, maxY, maxZ, newState,
                newState, alwaysReplace);

        // super.fillWithMetadataBlocks(par1World, par2StructureBoundingBox,
        // minX, minY, minZ, maxX, maxY, maxZ, i2, j2, k2, l2, alwaysReplace);
        // super.fillWithMetadataBlocks(par1World, par2StructureBoundingBox,
        // minX, minY, minZ, maxX, maxY, maxZ, placeBlockID, placeBlockMeta,
        // placeBlockID, placeBlockMeta, alwaysReplace);
    }

    protected void fillRandomly(World world, StructureBoundingBox structBB, Random rnd, float chance, int minX,
            int minY, int minZ, int maxX, int maxY, int maxZ, Block placeBlock, Block replaceBlock)
    {

        IBlockState state1 = placeBlock.getDefaultState();
        IBlockState state2 = replaceBlock.getDefaultState();

        super.func_175805_a(world, structBB, rnd, chance, minX, minY, minZ, maxX, maxY, maxZ, state1, state2, false);

    }

    protected void fillDownwards(World world, Block block, int par3, int xx, int par5, int zz,
            StructureBoundingBox structBB)
    {
        fillCurrentPositionBlocksDownwards(world, block, par3, xx, par5, zz, structBB);
    }

    protected void placeBlock(World world, Block block, int metadata, int posX, int posY, int posZ,
            StructureBoundingBox structBB)
    {
        placeBlockAtCurrentPosition(world, block, metadata, posX, posY, posZ, structBB);
    }

    protected void placeAir(World world, int posX, int posY, int posZ, StructureBoundingBox structBB)
    {
        placeBlockAtCurrentPosition(world, Blocks.air, 0, posX, posY, posZ, structBB);
    }

    /** Warning: Sets the position *below* this one to air! */
    protected void placeTorch(World world, StructureBoundingBox structBB, int posX, int posY, int posZ, EnumFacing dir)
    {
        placeBlock(world, Blocks.stone, 0, posX, posY - 1, posZ, structBB);
        placeBlock(world, Blocks.torch, dir.ordinal(), posX, posY, posZ, structBB);
        placeAir(world, posX, posY - 1, posZ, structBB);
    }

    /** Does nothing if amount <= 0. Else tries to spawn entities by calling
     * getEntity(index) as often as needed.
     * 
     * @return Amount of entities spawned */
    protected int spawnEntity(World world, StructureBoundingBox structBB, int posX, int posY, int posZ, int amount)
    {
        int spawned = 0;
        for (int idx = 0; idx < amount; ++idx)
        {
            int globalX = getXWithOffset(posX, posZ);
            int globalY = getYWithOffset(posY);
            int globalZ = getZWithOffset(posX, posZ);

            if (structBB.isVecInside(new Vec3i(globalX, globalY, globalZ)))
            {
                EntityLivingBase entity = getEntity(world, idx);
                if (null != entity)
                {
                    ++spawned;
                    entity.setLocationAndAngles((double) globalX + 0.5d, (double) globalY, (double) globalZ + 0.5d,
                            0.5f, 0.0f);
                    world.spawnEntityInWorld(entity);
                }
            }
        }

        return spawned;
    }

    /** Spawn a single villager. Return that villager or <i>null</i> if spawning
     * failed, for further modification. */
    protected EntityVillager spawnVillager(World world, StructureBoundingBox structBB, int posX, int posY, int posZ)
    {
        return spawnVillager(world, structBB, posX, posY, posZ, 0);
    }

    /** Spawn a single villager. Return that villager or <i>null</i> if spawning
     * failed, for further modification. */
    protected EntityVillager spawnVillager(World world, StructureBoundingBox structBB, int posX, int posY, int posZ,
            int index)
    {
        int globalX = getXWithOffset(posX, posZ);
        int globalY = getYWithOffset(posY);
        int globalZ = getZWithOffset(posX, posZ);

        EntityVillager entityvillager = null;
        if (structBB.isVecInside(new Vec3i(globalX, globalY, globalZ)))
        {
            // entityvillager = new EntityVillager(world,
            // getVillagerType(index));
            // entityvillager.setLocationAndAngles((double)globalX + 0.5D,
            // (double)globalY, (double)globalZ + 0.5D, 0.0F, 0.0F);
            // world.spawnEntityInWorld(entityvillager); //TODO villager spawn
        }

        return entityvillager;
    }

    protected static boolean canVillageGoDeeper(StructureBoundingBox par0StructureBoundingBox)
    {
        return (par0StructureBoundingBox != null) && (par0StructureBoundingBox.minY > 10);
    }

    protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox)
    {
        int i = 0;
        int j = 0;
        for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; k++)
        {
            for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; l++)
            {
                if (par2StructureBoundingBox.isVecInside(new Vec3i(l, 64, k)))
                {
                    BlockPos pos = new BlockPos(l, j, k);
                    i += Math.max(par1World.getTopSolidOrLiquidBlock(pos).getY(),
                            par1World.provider.getAverageGroundLevel());
                    j++;
                }
            }
        }
        if (j == 0) { return -1; }
        return i / j;
    }

    /** Overwrites air and liquids from selected position downwards, stops at
     * hitting anything else. */
    protected void fillCurrentPositionBlocksDownwards(World par1World, Block par2, int par3, int par4, int par5,
            int par6, StructureBoundingBox par7StructureBoundingBox)
    {
        int j1 = this.getXWithOffset(par4, par6);
        int k1 = this.getYWithOffset(par5);
        int l1 = this.getZWithOffset(par4, par6);

        IBlockState oldState = par2.getStateFromMeta(par6);
        IBlockState newState = super.func_175847_a(oldState);

        BlockPos pos = new BlockPos(j1, k1, l1);
        // if (par7StructuregetEntityBoundingBox().isVecInside(j1, k1, l1))
        {
            while ((!par1World.getBlockState(pos).getBlock().isNormalCube() || par1World.isAirBlock(pos)
                    || par1World.getBlockState(pos).getBlock().getMaterial().isLiquid()
                    || par1World.getBlockState(pos).getBlock().isLeaves(par1World, pos)) && k1 > 1)
            {
                par1World.setBlockState(pos, newState, 0);
                --k1;
                pos = new BlockPos(j1, k1, l1);
            }
        }
    }

    /** Deletes all continuous blocks from selected position upwards. Stops at
     * hitting air. */
    protected void clearCurrentPositionBlocksUpwards(World par1World, int par2, int par3, int par4,
            StructureBoundingBox par5StructureBoundingBox)
    {
        int l = this.getXWithOffset(par2, par4);
        int i1 = this.getYWithOffset(par3);
        int j1 = this.getZWithOffset(par2, par4);

        BlockPos pos = new BlockPos(l, i1, j1);

        // if (par5StructuregetEntityBoundingBox().isVecInside(l, i1, j1))
        {
            while (!par1World.isAirBlock(pos) && i1 < 255)
            {
                par1World.setBlockToAir(pos);
                ++i1;
            }
        }
    }

    Vector3 toAbsolute(int x, int y, int z)
    {
        int j1 = this.getXWithOffset(x, z);
        int k1 = this.getYWithOffset(y);
        int l1 = this.getZWithOffset(x, z);

        return Vector3.getNewVectorFromPool().set(j1, k1, l1);
    }

    int getDir()
    {
        int ret = 0;

        if (coordBaseMode == null) return ret;

        if (coordBaseMode.ordinal() == 0)
        {
            ret |= 2;
        }
        else if (coordBaseMode.ordinal() == 1)
        {
            ret |= 5;
        }
        else if (coordBaseMode.ordinal() == 2)
        {
            ret |= 3;
        }
        else if (coordBaseMode.ordinal() == 3)
        {
            ret |= 4;
        }
        return ret;
    }

    /** Returns the direction-shifted metadata for blocks that require
     * orientation, e.g. doors, stairs, ladders. Parameters: block ID, original
     * metadata */
    protected int getMetaWithOffset(Block par1, int par2)
    {
        if (par1 == PokecubeItems.getBlock("tradingtable") || par1 == PokecubeItems.getBlock("pc"))
        {
            if (par1 == PokecubeItems.getBlock("pc"))
            {
                IBlockState state = par1.getStateFromMeta(par2).withProperty(BlockPC.FACING,
                        coordBaseMode.getOpposite());
                par2 = par1.getMetaFromState(state);
                return par2;
            }
            if (par1 == PokecubeItems.getBlock("tradingtable"))
            {
                IBlockState state = par1.getStateFromMeta(par2);
                return par1.getMetaFromState(state.withProperty(BlockTradingTable.FACING, coordBaseMode));
            }
        }
        int ret = par2;
        return ret;
    }

    /** Override this method for custom (non-villager) entity spawning.
     * 
     * @param world
     *            the World to spawn the entity in
     * @param index
     *            the index number of the entity
     * @return the EntityLivingBase to spawn, or null if we don't spawn any */
    protected EntityLivingBase getEntity(World world, int index)
    {
        return null;
    }

}
