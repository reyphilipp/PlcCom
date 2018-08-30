/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Control Net Path for class, instance, attribute
 * <p>
 * Example (with suitable static import):
 * <p>
 * <code>CNPath path = Identity.instance(1).attr(7)</code>
 *
 * @author Kay Kasemir, László Pataki
 */
@SuppressWarnings("nls")
public class CNClassPath extends CNPath
{
    private int class_code;
    private String class_name;
    private int instance = 1, attr = 0;

    public CNClassPath()
    {
    }

    protected CNClassPath(final int class_code, final String class_name)
    {
        this.class_code = class_code;
        this.class_name = class_name;
    }

    public CNClassPath instance(final int instance)
    {
        this.instance = instance;
        return this;
    }

    public CNPath attr(final int attr)
    {
        this.attr = attr;
        return this;
    }

    /** @return Path length in words */
    public byte getPathLength()
    {
        return this.attr == 0 ? (byte) 2 : (byte) 3;
    }

    @Override
    public int getRequestSize()
    { // Convert words into bytes
        return this.getPathLength() * 2;
    }

    /** {@inheritDoc} */
    @Override
    public void encode(final ByteBuffer buf, final StringBuilder log)
    {
        buf.put(this.getPathLength());
        buf.put((byte) 0x20);
        buf.put((byte) this.class_code);
        buf.put((byte) 0x24);
        buf.put((byte) this.instance);
        if (this.attr > 0)
        {
            buf.put((byte) 0x30);
            buf.put((byte) this.attr);
        }
    }

    @Override
    public String toString()
    {
        if (this.attr > 0)
        {
            return String.format(
                    "Path (3 el) Class(0x20) 0x%X (%s), instance(0x24) %d, attrib.(0x30) 0x%X",
                    this.class_code, this.class_name, this.instance, this.attr);
        }
        return String.format(
                "Path (2 el) Class(0x20) 0x%X (%s), instance(0x24) %d",
                this.class_code, this.class_name, this.instance);
    }

    @Override
    public int getResponseSize(final ByteBuffer buf) throws Exception
    {
        return 2 + this.getPathLength() * 2;
    }

    @Override
    public void decode(final ByteBuffer buf, int available,
            final StringBuilder log) throws Exception
    {
        final byte[] raw = new byte[2];
        buf.get(raw);
        available = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN)
                .getShort();

        if (raw[0] == 0x02)
        {
            buf.get(raw);
            if (raw[0] == 0x20)
            {
                this.class_code = new Integer(raw[1]);
                this.class_name = "Ethernet Link";
            }
            buf.get(raw);
            if (raw[0] == 0x24)
            {
                this.instance(new Integer(raw[1]));
            }
        }
    }
}
