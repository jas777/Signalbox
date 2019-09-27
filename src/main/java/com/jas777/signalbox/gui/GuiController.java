package com.jas777.signalbox.gui;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.tileentity.ControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class GuiController extends GuiScreen {

    private BlockPos pos;

    private GuiButton buttonVariantOnPlus;
    private GuiButton buttonVariantOnMinus;
    private GuiButton buttonVariantOffPlus;
    private GuiButton buttonVariantOffMinus;

    private GuiTextField channelTextField;
    private GuiTextField idTextField;

    private final ResourceLocation texture = new ResourceLocation(Signalbox.MODID, "textures/gui/controller_background.png");

    private final int BUTTON_VARIANT_ON_PLUS = 0;
    private final int BUTTON_VARIANT_ON_MINUS = 1;

    private final int BUTTON_VARIANT_OFF_PLUS = 2;
    private final int BUTTON_VARIANT_OFF_MINUS = 3;

    private final int TEXT_CHANNEL = 4;
    private final int TEXT_ID = 5;

    private final int guiWidth = 248;
    private final int guiHeight = 166;

    private int variantOnStringLength;
    private int variantOffStringLength;

    public GuiController(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        GlStateManager.pushMatrix();
        {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 1);
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            drawTexturedModalRect(centerX, centerY, 0, 0, guiWidth, guiHeight);
        }
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        {

            ControllerTileEntity tileEntity = (ControllerTileEntity) mc.world.getTileEntity(pos);
            if (tileEntity == null) mc.displayGuiScreen(null);

            fontRenderer.drawString("Signal variant [ON]:" + tileEntity.getVariantOn(), centerX + 5, centerY + 30, 0x000000);
            fontRenderer.drawString("Signal variant [OFF]:" + tileEntity.getVariantOff(), centerX + 5, centerY + 60, 0x000000);

            buttonVariantOnPlus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonVariantOnMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            buttonVariantOffPlus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonVariantOffMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            channelTextField.drawTextBox();
            idTextField.drawTextBox();

            fontRenderer.drawString("Channel", centerX + 60, centerY + 96, 0x000000);
            fontRenderer.drawString("Signal ID", centerX + 60, centerY + 116, 0x000000);
        }
        GlStateManager.popMatrix();

    }

    @Override
    public void initGui() {

        ControllerTileEntity tileEntity = (ControllerTileEntity) mc.world.getTileEntity(pos);

        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        if (tileEntity == null) mc.displayGuiScreen(null);

        variantOnStringLength = fontRenderer.getStringWidth("Signal variant [ON]:" + tileEntity.getVariantOn());
        variantOffStringLength = fontRenderer.getStringWidth("Signal variant [OFF]:" + tileEntity.getVariantOff());

        buttonList.clear();

        int halfFontHeight = fontRenderer.FONT_HEIGHT / 2;

        buttonList.add(buttonVariantOnPlus = new GuiButton(BUTTON_VARIANT_ON_PLUS, centerX + variantOnStringLength + 15, centerY + 29 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantOnMinus = new GuiButton(BUTTON_VARIANT_ON_MINUS, centerX + variantOnStringLength + 40, centerY + 29 - halfFontHeight, 20, 20, "-"));

        buttonList.add(buttonVariantOffPlus = new GuiButton(BUTTON_VARIANT_OFF_PLUS, centerX + variantOffStringLength + 15, centerY + 59 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantOffMinus = new GuiButton(BUTTON_VARIANT_OFF_MINUS, centerX + variantOffStringLength + 40, centerY + 59 - halfFontHeight, 20, 20, "-"));

        channelTextField = new GuiTextField(TEXT_CHANNEL, fontRenderer, centerX + 5, centerY + 94, 50, fontRenderer.FONT_HEIGHT + 2);
        idTextField = new GuiTextField(TEXT_ID, fontRenderer, centerX + 5, centerY + 114, 50, fontRenderer.FONT_HEIGHT + 2);

        channelTextField.setValidator(NumberUtils::isCreatable);
        idTextField.setValidator(NumberUtils::isCreatable);

        channelTextField.setText("" + tileEntity.getChannel());
        idTextField.setText("" + tileEntity.getId());

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        ControllerTileEntity tileEntity = (ControllerTileEntity) mc.world.getTileEntity(pos);

        int channel = Integer.parseInt(channelTextField.getText());
        int id = Integer.parseInt(idTextField.getText());

        switch (button.id) {
            case BUTTON_VARIANT_ON_PLUS:
                if (tileEntity.getVariantOn() >= tileEntity.getMaxVariant()) {
                    tileEntity.setVariantOn(0);
                } else {
                    tileEntity.setVariantOn(tileEntity.getVariantOn() + 1);
                }
                break;
            case BUTTON_VARIANT_ON_MINUS:
                if (tileEntity.getVariantOn() <= 0) {
                    tileEntity.setVariantOn(tileEntity.getMaxVariant());
                } else {
                    tileEntity.setVariantOn(tileEntity.getVariantOn() - 1);
                }
                break;
            case BUTTON_VARIANT_OFF_PLUS:
                if (tileEntity.getVariantOff() >= tileEntity.getMaxVariant()) {
                    tileEntity.setVariantOff(0);
                } else {
                    tileEntity.setVariantOff(tileEntity.getVariantOff() + 1);
                }
                break;
            case BUTTON_VARIANT_OFF_MINUS:
                if (tileEntity.getVariantOff() <= 0) {
                    tileEntity.setVariantOff(tileEntity.getMaxVariant());
                } else {
                    tileEntity.setVariantOff(tileEntity.getVariantOff() - 1);
                }
                break;

        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        channelTextField.textboxKeyTyped(typedChar, keyCode);
        idTextField.textboxKeyTyped(typedChar, keyCode);

        update();

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        channelTextField.mouseClicked(mouseX, mouseY, mouseButton);
        idTextField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void update() {
        ControllerTileEntity tileEntity = (ControllerTileEntity) mc.world.getTileEntity(pos);

        tileEntity.setChannel(Integer.parseInt(channelTextField.getText()));
        tileEntity.setId(Integer.parseInt(idTextField.getText()));
    }
}
