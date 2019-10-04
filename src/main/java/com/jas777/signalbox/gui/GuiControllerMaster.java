package com.jas777.signalbox.gui;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.channel.ChannelDispatcher;
import com.jas777.signalbox.network.signalpacket.PacketDispatcher;
import com.jas777.signalbox.network.signalpacket.PacketGuiReturn;
import com.jas777.signalbox.tileentity.ControllerMasterTileEntity;
import com.jas777.signalbox.tileentity.DisplayTileEntity;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.io.IOException;

public class GuiControllerMaster extends GuiScreen {

    private ControllerMasterTileEntity tile;

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

    private boolean displayOnFrequency = false;

    private SignalTileEntity signal;
    private ChannelDispatcher dispatcher;

    public GuiControllerMaster(ControllerMasterTileEntity tile) {
        this.tile = tile;

        this.dispatcher = Signalbox.instance.getChannelDispatcher();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        displayOnFrequency = Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), Integer.parseInt(channelTextField.getText()), Integer.parseInt(idTextField.getText())) instanceof DisplayTileEntity;

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

            if (tile == null) mc.displayGuiScreen(null);

            if (signal != null) {
                fontRenderer.drawString("Signal mode: " + signal.getMode().getName(), centerX + 5, centerY + 15, 0x000000);

                fontRenderer.drawString("Signal variant [ON]: " + tile.getVariantOn(), centerX + 5, centerY + 40, 0x000000);
                fontRenderer.drawString("Signal variant [OFF]: " + tile.getVariantOff(), centerX + 5, centerY + 70, 0x000000);
            }

            buttonVariantOnPlus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonVariantOnMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            buttonVariantOffPlus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonVariantOffMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            if (Integer.parseInt(channelTextField.getText()) <= 0 || displayOnFrequency) {
                channelTextField.setTextColor(Color.RED.getRGB());
            } else {
                channelTextField.setTextColor(Color.WHITE.getRGB());
            }

            if (Integer.parseInt(idTextField.getText()) <= 0 || displayOnFrequency) {
                idTextField.setTextColor(Color.RED.getRGB());
            } else {
                idTextField.setTextColor(Color.WHITE.getRGB());
            }

            if (displayOnFrequency) {
                fontRenderer.drawString("Display on frequency!", centerX + 70, centerY + 116, Color.RED.getRGB());
            }

            channelTextField.drawTextBox();
            idTextField.drawTextBox();

            fontRenderer.drawString("Channel", centerX + 60, centerY + 106, 0x000000);
            fontRenderer.drawString("Signal ID", centerX + 60, centerY + 126, 0x000000);

            GlStateManager.translate(12, 12, 0);
        }
        GlStateManager.popMatrix();

    }

    @Override
    public void initGui() {

        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        if (tile == null) mc.displayGuiScreen(null);

        variantOnStringLength = fontRenderer.getStringWidth("Signal variant [ON]:" + tile.getVariantOn());
        variantOffStringLength = fontRenderer.getStringWidth("Signal variant [OFF]:" + tile.getVariantOff());

        if (tile.getVariantOn() > tile.getMaxVariant() || tile.getVariantOff() > tile.getMaxVariant()) {
            tile.setVariantOn(0);
            tile.setVariantOff(0);
        }

        buttonList.clear();

        int halfFontHeight = fontRenderer.FONT_HEIGHT / 2;

        buttonList.add(buttonVariantOnPlus = new GuiButton(BUTTON_VARIANT_ON_PLUS, centerX + variantOnStringLength + 15, centerY + 39 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantOnMinus = new GuiButton(BUTTON_VARIANT_ON_MINUS, centerX + variantOnStringLength + 40, centerY + 39 - halfFontHeight, 20, 20, "-"));

        buttonList.add(buttonVariantOffPlus = new GuiButton(BUTTON_VARIANT_OFF_PLUS, centerX + variantOffStringLength + 15, centerY + 69 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantOffMinus = new GuiButton(BUTTON_VARIANT_OFF_MINUS, centerX + variantOffStringLength + 40, centerY + 69 - halfFontHeight, 20, 20, "-"));

        channelTextField = new GuiTextField(TEXT_CHANNEL, fontRenderer, centerX + 5, centerY + 104, 50, fontRenderer.FONT_HEIGHT + 2);
        idTextField = new GuiTextField(TEXT_ID, fontRenderer, centerX + 5, centerY + 124, 50, fontRenderer.FONT_HEIGHT + 2);

        channelTextField.setValidator(NumberUtils::isCreatable);
        idTextField.setValidator(NumberUtils::isCreatable);

        channelTextField.setText("" + tile.getChannel());
        idTextField.setText("" + tile.getId());

        displayOnFrequency = !(Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), tile.getChannel(), tile.getId()) instanceof SignalTileEntity);
        if (!displayOnFrequency) {
            signal = (SignalTileEntity) Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), tile.getChannel(), tile.getId());
        }

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (displayOnFrequency) return;

        if (tile.getVariantOn() > tile.getMaxVariant() || tile.getVariantOff() > tile.getMaxVariant()) {
            tile.setVariantOn(0);
            tile.setVariantOff(0);
        }

        switch (button.id) {
            case BUTTON_VARIANT_ON_PLUS:
                if (tile.getVariantOn() >= tile.getMaxVariant()) {
                    tile.setVariantOn(0);
                } else {
                    tile.setVariantOn(tile.getVariantOn() + 1);
                }
                break;
            case BUTTON_VARIANT_ON_MINUS:
                if (tile.getVariantOn() <= 0) {
                    tile.setVariantOn(tile.getMaxVariant());
                } else {
                    tile.setVariantOn(tile.getVariantOn() - 1);
                }
                break;
            case BUTTON_VARIANT_OFF_PLUS:
                if (tile.getVariantOff() >= tile.getMaxVariant()) {
                    tile.setVariantOff(0);
                } else {
                    tile.setVariantOff(tile.getVariantOff() + 1);
                }
                break;
            case BUTTON_VARIANT_OFF_MINUS:
                if (tile.getVariantOff() <= 0) {
                    tile.setVariantOff(tile.getMaxVariant());
                } else {
                    tile.setVariantOff(tile.getVariantOff() - 1);
                }
                break;

        }

        if (tile.getWorld().isRemote) {
            tile.setChannel(Integer.parseInt(channelTextField.getText()));
            tile.setId(Integer.parseInt(idTextField.getText()));
            PacketGuiReturn packet = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(packet);
            tile.markDirty();
        }

        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        if (tile.getWorld().isRemote) {
            if (!(Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), Integer.parseInt(channelTextField.getText()), Integer.parseInt(idTextField.getText())) instanceof SignalTileEntity)) {
                tile.setChannel(0);
                tile.setId(0);
            };
            if (!displayOnFrequency) {
                if (Integer.parseInt(channelTextField.getText()) <= 0) return;
                tile.setChannel(Integer.parseInt(channelTextField.getText()));
                tile.setId(Integer.parseInt(idTextField.getText()));
                PacketGuiReturn packet = new PacketGuiReturn(tile);
                PacketDispatcher.sendToServer(packet);
                tile.markDirty();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        channelTextField.textboxKeyTyped(typedChar, keyCode);
        idTextField.textboxKeyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);

        displayOnFrequency = !(Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), Integer.parseInt(channelTextField.getText()), Integer.parseInt(idTextField.getText())) instanceof SignalTileEntity);

        if (!displayOnFrequency) {
            tile.setChannel(Integer.parseInt(channelTextField.getText()));
            tile.setId(Integer.parseInt(idTextField.getText()));
            tile.markDirty();
            signal = (SignalTileEntity) Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), tile.getChannel(), tile.getId());
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        channelTextField.mouseClicked(mouseX, mouseY, mouseButton);
        idTextField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}