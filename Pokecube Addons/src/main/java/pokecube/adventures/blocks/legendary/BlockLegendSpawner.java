package pokecube.adventures.blocks.legendary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pokecube.adventures.LegendaryConditions;
import pokecube.core.mod_Pokecube;
import pokecube.core.blocks.berries.IMetaBlock;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.stats.ISpecialSpawnCondition;
import pokecube.core.interfaces.IPokemob;
import thut.api.maths.Vector3;

public class BlockLegendSpawner extends Block implements IMetaBlock
{
    public PropertyInteger   TYPE;
    public ArrayList<String> types = new ArrayList<String>();

    public BlockLegendSpawner()
    {
        super(Material.rock);
        this.setCreativeTab(mod_Pokecube.creativeTabPokecubeBlocks);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Integer.valueOf(0)));
    }

    public void registerType(String pokemon)
    {
        if (types.size() > 15)
            throw new ArrayIndexOutOfBoundsException("Cannot add more legends to this block, please make another");
        types.add(pokemon);
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player,
            EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) { return true; }

        int meta = getMetaFromState(state);

        PokedexEntry entry = Database.getEntry(types.get(meta));
        if (entry == null) return false;

        int pokedexNb = entry.getPokedexNb();
        ISpecialSpawnCondition condition = ISpecialSpawnCondition.spawnMap.get(pokedexNb);
        if (condition != null)
        {
            Vector3 location = Vector3.getNewVectorFromPool().set(pos);
            if (condition.canSpawn(player, location))
            {
                EntityLiving entity = (EntityLiving) mod_Pokecube.core.createEntityByPokedexNb(pokedexNb, worldIn);
                entity.setHealth(entity.getMaxHealth());
                location.add(0, 1, 0).moveEntity(entity);
                condition.onSpawn((IPokemob) entity);

                if (((IPokemob) entity).getExp() < 100)
                {
                    ((IPokemob) entity).setExp(6000, true, true);
                }
                worldIn.spawnEntityInWorld(entity);
                location.freeVectorFromPool();
                return true;
            }
            location.freeVectorFromPool();
        }
        return false;
    }

    @Override
    /** Convert the given metadata into a BlockState for this Block */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, Integer.valueOf(meta));
    }

    @Override
    /** Convert the BlockState into the correct metadata value */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer) state.getValue(TYPE)).intValue();
    }

    @Override
    protected BlockState createBlockState()
    {
        if (TYPE == null)
        {
            if (LegendaryConditions.spawner1 == null)
                TYPE = PropertyInteger.create("type", 0, LegendaryConditions.SPAWNER1COUNT - 1);
        }
        return new BlockState(this, new IProperty[] { TYPE });
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "tile." + types.get(stack.getItemDamage());
    }

    @Override
    /** returns a list of blocks with the same ID, but different meta (eg: wood
     * returns 4 blocks) */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (int j = 0; j < types.size(); ++j)
        {
            list.add(new ItemStack(itemIn, 1, j));
        }
    }
}
