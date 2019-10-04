package com.jas777.signalbox.gui;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.channel.ChannelDispatcher;
import com.jas777.signalbox.network.signalpacket.PacketDispatcher;
import com.jas777.signalbox.network.signalpacket.PacketGuiReturn;
import com.jas777.signalbox.tileentity.ControllerDisplayTileEntity;
import com.jas777.signalbox.tileentity.DisplayTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.io.IOException;

public class GuiControllerDisplay extends GuiScreen {

    private ControllerDisplayTileEntity tile;

    private GuiButton buttonSpeedLimitPlus;
    private GuiButton buttonSpeedLimitMinus;

    private GuiTextField channelTextField;
    private GuiTextField idTextField;

    private final ResourceLocation texture = new ResourceLocation(Signalbox.MODID, "textures/gui/controller_background.png");

    private final int BUTTON_VARIANT_PLUS = 0;
    private final int BUTTON_VARIANT_MINUS = 1;

    private final int TEXT_CHANNEL = 4;
    private final int TEXT_ID = 5;

    private final int guiWidth = 248;
    private final int guiHeight = 166;

    private int speedLimitStringLength;

    private DisplayTileEntity display;
    private ChannelDispatcher dispatcher;

    public GuiControllerDisplay(ControllerDisplayTileEntity tile) {
        this.tile = tile;

        this.dispatcher = Signalbox.instance.getChannelDispatcher();
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

            if (tile == null) mc.displayGuiScreen(null);

            fontRenderer.drawString("Speed limit: " + tile.getSpeedLimit() * 10 + "km/h", centerX + 5, centerY + 30, 0x000000);

            buttonSpeedLimitPlus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonSpeedLimitMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            if (Integer.parseInt(channelTextField.getText()) <= 0) {
                channelTextField.setTextColor(Color.RED.getRGB());
            } else {
                channelTextField.setTextColor(Color.WHITE.getRGB());
            }

            if (Integer.parseInt(idTextField.getText()) <= 0) {
                idTextField.setTextColor(Color.RED.getRGB());
            } else {
                idTextField.setTextColor(Color.WHITE.getRGB());
            }

            channelTextField.drawTextBox();
            idTextField.drawTextBox();

            fontRenderer.drawString("Channel", centerX + 60, centerY + 96, 0x000000);
            fontRenderer.drawString("Signal ID", centerX + 60, centerY + 116, 0x000000);

            GlStateManager.translate(12, 12, 0);
        }
        GlStateManager.popMatrix();

    }

    @Override
    public void initGui() {

        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        if (tile == null) mc.displayGuiScreen(null);

        speedLimitStringLength = fontRenderer.getStringWidth("Speed limit:" + tile.getSpeedLimit() * 10 + "km/h");

        if (tile.getSpeedLimit() > tile.getMaxVariant() || tile.getSpeedLimit() > tile.getMaxVariant()) {
            tile.setSpeedLimit(1);
        }

        buttonList.clear();

        int halfFontHeight = fontRenderer.FONT_HEIGHT / 2;

        buttonList.add(buttonSpeedLimitPlus = new GuiButton(BUTTON_VARIANT_PLUS, centerX + speedLimitStringLength + 15, centerY + 29 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonSpeedLimitMinus = new GuiButton(BUTTON_VARIANT_MINUS, centerX + speedLimitStringLength + 40, centerY + 29 - halfFontHeight, 20, 20, "-"));

        channelTextField = new GuiTextField(TEXT_CHANNEL, fontRenderer, centerX + 5, centerY + 94, 50, fontRenderer.FONT_HEIGHT + 2);
        idTextField = new GuiTextField(TEXT_ID, fontRenderer, centerX + 5, centerY + 114, 50, fontRenderer.FONT_HEIGHT + 2);

        channelTextField.setValidator(NumberUtils::isCreatable);
        idTextField.setValidator(NumberUtils::isCreatable);

        channelTextField.setText("" + tile.getChannel());
        idTextField.setText("" + tile.getId());

        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (tile.getSpeedLimit() > tile.getMaxVariant()) {
            tile.setSpeedLimit(1);
        }

        switch (button.id) {
            case BUTTON_VARIANT_PLUS:
                if (tile.getSpeedLimit() >= tile.getMaxVariant()) {
                    tile.setSpeedLimit(1);
                } else {
                    tile.setSpeedLimit(tile.getSpeedLimit() + 1);
                }
                break;
            case BUTTON_VARIANT_MINUS:
                if (tile.getSpeedLimit() <= 0) {
                    tile.setSpeedLimit(tile.getMaxVariant());
                } else {
                    tile.setSpeedLimit(tile.getSpeedLimit() - 1);
                }
                break;

        }

        if (tile.getWorld().isRemote) {
            tile.setChannel(Integer.parseInt(channelTextField.getText()));
            tile.setId(Integer.parseInt(idTextField.getText()));
            PacketGuiReturn packet = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(packet);
        }

        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        if (tile.getWorld().isRemote) {
            if (!(Signalbox.instance.getChannelDispatcher().getReceiver(tile.getWorld(), Integer.parseInt(channelTextField.getText()), Integer.parseInt(idTextField.getText())) instanceof DisplayTileEntity)) return;
            if (Integer.parseInt(channelTextField.getText()) <= 0) return;
            tile.setChannel(Integer.parseInt(channelTextField.getText()));
            tile.setId(Integer.parseInt(idTextField.getText()));
            PacketGuiReturn packet = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(packet);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        channelTextField.textboxKeyTyped(typedChar, keyCode);
        idTextField.textboxKeyTyped(typedChar, keyCode);

        tile.setChannel(Integer.parseInt(channelTextField.getText()));
        tile.setId(Integer.parseInt(idTextField.getText()));

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        channelTextField.mouseClicked(mouseX, mouseY, mouseButton);
        idTextField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}