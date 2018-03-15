package entity.model;

import world.Block;

import java.nio.ByteBuffer;

public class Minion extends MoveEntity {
    public final Entity source;

    public Minion(EntityType entityType, int level, float x, float y, int wave, Block block, boolean isEnemy, Entity source) {
        super(entityType, level, x, y, wave, block, isEnemy);
        this.source = source;
    }

    public Minion(ByteBuffer buf, Block[][] blocks){
        super(buf,blocks);
        int sX = buf.getInt();
        int sY = buf.getInt();

        if(sX >= 0 && sX < blocks.length && sY >= 0 && sY < blocks[sX].length) {
            Entity tower = blocks[sX][sY].getTower();
            if (tower != null){
                source = tower;
                tower.addAmmo(-1);
            } else {
                source = null;
            }
        } else {
            source = null;
        }
    }

    @Override
    public void write(ByteBuffer buf){
        super.write(buf);
        if(source != null && source.getHp() > 0){
            buf.putInt(Math.round(source.getX()));
            buf.putInt(Math.round(source.getY()));
        } else {
            buf.putInt(-1);
            buf.putInt(-1);
        }
    }

    public static int byteSize(){
        return MoveEntity.byteSize();
    }

    @Override
    public byte firstByte(){
        byte src = super.firstByte();
        return (byte)(src | byteMask.isMinion.mask);
    }

    @Override
    public void firstByte(byte b){
        super.firstByte(b);
    }
}
