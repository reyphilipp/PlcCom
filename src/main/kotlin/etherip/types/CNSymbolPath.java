/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Control Net Path for element path
 * <p>
 * Example (with suitable static import):
 * <p>
 * <code>CNPath path = Symbol.name("my_tag")</code>
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CNSymbolPath extends CNPath
{
    /**
     * One element of a path
     * <p>
     * Contains a string path and an optional array index
     */
    class PathAndIndex
    {
        private final String path;
        private final Integer index;

        public PathAndIndex(final String path, final Integer index)
        {
            this.path = path;
            this.index = index;
        }

        public String getPath()
        {
            return this.path;
        }

        public Integer getIndex()
        {
            return this.index;
        }

        @Override
        public String toString()
        {
            if (this.index == null)
            {
                return this.path;
            }
            return this.path + "[" + this.index + "]";
        }
    };

    private final Pattern PATTERN_BRACKETS = Pattern.compile("\\[(\\d+)\\]");

    private final List<PathAndIndex> paths = new ArrayList<>();

    /**
     * Initialize
     *
     * @param symbol
     *            Name of symbol
     */
    protected CNSymbolPath(final String symbol)
    {
        for (final String s : symbol.split("\\."))
        {
            final Matcher m = this.PATTERN_BRACKETS.matcher(s);
            Integer index = null;
            String path = s;
            while (m.find())
            {
                final String match = m.group().replace("[", "").replace("]",
                        "");
                index = Integer.parseInt(match);
                path = path.replace("[" + match + "]", "");
            }
            this.paths.add(new PathAndIndex(path, index));
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getRequestSize()
    { // End of string is padded if length is odd
        int count = 0;
        for (final PathAndIndex s : this.paths)
        {
            count += 2 + s.getPath().length()
                    + (this.needPad(s.getPath()) ? 1 : 0);
            if (s.getIndex() != null)
            {
                count += 2;
            }
        }
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        // spec 4 p.21: "ANSI extended symbol segment"
        buf.put((byte) (this.getRequestSize() / 2));
        for (final PathAndIndex pi : this.paths)
        {
            final String s = pi.getPath();
            buf.put((byte) 0x91);
            buf.put((byte) s.length());
            buf.put(s.getBytes());
            if (this.needPad(s))
            {
                buf.put((byte) 0);
            }
            final Integer index = pi.getIndex();
            if (index != null)
            {
                // Path Segment 28, from wireshark
                buf.put((byte) 0x28);
                buf.put(index.byteValue());
            }
        }
    }

    /** @return Is path of odd length, requiring a pad byte? */
    private boolean needPad(final String s)
    {
        // Findbugs: x%2==1 fails for negative numbers
        return (s.length() % 2) != 0;
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Path Symbol(0x91) ");
        for (final PathAndIndex pi : this.paths)
        {
            if (buf.length() > 18)
            {
                buf.append(", ");
            }
            buf.append('\'').append(pi).append('\'');
            if (this.needPad(pi.getPath()))
            {
                buf.append(", 0x00");
            }
        }
        return buf.toString();
    }

    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void decode(final ByteBuffer buf, final int available,
            final StringBuilder log) throws Exception
    {
        // TODO Auto-generated method stub

    }
}
