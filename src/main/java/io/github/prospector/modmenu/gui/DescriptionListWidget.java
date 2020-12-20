package io.github.prospector.modmenu.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.MathHelper;

public class DescriptionListWidget extends EntryListWidget<DescriptionListWidget.DescriptionEntry> {

	private final ModsScreen parent;
	private final TextRenderer textRenderer;
	private ModListEntry lastSelected = null;

	public DescriptionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight, ModsScreen parent) {
		super(client, width, height, top, bottom, entryHeight);
		this.parent = parent;
		this.textRenderer = client.textRenderer;
	}

	@Override
	public DescriptionEntry getSelected() {
		return null;
	}

	@Override
	public int getRowWidth() {
		return this.width - 10;
	}

	@Override
	protected int getScrollbarPositionX() {
		return this.width - 6 + left;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		ModListEntry selectedEntry = parent.getSelectedEntry();
		if (selectedEntry != lastSelected) {
			lastSelected = selectedEntry;
			clearEntries();
			setScrollAmount(-Double.MAX_VALUE);
			String description = lastSelected.getMod().getDescription();
			if (lastSelected != null && description != null && !description.isEmpty()) {
				for (OrderedText line : textRenderer.wrapLines(new LiteralText(description.replaceAll("\n", "\n\n")), getRowWidth() - 5)) {
					children().add(new DescriptionEntry(line, this));
				}
			}
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();

		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(this.left, (this.top + 4), 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.right, (this.top + 4), 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.right, this.top, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.left, this.top, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.left, this.bottom, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.right, this.bottom, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.right, (this.bottom - 4), 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.left, (this.bottom - 4), 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 0).next();
		tessellator.draw();

		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(this.left, this.bottom, 0.0D).color(0, 0, 0, 128).next();
		bufferBuilder.vertex(this.right, this.bottom, 0.0D).color(0, 0, 0, 128).next();
		bufferBuilder.vertex(this.right, this.top, 0.0D).color(0, 0, 0, 128).next();
		bufferBuilder.vertex(this.left, this.top, 0.0D).color(0, 0, 0, 128).next();
		tessellator.draw();

		int k = this.getRowLeft();
		int l = this.top + 4 - (int) this.getScrollAmount();
		this.renderList(matrices, k, l, mouseX, mouseY, delta);
		this.renderScrollBar(bufferBuilder, tessellator);

		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
	}

	public void renderScrollBar(BufferBuilder bufferBuilder, Tessellator tessellator) {
		int scrollbarStartX = this.getScrollbarPositionX();
		int scrollbarEndX = scrollbarStartX + 6;
		int maxScroll = this.getMaxScroll();
		if (maxScroll > 0) {
			RenderSystem.disableTexture();
			int p = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
			p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
			int q = (int) this.getScrollAmount() * (this.bottom - this.top - p) / maxScroll + this.top;
			if (q < this.top) {
				q = this.top;
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(scrollbarStartX, this.bottom, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(scrollbarEndX, this.bottom, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(scrollbarEndX, this.top, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(scrollbarStartX, this.top, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(scrollbarStartX, q + p, 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(scrollbarEndX, q + p, 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(scrollbarEndX, q, 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(scrollbarStartX, q, 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(scrollbarStartX, q + p - 1, 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(scrollbarEndX - 1, q + p - 1, 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(scrollbarEndX - 1, q, 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(scrollbarStartX, q, 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
			tessellator.draw();
		}
	}

	protected class DescriptionEntry extends EntryListWidget.Entry<DescriptionEntry> {
		private final DescriptionListWidget widget;
		protected OrderedText text;

		public DescriptionEntry(OrderedText text, DescriptionListWidget widget) {
			this.text = text;
			this.widget = widget;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int itemWidth, int itemHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
			if (widget.top > y || widget.bottom - textRenderer.fontHeight < y) {
				return;
			}
			textRenderer.drawWithShadow(matrices, text, x, y, 0xAAAAAA);
		}
	}

}
