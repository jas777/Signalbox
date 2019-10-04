package com.jas777.signalbox.items;

import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.integration.ImmersiveRailroading;
import com.jas777.signalbox.network.packet.PacketSetSignalOccupationOriginOnServer;
import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import scala.Tuple2;

public class ItemSignalLinker extends BaseItem {

    public ItemSignalLinker() {
        super("signal_linker");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumActionResult result = null;
        boolean finished = false;
        if (worldIn.isRemote) {
            result = EnumActionResult.SUCCESS;
        } else {
            if (worldIn.getBlockState(pos).getBlock() instanceof BaseSignal) {
                SignalTileEntity te = (SignalTileEntity) worldIn.getTileEntity(pos);

                if (te != null && te.getMode() == SignalMode.AUTO) {
                    result = pairToSignal(worldIn, te, player);
                    finished = true;
                }
            }
            if (!finished) {
                IBlockState state = worldIn.getBlockState(pos);
                if (state.getBlock() instanceof BaseSignal) {
                    SignalTileEntity te = (SignalTileEntity) worldIn.getTileEntity(pos);
                    if (te.getMode() != SignalMode.AUTO) {
                        result = pairToManualSignal(worldIn, pos, player);
                    } else {
                        try {
                            Class railBaseClass = Class.forName("cam72cam.immersiverailroading.blocks.BlockRailBase");

                            if (railBaseClass.isAssignableFrom(state.getBlock().getClass())) {
                                result = setOccupationOrigin(worldIn, pos, player);
                                finished = true;
                            }
                        } catch (Exception e) {
                        }
                        if (!finished) {
                            result = EnumActionResult.PASS;
                        }
                    }
                }
            }
        }

        return result;
    }

    private EnumActionResult pairToSignal(World worldIn, SignalTileEntity te, EntityPlayer player) {
        NBTTagCompound tag = getTagOfLinker(player);

        int[] pairingpos = null;
        if (player.isSneaking()) {
            if (tag.hasKey("pairingpos")) {
                tag.removeTag("pairingpos");
                ;
                player.sendMessage(new TextComponentString("Unpaired from Signal"));
            }


            if (tag.hasKey("occupationpairingpos")) {
                tag.removeTag("occupationpairingpos");
                player.sendMessage(new TextComponentString("Stopped setting occupation origin"));
            } else {
                tag.setIntArray("occupationpairingpos", new int[]{te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()});
                player.sendMessage(new TextComponentString("Starting setting occupation origin"));
            }
            return EnumActionResult.SUCCESS;
        }

        if (tag.hasKey("occupationpairingpos")) {
            tag.removeTag("occupationpairingpos");
            player.sendMessage(new TextComponentString("Stopped setting occupation origin"));
        }

        if (tag.hasKey("pairingpos")) {
            pairingpos = tag.getIntArray("pairingpos");
            if (pairingpos[0] == te.getPos().getX() &&
                    pairingpos[1] == te.getPos().getY() &&
                    pairingpos[2] == te.getPos().getZ()) {
                player.sendMessage(new TextComponentString("Unpaired from Signal"));
            } else {
                SignalTileEntity pairParent = (SignalTileEntity) worldIn.getTileEntity(new BlockPos(pairingpos[0], pairingpos[1], pairingpos[2]));
                if (pairParent == null) {
                    player.sendMessage(new TextComponentString("Could not find the pairing origin.  Clearing pair origin."));
                } else {
                    pairParent.setEndPoint(new Tuple2<BlockPos, BlockPos>(new BlockPos(te.getOrigin()), te.getPos()));
                    player.sendMessage(new TextComponentString("Paired!  Clearing pair origin."));
                }
            }
            tag.removeTag("pairingpos");
        } else {
            tag.setIntArray("pairingpos", new int[]{te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()});
            player.sendMessage(new TextComponentString("Paired to Signal"));
        }

        player.inventory.getCurrentItem().setTagCompound(tag);
        return EnumActionResult.SUCCESS;
    }

    private EnumActionResult pairToManualSignal(World worldIn, BlockPos pos, EntityPlayer player) {
        NBTTagCompound tag = getTagOfLinker(player);

        int[] pairingpos = null;
        if (tag.hasKey("pairingpos")) {
            pairingpos = tag.getIntArray("pairingpos");
            BlockPos parentPos = new BlockPos(pairingpos[0], pairingpos[1], pairingpos[2]);
            SignalTileEntity te = (SignalTileEntity) worldIn.getTileEntity(parentPos);

            if (te == null) {
                player.sendMessage(new TextComponentString("Could not find the pairing origin.  Clearing pair origin."));
            } else {
                IBlockState signState = worldIn.getBlockState(pos);
                EnumFacing facing = signState.getValue(BaseSignal.FACING);
                Vec3d origin = ImmersiveRailroading.findOrigin(pos, facing, worldIn);

                if (origin.y == -1) {
                    player.sendMessage(new TextComponentString("Could not find track nearby.  Try again."));
                    return EnumActionResult.SUCCESS;
                }

                te.setEndPoint(new Tuple2<BlockPos, BlockPos>(new BlockPos(origin), pos));
                player.sendMessage(new TextComponentString("Paired!  Clearing pair origin."));
            }

            tag.removeTag("pairingpos");
        }

        player.inventory.getCurrentItem().setTagCompound(tag);
        return EnumActionResult.SUCCESS;
    }

    private EnumActionResult setOccupationOrigin(World worldIn, BlockPos pos, EntityPlayer player) {
        NBTTagCompound tag = getTagOfLinker(player);

        if (tag.hasKey("pairingpos")) {
            tag.removeTag("pairingpos");
            player.sendMessage(new TextComponentString("Stopped pairing with signal"));
        }

        int[] pairingposarray = tag.getIntArray("occupationpairingpos");
        BlockPos pairingpos = new BlockPos(pairingposarray[0], pairingposarray[1], pairingposarray[2]);
        IBlockState signalState = worldIn.getBlockState(pairingpos);
        Vec3d trackPos = ImmersiveRailroading.findOrigin(pos, signalState.getValue(BaseSignal.FACING), worldIn);
        BlockPos originPos = new BlockPos(trackPos.x, trackPos.y, trackPos.z);

        PacketSetSignalOccupationOriginOnServer packet = new PacketSetSignalOccupationOriginOnServer(pairingpos, originPos);
        Signalbox.network.sendToServer(packet);

        player.sendMessage(new TextComponentString("Set origin!  Clearing"));
        tag.removeTag("occupationpairingpos");

        return EnumActionResult.SUCCESS;
    }

    private NBTTagCompound getTagOfLinker(EntityPlayer player) {
        NBTTagCompound tag = player.inventory.getCurrentItem().getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }

        return tag;
    }

}
