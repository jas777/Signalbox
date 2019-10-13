package com.jas777.signalbox.tileentity.tesr;

import com.jas777.signalbox.blocks.BaseDisplay;
import com.jas777.signalbox.tileentity.DisplayTileEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class DisplayTileEntityRenderer extends TileEntitySpecialRenderer<DisplayTileEntity> {

    @Override
    public void render(DisplayTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        GlStateManager.pushMatrix();

        BaseDisplay display = te.getDisplay();

        float displayMiddleHorizontal = (float) (display.getDisplayX1() + (display.getDisplayX2() - display.getDisplayX1()) / 2);
        float displayMiddleVertical = (float) (display.getDisplayY1() + (display.getDisplayY2() - display.getDisplayY1()) / 2);

        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

        EnumFacing facing = EnumFacing.getFront(5 - (te.getBlockMetadata() & 3));
        float f1 = facing.getHorizontalAngle();
        GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);

        GlStateManager.translate(displayMiddleHorizontal - 0.5F, displayMiddleVertical - 0.5F, (0.5F - display.getDisplayZ()) + 0.01);

        GlStateManager.enableRescaleNormal();

        FontRenderer fontRenderer = this.getFontRenderer();
        GlStateManager.scale(0.025516667F, -0.025516667F, 0.025516667F);
        GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
        GlStateManager.depthMask(false);

        String s = te.getDisplayedText();

        if (te.isActive()) {
            fontRenderer.drawString(s, -fontRenderer.getStringWidth(s) / 2, -fontRenderer.FONT_HEIGHT / 2, te.getDisplay().getColor());
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

    }
}
