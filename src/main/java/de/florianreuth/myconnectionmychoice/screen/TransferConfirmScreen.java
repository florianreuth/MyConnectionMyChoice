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

package de.florianreuth.myconnectionmychoice.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public final class TransferConfirmScreen extends Screen {

    private static final int BUTTON_WIDTH = 250;
    private static final int PADDING = 3;

    private final TransferDecisionAction action;
    private final boolean connectionAlive;
    private final Component description;

    public TransferConfirmScreen(final TransferDecisionAction action, final Component description, final boolean connectionAlive) {
        super(Component.translatable("base.mcmc.screen.title"));
        this.action = action;
        this.connectionAlive = connectionAlive;
        this.description = description;
    }

    @Override
    protected void init() {
        super.init();

        final int totalHeight = Button.DEFAULT_HEIGHT * 3 + PADDING * 2;
        final int startY = this.height / 2 - totalHeight / 2;

        addRenderableWidget(Button
                .builder(Component.translatable("base.mcmc.screen.accept"), _ -> action.accept(TransferDecision.ACCEPT))
                .pos(this.width / 2 - BUTTON_WIDTH / 2, startY)
                .size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT)
                .build());

        addRenderableWidget(Button
                .builder(Component.translatable("base.mcmc.screen.accept.hide_transfer_intent"), _ -> action.accept(TransferDecision.ACCEPT_HIDE_TRANSFER_INTENT))
                .pos(this.width / 2 - BUTTON_WIDTH / 2, startY + Button.DEFAULT_HEIGHT + PADDING)
                .size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT)
                .build());

        addRenderableWidget(Button
                .builder(connectionAlive ? Component.translatable("base.mcmc.screen.ignore") : Component.translatable("base.mcmc.screen.cancel"), button -> action.accept(TransferDecision.DECLINE))
                .pos(this.width / 2 - BUTTON_WIDTH / 2, startY + (Button.DEFAULT_HEIGHT + PADDING) * 2)
                .size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT)
                .build());
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        final Matrix3x2fStack pose = graphics.pose();

        pose.pushMatrix();
        pose.scale(2.0F, 2.0F);
        graphics.text(font, title, this.width / 4 - font.width(title) / 2, 10, -1, true);
        pose.popMatrix();

        graphics.text(font, description, this.width / 2 - font.width(description) / 2, this.height / 2 - 48, -1, true);
    }

    @FunctionalInterface
    public interface TransferDecisionAction {

        void accept(TransferDecision decision);

    }

    public enum TransferDecision {
        ACCEPT,
        ACCEPT_HIDE_TRANSFER_INTENT,
        DECLINE
    }

}

