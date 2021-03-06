/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package cn.ezandroid.aq.test;

import java.io.Serializable;
import java.util.Random;

/**
 * Zobrist哈希表
 * <p>
 * 实现了棋盘的每个状态都有一个唯一的Hash值
 * 参见 http://en.wikipedia.org/wiki/Zobrist_hashing
 *
 * @author Barry Becker
 */
public final class ZobristHash implements Cloneable, Serializable {

    private static final long serialVersionUID = 42L;

    public static final int STATE_EMPTY = 0;
    public static final int STATE_BLACK = 1;
    public static final int STATE_WHITE = 2;

    // 棋盘的Hash表
    private long[][][] mBoardHashTable;
    // Pass的Hash值
    private long mPassHash;

    private HashKey mCurrentKey;

    private byte mBoardSize;

    public ZobristHash(byte boardSize) {
        mBoardSize = boardSize;
        initZobristHash(mBoardSize);
        mCurrentKey = new HashKey();
    }

    public ZobristHash(byte boardSize, HashKey hashKey) {
        mBoardSize = boardSize;
        initZobristHash(mBoardSize);
        mCurrentKey = hashKey.copy();
    }

    public ZobristHash(byte boardSize, long passHash, long[][][] boardHashTable) {
        mBoardSize = boardSize;
        mPassHash = passHash;
        mBoardHashTable = boardHashTable;
        mCurrentKey = new HashKey();
    }

    public ZobristHash(byte boardSize, long passHash, long[][][] boardHashTable, HashKey hashKey) {
        mBoardSize = boardSize;
        mPassHash = passHash;
        mBoardHashTable = boardHashTable;
        mCurrentKey = hashKey.copy();
    }

    public ZobristHash(Game board) {
        mBoardSize = (byte) board.getBoardSize();
        initZobristHash(mBoardSize);
        mCurrentKey = getInitialKey(board);
    }

    public ZobristHash(Game board, long passHash, long[][][] boardHashTable) {
        mBoardSize = (byte) board.getBoardSize();
        mPassHash = passHash;
        mBoardHashTable = boardHashTable;
        mCurrentKey = getInitialKey(board);
    }

    @Override
    public ZobristHash clone() throws CloneNotSupportedException {
        return new ZobristHash(mBoardSize, mPassHash, mBoardHashTable, mCurrentKey);
    }

    private void initZobristHash(int boardSize) {
        int positionStateCount = getPositionStateCount();
        Random random = new Random(0);
        mPassHash = random.nextLong();
        mBoardHashTable = new long[positionStateCount][boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                for (int state = 0; state < positionStateCount; state++) {
                    mBoardHashTable[state][i][j] = random.nextLong();
                }
            }
        }
    }

    private HashKey getInitialKey(Game board) {
        mCurrentKey = new HashKey();
        // FIXME 无视了Pass着，需要考虑吗？
        int boardSize = board.getBoardSize();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Intersection intersection = new Intersection(i, j);
                if (board.occupied(intersection)) {
                    applyPositionToKey(i, j, getStateIndex(board, intersection));
                }
            }
        }
        return mCurrentKey;
    }

    private int getStateIndex(Game board, Intersection intersection) {
        Chain chain = board.getChain(intersection);
        if (chain != null) {
            switch (chain.getStoneColor()) {
                case BLACK:
                    return STATE_BLACK;
                case WHITE:
                    return STATE_WHITE;
            }
        }
        return STATE_EMPTY;
    }

    long getPassHash() {
        return mPassHash;
    }

    long[][][] getBoardHashTable() {
        return mBoardHashTable;
    }

    /**
     * 一个位置的状态数
     * <p>
     * 空0、黑1、白2
     *
     * @return
     */
    public static int getPositionStateCount() {
        return 3;
    }

    /**
     * 获取当前棋盘状态的Hash值
     *
     * @return
     */
    public HashKey getKey() {
        return mCurrentKey;
    }

    public byte getBoardSize() {
        return mBoardSize;
    }

    public void applyMove(int x, int y, int stateIndex) {
        applyPositionToKey(x, y, stateIndex);
    }

    public void applyPassingMove() {
        mCurrentKey.applyMove(mPassHash);
    }

    public void applyMoveNumber(int number) {
        mCurrentKey.applyMove(number);
    }

    private void applyPositionToKey(int x, int y, int stateIndex) {
        long specialNum = mBoardHashTable[stateIndex][x][y];
        mCurrentKey.applyMove(specialNum);
    }
}
