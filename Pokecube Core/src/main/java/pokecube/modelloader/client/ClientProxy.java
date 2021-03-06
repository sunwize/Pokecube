/**
 *
 */
package pokecube.modelloader.client;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.modelloader.CommonProxy;
import pokecube.modelloader.ModPokecubeML;
import pokecube.modelloader.client.custom.RenderAdvancedPokemobModel;
import pokecube.modelloader.client.custom.animation.AnimationLoader;
import pokecube.modelloader.client.custom.animation.AnimationLoader.Model;
import pokecube.modelloader.client.custom.obj.BakedRenderer;
import pokecube.modelloader.client.gui.GuiAnimate;
import pokecube.modelloader.client.tabula.TabulaPackLoader;
import pokecube.modelloader.items.ItemModelReloader;

/** @author Manchou */
public class ClientProxy extends CommonProxy
{

    public static HashMap<String, Object>            modelProviders = new HashMap<String, Object>();
    public static HashMap<String, ArrayList<String>> modModels      = new HashMap<String, ArrayList<String>>();

    @Override
    public void registerModelProvider(String modid, Object mod)
    {
        super.registerModelProvider(modid, mod);
        if (!modelProviders.containsKey(modid)) modelProviders.put(modid, mod);
    }

    @Override
    public void preInit()
    {
        super.preInit();
        OBJLoader.instance.addDomain(ModPokecubeML.ID);
        B3DLoader.instance.addDomain(ModPokecubeML.ID);
    }

    @Override
    public void postInit()
    {
        BakedRenderer.init();
        AnimationLoader.loaded = true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerRenderInformation()
    {
        modelProviders.put(ModPokecubeML.ID, ModPokecubeML.instance);
        populateModels();
        for (String modid : modelProviders.keySet())
        {
            Object mod = modelProviders.get(modid);
            if (modModels.containsKey(modid))
            {
                for (String s : modModels.get(modid))
                {
                    if (AnimationLoader.models.containsKey(s))
                    {
                        Model model = AnimationLoader.models.get(s);
                        if (model.model.getResourcePath().contains(".obj")
                                || model.model.getResourcePath().contains(".b3d"))
                        {
                            RenderAdvancedPokemobModel render;
                            PokecubeMod.getProxy().registerPokemobRenderer(s,
                                    render = new RenderAdvancedPokemobModel(s, 1), mod);
                            AnimationLoader.getModel(s);
                            render.model2Loc = model.model.toString();
                        }
                        else
                        {
                            PokecubeMod.getProxy().registerPokemobRenderer(s, new RenderAdvancedPokemobModel(s, 1),
                                    mod);
                        }
                    }
                }
            }
        }
        for (PokedexEntry entry : TabulaPackLoader.modelMap.keySet())
        {
            if (entry == null) continue;

            Object mod = null;
            for (String modid : modelProviders.keySet())
            {
                if (modid.equalsIgnoreCase(entry.getModId()))
                {
                    mod = modelProviders.get(modid);
                    break;
                }
            }
            if (mod != null) PokecubeMod.getProxy().registerPokemobRenderer(entry.getName(),
                    new RenderAdvancedPokemobModel(entry.getName(), 1), mod);
        }
    }

    @Override
    public void init()
    {
        super.init();
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemModelReloader.instance, 0,
                new ModelResourceLocation("pokecube_ml:modelreloader", "inventory"));
    }

    public static void populateModels()
    {
        System.out.println("Looking for models");
        TabulaPackLoader.clear();
        for (String mod : modelProviders.keySet())
        {
            for (PokedexEntry p : Database.allFormes)
            {
                try
                {
                    ResourceLocation tex = new ResourceLocation(mod, AnimationLoader.MODELPATH+p.getName()+".xml");
                    IResource res = Minecraft.getMinecraft().getResourceManager().getResource(tex);
                    res.getInputStream().close();
                    ArrayList<String> models = modModels.get(mod);
                    if (models == null)
                    {
                        modModels.put(mod, models = new ArrayList<String>());
                    }
                    if (!models.contains(p.getName())) models.add(p.getName());
                }
                catch (Exception e)
                {
                    try
                    {
                        ResourceLocation tex = new ResourceLocation(mod, AnimationLoader.MODELPATH+p.getName()+".tbl");
                        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(tex);
                        res.getInputStream().close();
                        ArrayList<String> models = modModels.get(mod);
                        if (models == null)
                        {
                            modModels.put(mod, models = new ArrayList<String>());
                        }
                        if (!models.contains(p.getName())) models.add(p.getName());
                    }
                    catch (Exception e1)
                    {
                        try
                        {
                            ResourceLocation tex = new ResourceLocation(mod, AnimationLoader.MODELPATH+p.getName()+".x3d");
                            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(tex);
                            res.getInputStream().close();
                            ArrayList<String> models = modModels.get(mod);
                            if (models == null)
                            {
                                modModels.put(mod, models = new ArrayList<String>());
                            }
                            if (!models.contains(p.getName())) models.add(p.getName());
                        }
                        catch (Exception e2)
                        {

                        }
                    }
                }
            }
            if (modModels.containsKey(mod))
            {
                for (String s : modModels.get(mod))
                {
                    if (!AnimationLoader.initModel(mod + ":" + AnimationLoader.MODELPATH + s))
                    {
                        TabulaPackLoader.loadModel(mod + ":" + AnimationLoader.MODELPATH + s);
                    }
                }
            }
        }
        TabulaPackLoader.postProcess();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return new GuiAnimate();
    }
}
