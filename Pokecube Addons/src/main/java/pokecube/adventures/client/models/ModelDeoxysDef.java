package pokecube.adventures.client.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import pokecube.core.client.models.APokemobModel;

public class ModelDeoxysDef extends APokemobModel 
{
	  //fields
	    ModelRenderer Waist;
	    ModelRenderer Left_upper_leg;
	    ModelRenderer Left_lower_leg;
	    ModelRenderer Left_hip;
	    ModelRenderer Right_upper_leg;
	    ModelRenderer Right_lower_leg;
	    ModelRenderer Right_hip;
	    ModelRenderer Waist_cont;
	    ModelRenderer Torso;
	    ModelRenderer Left_Shoulder;
	    ModelRenderer Right_Shoulder;
	    ModelRenderer Neck;
	    ModelRenderer Head;
	    ModelRenderer Left_upper_pink_tentacle;
	    ModelRenderer Left_middle_pink_tentacle;
	    ModelRenderer Left_lower_pink_tentacle;
	    ModelRenderer Right_upper_pink_tentacle;
	    ModelRenderer Right_middle_pink_tentacle;
	    ModelRenderer Right_lower_pink_tentacle;
	    ModelRenderer Left_upper_blue_tentacle;
	    ModelRenderer Left_middle_blue_tentacle;
	    ModelRenderer Left_lower_blue_tentacle;
	    ModelRenderer Right_lower_blue_tentacle;
	    ModelRenderer Right_middle_blue_tentacle;
	    ModelRenderer Right_upper_blue_tentacle;
	    ModelRenderer Head_Spike;
	  
	  public ModelDeoxysDef()
	  {
	    textureWidth = 128;
	    textureHeight = 128;
	    
	      Waist = new ModelRenderer(this, 0, 57);
	      Waist.addBox(-6.5F, -5F, -5.5F, 13, 10, 11);
	      Waist.setRotationPoint(0F, -6F, 0F);
	      Waist.setTextureSize(128, 128);
	      Waist.mirror = true;
	      setRotation(Waist, 0F, 0F, 0F);
	      Left_upper_leg = new ModelRenderer(this, 0, 78);
	      Left_upper_leg.addBox(0F, -3F, -3.5F, 7, 17, 7);
	      Left_upper_leg.setRotationPoint(5F, -4F, 0F);
	      Left_upper_leg.setTextureSize(128, 128);
	      Left_upper_leg.mirror = true;
	      setRotation(Left_upper_leg, 0F, 0F, 0F);
	      Left_lower_leg = new ModelRenderer(this, 28, 78);
	      Left_lower_leg.addBox(0.5F, 11F, -2F, 6, 17, 4);
	      Left_lower_leg.setRotationPoint(5F, -4F, 0F);
	      Left_lower_leg.setTextureSize(128, 128);
	      Left_lower_leg.mirror = true;
	      setRotation(Left_lower_leg, 0F, 0F, 0F);
	      Left_hip = new ModelRenderer(this, 0, 102);
	      Left_hip.addBox(0F, -11F, -5.5F, 4, 14, 11);
	      Left_hip.setRotationPoint(5F, -4F, 0F);
	      Left_hip.setTextureSize(128, 128);
	      Left_hip.mirror = true;
	      setRotation(Left_hip, 0F, 0F, 0.3316126F);
	      Right_upper_leg = new ModelRenderer(this, 0, 78);
	      Right_upper_leg.addBox(-7F, -3F, -3.5F, 7, 17, 7);
	      Right_upper_leg.setRotationPoint(-5F, -4F, 0F);
	      Right_upper_leg.setTextureSize(128, 128);
	      Right_upper_leg.mirror = true;
	      setRotation(Right_upper_leg, 0F, 0F, 0F);
	      Right_lower_leg = new ModelRenderer(this, 28, 78);
	      Right_lower_leg.addBox(-6.5F, 11F, -2F, 6, 17, 4);
	      Right_lower_leg.setRotationPoint(-5F, -4F, 0F);
	      Right_lower_leg.setTextureSize(128, 128);
	      Right_lower_leg.mirror = true;
	      setRotation(Right_lower_leg, 0F, 0F, 0F);
	      Right_hip = new ModelRenderer(this, 0, 102);
	      Right_hip.addBox(-4F, -11F, -5.5F, 4, 14, 11);
	      Right_hip.setRotationPoint(-5F, -4F, 0F);
	      Right_hip.setTextureSize(128, 128);
	      Right_hip.mirror = true;
	      setRotation(Right_hip, 0F, 0F, -0.3316126F);
	      Waist_cont = new ModelRenderer(this, 0, 41);
	      Waist_cont.addBox(-5.5F, -11F, -4.5F, 11, 6, 9);
	      Waist_cont.setRotationPoint(0F, -6F, 0F);
	      Waist_cont.setTextureSize(128, 128);
	      Waist_cont.mirror = true;
	      setRotation(Waist_cont, 0F, 0F, 0F);
	      Torso = new ModelRenderer(this, 0, 12);
	      Torso.addBox(-7.5F, -26F, -6.5F, 15, 16, 13);
	      Torso.setRotationPoint(0F, -6F, 0F);
	      Torso.setTextureSize(128, 128);
	      Torso.mirror = true;
	      setRotation(Torso, 0F, 0F, 0F);
	      Left_Shoulder = new ModelRenderer(this, 48, 0);
	      Left_Shoulder.addBox(-0.5F, -4F, -4F, 7, 8, 8);
	      Left_Shoulder.setRotationPoint(7F, -28F, 0F);
	      Left_Shoulder.setTextureSize(128, 128);
	      Left_Shoulder.mirror = true;
	      setRotation(Left_Shoulder, 0F, 0F, -0.2617994F);
	      Right_Shoulder = new ModelRenderer(this, 48, 0);
	      Right_Shoulder.addBox(-6.5F, -4F, -4F, 7, 8, 8);
	      Right_Shoulder.setRotationPoint(-7F, -28F, 0F);
	      Right_Shoulder.setTextureSize(128, 128);
	      Right_Shoulder.mirror = true;
	      setRotation(Right_Shoulder, 0F, 0F, 0.2617994F);
	      Neck = new ModelRenderer(this, 0, 0);
	      Neck.addBox(-3.5F, -3F, -3F, 7, 3, 7);
	      Neck.setRotationPoint(0F, -31F, 0F);
	      Neck.setTextureSize(128, 128);
	      Neck.mirror = true;
	      setRotation(Neck, 0.1745329F, 0F, 0F);
	      Head = new ModelRenderer(this, 86, 0);
	      Head.addBox(-5.5F, -16F, -5F, 11, 14, 10);
	      Head.setRotationPoint(0F, -31F, 0F);
	      Head.setTextureSize(128, 128);
	      Head.mirror = true;
	      setRotation(Head, 0F, 0F, 0F);
	      Left_upper_pink_tentacle = new ModelRenderer(this, 110, 25);
	      Left_upper_pink_tentacle.addBox(4F, 1F, -3F, 2, 18, 6);
	      Left_upper_pink_tentacle.setRotationPoint(7F, -28F, 0F);
	      Left_upper_pink_tentacle.setTextureSize(128, 128);
	      Left_upper_pink_tentacle.mirror = true;
	      setRotation(Left_upper_pink_tentacle, 0F, 0F, -0.7853982F);
	      Left_middle_pink_tentacle = new ModelRenderer(this, 110, 25);
	      Left_middle_pink_tentacle.addBox(9F, 16F, -3F, 2, 18, 6);
	      Left_middle_pink_tentacle.setRotationPoint(7F, -28F, 0F);
	      Left_middle_pink_tentacle.setTextureSize(128, 128);
	      Left_middle_pink_tentacle.mirror = true;
	      setRotation(Left_middle_pink_tentacle, 0F, 0F, -0.5061455F);
	      Left_lower_pink_tentacle = new ModelRenderer(this, 110, 25);
	      Left_lower_pink_tentacle.addBox(24F, 24F, -3F, 2, 18, 6);
	      Left_lower_pink_tentacle.setRotationPoint(7F, -28F, 0F);
	      Left_lower_pink_tentacle.setTextureSize(128, 128);
	      Left_lower_pink_tentacle.mirror = true;
	      setRotation(Left_lower_pink_tentacle, 0F, 0F, 0F);
	      Right_upper_pink_tentacle = new ModelRenderer(this, 110, 25);
	      Right_upper_pink_tentacle.addBox(-6F, 1F, -3F, 2, 18, 6);
	      Right_upper_pink_tentacle.setRotationPoint(-7F, -28F, 0F);
	      Right_upper_pink_tentacle.setTextureSize(128, 128);
	      Right_upper_pink_tentacle.mirror = true;
	      setRotation(Right_upper_pink_tentacle, 0F, 0F, 0.7853982F);
	      Right_middle_pink_tentacle = new ModelRenderer(this, 110, 25);
	      Right_middle_pink_tentacle.addBox(-11F, 16F, -3F, 2, 18, 6);
	      Right_middle_pink_tentacle.setRotationPoint(-7F, -28F, 0F);
	      Right_middle_pink_tentacle.setTextureSize(128, 128);
	      Right_middle_pink_tentacle.mirror = true;
	      setRotation(Right_middle_pink_tentacle, 0F, 0F, 0.5061455F);
	      Right_lower_pink_tentacle = new ModelRenderer(this, 110, 25);
	      Right_lower_pink_tentacle.addBox(-26F, 24F, -3F, 2, 18, 6);
	      Right_lower_pink_tentacle.setRotationPoint(-7F, -28F, 0F);
	      Right_lower_pink_tentacle.setTextureSize(128, 128);
	      Right_lower_pink_tentacle.mirror = true;
	      setRotation(Right_lower_pink_tentacle, 0F, 0F, 0F);
	      Left_upper_blue_tentacle = new ModelRenderer(this, 107, 55);
	      Left_upper_blue_tentacle.addBox(2F, 4F, -3F, 2, 18, 6);
	      Left_upper_blue_tentacle.setRotationPoint(7F, -28F, 0F);
	      Left_upper_blue_tentacle.setTextureSize(128, 128);
	      Left_upper_blue_tentacle.mirror = true;
	      setRotation(Left_upper_blue_tentacle, 0F, 0F, -0.5934119F);
	      Left_middle_blue_tentacle = new ModelRenderer(this, 107, 55);
	      Left_middle_blue_tentacle.addBox(9.5F, 17.5F, -3F, 2, 18, 6);
	      Left_middle_blue_tentacle.setRotationPoint(7F, -28F, 0F);
	      Left_middle_blue_tentacle.setTextureSize(128, 128);
	      Left_middle_blue_tentacle.mirror = true;
	      setRotation(Left_middle_blue_tentacle, 0F, 0F, -0.2094395F);
	      Left_lower_blue_tentacle = new ModelRenderer(this, 107, 55);
	      Left_lower_blue_tentacle.addBox(16.5F, 31F, -4F, 2, 18, 6);
	      Left_lower_blue_tentacle.setRotationPoint(7F, -28F, 0F);
	      Left_lower_blue_tentacle.setTextureSize(128, 128);
	      Left_lower_blue_tentacle.mirror = true;
	      setRotation(Left_lower_blue_tentacle, 0F, 0F, 0F);
	      Right_lower_blue_tentacle = new ModelRenderer(this, 107, 55);
	      Right_lower_blue_tentacle.addBox(-16.5F, 31F, -3F, 2, 18, 6);
	      Right_lower_blue_tentacle.setRotationPoint(-7F, -28F, 0F);
	      Right_lower_blue_tentacle.setTextureSize(128, 128);
	      Right_lower_blue_tentacle.mirror = true;
	      setRotation(Right_lower_blue_tentacle, 0F, 0F, 0F);
	      Right_middle_blue_tentacle = new ModelRenderer(this, 107, 55);
	      Right_middle_blue_tentacle.addBox(-9.5F, 17.5F, -3F, 2, 18, 6);
	      Right_middle_blue_tentacle.setRotationPoint(-7F, -28F, 0F);
	      Right_middle_blue_tentacle.setTextureSize(128, 128);
	      Right_middle_blue_tentacle.mirror = true;
	      setRotation(Right_middle_blue_tentacle, 0F, 0F, 0.2094395F);
	      Right_upper_blue_tentacle = new ModelRenderer(this, 107, 55);
	      Right_upper_blue_tentacle.addBox(-2F, 2F, -3F, 2, 18, 6);
	      Right_upper_blue_tentacle.setRotationPoint(-7F, -28F, 0F);
	      Right_upper_blue_tentacle.setTextureSize(128, 128);
	      Right_upper_blue_tentacle.mirror = true;
	      setRotation(Right_upper_blue_tentacle, 0F, 0F, 0.5934119F);
	      Head_Spike = new ModelRenderer(this, 56, 26);
	      Head_Spike.addBox(-7.5F, -17F, -4F, 15, 17, 10);
	      Head_Spike.setRotationPoint(0F, -31F, 0F);
	      Head_Spike.setTextureSize(128, 128);
	      Head_Spike.mirror = true;
	      setRotation(Head_Spike, 0F, 0F, 0F);
	  }
	  
	  @Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	  {
	    super.render(entity, f, f1, f2, f3, f4, f5);
	    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	    Waist.render(f5);
	    Left_upper_leg.render(f5);
	    Left_lower_leg.render(f5);
	    Left_hip.render(f5);
	    Right_upper_leg.render(f5);
	    Right_lower_leg.render(f5);
	    Right_hip.render(f5);
	    Waist_cont.render(f5);
	    Torso.render(f5);
	    Left_Shoulder.render(f5);
	    Right_Shoulder.render(f5);
	    Neck.render(f5);
	    Head.render(f5);
	    Left_upper_pink_tentacle.render(f5);
	    Left_middle_pink_tentacle.render(f5);
	    Left_lower_pink_tentacle.render(f5);
	    Right_upper_pink_tentacle.render(f5);
	    Right_middle_pink_tentacle.render(f5);
	    Right_lower_pink_tentacle.render(f5);
	    Left_upper_blue_tentacle.render(f5);
	    Left_middle_blue_tentacle.render(f5);
	    Left_lower_blue_tentacle.render(f5);
	    Right_lower_blue_tentacle.render(f5);
	    Right_middle_blue_tentacle.render(f5);
	    Right_upper_blue_tentacle.render(f5);
	    Head_Spike.render(f5);
	  }
	  
	  @Override
	protected void setRotation(ModelRenderer model, float x, float y, float z)
	  {
	    model.rotateAngleX = x;
	    model.rotateAngleY = y;
	    model.rotateAngleZ = z;
	  }
	  
	  @Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e)
	  {
	    super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	  }

	}