package com.jas777.signalbox.gui;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.network.signalpacket.PacketDispatcher;
import com.jas777.signalbox.network.signalpacket.PacketGuiReturn;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class GuiSignal extends GuiScreen {

    private SignalTileEntity tile;

    private GuiTextField channelTextField;
    private GuiTextField idTextField;

    private final ResourceLocation texture = new ResourceLocation(Signalbox.MODID, "textures/gui/controller_background.png");

    private final int TEXT_CHANNEL = 1;
    private final int TEXT_ID = 2;

    private final int guiWidth = 248;
    private final int guiHeight = 166;

    public GuiSignal(SignalTileEntity tile) {
        this.tile = tile;
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

        buttonList.clear();

        channelTextField = new GuiTextField(TEXT_CHANNEL, fontRenderer, centerX + 5, centerY + 94, 50, fontRenderer.FONT_HEIGHT + 2);
        idTextField = new GuiTextField(TEXT_ID, fontRenderer, centerX + 5, centerY + 114, 50, fontRenderer.FONT_HEIGHT + 2);

        channelTextField.setValidator(NumberUtils::isCreatable);
        idTextField.setValidator(NumberUtils::isCreatable);

        channelTextField.setText("" + tile.getChannel());
        idTextField.setText("" + tile.getId());

        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        if (tile.getWorld().isRemote) {
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

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        channelTextField.mouseClicked(mouseX, mouseY, mouseButton);
        idTextField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
