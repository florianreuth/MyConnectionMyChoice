/*
 * This file is part of MyConnectionMyChoice - https://github.com/florianreuth/MyConnectionMyChoice
 * Copyright (C) 2024-2026 Florian Reuth <git@florianreuth.de> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianreuth.myconnectionmychoice.injection.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.florianreuth.myconnectionmychoice.screen.TransferConfirmScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class MixinClientCommonPacketListenerImpl {

    @WrapOperation(method = "handleTransfer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ConnectScreen;startConnecting(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;ZLnet/minecraft/client/multiplayer/TransferState;)V"))
    private void hookConfirmScreen(Screen parent, Minecraft minecraft, ServerAddress hostAndPort, ServerData data, boolean isQuickPlay, TransferState transferState, Operation<Void> original) {
        final Component description = Component.translatable("base.mcmc.screen.description", ChatFormatting.GOLD + hostAndPort.getHost() + ":" + hostAndPort.getPort());
        minecraft.setScreen(new TransferConfirmScreen(decision -> {
            if (decision == TransferConfirmScreen.TransferDecision.DECLINE) {
                minecraft.setScreen(new JoinMultiplayerScreen(new TitleScreen()));
                return;
            }

            final TransferState state = decision == TransferConfirmScreen.TransferDecision.ACCEPT_HIDE_TRANSFER_INTENT ? null : transferState;
            original.call(parent, minecraft, hostAndPort, data, isQuickPlay, state);
        }, description, false));
    }

}
