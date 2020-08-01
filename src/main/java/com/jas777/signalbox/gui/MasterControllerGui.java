package com.jas777.signalbox.gui;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.base.signal.SignalMode;
import com.jas777.signalbox.network.PacketDispatcher;
import com.jas777.signalbox.network.PacketGuiReturn;
import com.jas777.signalbox.tileentity.SignalControllerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class MasterControllerGui extends GuiScreen {

    private final SignalControllerTileEntity tileEntity;

    private final int guiWidth = 248;
    private final int guiHeight = 166;

    private final ResourceLocation texture = new ResourceLocation(Signalbox.MODID, "textures/gui/controller_background.png");

    // Mode buttons

    private GuiButton buttonModeAnalog;
    private GuiButton buttonModeDigital;
    private GuiButton buttonModeAuto;

    // Variant buttons

    private GuiButton buttonVariantOnPlus;
    private GuiButton buttonVariantOnMinus;
    private GuiButton buttonVariantOffPlus;
    private GuiButton buttonVariantOffMinus;
    private GuiButton buttonVariantNocPlus; // Auto only! (Next-occupied variant)
    private GuiButton buttonVariantNocMinus; // Auto only! (Next-occupied variant)

    // Frequency settings

    private GuiButton buttonChannelPlusOne;
    private GuiButton buttonChannelPlusTen;
    private GuiButton buttonChannelPlusHundred;

    private GuiButton buttonChannelMinusOne;
    private GuiButton buttonChannelMinusTen;
    private GuiButton buttonChannelMinusHundred;

    private GuiButton buttonFrequencyPlusOne;
    private GuiButton buttonFrequencyPlusTen;
    private GuiButton buttonFrequencyPlusHundred;

    private GuiButton buttonFrequencyMinusOne;
    private GuiButton buttonFrequencyMinusTen;
    private GuiButton buttonFrequencyMinusHundred;

    // Button IDs

    private final int BUTTON_MODE_ANALOG = 18;
    private final int BUTTON_MODE_DIGITAL = 19;
    private final int BUTTON_MODE_AUTO = 20;

    private final int BUTTON_VARIANT_ON_PLUS = 0;
    private final int BUTTON_VARIANT_ON_MINUS = 1;

    private final int BUTTON_VARIANT_OFF_PLUS = 2;
    private final int BUTTON_VARIANT_OFF_MINUS = 3;

    private final int BUTTON_VARIANT_NOC_PLUS = 4;
    private final int BUTTON_VARIANT_NOC_MINUS = 5;

    private final int BUTTON_CHANNEL_PLUS_ONE = 6;
    private final int BUTTON_CHANNEL_PLUS_TEN = 7;
    private final int BUTTON_CHANNEL_PLUS_HUNDRED = 8;

    private final int BUTTON_CHANNEL_MINUS_ONE = 9;
    private final int BUTTON_CHANNEL_MINUS_TEN = 10;
    private final int BUTTON_CHANNEL_MINUS_HUNDRED = 11;

    private final int BUTTON_FREQUENCY_PLUS_ONE = 12;
    private final int BUTTON_FREQUENCY_PLUS_TEN = 13;
    private final int BUTTON_FREQUENCY_PLUS_HUNDRED = 14;

    private final int BUTTON_FREQUENCY_MINUS_ONE = 15;
    private final int BUTTON_FREQUENCY_MINUS_TEN = 16;
    private final int BUTTON_FREQUENCY_MINUS_HUNDRED = 17;

    // Internal variables

    private int variantOnStringLength;
    private int variantOffStringLength;
    private int variantNocStringLength;

    public MasterControllerGui(SignalControllerTileEntity te) {
        this.tileEntity = te;
    }

    @Override
    public void initGui() {

        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height / 2) - guiHeight / 2;

        int halfFontHeight = fontRenderer.FONT_HEIGHT / 2;

        variantOnStringLength = fontRenderer.getStringWidth("Signal variant [Free]: " + tileEntity.getVariantOn());
        variantOffStringLength = fontRenderer.getStringWidth("Signal variant [Occupied]: " + tileEntity.getVariantOff());
        variantNocStringLength = fontRenderer.getStringWidth("Signal variant [Mext occupied]: " + tileEntity.getVariantNoc());

        // Mode buttons init
        // fontRenderer.drawString("Signal mode: §2" + tileEntity.getMode(), centerX + 5, centerY + 15, 0);

        int modeStringLength = fontRenderer.getStringWidth("Signal mode: ");

        int analogStringLength = fontRenderer.getStringWidth(SignalMode.ANALOG.toString());
        int digitalStringLength = fontRenderer.getStringWidth(SignalMode.DIGITAL.toString());
        int autoStringLength = fontRenderer.getStringWidth(SignalMode.AUTO.toString());

        buttonList.add(buttonModeAnalog = new GuiButton(BUTTON_MODE_ANALOG, centerX + modeStringLength + 5, centerY + 13 - halfFontHeight, analogStringLength + 4, 20, "Analog"));
        buttonList.add(buttonModeDigital = new GuiButton(BUTTON_MODE_DIGITAL, centerX + modeStringLength + analogStringLength + 14, centerY + 13 - halfFontHeight, digitalStringLength + 4, 20, "Digital"));
        buttonList.add(buttonModeAuto = new GuiButton(BUTTON_MODE_AUTO, centerX + modeStringLength + analogStringLength + digitalStringLength + 23, centerY + 13 - halfFontHeight, autoStringLength + 8, 20, "Auto"));

        // Variant buttons init

        buttonList.add(buttonVariantOnPlus = new GuiButton(BUTTON_VARIANT_ON_PLUS, centerX + variantNocStringLength + 25, centerY + 44 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantOnMinus = new GuiButton(BUTTON_VARIANT_ON_MINUS, centerX + variantNocStringLength + 50, centerY + 44 - halfFontHeight, 20, 20, "-"));

        buttonList.add(buttonVariantOffPlus = new GuiButton(BUTTON_VARIANT_OFF_PLUS, centerX + variantNocStringLength + 25, centerY + 68 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantOffMinus = new GuiButton(BUTTON_VARIANT_OFF_MINUS, centerX + variantNocStringLength + 50, centerY + 68 - halfFontHeight, 20, 20, "-"));

        buttonList.add(buttonVariantNocPlus = new GuiButton(BUTTON_VARIANT_NOC_PLUS, centerX + variantNocStringLength + 25, centerY + 93 - halfFontHeight, 20, 20, "+"));
        buttonList.add(buttonVariantNocMinus = new GuiButton(BUTTON_VARIANT_NOC_MINUS, centerX + variantNocStringLength + 50, centerY + 93 - halfFontHeight, 20, 20, "-"));

        // Frequency buttons init

        int plusWidth = fontRenderer.getStringWidth("+") + 2;
        int minusWidth = fontRenderer.getStringWidth("-") + 2;

        buttonList.add(buttonChannelPlusOne = new GuiButton(BUTTON_CHANNEL_PLUS_ONE, centerX + 165, centerY + 127 - halfFontHeight, plusWidth, fontRenderer.FONT_HEIGHT + 2, "+"));
        buttonList.add(buttonChannelPlusTen = new GuiButton(BUTTON_CHANNEL_PLUS_TEN, centerX + 167 + plusWidth, centerY + 127 - halfFontHeight, plusWidth * 2, fontRenderer.FONT_HEIGHT + 2, "++"));
        buttonList.add(buttonChannelPlusHundred = new GuiButton(BUTTON_CHANNEL_PLUS_HUNDRED, centerX + 169 + plusWidth * 3, centerY + 127 - halfFontHeight, plusWidth * 3, fontRenderer.FONT_HEIGHT + 2, "+++"));

        buttonList.add(buttonChannelMinusOne = new GuiButton(BUTTON_CHANNEL_MINUS_ONE, centerX + 5, centerY + 127 - halfFontHeight, minusWidth, fontRenderer.FONT_HEIGHT + 2, "-"));
        buttonList.add(buttonChannelMinusTen = new GuiButton(BUTTON_CHANNEL_MINUS_TEN, centerX + 7 + minusWidth, centerY + 127 - halfFontHeight, minusWidth * 2, fontRenderer.FONT_HEIGHT + 2, "--"));
        buttonList.add(buttonChannelMinusHundred = new GuiButton(BUTTON_CHANNEL_MINUS_HUNDRED, centerX + 9 + minusWidth * 3, centerY + 127 - halfFontHeight, minusWidth * 3, fontRenderer.FONT_HEIGHT + 2, "---"));

        buttonList.add(buttonFrequencyPlusOne = new GuiButton(BUTTON_FREQUENCY_PLUS_ONE, centerX + 165, centerY + 147 - halfFontHeight, plusWidth, fontRenderer.FONT_HEIGHT + 2, "+"));
        buttonList.add(buttonFrequencyPlusTen = new GuiButton(BUTTON_FREQUENCY_PLUS_TEN, centerX + 167 + plusWidth, centerY + 147 - halfFontHeight, plusWidth * 2, fontRenderer.FONT_HEIGHT + 2, "++"));
        buttonList.add(buttonFrequencyPlusHundred = new GuiButton(BUTTON_FREQUENCY_PLUS_HUNDRED, centerX + 169 + plusWidth * 3, centerY + 147 - halfFontHeight, plusWidth * 3, fontRenderer.FONT_HEIGHT + 2, "+++"));

        buttonList.add(buttonFrequencyMinusOne = new GuiButton(BUTTON_FREQUENCY_MINUS_ONE, centerX + 5, centerY + 147 - halfFontHeight, minusWidth, fontRenderer.FONT_HEIGHT + 2, "-"));
        buttonList.add(buttonFrequencyMinusTen = new GuiButton(BUTTON_FREQUENCY_MINUS_TEN, centerX + 7 + minusWidth, centerY + 147 - halfFontHeight, minusWidth * 2, fontRenderer.FONT_HEIGHT + 2, "--"));
        buttonList.add(buttonFrequencyMinusHundred = new GuiButton(BUTTON_FREQUENCY_MINUS_HUNDRED, centerX + 9 + minusWidth * 3, centerY + 147 - halfFontHeight, minusWidth * 3, fontRenderer.FONT_HEIGHT + 2, "---"));

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

            fontRenderer.drawString("Signal mode: §2" + tileEntity.getMode(), centerX + 5, centerY + 15, 0);

            if (tileEntity.getMode() == SignalMode.AUTO) {
                fontRenderer.drawString("Signal variant [§aFree§r]: " + tileEntity.getVariantOn(), centerX + 5, centerY + 45, 0);
                fontRenderer.drawString("Signal variant [§cOccupied§r]: " + tileEntity.getVariantOff(), centerX + 5, centerY + 70, 0);
                fontRenderer.drawString("Signal variant [§eNext occupied§r]: " + tileEntity.getVariantNoc(), centerX + 5, centerY + 95, 0);

                buttonVariantOnPlus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantOnMinus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantOffPlus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantOffMinus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantNocPlus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantNocMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            } else if (tileEntity.getMode() != SignalMode.DIGITAL) {

                fontRenderer.drawString("Signal variant [§aFree§r]: " + tileEntity.getVariantOn(), centerX + 5, centerY + 45, 0);
                fontRenderer.drawString("Signal variant [§cOccupied§r]: " + tileEntity.getVariantOff(), centerX + 5, centerY + 70, 0);

                buttonVariantOnPlus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantOnMinus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantOffPlus.drawButton(mc, mouseX, mouseY, partialTicks);
                buttonVariantOffMinus.drawButton(mc, mouseX, mouseY, partialTicks);

            }

            buttonModeAnalog.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonModeDigital.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonModeAuto.drawButton(mc, mouseX, mouseY, partialTicks);

            switch (tileEntity.getMode()) {
                case ANALOG:
                    buttonModeAnalog.enabled = false;
                    buttonModeDigital.enabled = true;
                    buttonModeAuto.enabled = true;
                    break;
                case DIGITAL:
                    buttonModeDigital.enabled = false;
                    buttonModeAnalog.enabled = true;
                    buttonModeAuto.enabled = true;
                    break;
                case AUTO:
                    buttonModeAuto.enabled = false;
                    buttonModeAnalog.enabled = true;
                    buttonModeDigital.enabled = true;
                    break;
            }

            buttonChannelPlusOne.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonChannelPlusTen.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonChannelPlusHundred.drawButton(mc, mouseX, mouseY, partialTicks);

            buttonChannelMinusOne.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonChannelMinusTen.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonChannelMinusHundred.drawButton(mc, mouseX, mouseY, partialTicks);

            buttonFrequencyPlusOne.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonFrequencyPlusTen.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonFrequencyPlusHundred.drawButton(mc, mouseX, mouseY, partialTicks);

            buttonFrequencyMinusOne.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonFrequencyMinusTen.drawButton(mc, mouseX, mouseY, partialTicks);
            buttonFrequencyMinusHundred.drawButton(mc, mouseX, mouseY, partialTicks);

            int plusWidth = fontRenderer.getStringWidth("+") + 2;
            int minusWidth = fontRenderer.getStringWidth("-") + 2;

            fontRenderer.drawString("Channel: §9" + tileEntity.getFrequency(), centerX + 15 + plusWidth * 6, centerY + 125, 0);
            fontRenderer.drawString("Frequency: §2" + tileEntity.getSubFrequency(), centerX + 15 + plusWidth * 6, centerY + 145, 0);

            fontRenderer.drawString("█", centerX, centerY + 170, tileEntity.isActive() ? 0x00FF00 : 0xFF0000);

        }
        GlStateManager.popMatrix();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        switch (button.id) {
            case BUTTON_MODE_ANALOG:
                tileEntity.setMode(SignalMode.ANALOG);
                break;
            case BUTTON_MODE_DIGITAL:
                tileEntity.setMode(SignalMode.DIGITAL);
                break;
            case BUTTON_MODE_AUTO:
                tileEntity.setMode(SignalMode.AUTO);
                break;
            case BUTTON_VARIANT_ON_PLUS:
                if (tileEntity.getVariantOn() == tileEntity.getMaxVariant()) {
                    tileEntity.setVariantOn(0);
                } else {
                    tileEntity.setVariantOn(tileEntity.getVariantOn() + 1);
                }
                break;
            case BUTTON_VARIANT_OFF_PLUS:
                if (tileEntity.getVariantOff() == tileEntity.getMaxVariant()) {
                    tileEntity.setVariantOff(0);
                } else {
                    tileEntity.setVariantOff(tileEntity.getVariantOff() + 1);
                }
                break;
            case BUTTON_VARIANT_NOC_PLUS:
                if (tileEntity.getVariantNoc() == tileEntity.getMaxVariant()) {
                    tileEntity.setVariantNoc(0);
                } else {
                    tileEntity.setVariantNoc(tileEntity.getVariantNoc() + 1);
                }
                break;
            case BUTTON_VARIANT_ON_MINUS:
                if (tileEntity.getVariantOn() == 0) {
                    tileEntity.setVariantOn(tileEntity.getMaxVariant());
                } else {
                    tileEntity.setVariantOn(tileEntity.getVariantOn() - 1);
                }
                break;
            case BUTTON_VARIANT_OFF_MINUS:
                if (tileEntity.getVariantOff() == 0) {
                    tileEntity.setVariantOff(tileEntity.getMaxVariant());
                } else {
                    tileEntity.setVariantOff(tileEntity.getVariantOff() - 1);
                }
                break;
            case BUTTON_VARIANT_NOC_MINUS:
                if (tileEntity.getVariantNoc() == 0) {
                    tileEntity.setVariantNoc(tileEntity.getMaxVariant());
                } else {
                    tileEntity.setVariantNoc(tileEntity.getVariantNoc() - 1);
                }
                break;
            case BUTTON_CHANNEL_PLUS_ONE:
                if (tileEntity.getChannel() == null) {
                    tileEntity.setFrequency(1);
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(1));
                } else {
                    tileEntity.setFrequency(tileEntity.getChannel().getFrequency() + 1);
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(tileEntity.getFrequency()));
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                }
                break;
            case BUTTON_CHANNEL_PLUS_TEN:
                if (tileEntity.getChannel() == null) {
                    tileEntity.setFrequency(10);
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(10));
                    break;
                }
                tileEntity.setFrequency(tileEntity.getChannel().getFrequency() + 10);
                tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(tileEntity.getFrequency()));
                tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                break;
            case BUTTON_CHANNEL_PLUS_HUNDRED:
                if (tileEntity.getChannel() == null) {
                    tileEntity.setFrequency(100);
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(100));
                    break;
                }
                tileEntity.setFrequency(tileEntity.getChannel().getFrequency() + 100);
                tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(tileEntity.getFrequency()));
                tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                break;
            case BUTTON_CHANNEL_MINUS_ONE:
                if (tileEntity.getChannel() == null) {
                    break;
                }
                tileEntity.setFrequency(tileEntity.getChannel().getFrequency() - 1);

                if (tileEntity.getFrequency() > 0) {
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(tileEntity.getFrequency()));
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                } else {
                    tileEntity.setFrequency(0);
                    tileEntity.setChannel(null);
                }

                break;
            case BUTTON_CHANNEL_MINUS_TEN:
                if (tileEntity.getChannel() == null) {
                    break;
                }
                tileEntity.setFrequency(tileEntity.getChannel().getFrequency() - 10);

                if (tileEntity.getFrequency() > 0) {
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(tileEntity.getFrequency()));
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                } else {
                    tileEntity.setFrequency(0);
                    tileEntity.setChannel(null);
                }

                break;
            case BUTTON_CHANNEL_MINUS_HUNDRED:
                if (tileEntity.getChannel() == null) {
                    break;
                }
                tileEntity.setFrequency(tileEntity.getChannel().getFrequency() - 100);

                if (tileEntity.getFrequency() > 0) {
                    tileEntity.setChannel(Signalbox.channelDispatcher.addChannel(tileEntity.getFrequency()));
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                } else {
                    tileEntity.setFrequency(0);
                    tileEntity.setChannel(null);
                }

                break;
            case BUTTON_FREQUENCY_PLUS_ONE:
                tileEntity.setSubFrequency(tileEntity.getSubFrequency() + 1);
                if (tileEntity.getChannel() != null) {
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                }
                break;
            case BUTTON_FREQUENCY_PLUS_TEN:
                tileEntity.setSubFrequency(tileEntity.getSubFrequency() + 10);
                if (tileEntity.getChannel() != null) {
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                }
                break;
            case BUTTON_FREQUENCY_PLUS_HUNDRED:
                tileEntity.setSubFrequency(tileEntity.getSubFrequency() + 100);
                if (tileEntity.getChannel() != null) {
                    tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                }
                break;
            case BUTTON_FREQUENCY_MINUS_ONE:
                if (tileEntity.getSubFrequency() - 1 > 0) {
                    tileEntity.setSubFrequency(tileEntity.getSubFrequency() - 1);
                    if (tileEntity.getChannel() != null) {
                        tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                    }
                }

                break;
            case BUTTON_FREQUENCY_MINUS_TEN:
                if (tileEntity.getSubFrequency() - 10 > 0) {
                    tileEntity.setSubFrequency(tileEntity.getSubFrequency() - 10);
                    if (tileEntity.getChannel() != null) {
                        tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                    }
                }

                break;
            case BUTTON_FREQUENCY_MINUS_HUNDRED:
                if (tileEntity.getSubFrequency() - 100 > 0) {
                    tileEntity.setSubFrequency(tileEntity.getSubFrequency() - 100);
                    if (tileEntity.getChannel() != null) {
                        tileEntity.getChannel().tune(tileEntity.getSubFrequency(), tileEntity);
                    }
                }

                break;
        }

    }

    @Override
    public void onGuiClosed() {
        if (tileEntity.getWorld().isRemote) {
            PacketGuiReturn packet = new PacketGuiReturn(tileEntity);
            PacketDispatcher.sendToServer(packet);
        }
    }
}
