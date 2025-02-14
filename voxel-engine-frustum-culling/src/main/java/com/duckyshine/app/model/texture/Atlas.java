package com.duckyshine.app.model.texture;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;

import java.nio.file.Path;

import java.awt.Graphics2D;
import java.awt.AlphaComposite;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.duckyshine.app.math.Direction;

import com.duckyshine.app.model.BlockType;

import com.duckyshine.app.utility.ResourceFinder;

import com.duckyshine.app.debug.Debug;

import static org.lwjgl.opengl.GL30.*;

public class Atlas {
    private final static int WIDTH = 32;
    private final static int HEIGHT = 48;

    private final static int IMAGE_PER_ROW = 2;

    private final static int IMAGE_SIZE = 16;

    private final static String FORMAT = ".png";

    private final static String PARENT_DIRECTORY = "textures/blocks/";

    public static void setup(boolean isUsingAtlas) {
        int textureId = glGenTextures();

        Atlas.initialise(textureId);

        Atlas.create(isUsingAtlas);
    }

    private static void initialise(int textureId) {
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D_ARRAY, textureId);

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        // glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        // glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage3D(
                GL_TEXTURE_2D_ARRAY,
                0,
                GL_RGBA,
                Atlas.IMAGE_SIZE,
                Atlas.IMAGE_SIZE,
                BlockType.values().length * 6,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                (ByteBuffer) null);
    }

    private static void create(boolean isUsingAtlas) {
        for (BlockType blockType : BlockType.values()) {
            if (isUsingAtlas) {
                ByteBuffer atlas = Atlas.get(blockType);

                Atlas.add(atlas, blockType.getIndex());
            } else {
                Atlas.addTextures(blockType);
            }
        }
    }

    private static ByteBuffer get(BlockType blockType) {
        String filepath = null;

        String blockName = blockType.getName();

        String directory = ResourceFinder.getResourcePath(Atlas.PARENT_DIRECTORY + blockName).toString();

        List<File> files = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            String filename = direction.getName() + Atlas.FORMAT;

            filepath = ResourceFinder.getFile(Atlas.PARENT_DIRECTORY, blockName, filename);

            files.add(new File(filepath));
        }

        return Atlas.build(files, directory, blockName);
    }

    private static void addTextures(BlockType blockType) {
        String filepath = null;

        String blockName = blockType.getName();

        for (Direction direction : Direction.values()) {
            String filename = direction.getName() + Atlas.FORMAT;

            filepath = ResourceFinder.getFile(Atlas.PARENT_DIRECTORY, blockName, filename);

            // Could refactor here
            File file = new File(filepath);

            BufferedImage atlas = Atlas.getImage(file);

            ByteBuffer buffer = Atlas.getByteBufferFromImage(atlas);

            Atlas.add(buffer, blockType, direction);
        }
    }

    private static ByteBuffer build(List<File> files, String directory, String blockName) {
        BufferedImage atlas = new BufferedImage(Atlas.WIDTH, Atlas.HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D canvas = Atlas.getCanvas(atlas);

        for (int i = 0; i < files.size(); i++) {
            BufferedImage image = Atlas.getImage(files.get(i));

            int row = i / Atlas.IMAGE_PER_ROW;
            int column = i % Atlas.IMAGE_PER_ROW;

            int x = column * Atlas.IMAGE_SIZE;
            int y = row * Atlas.IMAGE_SIZE;

            canvas.drawImage(image, x, y, Atlas.IMAGE_SIZE, Atlas.IMAGE_SIZE, null);
        }

        canvas.dispose();

        Atlas.save(atlas, directory, blockName);

        return getByteBufferFromImage(atlas);
    }

    private static void save(BufferedImage image, String directory, String filename) {
        String outputPath = Path.of(directory, filename).toString();

        try {
            File file = new File(outputPath + Atlas.FORMAT);

            ImageIO.write(image, "png", file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static Graphics2D getCanvas(BufferedImage image) {
        Graphics2D canvas = image.createGraphics();

        canvas.setComposite(AlphaComposite.Clear);

        canvas.fillRect(0, 0, Atlas.WIDTH, Atlas.HEIGHT);

        canvas.setComposite(AlphaComposite.SrcOver);

        return canvas;
    }

    private static ByteBuffer getByteBufferFromImage(BufferedImage image) {
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        byte[] bytes = new byte[pixels.length << 2];

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];

            int offset = i << 2;

            bytes[offset] = Atlas.getRedChannelValue(pixel);
            bytes[offset + 1] = Atlas.getGreenChannelValue(pixel);
            bytes[offset + 2] = Atlas.getBlueChannelValue(pixel);
            bytes[offset + 3] = Atlas.getAlphaChannelValue(pixel);
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder());

        buffer.put(bytes).flip();

        return buffer;
    }

    private static byte getRedChannelValue(int pixel) {
        return (byte) ((pixel >> 16) & 0xFF);
    }

    private static byte getGreenChannelValue(int pixel) {
        return (byte) ((pixel >> 8) & 0xFF);
    }

    private static byte getBlueChannelValue(int pixel) {
        return (byte) (pixel & 0xFF);
    }

    private static byte getAlphaChannelValue(int pixel) {
        return (byte) ((pixel >> 24) & 0xFF);
    }

    private static BufferedImage getImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private static void add(ByteBuffer atlas, BlockType blockType, Direction direction) {
        int index = blockType.getIndex() * 6 + direction.getIndex();

        Atlas.add(atlas, index);
    }

    private static void add(ByteBuffer atlas, int index) {
        glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY,
                0,
                0,
                0,
                index,
                Atlas.IMAGE_SIZE,
                Atlas.IMAGE_SIZE,
                1,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                atlas);
    }
}
