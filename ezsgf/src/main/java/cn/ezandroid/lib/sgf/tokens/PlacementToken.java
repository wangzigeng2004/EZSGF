/*
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

package cn.ezandroid.lib.sgf.tokens;

import java.io.IOException;
import java.io.StreamTokenizer;

import cn.ezandroid.lib.sgf.Point;

/**
 * A token that contains the point at which is was played, or the point
 * to which it refers.
 * <p>
 * Most often, this class will be used by BlackMoveToken and WhiteMoveToken.
 */
public class PlacementToken extends SGFToken {

    private Point mPoint = new Point();

    public PlacementToken() {}

    protected boolean parseContent(StreamTokenizer st)
            throws IOException {
        return parsePoint(st);
    }

    /**
     * Parses a point, sets the X and Y values of the PlacementToken
     * accordingly.  This can be called repeatedly for Tokens which take
     * any number of points (see: PlacementListToken).
     * <p>
     * The first opening '[' must have already been read; thus leaving two
     * letters and a closing ']'.  This method reads everything up to and
     * including ']'.  If a ']' follows immediately after the '[', then this
     * move is considered a pass in FF[4].
     * <p>
     * The letters are from 'a' through 'Z', inclusive, to represent row
     * (or column) 1 through 52, respectfully.
     * <p>
     * Please note that 'tt' is not treated as a Pass, in accordance to
     * SGF FF[3].  It is treated as a move at 20, 20.  It is up to the
     * application (which knows the size of the board) to interpret (20, 20)
     * as a pass--if the board size happens to be 19 lines or fewer.
     * <p>
     * Returns:
     * true - The point was perfectly parsed.
     * false - The point wasn't perfectly parsed.
     */
    protected boolean parsePoint(StreamTokenizer st)
            throws IOException {
        int token = st.nextToken();

        if ((token == (int) ']') || (token != StreamTokenizer.TT_WORD))
            return true;

        byte xCoord = -1;
        byte yCoord = -1;
        try {
            int xChar = st.sval.charAt(0);
            int yChar = st.sval.charAt(1);
            xCoord = coordFromChar(xChar);
            yCoord = coordFromChar(yChar);
            setX(xCoord);
            setY(yCoord);
        } catch (Exception e) {
            System.out.println("Error at row=" + yCoord + " col=" + xCoord);
            e.printStackTrace();
        }

        // Read the closing parenthesis; we're done.
        //
        return (st.nextToken() == (int) ']');
    }

    /**
     * Given a token whose value ranges between 'a' through 'z', or 'A'
     * through 'Z', this method returns the appropriate row/column value.  If
     * the token isn't between 'a' and 'z', or 'A' and 'Z', this returns 0;
     */
    static byte coordFromChar(int ch) {
        if ((ch >= 'a') && (ch <= 'z'))
            return (byte) (ch - 'a' + 1);

        if ((ch >= 'A') && (ch <= 'Z'))
            return (byte) (ch - 'A' + 1);

        return 0;
    }

    /**
     * Only subclasses (and classes in this package) may get at this class's
     * Point variable.  Everybody else must use getX() and getY().
     */
    protected Point getPoint() { return mPoint; }

    /**
     * Returns:
     * The X coordinate of the placement.
     */
    public byte getX() { return mPoint.x; }

    public void setX(byte x) { mPoint.x = x; }

    /**
     * Returns:
     * The Y coordinate of the placement.
     */
    public byte getY() { return mPoint.y; }

    public void setY(byte y) { mPoint.y = y; }
}