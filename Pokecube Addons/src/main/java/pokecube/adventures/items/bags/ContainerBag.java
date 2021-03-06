package pokecube.adventures.items.bags;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pokecube.adventures.handlers.PASaveHandler;
import pokecube.core.PokecubeItems;
import pokecube.core.mod_Pokecube;
import pokecube.core.interfaces.IPokemobUseable;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.core.network.PokecubePacketHandler;
import pokecube.core.network.PokecubePacketHandler.PokecubeServerPacket;

public class ContainerBag extends Container {


	public final InventoryBag inv;
	public final InventoryPlayer invPlayer;
	public static int STACKLIMIT = 64;
	public static int yOffset;
	public static int xOffset;
	public boolean release = false;
	
	public boolean[] toRelease = new boolean[54];
	
	int releaseNum = 0;
	
	public ContainerBag(InventoryPlayer ivplay)
	{
		super();
		xOffset = 0;
		yOffset = 0;
		inv = InventoryBag.getBag(ivplay.player.getUniqueID().toString());
		invPlayer = ivplay;
		
		bindInventories();
	}
	
	protected void bindInventories()
	{
	//	System.out.println("bind");
		clearSlots();
		bindPlayerInventory();
		bindPCInventory();
	}
	
	protected void bindPCInventory()
	{
		for (int i = 0; i < 6; i++) 
		{
            for (int j = 0; j < 9; j++) 
            {
                 addSlotToContainer(new SlotBag(inv, +j + i * 9, 8 + j * 18 + xOffset, 18 + i * 18 + yOffset));
            }
		}
		//int k = 0;
		for(Object o:inventorySlots)
		{
			if(o instanceof Slot)
			{
				((Slot)o).onSlotChanged();
			}
		}
	}
	
	 protected void bindPlayerInventory() 
	 {
		 int offset = 64 + yOffset;

         for (int i = 0; i < 9; i++) 
         {
                 addSlotToContainer(new Slot(invPlayer,  i, 8 + i * 18 + xOffset, 142+offset));
         }
         for (int i = 0; i < 3; i++) 
         {
                 for (int j = 0; j < 9; j++) 
                 {
                         addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9,
                                         8 + j * 18  + xOffset, 84 + i * 18+offset));
                 }
         }
	 }
	
    /**
     * 
     */
    protected Slot addSlotToContainer(Slot par1Slot)
    {
        par1Slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(par1Slot);
        this.inventoryItemStacks.add(inv.getStackInSlot(par1Slot.getSlotIndex()));
        return par1Slot;
    }
    
    protected void clearSlots()
    {
    	this.inventorySlots.clear();
    }
	
	@Override
	 public void onContainerClosed(EntityPlayer player)
	 {
		PASaveHandler.getInstance().saveBag();
		 super.onContainerClosed(player);
	 }
	
	public void updateInventoryPages(int dir, InventoryPlayer invent)
	{
		if(!(mod_Pokecube.isOnClientSide()&&FMLClientHandler.instance().getServer()!=null))
		{
			inv.setPage((inv.getPage()==0)&&(dir==-1)?InventoryBag.PAGECOUNT-1:(inv.getPage()+dir)%InventoryBag.PAGECOUNT);
		}
		
		bindInventories();
	}
	
	public void gotoInventoryPage(int page)
	{
		if(page - 1 == inv.getPage()) return;
		
		inv.setPage(page-1);

		if(mod_Pokecube.isOnClientSide())
		{
			boolean toReopen = true;
			if(FMLClientHandler.instance().getServer()==null)
			{
				toReopen = inv.opened[inv.getPage()];
			}
			if(toReopen)
			{
				inv.opened[inv.getPage()] = true;
				PokecubeServerPacket packet = PokecubePacketHandler.makeServerPacket((byte) 6, new byte[] {10});
		        PokecubePacketHandler.sendToServer(packet);
				return;
			}
		}
		bindInventories();
	}
	
	public void changeName(String name)
	{
		inv.boxes[inv.getPage()] = name;
		
		if(mod_Pokecube.isOnClientSide())
		{
			byte[] string = name.getBytes();
			byte[] message = new byte[string.length+2];
			
			message[0] = 11;
			message[1] = (byte) string.length;
			for(int i = 2; i<message.length; i++)
			{
				message[i] = string[i-2];
			}
	        PokecubeServerPacket packet = PokecubePacketHandler.makeServerPacket((byte) 6, message);
	        PokecubePacketHandler.sendToServer(packet);
			return;
		}
	}
	 
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
            ItemStack stack = null;
            return stack;
    }
    
    @Override
    public ItemStack slotClick(int i, int j, int flag,
            EntityPlayer entityplayer)
    {
//    	if(true)
//    		return super.slotClick(i, j, flag, entityplayer);
//    	
    	if (i < 0)
    		return null;
		if(mod_Pokecube.isOnClientSide()&&FMLClientHandler.instance().getServer()!=null)
		{
			return clientSlotClick(i, j, flag, entityplayer);
		}
        if (flag != 0 && flag != 5)
        {
            ItemStack itemstack = null;
            Slot slot = (Slot) inventorySlots.get(i);

            if (slot != null && slot.getHasStack())
            {
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();
                if(!ContainerBag.isItemValid(itemstack1)) return null;

                if (i > 35)
                {
                    if (!mergeItemStack(itemstack1, 0, 36, false))
                    {
                        return null;
                    }
                }
                else
                {
                    if (!mergeItemStack(itemstack1, 36, 89, false))
                    {
                        return null;
                    }
                }

                if (itemstack1.stackSize == 0)
                {
                    slot.putStack(null);
                }
                else
                {
                    slot.onSlotChanged();
                }

                if (itemstack1.stackSize != itemstack.stackSize)
                {
					slot.onPickupFromSlot(entityplayer, itemstack1);
                }
                else
                {
                    return null;
                }
            }
            
            //release = gpc.getReleaseState();

            if (itemstack != null && isItemValid(itemstack))
            {
                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return super.slotClick(i, j, flag, entityplayer);
        }
    }
	
    
    public ItemStack clientSlotClick(int i, int j, int flag,
            EntityPlayer player)
    {
    	ItemStack itemstack = invPlayer.getItemStack();
        Slot slot = (Slot) inventorySlots.get(i);
        ItemStack inSlot = slot.getStack();
    	if(flag == 0 || flag == 5)
    	{
	    	invPlayer.setItemStack(inSlot!=null?inSlot.copy():null);
	    	inSlot = itemstack;
	    	return inSlot;
    	}
        
    	return  inSlot;
    }
    
    /**
     * Returns true if the item is a filled pokecube.
     *
     * @param itemstack the itemstack to test
     * @return true if the id is a filled pokecube one, false otherwise
     */
    public static boolean isItemValid(ItemStack itemstack)
    {
    //	System.out.println(ConfigHandler.ONLYPOKECUBES);
    	
    	boolean valid = PokecubeItems.isValidHeldItem(itemstack) || itemstack.getItem() instanceof IPokemobUseable;
    	boolean cube = PokecubeItems.getEmptyCube(itemstack) == itemstack.getItem() && !PokecubeManager.isFilled(itemstack);
    	
        return valid || cube;
    }
    
    public Slot getSlot(int par1)
    {
    	return (Slot)this.inventorySlots.get(par1);
    }
    
    /**
     * args: slotID, itemStack to put in slot
     */
    public void putStackInSlot(int par1, ItemStack par2ItemStack)
    {
    	this.getSlot(par1).putStack(par2ItemStack);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * places itemstacks in first x slots, x being aitemstack.lenght
     */
    public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack)
    {
        for (int i = 0; i < par1ArrayOfItemStack.length; ++i)
        {
        	if(this.getSlot(i)!=null)
            this.getSlot(i).putStack(par1ArrayOfItemStack[i]);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public String getPage()
    {
    	return inv.boxes[inv.getPage()];
    }
    
    @SideOnly(Side.CLIENT)
    public String getPageNb()
    {
    	return Integer.toString(inv.getPage()+1);
    }
    
}
