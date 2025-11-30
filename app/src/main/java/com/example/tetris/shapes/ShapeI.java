import com.example.tetris.Piece;

public class ShapeI extends Piece {
    public ShapeI(int xMin, int yMin) {
        super(xMin, yMin);
        this.xMax = xMin + 3;
        this.yMax = yMin;
        this.blocks[0] = new int[]{yMin, xMin};
        this.blocks[1] = new int[]{yMin, xMin + 1};
        this.blocks[2] = new int[]{yMin, xMin + 2};
        this.blocks[3] = new int[]{yMin, xMin + 3};
        this.projection = getProjection();
    }

    @Override
    public void rotate() {
    }
}