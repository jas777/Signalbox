package com.jas777.signalbox.integration;

import cam72cam.immersiverailroading.blocks.BlockRailBase;
import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import cam72cam.immersiverailroading.library.SwitchState;
import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cam72cam.immersiverailroading.track.IIterableTrack;
import cam72cam.immersiverailroading.util.SwitchUtil;
import com.jas777.signalbox.Signalbox;
import com.jas777.signalbox.blocks.BaseSignal;
import com.jas777.signalbox.signal.SignalMode;
import com.jas777.signalbox.tileentity.ControllerMasterTileEntity;
import com.jas777.signalbox.tileentity.SignalTileEntity;
import com.jas777.signalbox.util.SignalMast;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ImmersiveRailroading {

    //
    //     CREDIT TO CSX8600#8230 FOR MAKING THOSE METHODS
    //

    public static Vec3d findOrigin(BlockPos currentPos, EnumFacing signalFacing, World world) {
        Vec3d retVal = new Vec3d(0, -1, 0);

        BlockPos workingPos = new BlockPos(currentPos);

        boolean correctedPos = false;

        for (int i = 0; i < workingPos.getY(); i++) {
            if (world.getBlockState(workingPos).getBlock() instanceof SignalMast || world.getBlockState(workingPos).getBlock() instanceof BaseSignal) {
                workingPos = workingPos.down();
                correctedPos = true;
            }
        }

        if (correctedPos && !(world.getBlockState(workingPos).getBlock() instanceof SignalMast || world.getBlockState(workingPos).getBlock() instanceof BaseSignal)) {
            workingPos = workingPos.up();
        }

//        boolean found = false;

        Vec3d center;
        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(workingPos.offset(direction)).getBlock() instanceof BlockRailBase) {
                workingPos = workingPos.offset(direction);
                TileRailBase tile = (TileRailBase) world.getTileEntity(workingPos);
                if (tile == null) {
                    continue;
                }
                Vec3d current = new Vec3d(workingPos.getX(), workingPos.getY(), workingPos.getZ());

                center = tile.getNextPosition(current, new Vec3d(0, 0, 0));

                if (center != null) return center;
            } else if (world.getBlockState(workingPos.offset(direction).offset(direction)).getBlock() instanceof BlockRailBase) {
                workingPos = workingPos.offset(direction).offset(direction);
                TileRailBase tile = (TileRailBase) world.getTileEntity(workingPos);
                if (tile == null) {
                    continue;
                }
                Vec3d current = new Vec3d(workingPos.getX(), workingPos.getY(), workingPos.getZ());

                center = tile.getNextPosition(current, new Vec3d(0, 0, 0));

                if (center != null) return center;
            }
        }

        return retVal;
    }

    public static Vec3d getNextPosition(Vec3d currentPosition, Vec3d motion, World world, SignalTileEntity.LastSwitchInfo lastSwitchInfo) {
        BlockPos currentBlockPos = new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z);
        TileEntity te = world.getTileEntity(currentBlockPos);

        int attempt = 0;
        while (te == null && attempt < 8) {
            switch (attempt) {
                case 0:
                case 4:
                    currentBlockPos = currentBlockPos.up();
                    break;
                case 1:
                case 5:
                    currentBlockPos = currentBlockPos.down(2);
                    break;
                case 2:
                    currentBlockPos = currentBlockPos.up();
                    EnumFacing direction = EnumFacing.getFacingFromVector((float) motion.x, (float) motion.y, (float) motion.z).rotateY();
                    currentBlockPos = currentBlockPos.offset(direction);
                    break;
                case 3:
                    direction = EnumFacing.getFacingFromVector((float) motion.x, (float) motion.y, (float) motion.z).rotateY().rotateY().rotateY();
                    currentBlockPos = currentBlockPos.offset(direction, 2);
                    break;
                case 6:
                    direction = EnumFacing.getFacingFromVector((float) motion.x, (float) motion.y, (float) motion.z).rotateY();
                    currentBlockPos = currentBlockPos.offset(direction, 2);
                    break;
                case 7:
                    currentBlockPos = currentBlockPos.up(2);
                    break;
            }

            te = world.getTileEntity(currentBlockPos);
            attempt++;
        }

        if (te == null) {
            return currentPosition;
        }

        TileRailBase railBase = (TileRailBase) te;

        TileRail railParent = null;
        TileRail rail = null;

        if (railBase instanceof TileRail) {
            railParent = railBase.getParentTile();
            rail = (TileRail) railBase;
        }

        if (rail != null) {
            if (railParent.info.settings.type != TrackItems.SWITCH) {
                lastSwitchInfo.lastSwitchPlacementPosition = null;
            } else if (lastSwitchInfo.lastSwitchPlacementPosition == null || !lastSwitchInfo.lastSwitchPlacementPosition.equals(railParent.info.placementInfo.placementPosition)) {
                // This is a new switch we are encountering - check to make sure
                // the route is lined in a valid fashion

                if (currentPosition.distanceTo(railParent.info.placementInfo.placementPosition) <= 0.5) {
                    // We are on the facing point of the switch...we're good to go
                    lastSwitchInfo.lastSwitchPlacementPosition = rail.info.placementInfo.placementPosition;
                } else {
                    // We are on the trailing point of the switch...we need to make sure we're lined
                    IIterableTrack switchBuilder = (IIterableTrack) railParent.info.getBuilder();

                    boolean isOnStraight = switchBuilder.isOnTrack(railParent.info, currentPosition);
                    SwitchState switchState = SwitchUtil.getSwitchState(rail);

                    if ((isOnStraight && switchState == SwitchState.TURN) ||
                            (!isOnStraight && switchState == SwitchState.STRAIGHT)) {
                        // We're incorrectly lined, stop here
                        return currentPosition;
                    }

                    if (switchState != SwitchState.NONE) {
                        // We're correctly lined, ignore the rest of this switch
                        lastSwitchInfo.lastSwitchPlacementPosition = railParent.info.placementInfo.placementPosition;
                    }
                }
            }
        }

        return railBase.getNextPosition(currentPosition, motion);
    }

    public static boolean hasStockNearby(Vec3d currentPosition, World world) {
        BlockPos currentBlockPos = new BlockPos(currentPosition);

        AxisAlignedBB bb = new AxisAlignedBB(currentBlockPos.south().west(), currentBlockPos.up(3).east().north());
        List<EntityMoveableRollingStock> stocks = world.getEntitiesWithinAABB(EntityMoveableRollingStock.class, bb);

        return !stocks.isEmpty();
    }

    public static int doNormalTick(ControllerMasterTileEntity controller) {
        SignalTileEntity signal = controller.getSignal();
        if (signal.getEndPoint() == null) {
            if (signal.getSignalVariant() != controller.getVariantOff()) {
                return controller.getVariantOff();
            }
        }

        if (signal.getBlocksTravelled() == 0) {
            if (!(controller.getWorld().getBlockState(signal.getPos()).getBlock() instanceof BaseSignal)) return -1;
            EnumFacing signalFacing = controller.getWorld().getBlockState(signal.getPos()).getValue(BaseSignal.FACING);
            BlockPos current = new BlockPos(signal.getOrigin());
            BlockPos motionBP = current.offset(signalFacing);

            signal.setLastMotion(new Vec3d(motionBP).subtract(new Vec3d(current)));
        }

        for (int i = 0; i < 2; i++) {
            if (signal.getLastLocation() == null) {
                signal.setLastLocation(signal.getOrigin());
            }

            if (signal.getLastMotion() == null) {
                signal.setLastMotion(new Vec3d(0, 0, 0));
            }

            Vec3d motion = signal.getLastMotion();

            if (motion == null) motion = new Vec3d(0, 0, 0);

            Vec3d nextLocation = ImmersiveRailroading.getNextPosition(signal.getLastLocation(), motion, signal.getWorld(), signal.getLastSwitchInfo());

            if (nextLocation == null) break;

            if (!controller.doChunkLoad(new BlockPos(nextLocation))) {
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                break;
            }

            if (ImmersiveRailroading.hasStockNearby(signal.getOrigin(), signal.getWorld()) || ImmersiveRailroading.hasStockNearby(nextLocation, signal.getWorld())) {
                signal.setLastTickTimedOut(false);
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                return controller.getVariantOff();
            } else if (signal.getEndPoint()._1().equals(new BlockPos(nextLocation))) {
                TileEntity masterTE = controller.getWorld().getTileEntity(signal.getEndPoint()._2());
                if (masterTE instanceof SignalTileEntity && ((SignalTileEntity) masterTE).getMode() == SignalMode.AUTO) {
                    SignalTileEntity masterTESignal = (SignalTileEntity) masterTE;

                    ControllerMasterTileEntity TEScontroller = (ControllerMasterTileEntity) Signalbox.instance.getControllerDispatcher().getControllers().get(signal.getFrequency());
                    if (TEScontroller == null) break;
                    if (masterTESignal.getSignalVariant() == TEScontroller.getVariantOn()) {
                        return controller.getVariantOn();
                    } else if (masterTESignal.getSignalVariant() == TEScontroller.getNextOccupied()) {
                        return controller.getVariantOn();
                    } else if (masterTESignal.getSignalVariant() == TEScontroller.getVariantOff()) {
                        return controller.getNextOccupied();
                    }

                    signal.setLastTickTimedOut(false);
                    signal.setLastLocation(null);
                    signal.setBlocksTravelled(0);
                    return -1;
                } else if (controller.getWorld().getBlockState(signal.getEndPoint()._2()).getBlock() instanceof BaseSignal) {
                    SignalTileEntity lastSignal = (SignalTileEntity) controller.getWorld().getTileEntity(signal.getEndPoint()._2());
                    ControllerMasterTileEntity lastSignalController = (ControllerMasterTileEntity) Signalbox.instance.getControllerDispatcher().getControllers().get(lastSignal.getFrequency());
                    if (lastSignalController != null) {
                        if (lastSignal.getSignalVariant() == lastSignalController.getVariantOn()) {
                            return controller.getVariantOn();
                        } else if (lastSignal.getSignalVariant() == lastSignalController.getNextOccupied()) {
                            return controller.getVariantOn();
                        } else if (lastSignal.getSignalVariant() == lastSignalController.getVariantOff()) {
                            return controller.getNextOccupied();
                        }
                    } else {
                        return controller.getNextOccupied();
                    }
                    signal.setLastTickTimedOut(false);
                    signal.setLastLocation(null);
                    signal.setBlocksTravelled(0);
                    return -1;
                }
            }

            signal.setLastMotion(nextLocation.subtract(signal.getLastLocation()));

            signal.setBlocksTravelled(signal.getBlocksTravelled() + 1);
            if (signal.getBlocksTravelled() >= 5000 || nextLocation == signal.getLastLocation()) {
                signal.setLastTickTimedOut(true);
                signal.setLastLocation(null);
                signal.setBlocksTravelled(0);
                return controller.getVariantOff();
            }

            signal.setLastLocation(nextLocation);
        }
        signal.markDirty();
        return -1;
    }

}
