/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.ebics.io;

import javax.crypto.spec.SecretKeySpec;

import org.kopi.ebics.exception.EbicsException;
import org.kopi.ebics.interfaces.ContentFactory;
import org.kopi.ebics.utils.Utils;


/**
 * A mean to split a given input file to
 * 1MB portions. this i useful to handle
 * big file uploading.
 *
 * @author Hachani
 */
public class Splitter {

    /**
     * The maximum size of a segment to be put in a request. base64 encoding adds
     * 33% to that, and we need to stay below 1 MB. This is where 700kB comes from,
     * to make sure we stay below that.
     */
    private static final int SEGMENT_SIZE = 700000;
    private byte[] input;
    private byte[] content;
    private int segmentSize;
    private int numSegments;

    /**
     * Constructs a new <code>FileSplitter</code> with a given file.
     *
     * @param input the input byte array
     */
    public Splitter(byte[] input) {
        this.input = input;
    }

    /**
     * Reads the input stream and splits it to segments of 1MB size.
     *
     * <p>EBICS Specification 2.4.2 - 7 Segmentation of the order data:
     *
     * <p>The following procedure is to be followed with segmentation:
     * <ol>
     *   <li> The order data is ZIP compressed
     *   <li> The compressed order data is encrypted in accordance with Chapter 6.2
     *   <li> The compressed, encrypted order data is base64-coded.
     *    <li> The result is to be verified with regard to the data volume:
     *      <ol>
     *        <li> If the resulting data volume is below the threshold of 1 MB = 1,048,576 bytes,
     *             the order data can be sent complete as a data segment within one transmission step
     *        <li> If the resulting data volume exceeds 1,048,576 bytes the data is to be
     *             separated sequentially and in a base64-conformant manner into segments
     *             that each have a maximum of 1,048,576 bytes.
     *     </ol>
     *
     * @param isCompressionEnabled enable compression?
     * @param keySpec              the secret key spec
     * @throws EbicsException
     */
    public final void readInput(boolean isCompressionEnabled, SecretKeySpec keySpec)
            throws EbicsException {
        try {
            if (isCompressionEnabled) {
                input = Utils.zip(input);
            }
            content = Utils.encrypt(input, keySpec);
            segmentation();
        } catch (Exception e) {
            throw new EbicsException(e.getMessage(), e);
        }
    }

    /**
     * Slits the input into 1MB portions.
     *
     * <p> EBICS Specification 2.4.2 - 7 Segmentation of the order data:
     *
     * <p>In Version H003 of the EBICS standard, order data that requires more than 1 MB of storage
     * space in compressed, encrypted and base64-coded form MUST be segmented before
     * transmission, irrespective of the transfer direction (upload/download).
     */
    private void segmentation() {

        numSegments = content.length / SEGMENT_SIZE;

        if (content.length % SEGMENT_SIZE != 0) {
            numSegments++;
        }

        segmentSize = content.length / numSegments;
    }


    /**
     * Returns the content of a data segment according to
     * a given segment number.
     *
     * @param segmentNumber the segment number
     * @return content of a data segment
     */
    public ContentFactory getContent(int segmentNumber) {
        byte[] segment;
        int offset;

        offset = segmentSize * (segmentNumber - 1);
        if (content.length < segmentSize + offset) {
            segment = new byte[content.length - offset];
        } else {
            segment = new byte[segmentSize];
        }

        System.arraycopy(content, offset, segment, 0, segment.length);
        return new ByteArrayContentFactory(segment);
    }

    /**
     * Returns the hole content.
     *
     * @return the input content.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns the total segment number.
     *
     * @return the total segment number.
     */
    public int getSegmentNumber() {
        return numSegments;
    }

    /**
     * Returns the size of each segment.
     *
     * @return the size of each segment.
     */
    int getSegmentSize() {
        return segmentSize;
    }
}
