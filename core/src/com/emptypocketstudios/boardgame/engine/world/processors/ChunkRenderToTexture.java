package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

public class ChunkRenderToTexture extends WorldChunkProcessor implements Disposable {
    public boolean renderImage = true;

    public int pixelsPerCell = 2;
    private float m_fboScaler = 1.5f;
    private boolean m_fboEnabled = true;
    private FrameBuffer m_fbo = null;
    private TextureRegion m_fboRegion = null;

    ShapeRenderer shapeRenderer = null;

    public ChunkRenderToTexture(WorldChunk chunk) {
        super(chunk);
    }

    @Override
    protected void run() {
        if(renderImage) {
            renderChunk();
            renderImage = false;
        }
    }

    public void render() {
        int width = chunk.numCellsX * pixelsPerCell;
        int height = chunk.numCellsY * pixelsPerCell;
        if (m_fboEnabled)      // enable or disable the supersampling
        {
            if (m_fbo == null) {
                // m_fboScaler increase or decrease the antialiasing quality
                m_fbo = new FrameBuffer(Pixmap.Format.RGB565, (int) (width * m_fboScaler), (int) (height * m_fboScaler), false);
                m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
                m_fboRegion.flip(false, true);
            }
            m_fbo.begin();
        }

        // this is the main render function
        renderChunk();

        if (m_fbo != null) {
            m_fbo.end();
        }
    }

    public void renderChunk(){

    }

    @Override
    public void dispose() {
        if(shapeRenderer!= null){
            shapeRenderer.dispose();
        }
    }
}
