package com.jas777.signalbox.gui;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.network.signalpacket.PacketDispatcher;
import com.jas777.signalbox.network.signalpacket.PacketGuiReturn;
import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.io.IOException;

public class GuiSignal extends GuiScreen {

    private SignalTileEntity tile;

    private GuiTextField channelTextField;
    private GuiTextField idTextField;

    private GuiButton buttonModePlus;
    private GuiButton buttonModeMinus;
    private GuiButton buttonSave;

    private final int BUTTON_MODE_PLUS = 0;
    private final int BUTTON_MODE_MINUS = 1;
    private final int BUTTON_SAVE = 2;

    private TextureAtlasSprite sprite;

    private final ResourceLocation texture = new ResourceLocation(Signalbox.MODID, "textures/gui/controller_background.png");

    private final int TEXT_CHANNEL = 1;
    private final int TEXT_ID = 2;

    private final int guiWidth = 248;
    private final int guiHeight = 166;

    private int modeStringLength;

    public GuiSignal(SignalTileEntity tile) {
        this.tile = tile;
        this.sprite = null;
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

            fontRenderer.drawString("Mode: " + tile.getMode().getName(), centerX + 30, centerY + 30, 0);

            buttonModePlus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonModeMinus.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonSave.drawButton(mc, mouseX, mouseY, partialTicks);

            channelTextField.drawTextBox();
            idTextField.drawTextBox();

            fontRenderer.drawString("Channel", centerX + 60, centerY + 96, 0x000000);
            fontRenderer.drawString("Signal ID", centerX + 60, centerY + 116, 0x000000);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void initGui() {

        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        if (tile == null) mc.displayGuiScreen(null);
        modeStringLength = fontRenderer.getStringWidth("Mode: " + tile.getMode().getName());

        buttonList.clear();

        int halfFontHeight = fontRenderer.FONT_HEIGHT / 2;

        buttonList.add(buttonModePlus = new GuiButton(BUTTON_MODE_PLUS, centerX + 5, centerY + 29 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonModeMinus = new GuiButton(BUTTON_MODE_MINUS, centerX + modeStringLength + 35, centerY + 29 - halfFontHeight, 20, 20, "-"));
        buttonList.add(buttonSave = new GuiButton(BUTTON_SAVE, centerX + 202, centerY + 140, 40, 20, "Save"));

        channelTextField = new GuiTextField(TEXT_CHANNEL, fontRenderer, centerX + 5, centerY + 94, 50, fontRenderer.FONT_HEIGHT + 2);
        idTextField = new GuiTextField(TEXT_ID, fontRenderer, centerX + 5, centerY + 114, 50, fontRenderer.FONT_HEIGHT + 2);

        channelTextField.setValidator(NumberUtils::isCreatable);
        idTextField.setValidator(NumberUtils::isCreatable);

        channelTextField.setText("" + tile.getChannel());
        idTextField.setText("" + tile.getId());

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        sprite = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        super.initGui();
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTON_MODE_PLUS:
                if (tile.getMode().ordinal() == SignalMode.values().length - 1) return;
                tile.setMode(SignalMode.values()[tile.getMode().ordinal() + 1]);
                break;
            case BUTTON_MODE_MINUS:
                if (tile.getMode().ordinal() == 0) return;
                tile.setMode(SignalMode.values()[tile.getMode().ordinal() - 1]);
                break;
            case BUTTON_SAVE:
                if (tile.getWorld().isRemote) {
                    tile.setChannel(Integer.parseInt(channelTextField.getText()));
                    tile.setId(Integer.parseInt(idTextField.getText()));
                    PacketGuiReturn packet = new PacketGuiReturn(tile);
                    PacketDispatcher.sendToServer(packet);
                }
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        if (tile.getWorld().isRemote) {
            tile.setChannel(Integer.parseInt(channelTextField.getText()));
            tile.setId(Integer.parseInt(idTextField.getText()));
            PacketGuiReturn packet = new PacketGuiReturn(tile);
            PacketDispatcher.sendToServer(packet);
            tile.markDirty();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        channelTextField.textboxKeyTyped(typedChar, keyCode);
        idTextField.textboxKeyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        channelTextField.mouseClicked(mouseX, mouseY, mouseButton);
        idTextField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
